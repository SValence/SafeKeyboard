package com.safe.keyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private SafeKeyboard safeKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText safeEdit = findViewById(R.id.safeEditText);
        LinearLayout keyboardContainer = findViewById(R.id.keyboardViewPlace);
        safeKeyboard = new SafeKeyboard(getApplicationContext(), keyboardContainer, safeEdit);
    }

    // 当点击返回键时, 如果软键盘正在显示, 则隐藏软键盘并是此次返回无效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (safeKeyboard.isShow()) {
                safeKeyboard.hideKeyboard();
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return false;
        } else
            return super.onKeyDown(keyCode, event);
    }
}
