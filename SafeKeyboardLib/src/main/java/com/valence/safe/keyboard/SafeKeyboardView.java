package com.valence.safe.keyboard;

import android.annotation.SuppressLint;
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
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * Created by Valence on 2018/3/7 0007.
 */

public class SafeKeyboardView extends KeyboardView {

    private static final String TAG = "SafeKeyboardView";

    private int specialKeyBgResId = R.drawable.keyboard_change_trans_lib;

    private final Context mContext;
    private boolean isCap;
    private boolean isCapLock;
    private boolean enableVibrate;
    private Drawable delDrawable;
    private Drawable lowDrawable;
    private Drawable upDrawable;
    private Drawable upDrawableLock;
    private Keyboard lastKeyboard;
    private int curKeyboardType;
    private KeyBgResEntitySet keyBgResEntitySet;
    private int keyTextSize;
    private int keyLabelSize;
    private int normalKeyBgResId;
    private int normalKeyTextColorId;
    /**
     * 按键的宽高至少是图标宽高的倍数
     */
    private static final int ICON2KEY = 2;

    // 键盘的一些自定义属性
    private boolean rememberLastType;

    public SafeKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        this.mContext = context;

        initAttrs(context, attrs, 0);
    }

    public SafeKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        this.mContext = context;

        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        /*if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SafeKeyboardView, defStyleAttr, 0);
            // randomDigit = array.getBoolean(R.styleable.SafeKeyboardView_random_digit, DIGIT_RANDOM);
            // onlyIdCard = array.getBoolean(R.styleable.SafeKeyboardView_only_id_card, ONLY_ID_CARD);
            // rememberLastType = array.getBoolean(R.styleable.SafeKeyboardView_remember_last_type, REM_LAST_TYPE);
            // enableVibrate = array.getBoolean(R.styleable.SafeKeyboardView_enable_vibrate, DEFAULT_ENABLE_VIBRATE);
            array.recycle();
        }*/
    }

    private void init(Context mContext) {
        this.isCap = false;
        this.isCapLock = false;
        // 默认三种图标
        if (delDrawable == null) {
            delDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_del_lib);
        }
        if (lowDrawable == null) {
            lowDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_capital_default_lib);
        }
        if (upDrawable == null) {
            upDrawable = ContextCompat.getDrawable(mContext, R.drawable.icon_capital_selected_lib);
        }
        if (upDrawableLock == null) {
            upDrawableLock = ContextCompat.getDrawable(mContext, R.drawable.icon_capital_selected_lock_lib);
        }
        this.lastKeyboard = null;
        this.curKeyboardType = 0;
        this.keyTextSize = 0;
        this.keyLabelSize = 0;
        this.normalKeyBgResId = 0;
        this.normalKeyTextColorId = 0;
    }

    public void setRememberLastType(boolean remember) {
        rememberLastType = remember;
    }

    public boolean isRememberLastType() {
        return rememberLastType;
    }

    public boolean isVibrateEnable() {
        return enableVibrate;
    }

    public void enableVibrate() {
        this.enableVibrate = true;
    }

    public void disableVibrate() {
        this.enableVibrate = false;
    }

    /*public void setKeyBgResEntitySet(KeyBgResEntitySet keyBgResEntitySet) {
        this.keyBgResEntitySet = keyBgResEntitySet;
    }*/

    // @Deprecated
    // @Override
    /*
     * 不要用, 使用下面的 setKeyboard(Keyboard keyboard, int type)
    public void setKeyboard(Keyboard keyboard) {
        super.setKeyboard(keyboard);
        this.lastKeyboard = keyboard;
        this.keyboardType = 0;
    }*/

    public void setKeyboard(Keyboard keyboard, int type, KeyBgResEntitySet entitySet) {
        super.setKeyboard(keyboard);
        this.lastKeyboard = keyboard;
        this.curKeyboardType = type;
        this.keyBgResEntitySet = entitySet;
    }

    public Keyboard getLastKeyboard() {
        return lastKeyboard;
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 记录一下:
        // 原先的逻辑有错误, 保留了 super.onDraw(canvas); 这样会导致系统调用两次 onDraw, 即这个方法
        // 一次是渲染 xml 文件中设置的背景、字体等样式
        // 一次是渲染自定义样式
        // 按照正常的理解, 系统渲染未自定义的按键, 自定义按键由我们这里的逻辑处理, 但是实际上系统会先把所有的按键样式渲染完毕,
        //    再渲染自定义（因为我的设备是自定义的样式覆盖在系统样式上面的）
        // 所以这里阻止系统默认渲染, 所有的按键均由我们自己来掌控, 那么 xml 的配置就不会生效了, 所有的样式配置会在
        //    SafeKeyboardConfig 中提供设置接口.
        // 这样也解决了两次 onDraw 的问题... 一直很困惑...

        // super.onDraw(canvas);
        try {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -5 || key.codes[0] == -2 || key.codes[0] == 100860 || key.codes[0] == -1) {
                    // 特殊按键已在 SafeKeyboardConfig 中提供了设置资源 id 的接口/配置, 在外面修改就可以了
                    drawSpecialKey(canvas, key);
                } else {
                    int keyBgResId = normalKeyBgResId;
                    int colorId = normalKeyTextColorId;
                    // 这里只允许修改除了特殊按键之外的其他所有按键的背景和字体颜色
                    if (keyBgResEntitySet != null && keyBgResEntitySet.isKeyBgHasSet()) {
                        // 设置了单个按键背景
                        // 根据当前的键盘类型, 决定绘制相应的背景
                        KeyBgResEntity resEntity = getKeyBgResEntity(key.codes[0]);
                        if (resEntity != null) {
                            // 配置了这个按键, 那么更新一下按键配置
                            keyBgResId = resEntity.getBgResId();
                            colorId = resEntity.getKeyTextColorId();
                        }
                        /*else {
                            // 说明没有配置这个按键
                            // 说明是普通按键, 直接按照默认的修改
                            // 上面配置过了, 不用再更新了
                        }*/
                    }
                    drawKeyBackground(keyBgResId, canvas, key);
                    drawTextAndIcon(canvas, key, null, getResources().getColor(colorId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private KeyBgResEntity getKeyBgResEntity(int code) {
        KeyBgResEntity resEntity = null;
        switch (curKeyboardType) {
            case 1:
            case 11:
                // 字母键盘/随机
                resEntity = keyBgResEntitySet.getLetterKeyboardBgResArray().get(code);
                break;
            case 2:
                // 符号键盘
                resEntity = keyBgResEntitySet.getSymbolKeyboardBgResArray().get(code);
                break;
            case 3:
            case 33:
                // 数字键盘/随机
                resEntity = keyBgResEntitySet.getNumSymbolKeyboardBgResArray().get(code);
                break;
            case 4:
            case 44:
                // 纯数字键盘/随机,
                resEntity = keyBgResEntitySet.getNumOnlyKeyboardBgResArray().get(code);
                break;
            case 55:
                // 中国身份证键盘/随机
                resEntity = keyBgResEntitySet.getIdCardKeyboardBgResArray().get(code);
                break;
            default:
                Log.e(TAG, "Keyboard Type Error: " + curKeyboardType);
        }
        return resEntity;
    }

    private void drawSpecialKey(Canvas canvas, Keyboard.Key key) {
        int color = Color.WHITE;
        if (key.codes[0] == -5) {
            drawKeyBackground(specialKeyBgResId, canvas, key);
            drawTextAndIcon(canvas, key, delDrawable, color);
        } else if (key.codes[0] == -2 || key.codes[0] == 100860 || key.codes[0] == 100861) {
            drawKeyBackground(specialKeyBgResId, canvas, key);
            drawTextAndIcon(canvas, key, null, color);
        } else if (key.codes[0] == -1) {
            if (isCapLock) {
                drawKeyBackground(specialKeyBgResId, canvas, key);
                drawTextAndIcon(canvas, key, upDrawableLock, color);
            } else if (isCap) {
                drawKeyBackground(specialKeyBgResId, canvas, key);
                drawTextAndIcon(canvas, key, upDrawable, color);
            } else {
                drawKeyBackground(specialKeyBgResId, canvas, key);
                drawTextAndIcon(canvas, key, lowDrawable, color);
            }
        }
    }

    private void drawKeyBackground(int id, Canvas canvas, Keyboard.Key key) {
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable = mContext.getResources().getDrawable(id);
        int[] state = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            drawable.setState(state);
        }
        drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        drawable.draw(canvas);
    }

    private void drawKeyBgDrawable(Drawable keyBgDrawable, Canvas canvas, Keyboard.Key key) {
        int[] state = key.getCurrentDrawableState();
        if (key.codes[0] != 0) {
            keyBgDrawable.setState(state);
        }
        keyBgDrawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
        keyBgDrawable.draw(canvas);
    }

    private void drawTextAndIcon(Canvas canvas, Keyboard.Key key, @Nullable Drawable drawable, int color) {
        try {
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(color);

            if (key.label != null) {
                String label = key.label.toString();

                // Field field;
                // if (label.length() > 1 && key.codes.length < 2) {
                // 先删除一下
                if (label.length() > 1) {
                    /*int labelTextSize = 0;
                    try {
                        // KeyboardView 中 mLabelTextSize 是自定义(特殊)按键的大小
                        // Size of the text for custom keys with some text and no icon.
                        // 我的理解就是所有的特殊按键, 删除、切换、带图标的按键等, 即 label 长度一般大于 1 的字符
                        field = KeyboardView.class.getDeclaredField(getContext().getString(R.string.mLabelTextSize));
                        field.setAccessible(true);
                        Object obj = field.get(this);
                        labelTextSize = obj == null ? 0 : (int) obj;
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                        Log.e(TAG, e.getLocalizedMessage());
                    }*/
                    paint.setTextSize(this.keyLabelSize);
                    paint.setTypeface(Typeface.DEFAULT_BOLD);
                    // Log.w(TAG, "label.length() > 1 && key.codes.length < 2; " + key.label + ", size " + this.keyLabelSize);
                } else {
                    // KeyboardView 中 keyTextSize 是字符按键的大小
                    // Size of the text for character keys.
                    // 我的理解就是所有字符的, 字母、数字、符号等, 即 label 长度为 1 的字符
                    // 下面注释掉是因为不知道怎么回事, 这里获取不到 mKeyTextSize 这个属性, 所以在 SafeKeyboardConfig 中新增了数值设置
                        /*field = KeyboardView.class.getDeclaredField(getContext().getString(R.string.mKeyTextSize));
                        field.setAccessible(true);
                        Object obj = field.get(this);
                        keyTextSize = obj == null ? 0 : (int) obj;*/
                    // Log.w(TAG, "else; " + key.label + ", size " + keyTextSize);

                    paint.setTextSize(keyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), bounds);
                canvas.drawText(key.label.toString(), key.x + (1.0f * key.width / 2),
                        (key.y + 1.0f * key.height / 2) + 1.0f * bounds.height() / 2, paint);
            }
            if (drawable == null) return;
            // 约定: 最终图标的宽度和高度都需要在按键的宽度和高度的二分之一以内
            // 如果: 图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 那就不需要变换, 否则就需要等比例缩小
            int iconSizeWidth, iconSizeHeight;
            key.icon = drawable;
            int iconH = px2dip(mContext, key.icon.getIntrinsicHeight());
            int iconW = px2dip(mContext, key.icon.getIntrinsicWidth());
            if (key.width >= (ICON2KEY * iconW) && key.height >= (ICON2KEY * iconH)) {
                //图标的实际宽度和高度都在按键的宽度和高度的二分之一以内, 不需要缩放, 因为图片已经够小或者按键够大
                setIconSize(canvas, key, iconW, iconH);
            } else {
                //图标的实际宽度和高度至少有一个不在按键的宽度或高度的二分之一以内, 需要等比例缩放, 因为此时图标的宽或者高已经超过按键的二分之一
                //需要把超过的那个值设置为按键的二分之一, 另一个等比例缩放
                //不管图标大小是多少, 都以宽度width为标准, 把图标的宽度缩放到和按键一样大, 并同比例缩放高度
                double multi = 1.0 * iconW / key.width;
                int tempIconH = (int) (iconH / multi);
                if (tempIconH <= key.height) {
                    //宽度相等时, 图标的高度小于等于按键的高度, 按照现在的宽度和高度设置图标的最终宽度和高度
                    iconSizeHeight = tempIconH / ICON2KEY;
                    iconSizeWidth = key.width / ICON2KEY;
                } else {
                    //宽度相等时, 图标的高度大于按键的高度, 这时按键放不下图标, 需要重新按照高度缩放
                    double mul = 1.0 * iconH / key.height;
                    int tempIconW = (int) (iconW / mul);
                    iconSizeHeight = key.height / ICON2KEY;
                    iconSizeWidth = tempIconW / ICON2KEY;
                }
                setIconSize(canvas, key, iconSizeWidth, iconSizeHeight);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIconSize(Canvas canvas, Keyboard.Key key, int iconSizeWidth, int iconSizeHeight) {
        int left = key.x + (key.width - iconSizeWidth) / 2;
        int top = key.y + (key.height - iconSizeHeight) / 2;
        int right = key.x + (key.width + iconSizeWidth) / 2;
        int bottom = key.y + (key.height + iconSizeHeight) / 2;
        key.icon.setBounds(left, top, right, bottom);
        key.icon.draw(canvas);
        key.icon = null;
    }

    public void setCap(boolean cap) {
        isCap = cap;
    }

    public void setCapLock(boolean isCapLock) {
        this.isCapLock = isCapLock;
    }

    public void setDelDrawable(Drawable delDrawable) {
        this.delDrawable = delDrawable;
    }

    public void setLowDrawable(Drawable lowDrawable) {
        this.lowDrawable = lowDrawable;
    }

    public void setUpDrawable(Drawable upDrawable) {
        this.upDrawable = upDrawable;
    }

    public void setUpDrawableLock(Drawable upDrawableLock) {
        this.upDrawableLock = upDrawableLock;
    }

    public void setSpecialKeyBgResId(int specialKeyBgResId) {
        this.specialKeyBgResId = specialKeyBgResId;
    }

    public void setKeyTextSize(int keyTextSize) {
        this.keyTextSize = dip2px(mContext, keyTextSize);
    }

    public void setKeyLabelSize(int keyLabelSize) {
        this.keyLabelSize = dip2px(mContext, keyLabelSize);
    }

    public void setNormalKeyTextColorId(int normalKeyTextColorId) {
        this.normalKeyTextColorId = normalKeyTextColorId;
    }

    public void setNormalKeyBgResId(int normalKeyBgResId) {
        this.normalKeyBgResId = normalKeyBgResId;
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
