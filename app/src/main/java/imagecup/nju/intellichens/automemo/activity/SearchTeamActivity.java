package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import imagecup.nju.intellichens.automemo.R;

public class SearchTeamActivity extends BaseActivity {
    private TextView teamtag;
    private RowItem team;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_search_team);

        teamtag = (TextView)findViewById(R.id.team_tag);
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
        // TODO search teams from server
        team = searchTeam(query);

        LinearLayout contentLl  = (LinearLayout) findViewById(R.id. ll_content );
        int num = contentLl.getChildCount();
        contentLl.removeViews(1, num - 1);
        if(team == null){
            teamtag.setVisibility(View.VISIBLE);
        }else{
            teamtag.setVisibility(View.INVISIBLE);
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
        RowItem tmp = null;
        if(query.equals("111111")){
            tmp = new RowItem("aaaaaa", "111111");
        }
        return tmp;
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