package com.safe.keyboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2018/3/7 0007.
 */

public class SafeKeyboard {

    private static final String TAG = "SafeKeyboard";

    private Context mContext;               //上下文

    private LinearLayout layout;
    private View keyContainer;              //自定义键盘的容器View
    private SafeKeyboardView keyboardView;  //键盘的View
    private Keyboard keyboardNumber;        //数字键盘
    private Keyboard keyboardLetter;        //字母键盘
    private Keyboard keyboardSymbol;        //符号键盘
    private static boolean isCapes = false;
    private boolean isShowStart = false;
    private boolean isHideStart = false;
    private int keyboardType = 1;
    private static final long HIDE_TIME = 300;
    private static final long SHOW_DELAY = 200;
    private static final long SHOW_TIME = 300;
    private static final long DELAY_TIME = 100;
    private Handler showHandler = new Handler(Looper.getMainLooper());
    private Handler hEndHandler = new Handler(Looper.getMainLooper());
    private Handler sEndHandler = new Handler(Looper.getMainLooper());
    private Drawable delDrawable;
    private Drawable lowDrawable;
    private Drawable upDrawable;
    private int keyboardContainerResId;
    private int keyboardResId;

    private TranslateAnimation showAnimation;
    private TranslateAnimation hideAnimation;
    private long lastTouchTime;
    private EditText mEditText;

    SafeKeyboard(Context mContext, LinearLayout layout, EditText mEditText, int id, int keyId) {
        this.mContext = mContext;
        this.layout = layout;
        this.mEditText = mEditText;
        this.keyboardContainerResId = id;
        this.keyboardResId = keyId;

        initKeyboard();
        initAnimation();
        addListeners();
    }

    SafeKeyboard(Context mContext, LinearLayout layout, EditText mEditText, int id, int keyId,
                 Drawable del, Drawable low, Drawable up) {
        this.mContext = mContext;
        this.layout = layout;
        this.mEditText = mEditText;
        this.keyboardContainerResId = id;
        this.keyboardResId = keyId;
        this.delDrawable = del;
        this.lowDrawable = low;
        this.upDrawable = up;

        initKeyboard();
        initAnimation();
        addListeners();
    }

    private void initAnimation() {
        showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        showAnimation.setDuration(SHOW_TIME);
        hideAnimation.setDuration(HIDE_TIME);

        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isShowStart = true;
                // 在这里设置可见, 会出现第一次显示键盘时直接闪现出来, 没有动画效果, 后面正常
                // keyContainer.setVisibility(View.VISIBLE);
                // 动画持续时间 SHOW_TIME 结束后, 不管什么操作, 都需要执行, 把 isShowStart 值设为 false; 否则
                // 如果 onAnimationEnd 因为某些原因没有执行, 会影响下一次使用
                sEndHandler.removeCallbacks(showEnd);
                sEndHandler.postDelayed(showEnd, SHOW_TIME);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShowStart = false;
                keyContainer.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isHideStart = true;
                // 动画持续时间 HIDE_TIME 结束后, 不管什么操作, 都需要执行, 把 isHideStart 值设为 false; 否则
                // 如果 onAnimationEnd 因为某些原因没有执行, 会影响下一次使用
                hEndHandler.removeCallbacks(hideEnd);
                hEndHandler.postDelayed(hideEnd, HIDE_TIME);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isHideStart = false;
                if (keyContainer.getVisibility() != View.GONE) {
                    keyContainer.setVisibility(View.GONE);
                }
                keyContainer.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initKeyboard() {
        keyContainer = LayoutInflater.from(mContext).inflate(keyboardContainerResId, layout, true);
        keyContainer.setVisibility(View.GONE);
        keyboardNumber = new Keyboard(mContext, R.xml.keyboard_num);            //实例化数字键盘
        keyboardLetter = new Keyboard(mContext, R.xml.keyboard_letter);         //实例化字母键盘
        keyboardSymbol = new Keyboard(mContext, R.xml.keyboard_symbol);         //实例化符号键盘
        // 由于符号键盘与字母键盘共用一个KeyBoardView, 所以不需要再为符号键盘单独实例化一个KeyBoardView
        keyboardView = keyContainer.findViewById(keyboardResId);
        keyboardView.setDelDrawable(delDrawable);
        keyboardView.setLowDrawable(lowDrawable);
        keyboardView.setUpDrawable(upDrawable);
        keyboardView.setKeyboard(keyboardLetter);                         //给键盘View设置键盘
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);

        FrameLayout done = keyContainer.findViewById(R.id.keyboardDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isKeyboardShown()) {
                    hideKeyboard();
                }
            }
        });

        keyboardView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });
    }

    // 设置键盘点击监听
    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void onPress(int primaryCode) {
            if (keyboardType == 3) {
                keyboardView.setPreviewEnabled(false);
            } else {
                keyboardView.setPreviewEnabled(true);
                if (primaryCode == -1 || primaryCode == -5 || primaryCode == 32 || primaryCode == -2
                        || primaryCode == 100860 || primaryCode == -35) {
                    keyboardView.setPreviewEnabled(false);
                } else {
                    keyboardView.setPreviewEnabled(true);
                }
            }
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            try {
                Editable editable = mEditText.getText();
                int start = mEditText.getSelectionStart();
                int end = mEditText.getSelectionEnd();
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                    // 隐藏键盘
                    hideKeyboard();
                } else if (primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == -35) {

                    // 回退键,删除字符
                    if (editable != null && editable.length() > 0) {
                        if (start == end) { //光标开始和结束位置相同, 即没有选中内容
                            editable.delete(start - 1, start);
                        } else { //光标开始和结束位置不同, 即选中EditText中的内容
                            editable.delete(start, end);
                        }
                    }
                } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                    // 大小写切换
                    changeKeyboardLetterCase();
                    // 重新setKeyboard, 进而系统重新加载, 键盘内容才会变化(切换大小写)
                    keyboardType = 1;
                    switchKeyboard();
                } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                    // 数字与字母键盘互换
                    if (keyboardType == 3) { //当前为数字键盘
                        keyboardType = 1;
                    } else {        //当前不是数字键盘
                        keyboardType = 3;
                    }
                    switchKeyboard();
                } else if (primaryCode == 100860) {
                    // 字母与符号切换
                    if (keyboardType == 2) { //当前是符号键盘
                        keyboardType = 1;
                    } else {        //当前不是符号键盘, 那么切换到符号键盘
                        keyboardType = 2;
                    }
                    switchKeyboard();
                } else {
                    // 输入键盘值
                    // editable.insert(start, Character.toString((char) primaryCode));
                    editable.replace(start, end, Character.toString((char) primaryCode));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void swipeUp() {
        }
    };


    private void switchKeyboard() {
        switch (keyboardType) {
            case 1:
                keyboardView.setKeyboard(keyboardLetter);
                break;
            case 2:
                keyboardView.setKeyboard(keyboardSymbol);
                break;
            case 3:
                keyboardView.setKeyboard(keyboardNumber);
                break;
            default:
                Log.e(TAG, "ERROR keyboard type");
                break;
        }
    }

    private void changeKeyboardLetterCase() {
        List<Keyboard.Key> keyList = keyboardLetter.getKeys();
        if (isCapes) {
            for (Keyboard.Key key : keyList) {
                if (key.label != null && isUpCaseLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] += 32;
                }
            }
        } else {
            for (Keyboard.Key key : keyList) {
                if (key.label != null && isLowCaseLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] -= 32;
                }
            }
        }
        isCapes = !isCapes;
        keyboardView.setCap(isCapes);
    }

    public void hideKeyboard() {
        keyContainer.clearAnimation();
        keyContainer.startAnimation(hideAnimation);
    }

    /**
     * 只起到延时开始显示的作用
     */
    private final Runnable showRun = new Runnable() {
        @Override
        public void run() {
            showKeyboard();
        }
    };

    private final Runnable hideEnd = new Runnable() {
        @Override
        public void run() {
            isHideStart = false;
            if (keyContainer.getVisibility() != View.GONE) {
                keyContainer.setVisibility(View.GONE);
            }
        }
    };

    private final Runnable showEnd = new Runnable() {
        @Override
        public void run() {
            isShowStart = false;
            // 在迅速点击不同输入框时, 造成自定义软键盘和系统软件盘不停的切换, 偶尔会出现停在使用系统键盘的输入框时, 没有隐藏
            // 自定义软键盘的情况, 为了杜绝这个现象, 加上下面这段代码
            if (!mEditText.isFocused()) {
                hideKeyboard();
            }
        }
    };

    private void showKeyboard() {
        keyboardView.setKeyboard(keyboardLetter);
        keyContainer.setVisibility(View.VISIBLE);
        keyContainer.clearAnimation();
        keyContainer.startAnimation(showAnimation);
    }

    private boolean isLowCaseLetter(String str) {
        String letters = "abcdefghijklmnopqrstuvwxyz";
        return letters.contains(str);
    }

    private boolean isUpCaseLetter(String str) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return letters.contains(str);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addListeners() {
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideSystemKeyBoard((EditText) v);
                    if (!isKeyboardShown() && !isShowStart) {
                        showHandler.removeCallbacks(showRun);
                        showHandler.postDelayed(showRun, SHOW_DELAY);
                    }
                }
                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                boolean result = isValidTouch();
                if (v instanceof EditText) {
                    if (!hasFocus) {
                        if (result) {
                            if (isKeyboardShown() && !isHideStart) {
                                hideKeyboard();
                            }
                        } else {
                            hideKeyboard();
                        }
                    } else {
                        hideSystemKeyBoard((EditText) v);
                        if (result) {
                            if (!isKeyboardShown() && !isShowStart) {
                                showHandler.removeCallbacks(showRun);
                                showHandler.postDelayed(showRun, SHOW_DELAY);
                            }
                        } else {
                            showHandler.removeCallbacks(showRun);
                            showHandler.postDelayed(showRun, SHOW_DELAY + DELAY_TIME);
                        }
                    }
                }
            }
        });
    }

    public boolean isShow() {
        return isKeyboardShown();
    }

    //隐藏系统键盘关键代码
    private void hideSystemKeyBoard(EditText edit) {
        this.mEditText = edit;
        InputMethodManager imm = (InputMethodManager) this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        }

        int currentVersion = Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            edit.setInputType(0);
        } else {
            try {
                Method setShowSoftInputOnFocus = EditText.class.getMethod(methodName, Boolean.TYPE);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(edit, Boolean.FALSE);
            } catch (NoSuchMethodException e) {
                edit.setInputType(0);
                e.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isKeyboardShown() {
        return keyContainer.getVisibility() == View.VISIBLE;
    }

    private boolean isValidTouch() {
        long thisTouchTime = SystemClock.elapsedRealtime();
        if (thisTouchTime - lastTouchTime > 500) {
            lastTouchTime = thisTouchTime;
            return true;
        }
        lastTouchTime = thisTouchTime;
        return false;
    }

    public void setDelDrawable(Drawable delDrawable) {
        this.delDrawable = delDrawable;
        keyboardView.setDelDrawable(delDrawable);
    }

    public void setLowDrawable(Drawable lowDrawable) {
        this.lowDrawable = lowDrawable;
        keyboardView.setLowDrawable(lowDrawable);
    }

    public void setUpDrawable(Drawable upDrawable) {
        this.upDrawable = upDrawable;
        keyboardView.setUpDrawable(upDrawable);
    }
}
