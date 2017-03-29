package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.HttpConnector;

public class CreateTeamActivity extends BaseActivity {

    TeamCreateTask mCreateTask;

    // UI references.
    private EditText mTeamView;
    private EditText mDescriptionView;
    private View mProgressView;
    private View mTeamFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_create_team);

        // Set up the login form.
        mTeamView = (EditText) findViewById(R.id.name);
        mDescriptionView = (EditText) findViewById(R.id.description);
        Button CreateTeamButton = (Button) findViewById(R.id.create_team_button);
        CreateTeamButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mTeamFormView = findViewById(R.id.team_form);
        mProgressView = findViewById(R.id.create_team_progress);
    }

    private void attemptRegister() {
        if (mCreateTask != null) {
            return;
        }
        // Reset errors.
        mTeamView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mTeamView.getText().toString();
        String description = mDescriptionView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(description)){
            description = "No Team Description.";
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            mTeamView.setError(getString(R.string.error_field_required));
            focusView = mTeamView;
            cancel = true;
        } else if (!isNameValid(phone)) {
            mTeamView.setError(getString(R.string.error_invalid_team_name));
            focusView = mTeamView;
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
            mCreateTask = new TeamCreateTask(phone, description);
            mCreateTask.execute((Void) null);
        }
    }

    private boolean isNameValid(String name) {
        return name.length() >= 5;
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mTeamFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private class TeamCreateTask extends AsyncTask<Void, Void, Boolean>{

        private final String mName;
        private final String mDescription;

        TeamCreateTask(String name, String description) {
            mName = name;
            mDescription = description;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Map<String, String> paras = new HashMap<String, String>();
            paras.put("gName", mName);
            paras.put("depict", mDescription);
            paras.put("avatar", "no avatar");
            HttpConnector.post("group/create", paras);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreateTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(CreateTeamActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                mTeamView.setError(getString(R.string.error_repeat_phone));
                mTeamView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateTask = null;
            showProgress(false);
        }
    }
}
