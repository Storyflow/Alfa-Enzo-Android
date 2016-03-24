package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.SpannableUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class SignUpActivity extends BaseActivity {

    private static final int SIGN_IN = 1;

    private View signUpView;
    private View haveAccountView;
    private TextView privacyTextView;

    public static Intent newInstance() {
        return new Intent(StoryflowApplication.getInstance(), SignUpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        findViews();
        initGui();
        initLaunchAnimation(savedInstanceState);
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.greyMostLightest;
    }

    private void findViews() {
        signUpView = findViewById(R.id.activity_signup_signup_text_view);
        haveAccountView = findViewById(R.id.activity_signup_already_have_account);
        privacyTextView = (TextView)findViewById(R.id.activity_signup_privacy_text_view);
    }

    private void initGui() {
        signUpView.setOnClickListener(v -> {
            showWarning("Attention please, this feature is under development!");
        });
        haveAccountView.setOnClickListener(v -> {
            startActivityForResult(SignInActivity.newInstance(), SIGN_IN);
        });
        initPrivacyPolicyTextView();
    }

    private void initPrivacyPolicyTextView() {
        String privacyPolicyFullString = getResources().getString(R.string.privacy_policy_full_text);
        String clickablePrivacyPolicyText = getResources().getString(R.string.privacy_policy_clickable_privacy_policy);
        String clickableTermsOfServiceText = getResources().getString(R.string.privacy_policy_clickable_terms_of_service);

        SpannableString ss = new SpannableString(privacyPolicyFullString);
        SpannableUtils.setOnClick(ss, onPrivacyPolicyClicked, clickablePrivacyPolicyText, privacyPolicyFullString);
        SpannableUtils.setOnClick(ss, onTermsOfServiceClicked, clickableTermsOfServiceText, privacyPolicyFullString);

        privacyTextView.setText(ss);
        privacyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        privacyTextView.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SIGN_IN:
                    storeUserData(SignInActivity.extractUser(data), SignInActivity.extractPassword(data));
                    startActivity(BrowseStoriesActivity.newInstance());
                    finish();
                    break;
            }
        }
    }

    private void storeUserData(User user, String password) {
        StoryflowApplication.account().updateProfile(user);
        StoryflowApplication.account().setPassword(password);
    }

    private final ClickableSpan onTermsOfServiceClicked = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
            showWarning("Attention please, this feature is under development!");
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(getResources().getColor(R.color.greyDark));
            ds.setUnderlineText(false);
        }
    };

    private final ClickableSpan onPrivacyPolicyClicked = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
            showWarning("Attention please, this feature is under development!");
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(getResources().getColor(R.color.greyDark));
            ds.setUnderlineText(false);
        }
    };

    private void initLaunchAnimation(Bundle savedInstanceState) {
        View launchView = findViewById(R.id.launch_layout);
        if (savedInstanceState == null) {
            View circleView = launchView.findViewById(R.id.launch_circle_view);
            ViewUtils.getMeasuredSize(findViewById(R.id.launch_text_view), (width, height) -> {
                ViewUtils.setViewFrame(circleView, height, height);
                AnimationUtils.applyStartAnimation(launchView , circleView);
            });
        } else {
            ViewUtils.removeFromParent(launchView);
        }
    }
}
