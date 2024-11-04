package com.valence.safe.keyboard;

import android.util.Log;
import android.util.SparseArray;

public class KeyBgResEntitySet {

    private static final String TAG = "SK-KeyBgResEntitySet";

    private SparseArray<KeyBgResEntity> idCardKeyboardBgResArray;
    private SparseArray<KeyBgResEntity> letterKeyboardBgResArray;
    private SparseArray<KeyBgResEntity> numOnlyKeyboardBgResArray;
    private SparseArray<KeyBgResEntity> numSymbolKeyboardBgResArray;
    private SparseArray<KeyBgResEntity> symbolKeyboardBgResArray;

    public KeyBgResEntitySet() {
        idCardKeyboardBgResArray = new SparseArray<>();
        letterKeyboardBgResArray = new SparseArray<>();
        numOnlyKeyboardBgResArray = new SparseArray<>();
        numSymbolKeyboardBgResArray = new SparseArray<>();
        symbolKeyboardBgResArray = new SparseArray<>();
    }

    public KeyBgResEntitySet(SparseArray<KeyBgResEntity> idCardKeyboardBgResArray,
                             SparseArray<KeyBgResEntity> letterKeyboardBgResArray,
                             SparseArray<KeyBgResEntity> numOnlyKeyboardBgResArray,
                             SparseArray<KeyBgResEntity> numSymbolKeyboardBgResArray,
                             SparseArray<KeyBgResEntity> symbolKeyboardBgResArray) {
        this.idCardKeyboardBgResArray = deepCopy(idCardKeyboardBgResArray);
        this.letterKeyboardBgResArray = deepCopy(letterKeyboardBgResArray);
        this.numOnlyKeyboardBgResArray = deepCopy(numOnlyKeyboardBgResArray);
        this.numSymbolKeyboardBgResArray = deepCopy(numSymbolKeyboardBgResArray);
        this.symbolKeyboardBgResArray = deepCopy(symbolKeyboardBgResArray);
    }

    public SparseArray<KeyBgResEntity> getIdCardKeyboardBgResArray() {
        return idCardKeyboardBgResArray;
    }

    public void setIdCardKeyboardBgResArray(SparseArray<KeyBgResEntity> idCardKeyboardBgResArray) {
        this.idCardKeyboardBgResArray = deepCopy(idCardKeyboardBgResArray);
    }

    public SparseArray<KeyBgResEntity> getLetterKeyboardBgResArray() {
        return letterKeyboardBgResArray;
    }

    public void setLetterKeyboardBgResArray(SparseArray<KeyBgResEntity> letterKeyboardBgResArray) {
        this.letterKeyboardBgResArray = deepCopy(letterKeyboardBgResArray);
    }

    public SparseArray<KeyBgResEntity> getNumOnlyKeyboardBgResArray() {
        return numOnlyKeyboardBgResArray;
    }

    public void setNumOnlyKeyboardBgResArray(SparseArray<KeyBgResEntity> numOnlyKeyboardBgResArray) {
        this.numOnlyKeyboardBgResArray = deepCopy(numOnlyKeyboardBgResArray);
    }

    public SparseArray<KeyBgResEntity> getNumSymbolKeyboardBgResArray() {
        return numSymbolKeyboardBgResArray;
    }

    public void setNumSymbolKeyboardBgResArray(SparseArray<KeyBgResEntity> numSymbolKeyboardBgResArray) {
        this.numSymbolKeyboardBgResArray = deepCopy(numSymbolKeyboardBgResArray);
    }

    public SparseArray<KeyBgResEntity> getSymbolKeyboardBgResArray() {
        return symbolKeyboardBgResArray;
    }

    public void setSymbolKeyboardBgResArray(SparseArray<KeyBgResEntity> symbolKeyboardBgResArray) {
        this.symbolKeyboardBgResArray = deepCopy(symbolKeyboardBgResArray);
    }

    public boolean isKeyBgHasSet() {
        // 有任何一个不是空, 都说明设置了按键颜色, 都要重新进行重新渲染
        return (idCardKeyboardBgResArray != null && idCardKeyboardBgResArray.size() > 0) ||
                (letterKeyboardBgResArray != null && letterKeyboardBgResArray.size() > 0) ||
                (numOnlyKeyboardBgResArray != null && numOnlyKeyboardBgResArray.size() > 0) ||
                (numSymbolKeyboardBgResArray != null && numSymbolKeyboardBgResArray.size() > 0) ||
                (symbolKeyboardBgResArray != null && symbolKeyboardBgResArray.size() > 0);
    }

    private SparseArray<KeyBgResEntity> deepCopy(SparseArray<KeyBgResEntity> keyBgResEntityArray) {
        if (keyBgResEntityArray == null || keyBgResEntityArray.size() == 0) {
            Log.w(TAG, "要深度拷贝的 SparseArray 为空! 直接返回无数据的空 SparseArray");
            return new SparseArray<>();
        }
        SparseArray<KeyBgResEntity> newKeyBgResEntityArray = new SparseArray<>();
        for (int i = 0; i < keyBgResEntityArray.size(); i++) {
            KeyBgResEntity entity = keyBgResEntityArray.valueAt(i);
            if (entity == null) {
                entity = new KeyBgResEntity();
            } else {
                entity = entity.cloneSelf();
            }
            newKeyBgResEntityArray.put(keyBgResEntityArray.keyAt(i), entity);
        }

        return newKeyBgResEntityArray;
    }
}
