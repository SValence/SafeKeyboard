package com.safe.keyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Administrator on 2018/3/7 0007.
 */

public class SafeKeyboardView extends KeyboardView {

    private Context mContext;

    public SafeKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public SafeKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -5 || key.codes[0] == -35 || key.codes[0] == -2
                        || key.codes[0] == 100860 || key.codes[0] == -1) {
                    drawSpecialKey(canvas, key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawSpecialKey(Canvas canvas, Keyboard.Key key) {
        if (key.codes[0] == -5) {
            drawKeyBackground(R.drawable.keyboard_del_letter, canvas, key);
            drawText(canvas, key);
        } else if (key.codes[0] == -35) {
            drawKeyBackground(R.drawable.keyboard_del_num, canvas, key);
            drawText(canvas, key);
        } else if (key.codes[0] == -2 || key.codes[0] == 100860) {
            drawKeyBackground(R.drawable.keyboard_change, canvas, key);
            drawText(canvas, key);
        } else if (key.codes[0] == -1) {
            if (SafeKeyboard.isCapes()) {
                drawKeyBackground(R.drawable.keyboard_shift_up, canvas, key);
                drawText(canvas, key);
            } else {
                drawKeyBackground(R.drawable.keyboard_shift_low, canvas, key);
                drawText(canvas, key);
            }
        }
    }

    private void drawKeyBackground(int id, Canvas canvas, Keyboard.Key key) {
        Drawable drawable = mContext.getResources().getDrawable(id);
        int[] state = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            drawable.setState(state);
        }
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        drawable.draw(canvas);
    }

    private void drawText(Canvas canvas, Keyboard.Key key) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);

            if (key.label != null) {
                String label = key.label.toString();

                Field field;

                if (label.length() > 1 && key.codes.length < 2) {
                    int labelTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField(getContext().getString(R.string.mLabelTextSize));
                        field.setAccessible(true);
                        labelTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(labelTextSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    int keyTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField(getContext().getString(R.string.mLabelTextSize));
                        field.setAccessible(true);
                        keyTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(keyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(key.label.toString(), 0, key.label.toString()
                        .length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (key.width / 2),
                        (key.y + key.height / 2) + bounds.height() / 2, paint);
            } else if (key.icon != null) {
                key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2,
                        key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                        key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(),
                        key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
                key.icon.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
