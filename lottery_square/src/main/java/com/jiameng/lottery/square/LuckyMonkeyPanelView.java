package com.jiameng.lottery.square;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiameng.mr_jr.lottery_square.R;

import java.lang.ref.WeakReference;


/*
   eg:
     if (!luckyPanel.isGameRunning()) {
        luckyPanel.startGame();
     } else {
        int stayIndex = new Random().nextInt(8);
        Log.e("LuckyMonkeyPanelView", "====stay===" + stayIndex);
        luckyPanel.tryToStop(stayIndex);
    }
*/

public class LuckyMonkeyPanelView extends FrameLayout {

    public static final int START_MARQUEE = 1001;
    public static final int START_GAME = 1002;

    private ImageView marqueeStateOne;
    private ImageView marqueeStateTwo;

    private PanelItemView itemView1, itemView2, itemView3, itemView4, itemView6, itemView7, itemView8, itemView9;

    private ItemView[] itemViewArr = new ItemView[8];
    private int currentIndex = 0;
    private int currentTotal = 0;
    private int stayIndex = 0;

    private boolean isMarqueeRunning = false;
    private boolean isGameRunning = false;
    private boolean isTryToStop = false;

    private static final int DEFAULT_SPEED = 150;
    private static final int MIN_SPEED = 50;
    private int currentSpeed = DEFAULT_SPEED;

    public LuckyMonkeyPanelView(@NonNull Context context) {
        this(context, null);
    }

    public LuckyMonkeyPanelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckyMonkeyPanelView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_lucky_mokey_panel, this);
        setupView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startMarquee();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopMarquee();
        super.onDetachedFromWindow();
    }

    private void setupView() {
        marqueeStateOne = findViewById(R.id.marquee_state_one);
        marqueeStateTwo = findViewById(R.id.marquee_state_two);
        itemView1 = findViewById(R.id.item1);
        itemView2 = findViewById(R.id.item2);
        itemView3 = findViewById(R.id.item3);
        itemView4 = findViewById(R.id.item4);
        itemView6 = findViewById(R.id.item6);
        itemView7 = findViewById(R.id.item7);
        itemView8 = findViewById(R.id.item8);
        itemView9 = findViewById(R.id.item9);

        itemViewArr[0] = itemView4;
        itemViewArr[1] = itemView1;
        itemViewArr[2] = itemView2;
        itemViewArr[3] = itemView3;
        itemViewArr[4] = itemView6;
        itemViewArr[5] = itemView9;
        itemViewArr[6] = itemView8;
        itemViewArr[7] = itemView7;
    }

    private void stopMarquee() {
        isMarqueeRunning = false;
        isGameRunning = false;
        isTryToStop = false;
    }

    private MarqueeRunningHandler mHandler = new MarqueeRunningHandler(this);

    private static class MarqueeRunningHandler extends Handler {
        private WeakReference<LuckyMonkeyPanelView> panelView;

        public MarqueeRunningHandler(LuckyMonkeyPanelView luckyMonkeyPanelView) {
            panelView = new WeakReference<>(luckyMonkeyPanelView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_MARQUEE: {
                    LuckyMonkeyPanelView panelView = this.panelView.get();
                    if (panelView != null) {
                        if (panelView.marqueeStateOne != null && panelView.marqueeStateTwo != null) {
                            if (VISIBLE == panelView.marqueeStateOne.getVisibility()) {
                                panelView.marqueeStateOne.setVisibility(GONE);
                                panelView.marqueeStateTwo.setVisibility(VISIBLE);
                            } else {
                                panelView.marqueeStateOne.setVisibility(VISIBLE);
                                panelView.marqueeStateTwo.setVisibility(GONE);
                            }
                        }
                        if (panelView.isMarqueeRunning) {
                            panelView.mHandler.sendEmptyMessageDelayed(START_MARQUEE, 250);
                        }
                    }
                }
                break;
                case START_GAME: {
                    LuckyMonkeyPanelView panelView = this.panelView.get();
                    if (panelView != null) {
                        int preIndex = panelView.currentIndex;
                        panelView.currentIndex++;
                        if (panelView.currentIndex >= panelView.itemViewArr.length) {
                            panelView.currentIndex = 0;
                        }
                        panelView.itemViewArr[preIndex].setFocus(false);
                        panelView.itemViewArr[panelView.currentIndex].setFocus(true);

                        if (panelView.isTryToStop && panelView.currentSpeed == DEFAULT_SPEED
                                && panelView.stayIndex == panelView.currentIndex) {
                            panelView.isGameRunning = false;
                            if (panelView.panelStateListener != null) {
                                panelView.panelStateListener.onPanelStateStop();
                            }
                        }
                        if (panelView.isGameRunning) {
                            panelView.mHandler.sendEmptyMessageDelayed(START_GAME, panelView.getInterruptTime());
                        }
                    }
                }
                break;
                default:
                    break;
            }
        }
    }

    private void startMarquee() {
        isMarqueeRunning = true;
        mHandler.sendEmptyMessageDelayed(START_MARQUEE, 250);
    }

    private long getInterruptTime() {
        currentTotal++;
        if (isTryToStop) {
            currentSpeed += 10;
            if (currentSpeed > DEFAULT_SPEED) {
                currentSpeed = DEFAULT_SPEED;
            }
        } else {
            if (currentTotal / itemViewArr.length > 0) {
                currentSpeed -= 10;
            }
            if (currentSpeed < MIN_SPEED) {
                currentSpeed = MIN_SPEED;
            }
        }
        return currentSpeed;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public void startGame() {
        if (panelStateListener != null) {
            panelStateListener.onPanelStateStart();
        }
        isGameRunning = true;
        isTryToStop = false;
        currentSpeed = DEFAULT_SPEED;
        mHandler.sendEmptyMessageDelayed(START_GAME, getInterruptTime());
    }

    /**
     * 获取当前停止的PanelItemView并进行动画操作
     *
     * @param context
     */
    public void excutePanelViewAnim(Context context) {
        setAnimators(context);
        initBackColor(context);//测试翻牌后的背景颜色
        PanelItemView panelItemView = (PanelItemView) itemViewArr[stayIndex];
        RelativeLayout itemFront = panelItemView.getItemFront();
        RelativeLayout itemBack = panelItemView.getItemBack();
        setCameraDistance(context, itemFront, itemBack);
        setFlipCard(itemFront, itemBack);
    }

    public void initBackColor(Context context) {
        for (ItemView itemView : itemViewArr) {
            PanelItemView panelItemView = (PanelItemView) itemView;
            panelItemView.getItemBackImg()
                    .setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_lucky_monkey_panel));
        }
    }

    private boolean mIsShowBack;
    private AnimatorSet mLeftInSet;
    private AnimatorSet mRightOutSet;


    // 设置动画
    public void setAnimators(Context context) {
        mLeftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.anim_in);
        mRightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.anim_out);

        // 设置点击事件
        mRightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mLeftInSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
    }

    // 改变视角距离, 贴近屏幕
    public static void setCameraDistance(Context context, RelativeLayout mCardFront, RelativeLayout mCardBack) {
        int distance = 16000;
        float scale = context.getResources().getDisplayMetrics().density * distance;
        mCardFront.setCameraDistance(scale);
        mCardBack.setCameraDistance(scale);
    }


    // 翻转卡片
    public void setFlipCard(View mCardFront, View mCardBack) {
        // 正面朝上
        if (!mIsShowBack) {
            mRightOutSet.setTarget(mCardFront);
            mLeftInSet.setTarget(mCardBack);
            mRightOutSet.start();
            mLeftInSet.start();
        } else { // 背面朝上
            mRightOutSet.setTarget(mCardBack);
            mLeftInSet.setTarget(mCardFront);
            mRightOutSet.start();
            mLeftInSet.start();
        }
        mIsShowBack = !mIsShowBack;
    }

    public void tryToStop(int position) {
        stayIndex = position;
        Log.e("LuckyMonkeyPanelView", "====stayIndex===" + stayIndex);
        isTryToStop = true;
    }

    private PanelStateListener panelStateListener;

    public void setPanelStateListener(PanelStateListener panelStateListener) {
        this.panelStateListener = panelStateListener;
    }
}
