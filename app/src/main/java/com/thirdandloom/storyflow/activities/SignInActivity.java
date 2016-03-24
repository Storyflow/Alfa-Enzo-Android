package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.Validation;
import com.thirdandloom.storyflow.views.dialog.ForgotPasswordDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends BaseActivity {
    private static final String USER_KEY = "user_key";
    private static final String PASSWORD_KEY = "password_key";

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

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.greyMostLightest;
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
        StoryflowApplication.restClient().signIn(userNameOrEmail, password, this::loginSuccess, this::showError);
    }

    private void resetPassword(String email) {
        showProgress(Gravity.RIGHT);
        StoryflowApplication.restClient().checkEmail(email, (user) -> {
            hideProgress();
            showWarning(R.string.email_was_successfully_sent);
        }, this::showError);
    }

    private void loginSuccess(User user) {
        hideProgress();
        setResult(RESULT_OK, getResultIntent(user));
        finish();
    }

    private Intent getResultIntent(User user) {
        Intent data = new Intent();
        data.putExtra(USER_KEY, user);
        data.putExtra(PASSWORD_KEY, passwordEditText.getText().toString());
        return data;
    }

    public static User extractUser(Intent data) {
        return (User) data.getExtras().getSerializable(USER_KEY);
    }

    @Nullable
    public static String extractPassword(Intent data) {
        return data.getExtras().getString(PASSWORD_KEY);
    }

    private void findViews() {
        loginEditText = (EditText)findViewById(R.id.activity_login_login_text_view);
        passwordEditText = (EditText)findViewById(R.id.activity_login_password_text_view);
        forgotPasswordView = findViewById(R.id.activity_login_forgot_password);
        continueButton = findViewById(R.id.activity_login_continue);
    }

}
