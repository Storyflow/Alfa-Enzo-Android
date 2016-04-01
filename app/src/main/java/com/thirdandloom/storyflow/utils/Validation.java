package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import org.w3c.dom.Text;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.regex.Pattern;

public class Validation {
    private static final int PASSWORD_LENGTH_MIN = 8;
    private static final Pattern emailPattern = android.util.Patterns.EMAIL_ADDRESS;
    private static final Pattern userNamePattern = Pattern.compile("^[a-z0-9-_.]{3,15}$");

    public static void splitNameCredentials(@Nullable String splitName, Action1<String> failed, Action2<String, String> success) {
        if (TextUtils.isEmpty(splitName)) {
            failed.call(StoryflowApplication.resources().getString(R.string.user_name_cannot_be_empty));
        } else {
            String[] splitString = splitName.trim().split("\\s+");
            if (splitString.length != 2) {
                failed.call(StoryflowApplication.resources().getString(R.string.name_should_contain_first_last_name_separated_by_space));
            } else {
                success.call(splitString[0], splitString[1]);
            }
        }
    }

    public static void signUpCredentials(@Nullable String userName, @Nullable String email, @Nullable String password, Action1<String> failed, Action0 success) {
        userName(userName, userNameErrorMessage -> {
            email(email, userNameErrorMessage, emailErrorMessage -> {
                password(password, emailErrorMessage, finalErrorMessage -> {
                    if (TextUtils.isEmpty(finalErrorMessage)) {
                        success.call();
                    } else {
                        failed.call(finalErrorMessage);
                    }
                });
            });
        });
    }

    public static void loginCredentials(@Nullable String emailOrUserName, @Nullable String password, Action1<Integer> failed, Action0 success) {
        emailOrUserName(emailOrUserName, failed, () -> password(password, failed, success));
    }

    private static void password(@Nullable String password, String errorMessage, Action1<String> next) {
        password(password, messageResId -> {
            next.call(errorMessage + StoryflowApplication.resources().getString(messageResId));
        }, () -> {
            next.call(errorMessage);
        });
    }

    private static void email(@Nullable String email, String errorMessage, Action1<String> next) {
        if (TextUtils.isEmpty(email)) {
            next.call(errorMessage + StoryflowApplication.resources().getString(R.string.email_cannot_be_empty));
        } else if (!isValidEmail(email)) {
            next.call(errorMessage + StoryflowApplication.resources().getString(R.string.invalid_email));
        } else {
            next.call(errorMessage);
        }
    }

    private static void userName(@Nullable String userName, Action1<String> next) {
        if (TextUtils.isEmpty(userName)) {
            next.call(StoryflowApplication.resources().getString(R.string.user_name_cannot_be_empty));
        } else if (!isValidName(userName)) {
            next.call(StoryflowApplication.resources().getString(R.string.user_name_contains_incorrect_symbols_and_must_be_in_lowercase));
        } else {
            next.call(StringUtils.EMPTY);
        }
    }

    private static void emailOrUserName(@Nullable String emailOrUserName, Action1<Integer> failed, Action0 success) {
        if (TextUtils.isEmpty(emailOrUserName)) {
            failed.call(R.string.email_or_username_cannot_be_empty);
        } else if (!(isValidName(emailOrUserName) || isValidEmail(emailOrUserName))) {
            failed.call(R.string.email_or_username_is_invalid);
        } else {
            success.call();
        }
    }

    private static void password(@Nullable String password, Action1<Integer> failed, Action0 success) {
        if (TextUtils.isEmpty(password)) {
            failed.call(R.string.password_cannot_be_empty);
        } else if (password.length() < PASSWORD_LENGTH_MIN) {
            failed.call(R.string.password_is_invalid);
        } else {
            success.call();
        }
    }

    public static boolean isValidEmail(@NonNull String email) {
        return emailPattern.matcher(email).matches();
    }

    private static boolean isValidName(@NonNull String userName) {
        return userNamePattern.matcher(userName).matches();
    }
}
