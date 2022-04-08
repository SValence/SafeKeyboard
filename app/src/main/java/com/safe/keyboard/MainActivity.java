package com.safe.keyboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.valence.safe.keyboard.SafeKeyboard;

public class MainActivity extends AppCompatActivity {

    private SafeKeyboard safeKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText safeEdit = findViewById(R.id.safeEditText);
        EditText safeEdit2 = findViewById(R.id.safeEditText2);
        EditText safeEdit3 = findViewById(R.id.safeEditText3);
        EditText safeEdit4 = findViewById(R.id.safeEditText4);
        EditText safeEdit5 = findViewById(R.id.safe_keyboard_safe_edit);
        AppCompatEditText appCompatEditText = findViewById(R.id.safeAppCompactEditText);
        View rootView = findViewById(R.id.main_root);
        View scrollLayout = findViewById(R.id.scroll_layout);
        LinearLayout keyboardContainer = findViewById(R.id.safe_keyboard_place);
        safeKeyboard = new SafeKeyboard(getApplicationContext(), keyboardContainer, rootView, scrollLayout);
        safeKeyboard.putEditText(safeEdit);
        safeKeyboard.putRandomEdit(safeEdit2);
        safeKeyboard.putRandomEdit(safeEdit3);
        safeKeyboard.putEditText(safeEdit4);
        safeKeyboard.putRandomEdit(safeEdit5);
        safeKeyboard.putEditText(appCompatEditText);
        safeKeyboard.putEditText2IdCardType(safeEdit3.getId(), safeEdit3);
        safeKeyboard.setForbidPreview(true);
//        safeKeyboard.setDelDrawable(this.getResources().getDrawable(R.drawable.icon_del));
//        safeKeyboard.setLowDrawable(this.getResources().getDrawable(R.drawable.icon_capital_default));
//        safeKeyboard.setUpDrawable(this.getResources().getDrawable(R.drawable.icon_capital_selected));

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
}
