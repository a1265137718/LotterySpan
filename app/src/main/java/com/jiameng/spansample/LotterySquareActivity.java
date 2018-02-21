package com.jiameng.spansample;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jiameng.lottery.square.LuckyMonkeyPanelView;
import com.jiameng.lottery.square.PanelStateListener;

import java.util.Random;

public class LotterySquareActivity extends AppCompatActivity {
    private static final String TAG = "LotterySquareActivity";
    private LuckyMonkeyPanelView luckyPanel;
    private ImageView fortuneSound;
    private Button btnAction;

    private SoundPool soundPool = null;
    private int explosionId = 0;    //内存加载ID
    private int playSourceId = 0;    //播放ID
    private boolean isPlaySound = true;    //播放ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_square);
        luckyPanel = findViewById(R.id.lucky_panel);
        fortuneSound = findViewById(R.id.fortune_sound);
        btnAction = findViewById(R.id.btn_action);

        luckyPanel.setPanelStateListener(new PanelStateListener() {
            @Override
            public void onPanelStateStart() {
                Log.e(TAG, "方法名>>onPanelStateStart()>>>>" + "转盘开始转动");
                //播放音效
                if (isPlaySound) {
                    soundBegin();
                }
            }

            @Override
            public void onPanelStateStop() {
                Log.e(TAG, "方法名>>onPanelStateStop()>>>>" + "转盘停止转动");
                //根据业务需要在此处进行-> 动画|弹窗
                luckyPanel.excutePanelViewAnim(LotterySquareActivity.this);
                //停止音效
                soundStop();
            }
        });
        fortuneSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaySound) {
                    fortuneSound.setImageDrawable(ContextCompat.getDrawable(LotterySquareActivity.this, R.drawable.ic_volume_off_24dp));
                } else {
                    fortuneSound.setImageDrawable(ContextCompat.getDrawable(LotterySquareActivity.this, R.drawable.ic_volume_up_24dp));
                }
                isPlaySound = !isPlaySound;
            }
        });

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!luckyPanel.isGameRunning()) {
                    luckyPanel.startGame();
                } else {
                    int stayIndex = new Random().nextInt(8);
                    //设定停留的位置,一般为服务器传过来的对应的数据
                    luckyPanel.tryToStop(stayIndex);

                }
            }
        });
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundPool == null) {
            //指定声音池的最大音频流数目为10，声音品质为5
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            //载入音频流，返回在池中的id
            explosionId = this.soundPool.load(this, R.raw.music, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (soundPool != null) {
            soundPool.stop(explosionId);
            soundPool.release();
            soundPool = null;
        }
    }

    /**
     * Description:转盘开始旋转
     */
    public void soundBegin() {
        /**
         * 播放音频，
         * 第二个参数为左声道音量;
         * 第三个参数为右声道音量;
         * 第四个参数为优先级；
         * 第五个参数为循环次数，0不循环，-1循环;
         * 第六个参数为速率，速率    最低0.5最高为2，1代表正常速度
         */
        playSourceId = soundPool.play(explosionId, 1, 1, 0, -1, 1);
    }

    private void soundStop() {
        soundPool.stop(playSourceId);
    }

}
