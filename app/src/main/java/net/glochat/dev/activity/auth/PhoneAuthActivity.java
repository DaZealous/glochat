package net.glochat.dev.activity.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.hbb20.CountryCodePicker;

import net.glochat.dev.R;
import net.glochat.dev.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class PhoneAuthActivity extends BaseActivity {

    public static final String PHONE_NUMBER = "phone_auth";
    @BindView(R.id.countryCodePicker)
    CountryCodePicker countryCodePicker;
    @BindView(R.id.phone_number_editText)
    EditText phoneNumberField;
    @BindView(R.id.next_button)
    Button nextButton;



    @Override
    protected void onCreate() {
        countryCodePicker.registerCarrierNumberEditText(phoneNumberField);


        countryCodePicker.setPhoneNumberValidityChangeListener(isValidNumber -> {
            if (isValidNumber) {
                nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_gradient));
                nextButton.setEnabled(true);
            }
            else {
                nextButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_grey_gradient));
                nextButton.setEnabled(false);
            }
        });
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_phone_auth;
    }

    @OnClick(R.id.next_button)
    void validatePhoneNumber() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(String.format("%s%n%n%s%n%n%s%n", "we will be verifying the phone number:", getPhoneNumberField(), "Is this OK, or would you like to edit the number?"))
                    .setPositiveButton("Ok", (dialog, which) -> {
                        Intent intent = new Intent(this, VerifyPhoneNumber.class);
                        intent.putExtra(PHONE_NUMBER, getPhoneNumberField());
                        startActivity(intent);
                        finish();
                    })
                    .setNeutralButton("EDIT", (dialog, which) -> {

                    })
                    .show();

        }


    public String getPhoneNumberField() {
        return phoneNumberField.getText().toString();
    }
}