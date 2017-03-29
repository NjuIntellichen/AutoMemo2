package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.HttpConnector;
import imagecup.nju.intellichens.automemo.util.User;

public class RecordActivity extends BaseActivity {
    private List<RowItem> records;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_record);

        String user = User.getId();
        records = searchUserRecord(user);
        LinearLayout contentLl  = (LinearLayout) findViewById(R.id. ll_content );
        createContentView(contentLl);
    }

    private void createContentView(LinearLayout contentLayout) {
        View childView;
        Button keyName;
        TextView keyTime;
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater. from (this);

        for  ( index = 0; index < records.size(); index++) {
            childView = layoutInflater.inflate(R.layout.row_record ,  null);
            keyTime = (TextView)childView.findViewById(R.id.tv_time);
            keyName = (Button) childView.findViewById(R.id. tv_name );
            keyTime.setText(records.get(index).time);
            keyName.setText(records.get(index).name);
            keyName.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(RecordActivity.this, BaseActivity.class);
                    intent.putExtra("id", RecordActivity.this.records.get(RecordActivity.this.index).id);
                    startActivity(intent);
                }
            });
            contentLayout.addView(childView);
        }
    }

    private List<RowItem> searchUserRecord(String id){
        JSONArray array = (JSONArray)HttpConnector.get("record/user/" + id, null);
        List<RowItem> tmp = new ArrayList<RowItem>();
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject obj = array.getJSONObject(i);
                tmp.add(new RowItem(obj.getString("record_name"), obj.getString("time"), obj.getString("rid")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tmp;
    }

    class RowItem{
        String name;
        String time;
        String id;

        public RowItem(String name, String time, String id) {
            this.name = name;
            this.time = time;
            this.id = id;
        }
    }
}
