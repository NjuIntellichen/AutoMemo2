package imagecup.nju.intellichens.automemo.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.HttpConnector;

/**
 * A register screen that offers register via phone/password.
 */
public class RegisterActivity extends AppCompatActivity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mPhoneView;
    private EditText mPasswordView;
    private EditText mComfirmPasswordView;
    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the login form.
        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone);
        mPasswordView = (EditText) findViewById(R.id.password);
        mComfirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        Button RegisterButton = (Button) findViewById(R.id.register_button);
        RegisterButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);
        mComfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirm_password = mComfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_short_password));
            focusView = mPasswordView;
            cancel = true;
        }else if(!isPasswordValid(confirm_password)){
            mComfirmPasswordView.setError(getString(R.string.error_short_password));
            focusView = mComfirmPasswordView;
            cancel = true;
        }else if(!isPasswordValid(password, confirm_password)){
            mComfirmPasswordView.setError(getString(R.string.error_different_password));
            focusView = mComfirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!isPhoneValid(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(phone, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPhoneValid(String phone) {
        if(phone.length() != 11){
            return false;
        }
        for (int i = 0; i < phone.length(); i++){
            if(phone.charAt(i) < '0' || phone.charAt(i) > '9'){
                return false;
            }
        }
        return true;
    }

    private boolean isPasswordValid(String password){
        return !TextUtils.isEmpty(password) && password.length() > 5;
    }

    private boolean isPasswordValid(String password, String confirm_password) {
        return password.equals(confirm_password);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // The ViewPropertyAnimator APIs are not available, so simply show
        // and hide the relevant UI components.
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mPhone;
        private final String mPassword;

        UserRegisterTask(String phone, String password) {
            mPhone = phone;
            mPassword = password;
        }

        protected Boolean doInBackground(Void... params) {
            Map<String, String> paras = new HashMap<String, String>();
            paras.put("phone", mPhone);
            paras.put("pwd", mPassword);
            JSONObject result = (JSONObject) HttpConnector.post("register", paras);
            try {
                if(result == null && result.getInt("res") == -1){
                    return false;
                }
            } catch (JSONException e) {
                Log.e("Register Error", e.getMessage());
                return false;
            }
            Log.e("Register Error", "Already Registered");
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                mPhoneView.setError(getString(R.string.error_repeat_phone));
                mPhoneView.requestFocus();
            }
        }

        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}