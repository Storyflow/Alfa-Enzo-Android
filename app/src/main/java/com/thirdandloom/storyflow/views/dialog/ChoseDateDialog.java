package com.thirdandloom.storyflow.views.dialog;

import com.adobe.creativesdk.aviary.internal.utils.DateTimeUtils;
import com.afollestad.materialdialogs.MaterialDialog;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.DateUtils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import java.util.Calendar;

public class ChoseDateDialog extends MaterialDialog {

    private DatePicker datePicker;
    private int selectedYear;
    private int selectedMonthOfYear;
    private int selectedDayOfMonth;

    protected ChoseDateDialog(ChoseDateDialog.Builder builder) {
        super(builder);
        datePicker = (DatePicker)getCustomView().findViewById(R.id.date_picker);
        init(builder.startDateCalendar);
    }

    private void init(Calendar calendar) {
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonthOfYear = calendar.get(Calendar.MONTH);
        selectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(selectedYear, selectedMonthOfYear, selectedDayOfMonth, (view, year, monthOfYear, dayOfMonth) -> {
            selectedYear = year;
            selectedDayOfMonth = dayOfMonth;
            selectedMonthOfYear = monthOfYear;
        });
    }

    @NonNull
    public Calendar getPickedDateCalendar() {
        Calendar calendar = DateUtils.todayCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
        calendar.set(Calendar.MONTH, selectedMonthOfYear);
        calendar.set(Calendar.YEAR, selectedYear);
        return calendar;
    }

    public static class Builder extends ConfirmationDialogBuilder {
        private Calendar startDateCalendar;

        public Builder(@NonNull Activity activity) {
            super(activity);
            title(R.string.select_date);
            customView(R.layout.date_picker, true);
        }

        public Builder onPositive(MaterialDialog.SingleButtonCallback callback) {
            super.onPositive(callback);
            return this;
        }


        public Builder startCalendar(Calendar startDateCalendar) {
            this.startDateCalendar = startDateCalendar;
            return this;
        }

        @Override
        public ChoseDateDialog build() {
            return new ChoseDateDialog(this);
        }
    }
}
