package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.R;
import rx.functions.Action0;
import rx.functions.Action1;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.regex.Pattern;

public class Validation {
    private static final int PASSWORD_LENGTH_MIN = 8;
    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern userNamePattern = Pattern.compile("^[a-z0-9-_.]{3,15}$", Pattern.CASE_INSENSITIVE);

    public static void loginCredentials(@Nullable String emailOrUserName, @Nullable String password, Action1<Integer> failed, Action0 success) {
        emailOrUserName(emailOrUserName, failed, () -> password(password, failed, success));
    }

    private static void emailOrUserName(@Nullable String emailOrUserName, Action1<Integer> failed, Action0 success) {
        if (TextUtils.isEmpty(emailOrUserName)) {
            failed.call(R.string.email_or_username_cannot_be_empty);
        } else if (isValidName(emailOrUserName) || isValidEmail(emailOrUserName)) {
            success.call();
        } else  {
            failed.call(R.string.email_or_username_is_invalid);
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

    private static boolean isValidEmail(@NonNull String email) {
        return emailPattern.matcher(email).matches();
    }

    private static boolean isValidName(@NonNull String userName) {
        return userNamePattern.matcher(userName).matches();
    }
}
