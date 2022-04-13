package com.safe.keyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.valence.safe.keyboard.SafeKeyboard;
import com.valence.safe.keyboard.SafeKeyboardConfig;

public class IncludeSameEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_include_same_edit);

        View includeViewOne = findViewById(R.id.includeOneLayout);
        View includeViewTwo = findViewById(R.id.includeTwoLayout);
        View includeViewThree = findViewById(R.id.includeThreeLayout);

        EditText includeEditOne = includeViewOne.findViewById(R.id.normalEditText);
        EditText includeEditOneOther = includeViewOne.findViewById(R.id.normalEditText);
        EditText includeEditTwo = includeViewTwo.findViewById(R.id.normalEditText);
        EditText includeEditThree = includeViewThree.findViewById(R.id.normalEditText);

        View rootView = findViewById(R.id.includeRoot);             // 是 rootView
        View scrollView = findViewById(R.id.scrollViewLayout);      // 是 scrollView, 也可以是 rootView
        LinearLayout container = findViewById(R.id.safe_keyboard_place);

        Log.i(getClass().getSimpleName(), includeEditOne.toString());
        Log.i(getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(includeEditOne)));
        Log.i(getClass().getSimpleName(), includeEditOneOther.toString());
        Log.i(getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(includeEditOneOther)));
        Log.w(getClass().getSimpleName(), includeEditTwo.toString());
        Log.w(getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(includeEditTwo)));
        Log.e(getClass().getSimpleName(), includeEditThree.toString());
        Log.e(getClass().getSimpleName(), Integer.toHexString(System.identityHashCode(includeEditThree)));

        Log.i(getClass().getSimpleName(), includeEditOne.getId() + "");
        Log.i(getClass().getSimpleName(), includeEditOneOther.getId() + "");
        Log.w(getClass().getSimpleName(), includeEditTwo.getId() + "");
        Log.e(getClass().getSimpleName(), includeEditThree.getId() + "");

        includeEditOne.setText("第一个");
        includeEditTwo.setText("第二个");
        includeEditThree.setText("第三个, 页面第四个 EditText");

        SafeKeyboardConfig config = SafeKeyboardConfig.getDefaultConfig();
        config.keyboardBgResId = R.color.transparent;
        SafeKeyboard safeKeyboard = new SafeKeyboard(getApplicationContext(), container, rootView, scrollView, config);
        safeKeyboard.setVibrateEnable(true);
        safeKeyboard.putEditText(includeEditOne);
        safeKeyboard.putRandomEdit(includeEditTwo);
        safeKeyboard.putVibrateEdit(includeEditThree);
    }
}