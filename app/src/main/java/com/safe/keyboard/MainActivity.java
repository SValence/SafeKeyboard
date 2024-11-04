package com.safe.keyboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.valence.safe.keyboard.KeyBgResEntity;
import com.valence.safe.keyboard.KeyBgResEntitySet;
import com.valence.safe.keyboard.SafeKeyboard;
import com.valence.safe.keyboard.SafeKeyboardConfig;

public class MainActivity extends AppCompatActivity {

    private SafeKeyboard safeKeyboard;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText safeEdit = findViewById(R.id.safeEditText);
        EditText safeEdit2 = findViewById(R.id.safeEditText2);
        EditText safeEdit3 = findViewById(R.id.safeEditText3);
        EditText safeEdit4 = findViewById(R.id.safeEditText4);
        EditText safeEdit5 = findViewById(R.id.safe_keyboard_safe_edit);
        EditText safeEdit6 = findViewById(R.id.safeEditText6);
        EditText safeEdit7 = findViewById(R.id.safeEditText7);
        AppCompatEditText appCompatEditText = findViewById(R.id.safeAppCompactEditText);
        View rootView = findViewById(R.id.main_root);
        View scrollLayout = findViewById(R.id.scroll_layout);
        LinearLayout keyboardContainer = findViewById(R.id.safe_keyboard_place);
        // SafeKeyboardConfig 用法
        SafeKeyboardConfig config = SafeKeyboardConfig.getDefaultConfig();
        config.keyboardTitle = "SValence智能安全软键盘";
        config.keyboardNumOnlyKeyNoneTitle = "StandByMe2";
        // config.keyboardKeyLabelSize = 20;
        config.keyboardNormalKeyBgResId = R.drawable.keyboard_normal_key_press_bg_trans;
        config.keyboardNormalKeyColorId = R.color.white;
        // config.keyboardBgResId = R.drawable.bg_keyboard_two;
        // config.keyboardDoneImgResId = R.drawable.keyboard_done_blue;
        // config.iconResIdDel = R.drawable.icon_delete;
        KeyBgResEntitySet entitySet = new KeyBgResEntitySet();
        SparseArray<KeyBgResEntity> numOnlyKeyboardBgResArray = new SparseArray<>();
        SparseArray<KeyBgResEntity> numSymbolKeyboardBgResArray = new SparseArray<>();
        SparseArray<KeyBgResEntity> idCardKeyboardBgResArray = new SparseArray<>();
//        @SuppressLint("UseCompatLoadingForDrawables")
//        Drawable keyBgDrawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.keyboard_press_bg_trans_test_red);
        KeyBgResEntity entity = new KeyBgResEntity(R.drawable.keyboard_key_custome_press_bg_trans, R.color.green);
        KeyBgResEntity entity3 = new KeyBgResEntity(R.drawable.keyboard_normal_key_press_bg_trans, R.color.green);
        numOnlyKeyboardBgResArray.put(49, entity);
        numOnlyKeyboardBgResArray.put(52, entity);
        numOnlyKeyboardBgResArray.put(54, entity3);
        numSymbolKeyboardBgResArray.put(49, entity);
        numSymbolKeyboardBgResArray.put(52, entity);
        numSymbolKeyboardBgResArray.put(54, entity3);
        idCardKeyboardBgResArray.put(49, entity);
        idCardKeyboardBgResArray.put(52, entity);
        idCardKeyboardBgResArray.put(54, entity3);
        entitySet.setNumOnlyKeyboardBgResArray(numOnlyKeyboardBgResArray);
        entitySet.setNumSymbolKeyboardBgResArray(numSymbolKeyboardBgResArray);
        entitySet.setIdCardKeyboardBgResArray(idCardKeyboardBgResArray);
        safeKeyboard = new SafeKeyboard(getApplicationContext(), keyboardContainer, rootView, scrollLayout, config);
        // 设置一个按键背景, 测试一下效果
        safeKeyboard.putEditText(safeEdit);
        // 设置几个随机键盘
        safeKeyboard.putRandomEdit(safeEdit2);
        // 同时设置随机键盘和身份证键盘
        safeKeyboard.putRandomEdit(safeEdit3);
        safeKeyboard.putEditText2IdCardType(safeEdit3);
        safeKeyboard.putRandomEdit(safeEdit5);
        // 先允许震动, 才能设置震动成功
        safeKeyboard.setVibrateEnable(true);
        safeKeyboard.putVibrateEdit(safeEdit4);
        safeKeyboard.putEditText(safeEdit6);
        safeKeyboard.putEditText2IdCardType(safeEdit7);
        safeKeyboard.putEditText(appCompatEditText);
        safeKeyboard.setEnablePreview(false);         // 设置是否按键预览
        // safeKeyboard.setEnableCancelInput(false);
        // safeKeyboard.enableRememberLastKeyboardType();
        // safeKeyboard.enableChangeLetCaseRefreshRandom();

        // 注意, 一定要在 SafeKeyboard 设置完之后在调用这里
        safeKeyboard.setEditKeyBgResArray(safeEdit2, entitySet);
        safeKeyboard.setEditKeyBgResArray(safeEdit3, entitySet);
        safeKeyboard.setEditKeyBgResArray(appCompatEditText, entitySet);

        initView();
    }

    private void initView() {
    }

    // 当点击返回键时, 如果软键盘正在显示, 则隐藏软键盘并是此次返回无效
    @Override
    public void onBackPressed() {
        if (safeKeyboard.stillNeedOptManually(false)) {
            safeKeyboard.hideKeyboard();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (safeKeyboard != null) {
            safeKeyboard.release();
            safeKeyboard = null;
        }
        super.onDestroy();
    }

    public void onAlertDialogClick(View view) {
        startActivity(new Intent(MainActivity.this, DialogSafeKeyboardActivity.class));
    }

    public void onPopupWindowTestClick(View view) {
        startActivity(new Intent(MainActivity.this, PopupWindowActivity.class));
    }

    public void onScrollEditTestClick(View view) {
        startActivity(new Intent(MainActivity.this, ScrollViewEditActivity.class));
    }

    public void onIncludeEditTestClick(View view) {
        startActivity(new Intent(MainActivity.this, IncludeSameEditActivity.class));
    }
}
