import asyncio
import os
import threading
import time
from collections import deque
from flask import Flask, jsonify, request
from TikTokLive import TikTokLiveClient
from TikTokLive.events import CommentEvent, GiftEvent, ConnectEvent, DisconnectEvent

app = Flask(__name__)
EVENTS = deque(maxlen=1000)
EVENT_ID = 0
LOCK = threading.Lock()
CLIENT = None
CLIENT_THREAD = None
CONNECTED = False
USERNAME = os.getenv("TIKTOK_UNIQUE_ID", "").strip().lstrip("@")
DEFAULT_TEAM = os.getenv("KINGLIVE_DEFAULT_TEAM", "lions").strip().lower()
if DEFAULT_TEAM not in ("lions", "dragons"):
    DEFAULT_TEAM = "lions"

def push_event(kind, team, points, details):
    global EVENT_ID
    with LOCK:
        EVENT_ID += 1
        EVENTS.append({
            "id": EVENT_ID, "type": kind, "team": team,
            "points": points, "details": details, "time": int(time.time())
        })

def team_from_text(value):
    text = (value or "").casefold()
    lion = any(word in text for word in ("leão", "leoes", "leões", "lion", "🦁"))
    dragon = any(word in text for word in ("dragão", "dragoes", "dragões", "dragon", "🐉"))
    if lion and not dragon:
        return "lions"
    if dragon and not lion:
        return "dragons"
    return None

async def run_client(username):
    global CLIENT, CONNECTED
    CLIENT = TikTokLiveClient(unique_id=username)

    @CLIENT.on(ConnectEvent)
    async def on_connect(_):
        global CONNECTED
        CONNECTED = True
        push_event("status", DEFAULT_TEAM, 0, f"Conectado à LIVE @{username}")

    @CLIENT.on(DisconnectEvent)
    async def on_disconnect(_):
        global CONNECTED
        CONNECTED = False
        push_event("status", DEFAULT_TEAM, 0, "Conexão TikTok LIVE encerrada")

    @CLIENT.on(CommentEvent)
    async def on_comment(event):
        team = team_from_text(getattr(event, "comment", "")) or DEFAULT_TEAM
        nickname = getattr(getattr(event, "user", None), "nickname", "Espectador")
        comment = getattr(event, "comment", "")
        push_event("comment", team, 1, f"{nickname}: {comment}")

    @CLIENT.on(GiftEvent)
    async def on_gift(event):
        gift = getattr(event, "gift", None)
        if gift is None:
            return
        # Do not score intermediate frames of a streak; score the final event once.
        if getattr(event, "streaking", False):
            return
        repeat_count = int(getattr(event, "repeat_count", 1) or 1)
        nickname = getattr(getattr(event, "user", None), "nickname", "Espectador")
        gift_name = getattr(gift, "name", "Presente")
        team = DEFAULT_TEAM
        push_event("gift", team, 5 * repeat_count,
                   f"{nickname} enviou {gift_name} x{repeat_count}")

    await CLIENT.start()

@app.get("/health")
def health():
    return jsonify({"ok": True, "connected": CONNECTED, "username": USERNAME or None,
                    "default_team": DEFAULT_TEAM})

@app.get("/api/events")
def get_events():
    try:
        since = int(request.args.get("since", "0"))
    except ValueError:
        return jsonify({"error": "since precisa ser um número inteiro"}), 400
    with LOCK:
        events = [event for event in EVENTS if event["id"] > since]
        latest = EVENT_ID
    return jsonify({"events": events, "latest_id": latest, "connected": CONNECTED})

@app.post("/api/connect")
def connect():
    global CLIENT_THREAD, USERNAME
    data = request.get_json(silent=True) or {}
    username = str(data.get("username") or USERNAME).strip().lstrip("@")
    if not username:
        return jsonify({"error": "Informe o @ do perfil que está ao vivo."}), 400
    if CLIENT_THREAD and CLIENT_THREAD.is_alive():
        return jsonify({"ok": True, "message": "Já existe uma tentativa de conexão."})
    USERNAME = username
    def runner():
        try:
            asyncio.run(run_client(username))
        except Exception as exc:
            push_event("error", DEFAULT_TEAM, 0, f"Falha de conexão: {type(exc).__name__}: {exc}")
    CLIENT_THREAD = threading.Thread(target=runner, daemon=True)
    CLIENT_THREAD.start()
    return jsonify({"ok": True, "message": f"Tentando conectar a @{username}"})

@app.get("/")
def index():
    return jsonify({"service": "King Live TikTok Bridge", "health": "/health",
                    "events": "/api/events?since=0",
                    "instructions": "POST /api/connect com JSON {\"username\":\"perfil\"}"})

if __name__ == "__main__":
    port = int(os.getenv("PORT", "10000"))
    app.run(host="0.0.0.0", port=port)
