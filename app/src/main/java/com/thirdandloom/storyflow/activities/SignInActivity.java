package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.Validation;
import com.thirdandloom.storyflow.views.dialog.ForgotPasswordDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends BaseActivity {

    private EditText loginEditText;
    private EditText passwordEditText;
    private View forgotPasswordView;
    private View continueButton;

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.getInstance(), SignInActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        findViews();
        initGui();
    }

    private void initGui() {
        setToolBarTitle(R.string.sign_in);
        continueButton.setOnClickListener(v -> {
            String userNameOrEmail = loginEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            Validation.loginCredentials(userNameOrEmail, password, this::showWarning, () -> {
                signIn(userNameOrEmail, password);
            });
        });
        forgotPasswordView.setOnClickListener(v -> {
            ForgotPasswordDialog dialog = new ForgotPasswordDialog.Builder(this).onPositive((forgotPasswordDialog, which) -> {
                resetPassword(((ForgotPasswordDialog) forgotPasswordDialog).getEmail());
            }).build();
            dialog.show();
        });
    }

    private void signIn(String userNameOrEmail, String password) {
        showProgress(Gravity.RIGHT);
        StoryflowApplication.restClient().signIn(userNameOrEmail, password, (user) -> {
            hideProgress();
        }, this::showError);
    }

    private void resetPassword(String email) {
        showProgress(Gravity.RIGHT);
        StoryflowApplication.restClient().checkEmail(email, (user) -> {
            hideProgress();
            showWarning("Reset password success! Feature is under development.");
        }, this::showError);
    }

    private void findViews() {
        setToolBar((Toolbar)findViewById(R.id.toolbar));
        loginEditText = (EditText)findViewById(R.id.activity_login_login_text_view);
        passwordEditText = (EditText)findViewById(R.id.activity_login_password_text_view);
        forgotPasswordView = findViewById(R.id.activity_login_forgot_password);
        continueButton = findViewById(R.id.activity_login_continue);
    }

}
