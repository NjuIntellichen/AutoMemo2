package imagecup.nju.intellichens.automemo.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.User;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_profile);

        //TODO
        createContentView (this.getUserInfo());
    }

    private List<RowItem> getUserInfo(){
        String id = User.getId();
        List<RowItem> mList  =  new ArrayList<RowItem>();
        mList .add( new  RowItem( "Nickname" ,  "Name" ));
        mList .add( new  RowItem( "Phone Number" ,  "13218882766" ));
        return  mList;
    }

    private void createContentView(List<RowItem> rowItemList) {
        LinearLayout contentLl  = (LinearLayout) findViewById(R.id.profile_content);
        View childView;
        TextView keyTv;
        TextView valueTv;
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater. from (this);
        for  ( int  i = 0; i < rowItemList.size(); i++) {
            childView = layoutInflater.inflate(R.layout.row_profile ,  null);
            keyTv = (TextView) childView.findViewById(R.id. tv_key );
            valueTv = (TextView) childView.findViewById(R.id. tv_value );
            keyTv.setText(rowItemList.get(i).key);
            valueTv.setText(rowItemList.get(i).value);
            contentLl.addView(childView);
        }
    }

    class RowItem {
        String  key ;
        String  value ;
        RowItem(String key, String value) {
            this . key  = key;
            this . value  = value;
        }
    }
}