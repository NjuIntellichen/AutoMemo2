package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import imagecup.nju.intellichens.automemo.R;

public class RecordInfoActivity extends BaseActivity implements View.OnClickListener{
    private Status status = Status.LOCK;
    private Button left_button;
    private Button right_button;
    private EditText record_info;
    private String text;

    private enum Status {
        //锁定
        LOCK,
        //编辑
        Edit,
        //撤销
        REVOKE,
        //提交
        SUBMIT
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_record_info);

        record_info = (EditText)findViewById(R.id.record_info);
        left_button = (Button)findViewById(R.id.left_button);
        right_button = (Button)findViewById(R.id.right_button);
        left_button.setOnClickListener(this);
        right_button.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v.getId() == R.id.left_button){
            if(status == Status.LOCK){
                status = Status.Edit;
                left_button.setText(R.string.action_record_save);
                right_button.setText(R.string.action_record_revoke);
                text = record_info.getText().toString();
                record_info.setEnabled(true);
            }else if(status == Status.Edit){
                status = Status.SUBMIT;
                left_button.setText(R.string.action_record_confirm);
                right_button.setText(R.string.action_record_cancel);
                record_info.setEnabled(false);
            }else if(status == Status.SUBMIT){
                status = Status.LOCK;
                left_button.setText(R.string.action_record_modify);
                right_button.setText(R.string.action_record_extend);
                text = record_info.getText().toString();
                //TODO 更新记录
            }else{
                status = Status.LOCK;
                left_button.setText(R.string.action_record_modify);
                right_button.setText(R.string.action_record_extend);
                record_info.setText(text);
            }
        }else if(v.getId() == R.id.right_button){
            if(status == Status.LOCK){
                //TODO continue record
                text = record_info.getText().toString();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                //TODO 分享
                intent.putExtra(Intent.EXTRA_SUBJECT, "title");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
            }else if(status == Status.Edit){
                status = Status.REVOKE;
                left_button.setText(R.string.action_record_confirm);
                right_button.setText(R.string.action_record_cancel);
                record_info.setEnabled(false);
            }else{
                status = Status.Edit;
                left_button.setText(R.string.action_record_save);
                right_button.setText(R.string.action_record_revoke);
                record_info.setEnabled(true);
            }
        }
    }
}
