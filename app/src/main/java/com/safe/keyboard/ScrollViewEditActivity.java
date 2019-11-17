package com.safe.keyboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ScrollViewEditActivity extends AppCompatActivity {

    private SafeKeyboard safeKeyboard;
    private EditText safeEdit8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_veiw_edit);
        setTitle(R.string.scroll_view_test);

        EditText safeEdit = findViewById(R.id.safeEdit);
        EditText safeEdit2 = findViewById(R.id.safeEdit2);
        safeEdit8 = findViewById(R.id.safeEdit8);
        LinearLayout keyboardContainer = findViewById(R.id.keyboardPlace);
        View rootView = findViewById(R.id.scrollRoot);
        View scrollLayout = findViewById(R.id.scrollViewScrollLayout);
        safeKeyboard = new SafeKeyboard(getApplicationContext(), keyboardContainer,
                R.layout.layout_keyboard_containor, R.id.safeKeyboardLetter, rootView, scrollLayout, false);
        safeKeyboard.putEditText(safeEdit);
        safeKeyboard.putEditText(safeEdit2);
        safeKeyboard.putEditText(safeEdit8);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        Log.i("ScrollLayoutSafeEdit8", safeEdit8.getLeft() + ", \t\t" + safeEdit8.getTop()
//                + ", \t\t" + safeEdit8.getRight() + ", \t\t" + safeEdit8.getBottom());
    }

    // 当点击返回键时, 如果软键盘正在显示, 则隐藏软键盘并是此次返回无效
    @Override
    public void onBackPressed() {
        if (safeKeyboard != null && safeKeyboard.stillNeedOptManually(false)) {
            safeKeyboard.hideKeyboard();
            return;
        }
        super.onBackPressed();
    }
}
