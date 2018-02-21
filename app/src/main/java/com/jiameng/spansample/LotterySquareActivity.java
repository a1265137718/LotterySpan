package com.jiameng.spansample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jiameng.lottery.square.LuckyMonkeyPanelView;

import java.util.Random;

public class LotterySquareActivity extends AppCompatActivity {

    private LuckyMonkeyPanelView luckyPanel;
    private Button btnAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_square);
        luckyPanel = findViewById(R.id.lucky_panel);
        btnAction = findViewById(R.id.btn_action);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckyPanel.isGameRunning()) {
                    luckyPanel.startGame();
                } else {
                    int stayIndex = new Random().nextInt(8);
                    Log.e("LuckyMonkeyPanelView", "====stay===" + stayIndex);
                    luckyPanel.tryToStop(stayIndex);
                }
            }
        });
    }
}
