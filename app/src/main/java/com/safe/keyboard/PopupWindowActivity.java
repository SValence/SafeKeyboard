package com.safe.keyboard;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class PopupWindowActivity extends AppCompatActivity {

    private SafeKeyboard safeKeyboard;
    private PopupWindow mPopWindow;
    private LinearLayout popupLayout;
    private CheckBox useSafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window);
        useSafe = findViewById(R.id.use_safe_key2);
        setTitle(R.string.popup_test);
    }

    public void onPopupWindowClick(View v) {
        if (useSafe.isChecked()) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null) {
                Toast.makeText(this, R.string.error_param_null_inflater, Toast.LENGTH_SHORT).show();
                return;
            }
            View windowView = inflater.inflate(R.layout.layout_single_edit, null, false);
            EditText safeEditPop = windowView.findViewById(R.id.single_edit);
            if (safeKeyboard == null) {
                LinearLayout keyboardContainer = windowView.findViewById(R.id.keyboardPlace);
                View rootView = windowView.findViewById(R.id.popupEditRoot);
                safeKeyboard = new SafeKeyboard(getApplicationContext(), keyboardContainer,
                        R.layout.layout_keyboard_containor, R.id.safeKeyboardLetter, rootView, safeEditPop);
                // 默认的预览功能是使用 PopupWindow 实现的, 而 PopupWindow 中不能再弹出 PopupWindow, 所以这里需要关闭预览功能
                safeKeyboard.setForbidPreview(true);
            }

            if (mPopWindow == null) {
                mPopWindow = new PopupWindow(windowView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT) {
                    @Override
                    public void dismiss() {
                        if (safeKeyboard != null && safeKeyboard.stillNeedOptManually(false)) {
                            safeKeyboard.hideKeyboard();
                            return;
                        }
                        super.dismiss();
                    }
                };
                mPopWindow.setTouchable(true);
                mPopWindow.setOutsideTouchable(true);
                mPopWindow.setFocusable(true);
                mPopWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFFFF));
                mPopWindow.setOnDismissListener(() -> {
                    if (safeKeyboard != null && safeKeyboard.stillNeedOptManually(false)) {
                        safeKeyboard.hideKeyboard();
                    }
                });
                mPopWindow.setOnDismissListener(() -> {
                    mPopWindow = null;
                    if (safeKeyboard != null) {
                        safeKeyboard.release();
                        safeKeyboard = null;
                    }
                });
                // mPopWindow.setTouchInterceptor((view1, motionEvent) -> {
                //     // 暂时使用不到这一层触摸监听事件
                //     return false;
                // });

                safeKeyboard.putEditText(safeEditPop);
                popupLayout = findViewById(R.id.popupWindowPlace);
            }

            mPopWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        if (safeKeyboard != null) {
            safeKeyboard.release();
            safeKeyboard = null;
        }
        super.onDestroy();
    }
}
