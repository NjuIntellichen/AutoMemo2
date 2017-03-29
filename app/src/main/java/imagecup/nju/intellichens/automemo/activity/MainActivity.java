package imagecup.nju.intellichens.automemo.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.recorder.AudioRecorder;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_AND_RECORD_AUTDIO = 0;
    private AudioRecorder recorder;
    private Button record_button;
    private Button pause_button;
    private Button reset_button;
    private Button upload_button;
    private TextView notification;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_main);

        notification = (TextView) findViewById(R.id.record_text);

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
            Snackbar.make(record_button, R.string.permission_rationale_record, Snackbar.LENGTH_INDEFINITE)
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

    private void setButton(){
        recorder = AudioRecorder.getInstance();
        record_button = (Button) findViewById(R.id.record_button);
        record_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                beginRecord();
            }
        });
        pause_button = (Button) findViewById(R.id.pause_button);
        pause_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pauseRecord();
            }
        });
        reset_button = (Button) findViewById(R.id.reset_button);
        reset_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                resetRecord();
            }
        });
        upload_button = (Button) findViewById(R.id.analyze_button);
        upload_button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                analyzeRecord();
            }
        });
    }

    private void beginRecord(){
        if(recorder.getStatus() == AudioRecorder.Status.STATUS_NO_READY){
            recorder.createDefaultAudio();
            recorder.startRecord();
        }else{
            recorder.startRecord();
        }
        record_button.setVisibility(Button.INVISIBLE);
        pause_button.setVisibility(Button.VISIBLE);
        notification.setText("Be Recording.");
    }

    private void pauseRecord(){
        recorder.pauseRecord();
        pause_button.setVisibility(Button.INVISIBLE);
        record_button.setVisibility(Button.VISIBLE);
        reset_button.setVisibility(Button.VISIBLE);
        upload_button.setVisibility(Button.VISIBLE);
        notification.setText("Record Paused");
    }

    private void resetRecord(){
        recorder.cancel();
        reset_button.setVisibility(Button.INVISIBLE);
        upload_button.setVisibility(Button.INVISIBLE);
        notification.setText("Prepare to Record");
    }

    private  void analyzeRecord(){
        String id = recorder.analysis();
        Intent intent = new Intent(MainActivity.this, RecordInfoActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}