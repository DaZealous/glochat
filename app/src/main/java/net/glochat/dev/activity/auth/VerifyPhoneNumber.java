package net.glochat.dev.activity.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.chaos.view.PinView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import net.glochat.dev.R;
import net.glochat.dev.activity.MainActivity;
import net.glochat.dev.base.BaseActivity;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;


public class VerifyPhoneNumber extends BaseActivity implements FirebaseAuth.AuthStateListener{
    private static final String TAG = VerifyPhoneNumber.class.getName();

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private static ProgressDialog progressDialog;

    private String mVerificationId;
    private boolean mVerificationInProgress = false;
  
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mPhoneNumberField;

    @BindView(R.id.sms_code_sub_header)
    TextView smsCodeSubHeader;
    @BindView(R.id.count_down_timer)
    TextView countDownTimerTextView;
    @BindView(R.id.resend_sms)
    Button resendSMS;
    @BindView(R.id.firstPinView)
    PinView smsCodeText;

    private Context context;
    private CountDownTimer countDownTimer;
    private long milliseconds = 60000;



    @Override
    protected void onCreate() {

        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra(PhoneAuthActivity.PHONE_NUMBER);
        setPhoneNumber(phoneNumber);
        smsCodeSubHeader.setText(String.format("%s %s", getResources().getString(R.string.sms_verify_sub_header), getmPhoneNumberField()));
        context = this;
        firebaseAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                mVerificationInProgress = false;
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                mVerificationInProgress = false;


                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Snackbar.make(smsCodeSubHeader.getRootView(), "Phone number cannot be verified", Snackbar.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {

                    Snackbar.make(smsCodeSubHeader.getRootView(), "Quota exceeded.", Snackbar.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

                Log.d(TAG, "onCodeSent:" + verificationId);

                mVerificationId = verificationId;
                mResendToken = token;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };

        startPhoneNumberVerification(getmPhoneNumberField());
        setCountDownTimerTextView(milliseconds);



        smsCodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    showProgressDialog(context, "Verifying");
                    verifyPhoneNumberWithCode(mVerificationId, s.toString());
                }
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected int setLayoutView() {
        return R.layout.activity_verify_auth;
    }

    @OnClick(R.id.resend_sms)
    void setResendSMS() {
        resendVerificationCode(getmPhoneNumberField(), mResendToken);
        resendSMS.setTextColor(getResources().getColor(R.color.grey));
        resendSMS.setEnabled(false);
        milliseconds *= 2;
        setCountDownTimerTextView(milliseconds);

    }

    private void setCountDownTimerTextView(long milliseconds) {
        countDownTimer = new CountDownTimer(milliseconds, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long timeCount = millisUntilFinished / 1000;

                countDownTimerTextView.setText(timeCount + " secs");

            }

            public void onFinish() {
                resendSMS.setTextColor(getResources().getColor(R.color.white, null));
                resendSMS.setEnabled(true);

            }
        }.start();
    }
    private void setPhoneNumber(String phoneNumber) {

        mPhoneNumberField = "+234" + phoneNumber.substring(1);
    }

    private String getmPhoneNumberField() {
        return mPhoneNumberField;
    }
    public static void showProgressDialog(Context context, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message + "...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    @OnClick(R.id.wrong_number)
    void setWrongNumber() {
        Intent intent = new Intent(this, PhoneAuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress) {
            startPhoneNumberVerification(getmPhoneNumberField());
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks);

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks, token);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {

            if (task.isComplete() && task.isSuccessful()) {
                System.out.println("Auth: Success Success Success");
                Log.d(TAG, "signInWithCredential:success");

                FirebaseUser user = task.getResult().getUser();
                countDownTimer.cancel();
                updateUI(STATE_SIGNIN_SUCCESS, user);
                Intent intent = new Intent(context, ProfileInfo.class);
                startActivity(intent);

            }
            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException && !task.isComplete()){

                hideProgressDialog();
                task.getException().printStackTrace();
                System.out.println("Auth: Failed Failed Failed");
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(VerifyPhoneNumber.this);
                alertDialog.setMessage("The code you entered is incorrect. Please try again")
                        .setPositiveButton("OK", (dialog, which) -> {
                            smsCodeText.setText("");
                            smsCodeText.requestFocus();
                        })
                        .show();

            }

            finishAndRemoveTask();
        });
    }


    private void signOut() {
        firebaseAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, firebaseAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        signOut();
    }
}
