package com.jiameng.lottery.square;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiameng.mr_jr.lottery_square.R;

/**
 * Created by jeanboy on 2017/4/20.
 */

public class PanelItemView extends FrameLayout implements ItemView {
    //遮罩
    private View overlay;
    private RelativeLayout itemFront;
    private RelativeLayout itemBack;
    private ImageView imgItemBack;

    public PanelItemView(Context context) {
        this(context, null);
    }

    public PanelItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_panel_item, this);
        overlay = findViewById(R.id.overlay);
        itemFront = findViewById(R.id.item_front);
        itemBack = findViewById(R.id.item_back);
        imgItemBack = findViewById(R.id.img_item_back);
    }

    public View getItemFrontOverlay() {
        return overlay;
    }

    public RelativeLayout getItemFront() {
        return itemFront;
    }

    public RelativeLayout getItemBack() {
        return itemBack;
    }

    public ImageView getItemBackImg() {
        return imgItemBack;
    }

    @Override
    public void setFocus(boolean isFocused) {
        if (overlay != null) {
            overlay.setVisibility(isFocused ? INVISIBLE : VISIBLE);
        }
    }

}
