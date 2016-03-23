package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        setContentView(R.layout.activity_login);

        findViews();
        initGui();
    }

    private void initGui() {
        setToolBarTitle(R.string.sign_in);
        continueButton.setOnClickListener(v -> {
            showWarning("Attention please, this feature is under development!");
        });
        forgotPasswordView.setOnClickListener(v -> {
            showWarning("Attention please, this feature is under development!");
        });
    }

    private void findViews() {
        setToolBar((Toolbar)findViewById(R.id.toolbar));
        loginEditText = (EditText)findViewById(R.id.activity_login_login_text_view);
        passwordEditText = (EditText)findViewById(R.id.activity_login_password_text_view);
        forgotPasswordView = findViewById(R.id.activity_login_forgot_password);
        continueButton = findViewById(R.id.activity_login_continue);
    }

}
