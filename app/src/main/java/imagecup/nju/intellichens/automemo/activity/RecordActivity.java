package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.User;

public class RecordActivity extends BaseActivity {
    private List<RowItem> records;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_record);

        Intent intent = getIntent();
        if(intent.hasExtra("team")){
            String team = intent.getStringExtra("team");
            records = searchTeamRecord(team);
        }else{
            String user = User.getId();
            records = searchUserRecord(user);
        }
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

    private List<RowItem> searchTeamRecord(String id){
        //TODO Team Record
        return null;
    }

    private List<RowItem> searchUserRecord(String id){
        List<RowItem> tmp = new ArrayList<RowItem>();
        tmp.add(new RowItem("123456", "2017.01.01 18:30:00", "123"));
        tmp.add(new RowItem("123456", "2017.01.01 18:30:00", "123"));
        tmp.add(new RowItem("123456", "2017.01.01 18:30:00", "123"));
        tmp.add(new RowItem("123456", "2017.01.01 18:30:00", "123"));
        //TODO User Record
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
