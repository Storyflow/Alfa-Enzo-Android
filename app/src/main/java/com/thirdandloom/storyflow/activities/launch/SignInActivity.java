package com.thirdandloom.storyflow.activities.launch;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.activities.BaseActivity;
import com.thirdandloom.storyflow.activities.BrowseStoriesActivity;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.Validation;
import com.thirdandloom.storyflow.views.dialog.ForgotPasswordDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends BaseActivity {

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.applicationContext, SignInActivity.class);
    }

    private EditText loginEditText;
    private EditText passwordEditText;
    private View forgotPasswordView;
    private View continueButton;

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
    public int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    private void initGui() {
        setTitle(R.string.sign_in);
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
        StoryflowApplication.restClient().signIn(userNameOrEmail, password, this::loginSuccess, (errorMessage, type) -> {
            showError(errorMessage);
        });
    }

    private void resetPassword(String email) {
        showProgress(Gravity.RIGHT);
        StoryflowApplication.restClient().checkEmail(email, (user) -> {
            hideProgress();
            showWarning(R.string.email_was_successfully_sent);
        }, (errorMessage, type) -> {
            showError(errorMessage);
        });
    }

    private void loginSuccess(User user) {
        hideProgress();
        StoryflowApplication.account().updateProfile(user);
        StoryflowApplication.account().setPassword(passwordEditText.getText().toString());

        Intent browsingActivityIntent = BrowseStoriesActivity.newInstance(false);
        browsingActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(browsingActivityIntent);
    }

    private void findViews() {
        loginEditText = (EditText)findViewById(R.id.activity_login_login_text_view);
        passwordEditText = (EditText)findViewById(R.id.activity_login_password_text_view);
        forgotPasswordView = findViewById(R.id.activity_login_forgot_password);
        continueButton = findViewById(R.id.activity_login_continue);
    }

}
