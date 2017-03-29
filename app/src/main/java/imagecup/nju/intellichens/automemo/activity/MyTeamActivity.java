package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import imagecup.nju.intellichens.automemo.R;

public class MyTeamActivity extends BaseActivity{
    int index;
    List<RowItem> teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_my_team);

        //TODO get all teams of user as a leader
        teams  =  new ArrayList<RowItem>();
        teams .add(new RowItem("Hani", "111111"));
        teams .add(new RowItem("Faker", "222222"));
        LinearLayout contentLl  = (LinearLayout) findViewById(R.id. content_leader );

        createContentView (contentLl);

        //TODO get all teams of user as a member
        teams  =  new ArrayList<RowItem>();
        teams .add(new RowItem("JY", "111111"));
        teams .add(new RowItem("Moistor", "222222"));
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
