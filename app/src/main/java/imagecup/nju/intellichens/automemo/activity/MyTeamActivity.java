package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.HttpConnector;
import imagecup.nju.intellichens.automemo.util.User;

public class MyTeamActivity extends BaseActivity{
    int index;
    List<RowItem> teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setToolBar(R.layout.activity_my_team);
        teams  =  new ArrayList<RowItem>();
        JSONArray array = (JSONArray) HttpConnector.get("group/get/my/" + User.getId(), null);
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject obj = array.getJSONObject(i);
                teams .add(new RowItem(obj.getString("group_name"), obj.getString("group_id")));
            } catch (JSONException e) {
            }
        }
        LinearLayout contentLl  = (LinearLayout) findViewById(R.id. content_leader );

        createContentView (contentLl);

        teams  =  new ArrayList<RowItem>();
        array = (JSONArray) HttpConnector.get("group/get/join/" + User.getId(), null);
        for (int i = 0; i < array.length(); i++){
            try {
                JSONObject obj = array.getJSONObject(i);
                teams .add(new RowItem(obj.getString("group_name"), obj.getString("group_id")));
            } catch (JSONException e) {
            }
        }
        contentLl  = (LinearLayout) findViewById(R.id. content_member );

        createContentView (contentLl);
    }

    private void createContentView(LinearLayout contentLayout) {
        View childView;
        Button keyTv;
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater. from (this);

        for  ( index = 0; index < teams.size(); index++) {
            childView = layoutInflater.inflate(R.layout.row_my_team_leader ,  null);
            keyTv = (Button) childView.findViewById(R.id. tv_key );
            keyTv.setText(teams.get(index).name);
            keyTv.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MyTeamActivity.this, TeamActivity.class);
                    intent.putExtra("id", MyTeamActivity.this.teams.get(MyTeamActivity.this.index).id);
                    startActivity(intent);
                }
            });
            contentLayout.addView(childView);
        }
    }

    class RowItem {
        String  name ;
        String  id ;

        RowItem(String name, String id) {
            super ();
            this . name  = name;
            this . id  = id;
        }
    }
}
