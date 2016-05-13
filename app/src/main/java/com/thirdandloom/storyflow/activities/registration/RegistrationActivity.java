package com.thirdandloom.storyflow.activities.registration;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.activities.BaseActivity;
import com.thirdandloom.storyflow.utils.Validation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

public class RegistrationActivity extends BaseActivity {

    private EditText userNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.applicationContext, RegistrationActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        findViews();
        initGui();
    }

    private void findViews() {
        userNameEditText = (EditText) findViewById(R.id.activity_registration_choose_user_name);
        emailEditText = (EditText) findViewById(R.id.activity_registration_choose_email);
        passwordEditText = (EditText) findViewById(R.id.activity_registration_choose_password);
        findViewById(R.id.activity_registration_continue).setOnClickListener(v -> {
            Validation.signUpCredentials(userName(), email(), password(), this::showWarning, () -> {
                chooseAvatarAndName();
            });
        });
    }

    private void chooseAvatarAndName() {
        startActivity(ChooseAvatarAndNameActivity.newInstance(userName(), email(), password()));
    }

    private String userName() {
        return userNameEditText.getText().toString().trim();
    }

    private String email() {
        return emailEditText.getText().toString().trim();
    }

    private String password() {
        return passwordEditText.getText().toString();
    }

    private void initGui() {
        setTitle(R.string.sign_up);
    }

    @Override
    protected int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }
}
