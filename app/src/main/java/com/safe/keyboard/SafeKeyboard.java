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
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2018/3/7 0007.
 */

public class SafeKeyboard {

    private static final String TAG = "SafeKeyboard";

    private Context mContext;               //上下文

    private LinearLayout keyboardParentView;
    private View keyContainer;              //自定义键盘的容器View
    private SafeKeyboardView keyboardView;  //键盘的View
    private Keyboard keyboardNumber;        //数字键盘
    private Keyboard keyboardNumberOnly;    //纯数字键盘
    private Keyboard keyboardLetterOnly;    //字母键盘
    private Keyboard keyboardLetterNum;     //字母数字键盘
    private Keyboard keyboardSymbol;        //符号键盘
    private Keyboard keyboardIdCard;        //中国身份证号码键盘
    private Keyboard keyboardLetter;        //字母键盘总成
    private static boolean isCapes = false;
    private boolean isCapLock = false;
    private boolean isShowStart = false;
    private boolean isHideStart = false;
    private boolean forbidPreview = false;  // 关闭按键预览功能
    private boolean letterWithNum = false;  // 字母键盘是否带有数字
    private int keyboardType = 1;
    private static final long HIDE_TIME = 300;
    private static final long SHOW_DELAY = 200;
    private static final long HIDE_DELAY = 50;
    private static final long SHOW_TIME = 300;
    private static final long DELAY_TIME = 100;
    private Handler safeHandler = new Handler(Looper.getMainLooper());
    private Drawable delDrawable;
    private Drawable lowDrawable;
    private Drawable upDrawable;
    private Drawable upDrawableLock;
    private int keyboardLayoutResId;
    private int mSafeKeyboardViewId;

    private TranslateAnimation showAnimation;
    private TranslateAnimation hideAnimation;
    private TranslateAnimation showScrollAnimation;
    private TranslateAnimation hideScrollAnimation;
    private long lastTouchTime;
    private EditText mCurrentEditText;
    private SparseArray<Keyboard.Key> randomDigitKeys;
    private SparseArray<Keyboard.Key> randomIdCardDigitKeys;
    private SparseIntArray mEditLastKeyboardTypeArray;

    private int mCurrentInputType;
    private HashMap<Integer, EditText> mEditMap;
    private HashMap<Integer, EditText> mIdCardEditMap;
    private View.OnTouchListener onEditTextTouchListener;
    private View rootView;
    private View mScrollLayout;
    private ViewTreeObserver.OnGlobalFocusChangeListener onGlobalFocusChangeListener;
    private ViewTreeObserver treeObserver;
    private ViewPoint downPoint;
    private ViewPoint upPoint;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mNeedScroll;
    private float toBackSize;

    // 已支持多 EditText 共用一个 SafeKeyboard

    /**
     * SafeKeyboard 构造方法, 传入必要的参数
     *
     * @param mContext            上下文Context
     * @param keyboardParentView  界面上显示 SafeKeyboard 的 View
     * @param keyboardLayoutResId SafeKeyboard 样式资源 id
     * @param mSafeKeyboardViewId 自定义的 SafeKeyboardView id
     * @param rootView            含有使用了 SafeKeyboard 的 EditText 的界面根布局 View
     *                            ( 多个 EditText 共用 SafeKeyboard 但其直接父布局不是同一个 View 时, 传入公共父布局)
     * @param scrollLayout        目标 EditText 父布局 View
     */
    SafeKeyboard(Context mContext, LinearLayout keyboardParentView, int keyboardLayoutResId, int mSafeKeyboardViewId,
                 @NonNull View rootView, @NonNull View scrollLayout) {
        this(mContext, keyboardParentView, keyboardLayoutResId, mSafeKeyboardViewId, rootView, scrollLayout, false);
    }

    /**
     * SafeKeyboard 构造方法, 传入必要的参数
     *
     * @param mContext            上下文Context
     * @param keyboardParentView  界面上显示 SafeKeyboard 的 View
     * @param keyboardLayoutResId SafeKeyboard 样式资源 id
     * @param mSafeKeyboardViewId 自定义的 SafeKeyboardView id
     * @param rootView            含有使用了 SafeKeyboard 的 EditText 的界面根布局 View
     * @param scrollLayout        目标 EditText 父布局 View
     *                            ( 多个 EditText 共用 SafeKeyboard 但其直接父布局不是同一个 View 时, 传入公共父布局)
     * @param letterWithNum       字母键盘是否带有数字
     */
    SafeKeyboard(Context mContext, LinearLayout keyboardParentView, int keyboardLayoutResId, int mSafeKeyboardViewId,
                 @NonNull View rootView, @NonNull View scrollLayout, boolean letterWithNum) {
        this.mContext = mContext;
        this.keyboardParentView = keyboardParentView;
        this.keyboardLayoutResId = keyboardLayoutResId;
        this.mSafeKeyboardViewId = mSafeKeyboardViewId;
        this.rootView = rootView;
        this.mScrollLayout = scrollLayout;
        this.letterWithNum = letterWithNum;

        initData();
        initKeyboard();
        initAnimation();
    }

    SafeKeyboard(Context mContext, LinearLayout keyboardParentView, int keyboardLayoutResId, int mSafeKeyboardViewId,
                 Drawable del, Drawable low, Drawable up, Drawable upLock, @NonNull View rootView, @NonNull ViewGroup scrollLayout) {
        this.mContext = mContext;
        this.keyboardParentView = keyboardParentView;
        this.keyboardLayoutResId = keyboardLayoutResId;
        this.mSafeKeyboardViewId = mSafeKeyboardViewId;
        this.delDrawable = del;
        this.lowDrawable = low;
        this.upDrawable = up;
        this.upDrawableLock = upLock;
        this.rootView = rootView;
        this.mScrollLayout = scrollLayout;

        initData();
        initKeyboard();
        initAnimation();
    }

    public void enableRememberLastKeyboardType(boolean enable) {
        keyboardView.setRememberLastType(enable);
    }

    private void initData() {
        isCapLock = false;
        isCapes = false;
        mNeedScroll = false;
        toBackSize = 0;
        downPoint = new ViewPoint();
        upPoint = new ViewPoint();
        mEditMap = new HashMap<>();
        mIdCardEditMap = new HashMap<>();
        mEditLastKeyboardTypeArray = new SparseIntArray();

        // 获取 WindowManager 实例, 得到屏幕的操作权
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            // 给 metrics 赋值
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            // 设备屏幕的宽度,高度变量
            mScreenWidth = metrics.widthPixels;
            mScreenHeight = metrics.heightPixels;
        }
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
                safeHandler.removeCallbacks(showEnd);
                safeHandler.postDelayed(showEnd, SHOW_TIME);
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
                safeHandler.removeCallbacks(hideEnd);
                safeHandler.postDelayed(hideEnd, HIDE_TIME);
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
        keyContainer = LayoutInflater.from(mContext).inflate(keyboardLayoutResId, keyboardParentView, true);
        keyContainer.setVisibility(View.GONE);
        keyboardNumber = new Keyboard(mContext, R.xml.keyboard_num_symbol);     //实例化数字键盘
        // 注: 这里有三个数字键盘,  keyboard_num_symbol:带部分符号;   keyboard_num:可切换的数字键盘;    keyboard_num_only:纯数字键盘, 不可切换
        keyboardNumberOnly = new Keyboard(mContext, R.xml.keyboard_num_only);

        keyboardLetterOnly = new Keyboard(mContext, R.xml.keyboard_letter);         //实例化字母键盘
        keyboardLetterNum = new Keyboard(mContext, R.xml.keyboard_letter_num);         //实例化字母键盘
        keyboardSymbol = new Keyboard(mContext, R.xml.keyboard_symbol);         //实例化符号键盘
        keyboardIdCard = new Keyboard(mContext, R.xml.keyboard_id_card_zn);     //实例化 IdCard(中国身份证) 键盘
        // 由于符号键盘与字母键盘共用一个KeyBoardView, 所以不需要再为符号键盘单独实例化一个KeyBoardView

        keyboardLetter = letterWithNum ? keyboardLetterNum : keyboardLetterOnly;

        lastTouchTime = 0L;

        initRandomDigitKeys();
        initIdCardRandomDigitKeys();

        keyboardView = keyContainer.findViewById(mSafeKeyboardViewId);
        if (delDrawable == null)
            delDrawable = mContext.getDrawable(R.drawable.icon_del);
        if (lowDrawable == null)
            lowDrawable = mContext.getDrawable(R.drawable.icon_capital_default);
        if (upDrawable == null)
            upDrawable = mContext.getDrawable(R.drawable.icon_capital_selected);
        if (upDrawableLock == null)
            upDrawableLock = mContext.getDrawable(R.drawable.icon_capital_selected_lock);
        keyboardView.setDelDrawable(delDrawable);
        keyboardView.setLowDrawable(lowDrawable);
        keyboardView.setUpDrawable(upDrawable);
        keyboardView.setUpDrawableLock(upDrawableLock);
        // setKeyboard(keyboardLetter);                         //给键盘View设置键盘
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(listener);

        FrameLayout done = keyContainer.findViewById(R.id.keyboardDone);
        done.setOnClickListener(v -> {
            if (isKeyboardShown()) {
                safeHandler.removeCallbacks(hideRun);
                safeHandler.removeCallbacks(showRun);
                safeHandler.postDelayed(hideRun, HIDE_DELAY);
//                hideKeyboard();
            }
        });

        keyboardView.setOnTouchListener((v, event) -> event.getAction() == MotionEvent.ACTION_MOVE);

        if (rootView != null) {
            treeObserver = rootView.getViewTreeObserver();
            onGlobalFocusChangeListener = (oldFocus, newFocus) -> {
                if (oldFocus instanceof EditText) {
                    // 上一个获得焦点的为 EditText
                    EditText oldEdit = (EditText) oldFocus;
                    if (mEditMap.get(oldEdit.getId()) != null) {
                        // 前 EditText 使用了 SafeKeyboard
                        // 新获取焦点的是 EditText
                        if (newFocus instanceof EditText) {
                            EditText newEdit = (EditText) newFocus;
                            if (mEditMap.get(newEdit.getId()) != null) {
                                // 该 EditText 也使用了 SafeKeyboard
                                // Log.i(TAG, "Safe --> Safe, 开始检查是否需要手动 show");
                                keyboardPreShow(newEdit);
                            } else {
                                // 该 EditText 没有使用 SafeKeyboard, 则隐藏 SafeKeyboard
                                // Log.i(TAG, "Safe --> 系统, 开始检查是否需要手动 hide");
                                keyboardPreHide();
                            }
                        } else {
                            // 新获取焦点的不是 EditText, 则隐藏 SafeKeyboard
                            // Log.i(TAG, "Safe --> 其他, 开始检查是否需要手动 hide");
                            keyboardPreHide();
                        }
                    } else {
                        // 前 EditText 没有使用 SafeKeyboard
                        // 新获取焦点的是 EditText
                        if (newFocus instanceof EditText) {
                            EditText newEdit = (EditText) newFocus;
                            // 该 EditText 使用了 SafeKeyboard, 则显示
                            if (mEditMap.get(newEdit.getId()) != null) {
                                // Log.i(TAG, "系统 --> Safe, 开始检查是否需要手动 show");
                                keyboardPreShow(newEdit);
                            } else {
                                // Log.i(TAG, "系统 --> 系统, 开始检查是否需要手动 hide");
                                keyboardPreHide();
                            }
                        } else {
                            // ... 否则不需要管理此次事件, 但是为保险起见, 可以隐藏一次 SafeKeyboard, 当然隐藏前需要判断是否已显示
                            // Log.i(TAG, "系统 --> 其他, 开始检查是否需要手动 hide");
                            keyboardPreHide();
                        }
                    }
                } else {
                    // 新获取焦点的是 EditText
                    if (newFocus instanceof EditText) {
                        EditText newEdit = (EditText) newFocus;
                        // 该 EditText 使用了 SafeKeyboard, 则显示
                        if (mEditMap.get(newEdit.getId()) != null) {
                            // Log.i(TAG, "其他 --> Safe, 开始检查是否需要手动 show");
                            keyboardPreShow(newEdit);
                        } else {
                            // Log.i(TAG, "其他 --> 系统, 开始检查是否需要手动 hide");
                            keyboardPreHide();
                        }
                    } else {
                        // ... 否则不需要管理此次事件, 但是为保险起见, 可以隐藏一次 SafeKeyboard, 当然隐藏前需要判断是否已显示
                        // Log.i(TAG, "其他 --> 其他, 开始检查是否需要手动 hide");
                        keyboardPreHide();
                    }
                }
            };
            treeObserver.addOnGlobalFocusChangeListener(onGlobalFocusChangeListener);
        } else {
            Log.e(TAG, "Root View is null!");
            // throw new Exception("Root View is null");
        }

        onEditTextTouchListener = (v, event) -> {
            if (v instanceof EditText) {
                EditText mEditText = (EditText) v;
                hideSystemKeyBoard(mEditText);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downPoint.setCoo_x((int) event.getRawX());
                    downPoint.setCoo_y((int) event.getRawY());
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    upPoint.setCoo_x((int) event.getRawX());
                    upPoint.setCoo_y((int) event.getRawY());
                    if (isTouchConsiderClick(downPoint, upPoint, mEditText) && mEditText.hasFocus()) {
                        if (mCurrentEditText == mEditText && isShow()) {
                            return false;
                        }
                        keyboardPreShow(mEditText);
                    }
                    downPoint.clearPoint();
                    upPoint.clearPoint();
                }
            }
            return false;
        };
    }

    /**
     * 用来计算按下和抬起时的两点位置的关系, 是否可以将此次 Touch 事件 看作 Click 事件
     * 两点各自的 x/y 轴距离不超过 10, 且两点中心点在目标 EditText 上 时, 返回 true, 否则 false
     *
     * @param down      按下时的位置点
     * @param up        抬起时的位置点
     * @param mEditText 目标 EditText
     * @return 是否考虑此次为点击事件
     */
    private boolean isTouchConsiderClick(ViewPoint down, ViewPoint up, EditText mEditText) {
        boolean flag = false;
        if (Math.abs(down.getCoo_x() - up.getCoo_x()) < 10 && Math.abs(down.getCoo_y() - up.getCoo_y()) < 10) {
            int[] position = new int[2];
            mEditText.getLocationOnScreen(position);
            int width = mEditText.getWidth();
            int height = mEditText.getHeight();
            int x = (down.getCoo_x() + up.getCoo_x()) / 2;
            int y = (down.getCoo_y() + up.getCoo_y()) / 2;
            if (position[0] + width >= x && position[1] + height >= y)
                flag = true;
        }

        return flag;
    }

    private void keyboardPreHide() {
        safeHandler.removeCallbacks(hideRun);
        safeHandler.removeCallbacks(showRun);
        if (stillNeedOptManually(false)) {
            safeHandler.postDelayed(hideRun, HIDE_DELAY);
        }
    }

    private void keyboardPreShow(final EditText mEditText) {
        safeHandler.removeCallbacks(showRun);
        safeHandler.removeCallbacks(hideRun);
        if (stillNeedOptManually(true)) {
            setCurrentEditText(mEditText);
            safeHandler.postDelayed(showRun, SHOW_DELAY);
        } else {
            final long delay = mNeedScroll ? HIDE_TIME + 50 : 0;
            doScrollLayoutBack(false);
            new Handler().postDelayed(() -> {
                // 如果已经显示了, 那么切换键盘即可
                setCurrentEditText(mEditText);
                setKeyboard(getKeyboardByInputType());
            }, delay);
        }
    }

    private void initRandomDigitKeys() {
        randomDigitKeys = new SparseArray<>();
        List<Keyboard.Key> keys = keyboardNumber.getKeys();
        for (Keyboard.Key key : keys) {
            int code = key.codes[0];
            if (code >= 48 && code <= 57)
                randomDigitKeys.put(code, key);
        }
    }

    private void initIdCardRandomDigitKeys() {
        randomIdCardDigitKeys = new SparseArray<>();
        List<Keyboard.Key> keys = keyboardIdCard.getKeys();
        for (Keyboard.Key key : keys) {
            int code = key.codes[0];
            if (code >= 48 && code <= 57)
                randomIdCardDigitKeys.put(code, key);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void putEditText(EditText mEditText) {
        if (mEditMap == null) mEditMap = new HashMap<>();
        mEditMap.put(mEditText.getId(), mEditText);
        mEditText.setOnTouchListener(onEditTextTouchListener);
    }

    public void putEditText2IdCardType(int id, EditText mEditText) {
        if (mIdCardEditMap == null) mIdCardEditMap = new HashMap<>();
        mIdCardEditMap.put(id, mEditText);
    }

    /**
     * 设置是否强制关闭预览功能
     * <p>
     * 解释：因为系统自带的 KeyboardView 的按键预览功能是使用 PopupWindow 来实现的, 那么在
     * PopupWindow 中使用了 SafeKeyboard (本软键盘), 那么必须关闭预览, 否则会直接崩溃.
     * ( 即调用 setForbidPreview(true) )
     *
     * @param forbidPreview 是否关闭预览
     */
    public void setForbidPreview(boolean forbidPreview) {
        this.forbidPreview = forbidPreview;
    }

    // 设置键盘点击监听
    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {

        @Override
        public void onPress(int primaryCode) {
            if (keyboardType == 3) {
                keyboardView.setPreviewEnabled(false);
            } else {
                keyboardView.setPreviewEnabled(!forbidPreview);
                if (primaryCode == -1 || primaryCode == -5 || primaryCode == 32 || primaryCode == -2
                        || primaryCode == 100860 || primaryCode == 100861 || primaryCode == -35) {
                    keyboardView.setPreviewEnabled(false);
                } else {
                    keyboardView.setPreviewEnabled(!forbidPreview);
                }
            }
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            try {
                Editable editable = mCurrentEditText.getText();
                int start = mCurrentEditText.getSelectionStart();
                int end = mCurrentEditText.getSelectionEnd();
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                    // 隐藏键盘
                    safeHandler.removeCallbacks(hideRun);
                    safeHandler.removeCallbacks(showRun);
                    safeHandler.post(hideRun/*, HIDE_DELAY*/);
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
                } else if (primaryCode == 100861) {
                    // TODO... 这里啥也不干
                } else {
                    // 输入键盘值
                    // editable.insert(start, Character.toString((char) primaryCode));
                    editable.replace(start, end, Character.toString((char) primaryCode));
                    if (mEditLastKeyboardTypeArray.get(mCurrentEditText.getId(), 1) == 1 && !isCapLock && isCapes) {
                        isCapes = isCapLock = false;
                        toLowerCase();
                        keyboardView.setCap(isCapes);
                        keyboardView.setCapLock(isCapLock);
                        switchKeyboard();
                    }
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

    private void refreshDigitKeyboard(Keyboard keyboard) {
        if (keyboard != null) {
            SparseArray<Keyboard.Key> randomKeys;
            if (keyboard == keyboardIdCard) {
                // 如果是 IdCard 键盘
                randomKeys = randomIdCardDigitKeys;
            } else {
                // 否则认为是 数字 键盘
                randomKeys = randomDigitKeys;
            }
            HashSet<Integer> set = new HashSet<>();
            while (set.size() < 10) {
                int num = (int) (Math.random() * 10);
                if (set.add(num)) {
                    // set.size() - 1 表示目前是第几个数字按键
                    Keyboard.Key key = randomKeys.get(set.size() - 1 + 48);
                    key.label = num + "";
                    key.codes[0] = 48 + num;
                }
            }
        } else {
            Log.w(TAG, "Refresh Digit ERROR! Keyboard is null");
        }
    }

    private void switchKeyboard() {
        switch (keyboardType) {
            case 1:
                setKeyboard(keyboardLetter);
                break;
            case 2:
                setKeyboard(keyboardSymbol);
                break;
            case 3:
                if (keyboardView.isRandomDigit()) {
                    refreshDigitKeyboard(keyboardNumber);
                }
                setKeyboard(keyboardNumber);
                break;
            default:
                Log.e(TAG, "ERROR keyboard type");
                break;
        }
    }

    private void setKeyboard(Keyboard keyboard) {
        int type;
        if (keyboard == keyboardLetter) {
            type = 1;
        } else if (keyboard == keyboardSymbol) {
            type = 2;
        } else if (keyboard == keyboardNumber || keyboard == keyboardNumberOnly || keyboard == keyboardIdCard) {
            type = 3;
        } else type = 1;
        mEditLastKeyboardTypeArray.put(mCurrentEditText.getId(), type);
        keyboardType = type;
        keyboardView.setKeyboard(keyboard);
        // hideSystemKeyBoard(mCurrentEditText);
    }

    private void changeKeyboardLetterCase() {
        if (!isCapes) {
            // 为小写时, 改为大写.
            toUpperCase();
        } else if (isCapLock) {
            toLowerCase();
        }
        if (isCapLock) {
            isCapLock = isCapes = false;
        } else if (isCapes) {
            isCapLock = true;
        } else {
            isCapes = true;
            isCapLock = false;
        }
        keyboardView.setCap(isCapes);
        keyboardView.setCapLock(isCapLock);
    }

    private void toLowerCase() {
        List<Keyboard.Key> keyList = keyboardLetter.getKeys();
        for (Keyboard.Key key : keyList) {
            if (key.label != null && isUpCaseLetter(key.label.toString())) {
                key.label = key.label.toString().toLowerCase();
                key.codes[0] += 32;
            }
        }
    }

    private void toUpperCase() {
        List<Keyboard.Key> keyList = keyboardLetter.getKeys();
        for (Keyboard.Key key : keyList) {
            if (key.label != null && isLowCaseLetter(key.label.toString())) {
                key.label = key.label.toString().toUpperCase();
                key.codes[0] -= 32;
            }
        }
    }

    public void hideKeyboard() {
        keyContainer.clearAnimation();
        keyContainer.startAnimation(hideAnimation);
    }

    /**
     * 只起到延时开始显示的作用
     */
    private final Runnable showRun = this::showKeyboard;

    private final Runnable hideRun = this::hideKeyboard;

    private final Runnable hideEnd = () -> {
        isHideStart = false;
        keyContainer.clearAnimation();
        if (keyContainer.getVisibility() != View.GONE) {
            keyContainer.setVisibility(View.GONE);
        }

        doScrollLayoutBack(true);
    };

    /**
     * 回落
     * @param isHide    回落的同时, SafeKeyboard 是否隐藏
     */
    private void doScrollLayoutBack(final boolean isHide) {
        if (mNeedScroll) {
            mNeedScroll = false;

            hideScrollAnimation = new TranslateAnimation(Animation.ABSOLUTE, mScrollLayout.getLeft(),
                    Animation.ABSOLUTE, mScrollLayout.getLeft(),
                    Animation.ABSOLUTE, mScrollLayout.getTop() - (isHide ? 0 : toBackSize),
                    Animation.ABSOLUTE, mScrollLayout.getTop() - (isHide ? toBackSize : 2 * toBackSize));
            hideScrollAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    int[] pos = {mScrollLayout.getLeft(), mScrollLayout.getTop() - (isHide ? 0 : (int) toBackSize),
                            mScrollLayout.getRight(), mScrollLayout.getBottom() - (isHide ? (int) toBackSize : 2 * (int) toBackSize)};
                    mScrollLayout.clearAnimation();
                    mScrollLayout.layout(pos[0], pos[1], pos[2], pos[3]);
//                    if (mScrollLayout instanceof ScrollView) {
//                        ScrollView scrollView = (ScrollView) mScrollLayout;
//                        scrollView.scrollTo(0, mCurrentEditText.getMeasuredHeight());
//                    }
                    hideScrollAnimation = null;
                    toBackSize = 0;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            hideScrollAnimation.setDuration(HIDE_TIME);
            hideScrollAnimation.setFillAfter(true);
            mScrollLayout.startAnimation(hideScrollAnimation);
        }
    }

    /**
     * 顶起
     */
    private void doScrollLayout() {
        // 计算 SafeKeyboard 显示后是否会遮挡住 EditText
        editNeedScroll(mCurrentEditText);
        if (mNeedScroll) {
            mScrollLayout.startAnimation(showScrollAnimation);
        }
    }

    private final Runnable showEnd = () -> {
        isShowStart = false;
        // 在迅速点击不同输入框时, 造成自定义软键盘和系统软件盘不停的切换, 偶尔会出现停在使用系统键盘的输入框时, 没有隐藏
        // 自定义软键盘的情况, 为了杜绝这个现象, 加上下面这段代码
        if (!mCurrentEditText.isFocused()) {
            safeHandler.removeCallbacks(hideRun);
            safeHandler.removeCallbacks(showRun);
            safeHandler.postDelayed(hideRun, HIDE_DELAY);
        }

        // 这个只能在 keyContainer 显示后才能调用, 只有这个时候才能获取到 keyContainer 的宽、高值
        doScrollLayout();
    };

    private void showKeyboard() {
        Keyboard mKeyboard = getKeyboardByInputType();
        if (mKeyboard != null && (mKeyboard == keyboardNumber || mKeyboard == keyboardIdCard
                || mKeyboard == keyboardNumberOnly) && keyboardView.isRandomDigit()) {
            refreshDigitKeyboard(mKeyboard);
        }
        setKeyboard(mKeyboard == null ? keyboardLetter : mKeyboard);
        keyContainer.setVisibility(View.VISIBLE);
        keyContainer.clearAnimation();
        keyContainer.startAnimation(showAnimation);
    }

    /**
     * @param mEditText 目标 EditText
     */
    private void editNeedScroll(EditText mEditText) {
        int keyboardHeight = keyContainer.getHeight();
        int keyStartY = mScreenHeight - keyboardHeight;
        int[] position = new int[2];
        mEditText.getLocationOnScreen(position);
        int mEditTextY = position[1] + mEditText.getHeight();
        mNeedScroll = mEditTextY - keyStartY > 0;
        if (mNeedScroll) {
            final float to = keyStartY - (mEditTextY + mEditText.getHeight()) - 10;
            toBackSize = to;
            showScrollAnimation = new TranslateAnimation(Animation.ABSOLUTE, mScrollLayout.getLeft(),
                    Animation.ABSOLUTE, mScrollLayout.getLeft(),
                    Animation.ABSOLUTE, mScrollLayout.getTop(),
                    Animation.ABSOLUTE, mScrollLayout.getTop() + to);
            showScrollAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    int[] pos = {mScrollLayout.getLeft(), mScrollLayout.getTop() + (int) to,
                            mScrollLayout.getRight(), mScrollLayout.getBottom() + (int) to};
                    mScrollLayout.clearAnimation();
                    mScrollLayout.layout(pos[0], pos[1], pos[2], pos[3]);
                    showScrollAnimation = null;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            showScrollAnimation.setDuration(SHOW_TIME);
            showScrollAnimation.setFillAfter(true);
        }
    }

    private Keyboard getKeyboardByInputType() {
        Keyboard lastKeyboard = keyboardLetter; // 默认字母键盘

        if (mCurrentInputType == InputType.TYPE_CLASS_NUMBER) {
            lastKeyboard = keyboardNumberOnly;
        } else if (mIdCardEditMap.get(mCurrentEditText.getId()) != null) {
            lastKeyboard = keyboardIdCard;
        } else if (keyboardView.isRememberLastType()) {
            int type = mEditLastKeyboardTypeArray.get(mCurrentEditText.getId(), 1);
            switch (type) {
                case 1:
                    lastKeyboard = keyboardLetter;
                    break;
                case 2:
                    lastKeyboard = keyboardSymbol;
                    break;
                case 3:
                    lastKeyboard = keyboardNumber;
                    break;
                default:
                    Log.e(TAG, "ERROR keyboard type");
                    break;
            }
        }

        return lastKeyboard;
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
    private void add2MapAllAndEditTextListeners(ArrayList<EditText> mEditTexts) {
        for (EditText mEditText : mEditTexts) {
            mEditMap.put(mEditText.getId(), mEditText);
            mEditText.setOnTouchListener(onEditTextTouchListener);
        }
    }

    private void setCurrentEditText(EditText mEditText) {
        mCurrentEditText = mEditText;
        mCurrentInputType = mEditText.getInputType();
    }

    public boolean isShow() {
        return isKeyboardShown();
    }

    //隐藏系统键盘关键代码
    private void hideSystemKeyBoard(EditText edit) {
        this.mCurrentEditText = edit;
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

    public boolean stillNeedOptManually(boolean preferShow) {
        boolean flag;
        if (preferShow) {
            // 想要显示
            flag = isHideStart || (!isKeyboardShown() && !isShowStart);
        } else {
            // 想要隐藏
            flag = isShowStart || (isKeyboardShown() && !isHideStart);
        }
        return flag;
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

    public void setUpDrawableLock(Drawable upDrawableLock) {
        this.upDrawableLock = upDrawableLock;
        keyboardView.setUpDrawable(upDrawableLock);
    }

    public void release() {
        mContext = null;
        isCapes = false;
        onEditTextTouchListener = null;
        if (treeObserver != null && onGlobalFocusChangeListener != null && treeObserver.isAlive()) {
            treeObserver.removeOnGlobalFocusChangeListener(onGlobalFocusChangeListener);
        }
        treeObserver = null;
        onGlobalFocusChangeListener = null;
        if (mEditLastKeyboardTypeArray != null) {
            mEditLastKeyboardTypeArray.clear();
            mEditLastKeyboardTypeArray = null;
        }
        if (mEditMap != null) {
            mEditMap.clear();
            mEditMap = null;
        }
        if (mIdCardEditMap != null) {
            mIdCardEditMap.clear();
            mIdCardEditMap = null;
        }
    }
}
