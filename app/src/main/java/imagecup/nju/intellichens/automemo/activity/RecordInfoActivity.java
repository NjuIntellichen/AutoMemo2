package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.HttpConnector;

public class RecordInfoActivity extends BaseActivity implements View.OnClickListener{
    private Status status = Status.LOCK;
    private Button left_button;
    private Button right_button;
    private EditText record_title;
    private EditText record_tag;
    private EditText record_info;
    private String title;
    private String tag;
    private String info;
    private String id;

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
        record_title = (EditText)findViewById(R.id.record_title);
        record_tag = (EditText)findViewById(R.id.record_tag);

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            id = intent.getStringExtra("id");
            JSONObject record = (JSONObject)HttpConnector.get("record/" + id, null);
            try {
                record_title.setText(record.getString("record_name"));
                record_info.setText(record.getString("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray tags = (JSONArray)HttpConnector.get("record/" + id + "/tags", null);
            String tag = "";
            for(int i = 0; i < tags.length(); i++){
                try {
                    tag += (tags.getJSONObject(i).getString("tag_name") + ", ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            record_tag.setText(tag);
        }

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
                title = record_title.getText().toString();
                tag = record_tag.getText().toString();
                info = record_info.getText().toString();
                record_title.setEnabled(true);
                record_tag.setEnabled(true);
                record_info.setEnabled(true);
            }else if(status == Status.Edit){
                status = Status.SUBMIT;
                left_button.setText(R.string.action_record_confirm);
                right_button.setText(R.string.action_record_cancel);
                record_title.setEnabled(false);
                record_tag.setEnabled(false);
                record_info.setEnabled(false);
            }else if(status == Status.SUBMIT){
                status = Status.LOCK;
                left_button.setText(R.string.action_record_modify);
                right_button.setText(R.string.action_record_extend);
                title = record_title.getText().toString();
                tag = record_tag.getText().toString();
                info = record_info.getText().toString();
                Map<String, String> paras = new HashMap<>();
                paras.put("text", info);
                paras.put("title", title);
                HttpConnector.post("record/" + id + "/update", paras);
                paras = new HashMap<>();
                paras.put("tags", info.replaceAll(", ", "#"));
                HttpConnector.post("record" + id + "/tags", paras);
                Toast.makeText(this, "Record Update", Toast.LENGTH_SHORT).show();
            }else{
                status = Status.LOCK;
                left_button.setText(R.string.action_record_modify);
                right_button.setText(R.string.action_record_extend);
                record_title.setText(title);
                record_tag.setText(tag);
                record_info.setText(info);
            }
        }else if(v.getId() == R.id.right_button){
            if(status == Status.LOCK){
                Intent intent = new Intent(RecordInfoActivity.this, MainActivity.class);
                intent.putExtra("history", RecordInfoActivity.this.id);
                startActivity(intent);
            }else if(status == Status.Edit){
                status = Status.REVOKE;
                left_button.setText(R.string.action_record_confirm);
                right_button.setText(R.string.action_record_cancel);
                record_title.setEnabled(false);
                record_tag.setEnabled(false);
                record_info.setEnabled(false);
            }else{
                status = Status.Edit;
                left_button.setText(R.string.action_record_save);
                right_button.setText(R.string.action_record_revoke);
                record_title.setEnabled(true);
                record_tag.setEnabled(true);
                record_info.setEnabled(true);
            }
        }
    }
}
