package de.domjos.myarchivemobile.custom;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.myarchivemobile.R;

public class CustomDatePickerField extends androidx.appcompat.widget.AppCompatEditText {
    private Context context;
    private String dateFormat, timeFormat;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private Calendar calendar;
    private boolean timePicker;

    public CustomDatePickerField(Context context) {
        super(context);

        this.setParams(context, null);
        this.initDialog();
        this.initActions();
    }

    public CustomDatePickerField(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setParams(context, attrs);
        this.initDialog();
        this.initActions();
    }

    public CustomDatePickerField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setParams(context, attrs);
        this.initDialog();
        this.initActions();
    }

    public void setTimePicker(boolean timePicker) {
        this.timePicker = timePicker;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public Date getDate() {
        try {
            if(this.getText() != null) {
                if(!this.getText().toString().trim().isEmpty()) {
                    if(this.timePicker) {
                        return ConvertHelper.convertStringToDate(this.getText().toString().trim(), this.dateFormat + " " + this.timeFormat);
                    } else {
                        return ConvertHelper.convertStringToDate(this.getText().toString().trim(), this.dateFormat);
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public void setDate(Date date) {
        try {
            if(date != null) {
                if(this.timePicker) {
                    this.setText(ConvertHelper.convertDateToString(date, this.dateFormat + " " + this.timeFormat));
                } else {
                    this.setText(ConvertHelper.convertDateToString(date, this.dateFormat));
                }
            }
        } catch (Exception ignored) {}
    }

    private void setParams(Context context, AttributeSet attrs) {
        this.context = context;

        this.dateFormat = "";
        this.timeFormat = "";

        if(attrs != null) {
            TypedArray array = this.context.obtainStyledAttributes(attrs, R.styleable.CustomDatePickerField);
            this.timePicker = array.getBoolean(R.styleable.CustomDatePickerField_showTime, false);
            this.dateFormat = array.getString(R.styleable.CustomDatePickerField_dateFormat);
            this.timeFormat = array.getString(R.styleable.CustomDatePickerField_timeFormat);
            array.recycle();
        }

        if(this.dateFormat != null) {
            if(this.dateFormat.trim().isEmpty()) {
                this.dateFormat = "yyyy-MM-dd";
            }
        } else {
            this.dateFormat = "yyyy-MM-dd";
        }
        if(this.timeFormat != null) {
            if(this.timeFormat.trim().isEmpty()) {
                this.timeFormat = "HH:mm:ss";
            }
        } else {
            this.timeFormat = "HH:mm:ss";
        }

        this.setInputType(InputType.TYPE_CLASS_DATETIME);
        this.setClickable(true);
        this.setKeyListener(DigitsKeyListener.getInstance("0123456789 .:-/"));
    }

    private void initDialog() {
        this.calendar = Calendar.getInstance();

        this.onTimeSetListener = (timePicker, i, i1) -> {
            this.calendar.set(Calendar.HOUR_OF_DAY, i);
            this.calendar.set(Calendar.MINUTE, i1);
            this.setText(ConvertHelper.convertDateToString(this.calendar.getTime(), this.dateFormat + " " + this.timeFormat));
        };

        this.onDateSetListener = (datePicker, i, i1, i2) -> {
            this.calendar.set(Calendar.YEAR, i);
            this.calendar.set(Calendar.MONTH, i1);
            this.calendar.set(Calendar.DAY_OF_MONTH, i2);
            this.setText(ConvertHelper.convertDateToString(this.calendar.getTime(), this.dateFormat));
            if(this.timePicker) {
                Calendar calendar = this.getDefault(this.dateFormat + " " + this.timeFormat);
                new TimePickerDialog(this.context, R.style.filePickerStyle, this.onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }
        };
    }

    private void initActions() {
        this.setOnClickListener(v -> {
            Calendar calendar = this.getDefault(this.dateFormat);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.context, R.style.filePickerStyle,
                this.onDateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private Calendar getDefault(String format) {
        Calendar calendar;
        try {
            Date dt = ConvertHelper.convertStringToDate(Objects.requireNonNull(this.getText()).toString().trim(), format);
            calendar = Calendar.getInstance();
            calendar.setTime(dt);
        } catch (Exception ex) {
            calendar = Calendar.getInstance();
        }
        return calendar;
    }
}
