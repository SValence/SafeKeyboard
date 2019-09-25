package com.safe.keyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class DialogSafeKeyboardActivity extends AppCompatActivity implements SafeKeyboardDialogFragment.onDialogResult {

    private CheckBox useSafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_safe_keyboard);
        useSafe = findViewById(R.id.use_safe_key);
        setTitle(R.string.alert_test);
    }

    public void onAlertDialogClick(View view) {
        SafeKeyboardDialogFragment fragment = SafeKeyboardDialogFragment.newInstance(useSafe.isChecked());
        fragment.setOnDialogResult(this);
        fragment.show(getSupportFragmentManager(), "SafeKeyboardDialogFragment");
    }

    @Override
    public void onDialogResults(Object t) {
        // TODO... 获得处理结果, 在这里添加你的业务代码
    }
}
