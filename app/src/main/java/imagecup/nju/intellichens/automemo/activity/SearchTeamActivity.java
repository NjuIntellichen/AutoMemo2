package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.HttpConnector;
import imagecup.nju.intellichens.automemo.util.User;

public class SearchTeamActivity extends BaseActivity implements View.OnClickListener {
    private TextView teamtag;
    private RowItem team;
    private Button button;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_search_team);

        teamtag = (TextView)findViewById(R.id.team_tag);
        button = (Button)findViewById(R.id.apply_button);
        button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(this);
        SearchView searchbar = (SearchView)findViewById(R.id.search_bar);
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchTeamActivity.this.setTeam(query);
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setTeam(String query){
        team = searchTeam(query);

        LinearLayout contentLl  = (LinearLayout) findViewById(R.id. ll_content );
        int num = contentLl.getChildCount();
        contentLl.removeViews(1, num - 1);
        if(team == null){
            teamtag.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
        }else{
            teamtag.setVisibility(View.INVISIBLE);
            button.setVisibility(View.VISIBLE);
            button.setText("Apply");
            button.setEnabled(true);
            createContentView(contentLl);
        }
    }

    private void createContentView(LinearLayout contentLayout) {
        View childView;
        Button keyTv;
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater. from (this);

        childView = layoutInflater.inflate(R.layout.row_my_team_leader ,  null);
        keyTv = (Button) childView.findViewById(R.id. tv_key );
        keyTv.setText(team.name);
        keyTv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SearchTeamActivity.this, TeamActivity.class);
                intent.putExtra("id", SearchTeamActivity.this.team.id);
                startActivity(intent);
            }
        });
        contentLayout.addView(childView);
    }

    private RowItem searchTeam(String query) {
        JSONObject result = (JSONObject) HttpConnector.get("group/search/" + query, null);
        RowItem tmp = null;
        try {
            if(result.getInt("res") == 1){
                JSONObject group = result.getJSONObject("group");
                tmp = new RowItem(group.getString("group_name"), group.getString("group_id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public void onClick(View v) {
        Map<String, String> map = new HashMap();
        map.put("gid", team.id);
        map.put("uid", User.getId());
        HttpConnector.post("group/apply", map);
        button.setText("Have Applied");
        button.setEnabled(false);
    }

    private class RowItem {
        String  name ;
        String  id ;

        RowItem(String name, String id) {
            super ();
            this . name  = name;
            this . id  = id;
        }
    }
}