package com.valence.safe.keyboard;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class SafeKeyboardDialogFragment extends DialogFragment {

    private SafeKeyboard safeKeyboard;
    private LinearLayout keyboardContainer;
    private View rootView;
    private View mScrollLayout;
    private EditText safeEdit;
    private Button confirm;
    private Button cancel;
    private onDialogResult mOnResult;
    private boolean ignoreCondition;

    public static SafeKeyboardDialogFragment newInstance(boolean useSafeKeyboard) {
        SafeKeyboardDialogFragment fragment = new SafeKeyboardDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("use_safe", useSafeKeyboard);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setOnDialogResult(onDialogResult mOnResult) {
        this.mOnResult = mOnResult;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.DialogFragment_FullScreen);

        ignoreCondition = false;
    }

    /**
     * 重写此方法是为了实现点击返回时, 如果 SafeKeyboard 处于显示状态, 先隐藏再 dismiss dialog
     *
     * @param savedInstanceState ...
     * @return ...
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(Objects.requireNonNull(getContext()), getTheme()) {
            @Override
            public void dismiss() {
                if (ignoreCondition) {
                    ignoreCondition = false;
                    super.dismiss();
                } else if (safeKeyboard != null && safeKeyboard.stillNeedOptManually(false)) {
                    safeKeyboard.hideKeyboard();
                } else super.dismiss();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        // 使 Dialog 全屏显示的代码
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(width, height);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog_fragment, container, false);
        keyboardContainer = view.findViewById(R.id.safe_keyboard_place);
        rootView = view.findViewById(R.id.dialog_fragment_root);
        mScrollLayout = view.findViewById(R.id.safe_keyboard_dialog_scroll_layout);
        safeEdit = view.findViewById(R.id.safe_keyboard_safe_edit);
        confirm = view.findViewById(R.id.safe_keyboard_dialog_confirm);
        cancel = view.findViewById(R.id.safe_keyboard_dialog_cancel);
        setListeners();
        return view;
    }

    private void setListeners() {
        cancel.setOnClickListener(v -> {
            Dialog dialog = getDialog();
            if (dialog != null) {
                // TODO... 获得处理结果, 在这里添加你的处理逻辑
                ignoreCondition = true;
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(v -> {
            Dialog dialog = getDialog();
            if (dialog != null) {
                ignoreCondition = true;
                dialog.dismiss();

                // TODO... 获得处理结果, 在这里添加你的处理逻辑
                // if (mOnResult != null) {
                //     mOnResult.onDialogResults(Boolean.TRUE);
                // }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        boolean useSafeKeyboard = getArguments().getBoolean("use_safe");
        if (useSafeKeyboard) {
            safeKeyboard = new SafeKeyboard(view.getContext(), keyboardContainer, rootView, mScrollLayout);
            safeKeyboard.putEditText(safeEdit);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (safeKeyboard != null) {
            safeKeyboard.release();
            safeKeyboard = null;
        }
        super.onDestroyView();
    }

    public interface onDialogResult {
        // 这里的 Object 以后会使用泛型代替
        void onDialogResults(Object t);
    }
}
