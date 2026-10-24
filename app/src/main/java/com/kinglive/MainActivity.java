package com.kinglive;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int lions = 0;
    private int dragons = 0;
    private TextView lionScore, dragonScore, status, eventLog, battleStatus;
    private ProgressBar lionBar, dragonBar;
    private LinearLayout root;
    private EditText bridgeUrlInput, tiktokUserInput;
    private volatile boolean bridgeRunning = false;
    private long lastEventId = 0;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final int bg = Color.rgb(12, 15, 31);
    private final int gold = Color.rgb(255, 197, 65);
    private final int muted = Color.rgb(177, 187, 211);
    private final int blue = Color.rgb(54, 132, 255);
    private final int red = Color.rgb(255, 75, 91);

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(12, 15, 31));
        getWindow().setNavigationBarColor(Color.rgb(12, 15, 31));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(26));
        root.setBackgroundColor(bg);
        scroll.addView(root);

        TextView brand = text("👑  KING LIVE", 29, gold, true);
        brand.setLetterSpacing(0.04f);
        root.addView(brand, params(-1, -2));
        TextView subtitle = text("ARENA DE BATALHAS AO VIVO", 12, muted, true);
        subtitle.setLetterSpacing(0.12f);
        root.addView(subtitle, params(-1, -2));
        addSpace(18);

        LinearLayout livePill = new LinearLayout(this);
        livePill.setGravity(Gravity.CENTER);
        livePill.setPadding(dp(12), dp(8), dp(12), dp(8));
        livePill.setBackground(round(Color.rgb(36, 42, 63), 24, Color.rgb(58, 67, 94)));
        TextView liveText = text("●  MODO DE TESTE  •  TIKTOK NÃO CONECTADO", 11, Color.rgb(255, 202, 103), true);
        livePill.addView(liveText);
        root.addView(livePill, params(-1, -2));
        addSpace(18);

        LinearLayout arena = new LinearLayout(this);
        arena.setOrientation(LinearLayout.VERTICAL);
        arena.setPadding(dp(14), dp(16), dp(14), dp(16));
        arena.setBackground(round(Color.rgb(24, 29, 51), 24, Color.rgb(54, 62, 94)));
        TextView arenaTitle = text("⚔️  BATALHA PRINCIPAL  ⚔️", 14, Color.WHITE, true);
        arena.addView(arenaTitle, params(-1, -2));
        addSpaceTo(arena, 10);

        LinearLayout teams = new LinearLayout(this);
        teams.setOrientation(LinearLayout.HORIZONTAL);
        teams.setGravity(Gravity.CENTER);
        arena.addView(teams, params(-1, -2));
        teams.addView(teamPanel("🦁", "LEÕES", blue, true), new LinearLayout.LayoutParams(0, dp(138), 1));
        TextView vs = text("VS", 15, gold, true);
        vs.setPadding(dp(8), 0, dp(8), 0);
        teams.addView(vs, params(-2, -2));
        teams.addView(teamPanel("🐉", "DRAGÕES", red, false), new LinearLayout.LayoutParams(0, dp(138), 1));

        lionScore = text("0", 34, Color.WHITE, true);
        dragonScore = text("0", 34, Color.WHITE, true);
        // Team panels are populated with score placeholders and updated below.
        LinearLayout scoreRow = new LinearLayout(this);
        scoreRow.setOrientation(LinearLayout.HORIZONTAL);
        scoreRow.setGravity(Gravity.CENTER);
        scoreRow.setPadding(0, dp(12), 0, 0);
        LinearLayout leftScore = new LinearLayout(this);
        leftScore.setOrientation(LinearLayout.VERTICAL);
        leftScore.setGravity(Gravity.CENTER);
        LinearLayout rightScore = new LinearLayout(this);
        rightScore.setOrientation(LinearLayout.VERTICAL);
        rightScore.setGravity(Gravity.CENTER);
        leftScore.addView(lionScore, params(-1, -2));
        lionBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        lionBar.setMax(100); lionBar.setProgress(0);
        lionBar.setProgressTintList(android.content.res.ColorStateList.valueOf(blue));
        leftScore.addView(lionBar, params(-1, dp(7)));
        rightScore.addView(dragonScore, params(-1, -2));
        dragonBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        dragonBar.setMax(100); dragonBar.setProgress(0);
        dragonBar.setProgressTintList(android.content.res.ColorStateList.valueOf(red));
        rightScore.addView(dragonBar, params(-1, dp(7)));
        scoreRow.addView(leftScore, new LinearLayout.LayoutParams(0, -2, 1));
        scoreRow.addView(rightScore, new LinearLayout.LayoutParams(0, -2, 1));
        arena.addView(scoreRow, params(-1, -2));
        root.addView(arena, params(-1, -2));
        addSpace(14);

        battleStatus = text("🔥 A batalha está pronta para começar!", 14, gold, true);
        battleStatus.setPadding(dp(8), dp(10), dp(8), dp(10));
        root.addView(battleStatus, params(-1, -2));
        addSpace(6);

        TextView testHeader = text("CONTROLES DE DEMONSTRAÇÃO", 12, muted, true);
        testHeader.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        root.addView(testHeader, params(-1, -2));
        addSpace(8);
        addButton("💬  Comentário de teste → Leões  (+1)", blue, v -> { lions++; update("Comentário de teste pontuou os Leões!"); });
        addButton("💬  Comentário de teste → Dragões  (+1)", red, v -> { dragons++; update("Comentário de teste pontuou os Dragões!"); });
        addButton("🎁  Presente de teste → Leões  (+5)", Color.rgb(26, 153, 123), v -> { lions += 5; update("Presente simulado para os Leões: +5!"); });
        addButton("🎁  Presente de teste → Dragões  (+5)", Color.rgb(151, 83, 218), v -> { dragons += 5; update("Presente simulado para os Dragões: +5!"); });
        addButton("↻  Reiniciar batalha", Color.rgb(54, 61, 84), v -> { lions = 0; dragons = 0; update("Nova batalha iniciada!"); });
        addSpace(16);

        LinearLayout connection = new LinearLayout(this);
        connection.setOrientation(LinearLayout.VERTICAL);
        connection.setPadding(dp(14), dp(14), dp(14), dp(14));
        connection.setBackground(round(Color.rgb(20, 34, 43), 18, Color.rgb(39, 104, 99)));
        TextView connectionTitle = text("🔌  INTEGRAÇÃO TIKTOK LIVE", 14, Color.rgb(116, 235, 199), true);
        connectionTitle.setGravity(Gravity.LEFT);
        connection.addView(connectionTitle, params(-1, -2));
        TextView connectionBody = text("Configure a URL HTTPS da ponte publicada e o @ do perfil que está ao vivo. Comentário real: +1. Presente real: +5 por unidade. Comentários com LEÃO/DRAGÃO escolhem a equipe; os demais e os presentes vão para a equipe padrão configurada na ponte.", 13, Color.rgb(209, 222, 230), false);
        connectionBody.setGravity(Gravity.LEFT);
        connectionBody.setPadding(0, dp(8), 0, dp(8));
        connection.addView(connectionBody, params(-1, -2));
        bridgeUrlInput = editField("https://sua-ponte.onrender.com", "URL HTTPS da ponte");
        tiktokUserInput = editField("@seuperfil", "@ do perfil que está ao vivo");
        connection.addView(bridgeUrlInput, params(-1, dp(52)));
        connection.addView(tiktokUserInput, params(-1, dp(52)));
        root.addView(connection, params(-1, -2));
        addButton("🔗  Conectar ao TikTok LIVE", Color.rgb(16, 126, 101), v -> connectBridge());
        addSpace(14);

        eventLog = text("📣  Último evento: nenhum", 12, muted, false);
        eventLog.setGravity(Gravity.LEFT);
        eventLog.setPadding(dp(4), dp(4), dp(4), dp(4));
        root.addView(eventLog, params(-1, -2));

        TextView footer = text("KING LIVE  •  VERSÃO DE DESENVOLVIMENTO", 10, Color.rgb(101, 112, 142), true);
        footer.setPadding(0, dp(18), 0, 0);
        root.addView(footer, params(-1, -2));
        setContentView(scroll);
    }

    private View teamPanel(String emoji, String name, int color, boolean left) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(6), dp(10), dp(6), dp(10));
        panel.setBackground(round(left ? Color.rgb(24, 58, 101) : Color.rgb(89, 35, 51), 18, color));
        TextView mascot = text(emoji, 38, Color.WHITE, false);
        panel.addView(mascot, params(-1, -2));
        TextView label = text(name, 13, Color.WHITE, true);
        panel.addView(label, params(-1, -2));
        return panel;
    }

    private EditText editField(String hint, String description) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint(hint);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(muted);
        input.setTextSize(14);
        input.setPadding(dp(10), dp(4), dp(10), dp(4));
        input.setBackground(round(Color.rgb(29, 39, 55), 10, Color.rgb(66, 88, 112)));
        input.setContentDescription(description);
        LinearLayout.LayoutParams p = params(-1, dp(52));
        p.setMargins(0, dp(3), 0, dp(3));
        return input;
    }

    private void connectBridge() {
        final String base = bridgeUrlInput.getText().toString().trim().replaceAll("/+$", "");
        final String username = tiktokUserInput.getText().toString().trim().replace("@", "");
        if (!base.startsWith("https://") || username.isEmpty()) {
            eventLog.setText("📣 Informe a URL HTTPS da ponte e o @ do perfil.");
            return;
        }
        if (bridgeRunning) {
            bridgeRunning = false;
            eventLog.setText("📣 Desconectando a leitura no aplicativo…");
            return;
        }
        bridgeRunning = true;
        eventLog.setText("📣 Conectando à ponte…");
        Thread worker = new Thread(() -> {
            try {
                JSONObject initial = requestJson("GET", base + "/api/events?since=0", null);
                lastEventId = initial.optLong("latest_id", 0);
                JSONObject body = new JSONObject();
                body.put("username", username);
                requestJson("POST", base + "/api/connect", body);
                mainHandler.post(() -> eventLog.setText("📣 Ponte acionada. Aguardando eventos reais…"));
                while (bridgeRunning) {
                    JSONObject response = requestJson("GET", base + "/api/events?since=" + lastEventId, null);
                    JSONArray events = response.optJSONArray("events");
                    if (events != null) {
                        for (int i = 0; i < events.length(); i++) {
                            JSONObject event = events.optJSONObject(i);
                            if (event == null) continue;
                            lastEventId = Math.max(lastEventId, event.optLong("id", lastEventId));
                            final String type = event.optString("type", "");
                            final String team = event.optString("team", "lions");
                            final int points = event.optInt("points", 0);
                            final String details = event.optString("details", type);
                            mainHandler.post(() -> {
                                if ("comment".equals(type) || "gift".equals(type)) {
                                    if ("dragons".equals(team)) dragons += points;
                                    else lions += points;
                                    update(details + " (+" + points + " pontos)");
                                } else {
                                    eventLog.setText("📣 " + details);
                                }
                            });
                        }
                    }
                    Thread.sleep(1800);
                }
            } catch (Exception e) {
                mainHandler.post(() -> eventLog.setText("📣 Erro na ponte: " + e.getMessage()));
            } finally {
                bridgeRunning = false;
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    private JSONObject requestJson(String method, String address, JSONObject body) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("Accept", "application/json");
        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            try (OutputStream out = connection.getOutputStream()) {
                out.write(body.toString().getBytes("UTF-8"));
            }
        }
        int code = connection.getResponseCode();
        java.io.InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
        }
        connection.disconnect();
        if (code < 200 || code >= 300) throw new Exception("HTTP " + code + ": " + result);
        return new JSONObject(result.toString());
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); t.setPadding(dp(3), dp(5), dp(3), dp(5));
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private GradientDrawable round(int color, int radius, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius));
        d.setStroke(dp(1), stroke);
        return d;
    }

    private LinearLayout.LayoutParams params(int w, int h) { return new LinearLayout.LayoutParams(w, h); }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private void addSpace(int height) { View s = new View(this); root.addView(s, params(1, dp(height))); }
    private void addSpaceTo(LinearLayout layout, int height) { View s = new View(this); layout.addView(s, params(1, dp(height))); }

    private void addButton(String label, int color, View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(label); b.setTextColor(Color.WHITE); b.setTextSize(13);
        b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setPadding(dp(8), dp(8), dp(8), dp(8));
        b.setBackground(round(color, 15, color));
        b.setOnClickListener(click);
        LinearLayout.LayoutParams p = params(-1, -2);
        p.setMargins(0, dp(4), 0, dp(4));
        root.addView(b, p);
    }

    private void update(String message) {
        lionScore.setText(String.valueOf(lions));
        dragonScore.setText(String.valueOf(dragons));
        int total = Math.max(1, lions + dragons);
        lionBar.setProgress(Math.min(100, lions * 100 / total));
        dragonBar.setProgress(Math.min(100, dragons * 100 / total));
        if (lions == dragons) battleStatus.setText("⚔️  Empate! A batalha está equilibrada.");
        else battleStatus.setText(lions > dragons ? "🔥  LEÕES estão na frente!" : "🔥  DRAGÕES estão na frente!");
        status = battleStatus;
        eventLog.setText("📣  Último evento: " + message);
    }
}
