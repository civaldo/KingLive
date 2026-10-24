package com.kinglive;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private int lions = 0;
    private int dragons = 0;
    private TextView score;
    private TextView status;
    private LinearLayout root;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(24, 32, 24, 24);
        root.setBackgroundColor(Color.rgb(17, 19, 40));

        TextView title = text("👑 KING LIVE ⚔️", 30, Color.rgb(255, 210, 75), true);
        root.addView(title, params(-1, -2));
        TextView subtitle = text("BATALHA DA LIVE", 16, Color.WHITE, true);
        root.addView(subtitle, params(-1, -2));
        addSpace(24);

        TextView teams = text("🦁  LEÕES     VS     🐉  DRAGÕES", 20, Color.WHITE, true);
        root.addView(teams, params(-1, -2));
        score = text("0   x   0", 42, Color.rgb(255, 210, 75), true);
        root.addView(score, params(-1, -2));
        status = text("A batalha está pronta!", 16, Color.LTGRAY, false);
        root.addView(status, params(-1, -2));
        addSpace(20);

        addButton("💬 Simular comentário: LEÃO", Color.rgb(40, 110, 220), v -> { lions++; update("Comentário de teste fortaleceu os Leões!"); });
        addButton("💬 Simular comentário: DRAGÃO", Color.rgb(190, 55, 65), v -> { dragons++; update("Comentário de teste fortaleceu os Dragões!"); });
        addButton("🎁 Simular presente para LEÕES", Color.rgb(40, 140, 100), v -> { lions += 5; update("Presente simulado: +5 para os Leões!"); });
        addButton("🎁 Simular presente para DRAGÕES", Color.rgb(150, 70, 160), v -> { dragons += 5; update("Presente simulado: +5 para os Dragões!"); });
        addButton("🔄 Reiniciar batalha", Color.rgb(70, 75, 95), v -> { lions = 0; dragons = 0; update("Nova batalha iniciada!"); });
        addSpace(12);
        TextView note = text("Versão de teste: comentários e presentes são simulados. A conexão real com TikTok LIVE ainda não está ativada.", 13, Color.LTGRAY, false);
        root.addView(note, params(-1, -2));
        setContentView(root);
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setGravity(Gravity.CENTER); t.setPadding(4, 8, 4, 8);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private LinearLayout.LayoutParams params(int w, int h) { return new LinearLayout.LayoutParams(w, h); }
    private void addSpace(int height) { View s = new View(this); root.addView(s, params(1, height)); }
    private void addButton(String label, int color, View.OnClickListener click) {
        Button b = new Button(this); b.setText(label); b.setTextColor(Color.WHITE); b.setAllCaps(false); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color)); b.setOnClickListener(click);
        LinearLayout.LayoutParams p = params(-1, -2); p.setMargins(0, 5, 0, 5); root.addView(b, p);
    }
    private void update(String message) { score.setText(lions + "   x   " + dragons); status.setText(message + (lions == dragons ? "" : (lions > dragons ? "  🦁 Leões na frente!" : "  🐉 Dragões na frente!"))); }
}
