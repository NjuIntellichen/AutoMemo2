package imagecup.nju.intellichens.automemo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.recorder.AudioRecorder;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnClickListener {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_AND_RECORD_AUTDIO = 0;
    private AudioRecorder recorder;
    private Button circle_button;
    private Button left_button;
    private Button right_button;
    private TextView notification;
    private View mProgressView;
    private View mAnalysisForm;
    private String team;
    private State state = State.NOT_READY;

    private AnalysisTask analysisTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_main);
        notification = (TextView) findViewById(R.id.record_text);
        mProgressView = findViewById(R.id.analysis_progress);
        mAnalysisForm = findViewById(R.id.analysis_form);

        Intent intent = getIntent();
        if(intent.hasExtra("team")){
            team = intent.getStringExtra("team");
            notification.setText("For " + team);
            state = State.READY;
        }else if(intent.hasExtra("history")){
            //TODO
            team = intent.getStringExtra("team");
            notification.setText("For " + team);
            state = State.READY;
        }else{
            notification.setText("Please select a team or history before recording");
            state = State.NOT_READY;
        }


        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if(requestPermission()){
                setButton();
            }
        }else{
            notification.setText("No SDK Card.");
        }
    }

    private boolean requestPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if(checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
            Snackbar.make(notification, R.string.permission_rationale_record, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_WRITE_EXTERNAL_STORAGE_AND_RECORD_AUTDIO);
                        }
                    });
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_WRITE_EXTERNAL_STORAGE_AND_RECORD_AUTDIO);
        }
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_AND_RECORD_AUTDIO) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setButton();
            }else{
                notification.setText("Permission Denied.");
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mAnalysisForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mAnalysisForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnalysisForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAnalysisForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setButton(){
        recorder = AudioRecorder.getInstance();
        circle_button = (Button) findViewById(R.id.record_button);
        circle_button.setOnClickListener(this);
        left_button = (Button) findViewById(R.id.left_button);
        left_button.setOnClickListener(this);
        right_button = (Button) findViewById(R.id.right_button);
        right_button.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.record_button){
            if(state == State.READY){
                circle_button.setText("Pause");
                left_button.setVisibility(View.INVISIBLE);
                right_button.setVisibility(View.INVISIBLE);
                beginRecord();
                state = State.RECORDING;
            }else if(state == State.RECORDING){
                circle_button.setText("Resume");
                left_button.setText("Analyze");
                right_button.setText("Abort");
                left_button.setVisibility(View.VISIBLE);
                right_button.setVisibility(View.VISIBLE);
                pauseRecord();
                state = State.PAUSE;
            }else if(state == State.PAUSE){
                circle_button.setText("Pause");
                left_button.setVisibility(View.INVISIBLE);
                right_button.setVisibility(View.INVISIBLE);
                beginRecord();
                state = State.RECORDING;
            }
        }else if(v.getId() == R.id.left_button){
            if(state == State.NOT_READY || state == State.READY){
                Intent intent = new Intent(MainActivity.this, MyTeamActivity.class);
                startActivity(intent);
            }else if(state == State.PAUSE){
                analyzeRecord();
            }
        }else{
            if(state == State.NOT_READY || state == State.READY){
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }else if(state == State.PAUSE){
                resetRecord();
                circle_button.setText("Record");
                left_button.setText("Team");
                right_button.setText("History");
                state = State.READY;
            }
        }
    }

    private void beginRecord(){
        if(recorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY){
            recorder.createDefaultAudio(team);
            recorder.startRecord();
        }else{
            recorder.startRecord();
        }
    }

    private void pauseRecord(){
        recorder.pauseRecord();
    }

    private void resetRecord(){
        recorder.cancel();
    }

    private  void analyzeRecord(){
        analysisTask = new AnalysisTask();
        analysisTask.execute((Void) null);
    }

    private enum State{
        NOT_READY,
        READY,
        RECORDING,
        PAUSE
    }

    public class AnalysisTask extends AsyncTask<Void, Void, Boolean> {
        private String record_id = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            record_id = recorder.analysis();
            if(record_id == null){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Intent intent = new Intent(MainActivity.this, RecordInfoActivity.class);
                intent.putExtra("id", record_id);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Fail to analyze the record", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            analysisTask = null;
//            showProgress(false);
        }
    }
}