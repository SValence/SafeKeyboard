package com.valence.safe.keyboard;

import android.widget.ImageView;

public class SafeKeyboardConfig {

    // 别问为什么是这个图, 问就是作者是同窗粉 2333
    private static final int DEFAULT_RES_ID_KEYBOARD_BG = R.drawable.keyboard_bg_default_lib;      // 也可以是颜色
    private static final int DEFAULT_RES_ID_ICON_DEL = R.drawable.icon_del_lib;
    private static final int DEFAULT_RES_ID_ICON_LOW_LETTER = R.drawable.icon_capital_default_lib;
    private static final int DEFAULT_RES_ID_ICON_UP_LETTER = R.drawable.icon_capital_selected_lib;
    private static final int DEFAULT_RES_ID_ICON_UP_LETTER_LOCK = R.drawable.icon_capital_selected_lock_lib;
    private static final int DEFAULT_RES_ID_SPECIAL_KEY_BG = R.drawable.keyboard_change_trans_lib;
    private static final int DEFAULT_LAYOUT_ID_KEYBOARD_CONTAINER = R.layout.layout_keyboard_container_lib;
    private static final int DEFAULT_SAFE_KEYBOARD_VIEW_ID = R.id.safeKeyboardViewId;
    private static final int DEFAULT_KEYBOARD_TITLE_TEXT_COLOR = R.color.white;
    private static final int DEFAULT_KEYBOARD_DONE_IMG_RES_ID = R.drawable.keyboard_done_img_lib;
    private static final int DEFAULT_KEYBOARD_DONE_IMG_LAYOUT_BG_RES_ID = R.drawable.bg_keyboard_done_layout_trans_lib;
    public static final int DEFAULT_KEYBOARD_KEY_TEXT_COLOR = R.color.white;
    public static final int DEFAULT_KEYBOARD_KEY_BG_RES_ID = R.drawable.keyboard_normal_key_press_bg_trans_lib;
    private static final int DEFAULT_KEYBOARD_KEY_TEXT_SIZE = 20; // sp
    private static final int DEFAULT_KEYBOARD_KEY_LABEL_SIZE = 14; // sp

    private static final ImageView.ScaleType DEFAULT_KEYBOARD_BG_SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;

    private static final boolean DEFAULT_LETTER_WITH_NUMBER = false;
    private static final boolean DEFAULT_ENABLE_VIBRATE = false;
    private static final boolean DEFAULT_ENABLE_CANCEL_INPUT = true;
    private static final boolean DEFAULT_CHANGE_LETTER_CASE_REFRESH_RANDOM = false;

    private static final long DEFAULT_SHOW_TIME = 150;
    private static final long DEFAULT_HIDE_TIME = 150;
    private static final long DEFAULT_DELAY_TIME = 100;
    private static final long DEFAULT_SHOW_DELAY = 100;
    private static final long DEFAULT_HIDE_DELAY = 50;

    private static final String DEFAULT_KEYBOARD_TITLE_TEXT = "智能加密安全软键盘";
    private static final String DEFAULT_KEYBOARD_NUM_ONLY_KEY_NONE_TITLE = "SValence";
    private static final int DEFAULT_RES_ID_KEYBOARD_SHIELD_IMG = R.drawable.shield_lib;

    public ImageView.ScaleType keyboardBgScaleType = DEFAULT_KEYBOARD_BG_SCALE_TYPE;
    public int keyboardBgResId = DEFAULT_RES_ID_KEYBOARD_BG;                        // 键盘背景资源id
    public int iconResIdDel = DEFAULT_RES_ID_ICON_DEL;                              // 删除键资源id
    public int iconResIdLowLetter = DEFAULT_RES_ID_ICON_LOW_LETTER;                 // 小写字母 shift 键资源id
    public int iconResIdUpLetter = DEFAULT_RES_ID_ICON_UP_LETTER;                   // 大写字母 shift 键资源id
    public int iconResIdUpLetterLock = DEFAULT_RES_ID_ICON_UP_LETTER_LOCK;          // 大写字母 shift 键锁定 资源id
    public int keyboardSpecialKeyBgResId = DEFAULT_RES_ID_SPECIAL_KEY_BG;           // 特殊按键背景资源id
    public int keyboardNormalKeyBgResId = DEFAULT_KEYBOARD_KEY_BG_RES_ID;           // 特殊按键背景资源id
    public int keyboardNormalKeyColorId = DEFAULT_KEYBOARD_KEY_TEXT_COLOR;           // 特殊按键背景资源id

    public int keyboardContainerLayoutId = DEFAULT_LAYOUT_ID_KEYBOARD_CONTAINER;    // 键盘显示样式布局资源id
    public int safeKeyboardViewId = DEFAULT_SAFE_KEYBOARD_VIEW_ID;

    // 纯数字键盘界面左下角多一个按键, 因为我不想删除这个按键, 所以这里加了这个属性, 用来定义并显示该按键显示的内容, 默认："SValence"
    public String keyboardNumOnlyKeyNoneTitle = DEFAULT_KEYBOARD_NUM_ONLY_KEY_NONE_TITLE;
    public String keyboardTitle = DEFAULT_KEYBOARD_TITLE_TEXT;                      // 按键提示上方正中提示文字
    public int keyboardShieldImgResId = DEFAULT_RES_ID_KEYBOARD_SHIELD_IMG;         // 按键提示上方正中安全图标资源id, 不限图片
    public int keyboardTitleColor = DEFAULT_KEYBOARD_TITLE_TEXT_COLOR;              // 按键提示上方正中提示文字颜色
    public int keyboardDoneImgResId = DEFAULT_KEYBOARD_DONE_IMG_RES_ID;             // 键盘顶部右侧隐藏键盘的图片按键
    public int keyboardDoneImgLayoutResId = DEFAULT_KEYBOARD_DONE_IMG_LAYOUT_BG_RES_ID;// 键盘顶部右侧隐藏键盘的图片按键
    public int keyboardKeyTextSize = DEFAULT_KEYBOARD_KEY_TEXT_SIZE;// 键盘顶部右侧隐藏键盘的图片按键
    public int keyboardKeyLabelSize = DEFAULT_KEYBOARD_KEY_LABEL_SIZE;// 键盘顶部右侧隐藏键盘的图片按键

    public long showDuration = DEFAULT_SHOW_TIME;
    public long hideDuration = DEFAULT_HIDE_TIME;
    public long showDelay = DEFAULT_DELAY_TIME;
    public long hideDelay = DEFAULT_SHOW_DELAY;
    public long delayDuration = DEFAULT_HIDE_DELAY;

    public final boolean letterWithNumber = false; // DEFAULT_LETTER_WITH_NUMBER;                   // 字母键盘是否包含数字
    public boolean enableVibrate = DEFAULT_ENABLE_VIBRATE;                          // 开启按键震动
    public boolean enableCancelInput = DEFAULT_ENABLE_CANCEL_INPUT;                      // 开启取消输入功能
    // public boolean changeLetCaseRefreshRandom = DEFAULT_CHANGE_LETTER_CASE_REFRESH_RANDOM; // 切换大小写时, 刷新随机位置


    public static SafeKeyboardConfig getDefaultConfig() {
        return new SafeKeyboardConfig();
    }
}
