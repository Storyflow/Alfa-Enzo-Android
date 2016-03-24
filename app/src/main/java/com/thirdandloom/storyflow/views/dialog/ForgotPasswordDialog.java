package com.thirdandloom.storyflow.views.dialog;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.SimpleTextWatcher;
import com.thirdandloom.storyflow.utils.Validation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class ForgotPasswordDialog extends MaterialDialog {
    private EditText emailEditText;

    protected ForgotPasswordDialog(Builder builder) {
        super(builder);
        View contentView = getCustomView();
        emailEditText = (EditText) contentView.findViewById(R.id.dialog_forgot_password_email_edit_text);
        updatePositiveButton();
        emailEditText.requestLayout();
        emailEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePositiveButton();
            }
        });
    }

    public String getEmail() {
        return emailEditText.getText().toString().trim();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void updatePositiveButton() {
        positiveButton.setEnabled(Validation.isValidEmail(getEmail()));
    }

    public static class Builder extends ConfirmationDialogBuilder {
        public Builder(@NonNull Activity activity) {
            super(activity);
            positiveText(R.string.reset);
            title(R.string.lets_reset_your_password);
            customView(R.layout.dialog_forgot_password, true);
        }

        public Builder onPositive(SingleButtonCallback callback) {
            super.onPositive(callback);
            return this;
        }

        @Override
        public ForgotPasswordDialog build() {
            return new ForgotPasswordDialog(this);
        }
    }
}
