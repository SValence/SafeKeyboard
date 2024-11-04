package com.valence.safe.keyboard;

public class KeyBgResEntity {
    private final int bgResId;
    private final int keyTextColorId;

    public KeyBgResEntity() {
        bgResId = SafeKeyboardConfig.DEFAULT_KEYBOARD_KEY_BG_RES_ID;
        keyTextColorId = SafeKeyboardConfig.DEFAULT_KEYBOARD_KEY_TEXT_COLOR;
    }

    public KeyBgResEntity(int bgResId, int keyTextColorId) {
        this.bgResId = bgResId;
        this.keyTextColorId = keyTextColorId;
    }

    public int getBgResId() {
        return bgResId;
    }

    public int getKeyTextColorId() {
        return keyTextColorId;
    }

    public KeyBgResEntity cloneSelf() {
        // super.clone();
        return new KeyBgResEntity(bgResId, keyTextColorId);
    }
}
