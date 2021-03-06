package imagecup.nju.intellichens.automemo.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.HttpConnector;

public class TeamActivity extends BaseActivity implements View.OnClickListener {
    private ExpandableListView expandableListView;

    private List<String> group_list;

    private List<List<RowItem>> item_list;

    private MyExpandableListViewAdapter adapter;

    private Button agree_button;

    private Button reject_button;

    private String gid;

    private String aid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setToolBar(R.layout.activity_team);
        this.setSampleData();

        Intent intent = getIntent();
        if(intent.hasExtra("id")){
            gid = intent.getStringExtra("id");
            Button button = (Button)findViewById(R.id.create_team_button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(TeamActivity.this, MainActivity.class);
                    intent.putExtra("team", TeamActivity.this.gid);
                    startActivity(intent);
                }
            });
        }
        agree_button = (Button)findViewById(R.id.agree_button);
        agree_button.setOnClickListener(this);
        reject_button = (Button)findViewById(R.id.reject_button);
        reject_button.setOnClickListener(this);
    }

    private void setSampleData(){
        group_list = new ArrayList<String>();
        group_list.add("Member");
        group_list.add("Record");

        List<RowItem> members = this.getTeamMembers(gid);
        List<RowItem> records = this.getTeamRecords(gid);
        List<RowItem> application = this.getTeamApplications(gid);
        item_list = new ArrayList<List<RowItem>>();
        item_list.add(members);
        item_list.add(records);
        if(application != null){
            group_list.add("Application");
            item_list.add(application);
        }

        expandableListView = (ExpandableListView)findViewById(R.id.expandlist);
        expandableListView.setGroupIndicator(null);

        // 监听组点击
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener(){
            @SuppressLint("NewApi")
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id){
                if (item_list.get(groupPosition).isEmpty()){
                    return true;
                }
                return false;
            }
        });

        // 监听每个分组里子控件的点击事件
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
                if(groupPosition == 0){
                    Intent intent = new Intent(TeamActivity.this, ProfileActivity.class);
                    intent.putExtra("id", item_list.get(groupPosition).get(childPosition).id);
                    startActivity(intent);
                }else if(groupPosition == 1){
                    Intent intent = new Intent(TeamActivity.this, RecordInfoActivity.class);
                    intent.putExtra("id", item_list.get(groupPosition).get(childPosition).id);
                    startActivity(intent);
                }else if(groupPosition == 2){
                    aid = item_list.get(groupPosition).get(childPosition).id;
                    agree_button.setVisibility(View.VISIBLE);
                    reject_button.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        adapter = new MyExpandableListViewAdapter(this);
        expandableListView.setAdapter(adapter);
    }

    public void onClick(View v) {
        Map<String, String> paras = new HashMap<>();
        paras.put("aid", aid);
        if(v.getId() == R.id.agree_button){
            paras.put("type", "1");
        }else if(v.getId() == R.id.reject_button){
            paras.put("type", "-1");
        }
        HttpConnector.post("group/doApply", paras);
        Intent intent = new Intent(TeamActivity.this, TeamActivity.class);
        intent.putExtra("id", gid);
        startActivity(intent);
    }

    private List<RowItem> getTeamMembers(String id){
        JSONArray array = (JSONArray) HttpConnector.get("group/get/user/" + id, null);
        List<RowItem> item_lt = new ArrayList<RowItem>();
        for(int i = 0; i < array.length(); i++){
            try {
                JSONObject obj = array.getJSONObject(i);
                item_lt.add(new RowItem(obj.getString("user_name"), obj.getString("uid")));
            } catch (JSONException e) {
            }
        }
        return item_lt;
    }

    private List<RowItem> getTeamRecords(String id){
        JSONArray array = (JSONArray) HttpConnector.get("record/group/" + id, null);
        List<RowItem> item_lt = new ArrayList<RowItem>();
        for(int i = 0; i < array.length(); i++){
            try {
                JSONObject obj = array.getJSONObject(i);
                item_lt.add(new RowItem(obj.getString("record_name"), obj.getString("rid"), obj.getString("time")));
            } catch (JSONException e) {
            }
        }
        return item_lt;
    }

    private List<RowItem> getTeamApplications(String id){
        JSONArray array = (JSONArray) HttpConnector.get("group/get/apply/" + id, null);
        List<RowItem> item_lt = new ArrayList<RowItem>();
        for(int i = 0; i < array.length(); i++){
            try {
                JSONObject obj = array.getJSONObject(i);
                item_lt.add(new RowItem(obj.getString("user_name"), obj.getString("aid")));
            } catch (JSONException e) {
            }
        }
        return item_lt;
    }

    class MyExpandableListViewAdapter extends BaseExpandableListAdapter{

        private Context context;

        MyExpandableListViewAdapter(Context context){
            this.context = context;
        }

        /**
         *
         * 获取组的个数
         *
         * @see android.widget.ExpandableListAdapter#getGroupCount()
         */
        @Override
        public int getGroupCount(){
            return group_list.size();
        }

        /**
         *
         * 获取指定组中的子元素个数
         *
         * @param groupPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
         */
        @Override
        public int getChildrenCount(int groupPosition){
            return item_list.get(groupPosition).size();
        }

        /**
         *
         * 获取指定组中的数据
         *
         * @param groupPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getGroup(int)
         */
        @Override
        public Object getGroup(int groupPosition){
            return group_list.get(groupPosition);
        }

        /**
         * 获取指定组中的指定子元素数据。
         * @param groupPosition
         * @param childPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getChild(int, int)
         */
        @Override
        public Object getChild(int groupPosition, int childPosition){
            return item_list.get(groupPosition).get(childPosition);
        }

        /**
         * 获取指定组的ID，这个组ID必须是唯一的
         * @param groupPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getGroupId(int)
         */
        public long getGroupId(int groupPosition){
            return groupPosition;
        }

        /**
         * 获取指定组中的指定子元素ID
         * @param groupPosition
         * @param childPosition
         * @return
         * @see android.widget.ExpandableListAdapter#getChildId(int, int)
         */
        public long getChildId(int groupPosition, int childPosition){
            return childPosition;
        }

        /**
         * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
         * @return
         * @see android.widget.ExpandableListAdapter#hasStableIds()
         */
        public boolean hasStableIds(){
            return true;
        }

        /**
         *
         * 获取显示指定组的视图对象
         *
         * @param groupPosition 组位置
         * @param isExpanded 该组是展开状态还是伸缩状态
         * @param convertView 重用已有的视图对象
         * @param parent 返回的视图对象始终依附于的视图组
         * @return
         * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View,
         *      android.view.ViewGroup)
         */
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
            GroupHolder groupHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.expandlist_group, null);
                groupHolder = new GroupHolder();
                groupHolder.txt = (TextView)convertView.findViewById(R.id.txt);
                groupHolder.count =  (TextView)convertView.findViewById(R.id.count);
                convertView.setTag(groupHolder);
            }else{
                groupHolder = (GroupHolder)convertView.getTag();
            }

            groupHolder.txt.setText(group_list.get(groupPosition));
            groupHolder.count.setText(Integer.toString(item_list.get(groupPosition).size()));
            return convertView;
        }

        /**
         *
         * 获取一个视图对象，显示指定组中的指定子元素数据。
         *
         * @param groupPosition 组位置
         * @param childPosition 子元素位置
         * @param isLastChild 子元素是否处于组中的最后一个
         * @param convertView 重用已有的视图(View)对象
         * @param parent 返回的视图(View)对象始终依附于的视图组
         * @return
         * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View,
         *      android.view.ViewGroup)
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            ItemHolder itemHolder;
            if (convertView == null){
                convertView = LayoutInflater.from(context).inflate(R.layout.expandlist_item, null);
                itemHolder = new ItemHolder();
                itemHolder.name = (TextView)convertView.findViewById(R.id.name);
                itemHolder.time = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(itemHolder);
            }
            else{
                itemHolder = (ItemHolder)convertView.getTag();
            }
            itemHolder.name.setText(item_list.get(groupPosition).get(childPosition).name);
            itemHolder.time.setText(item_list.get(groupPosition).get(childPosition).time);
            return convertView;
        }

        /**
         * 是否选中指定位置上的子元素。
         * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition){
            return true;
        }
    }

    private class GroupHolder{
        TextView txt;
        TextView count;
    }

    private class ItemHolder{
        TextView name;
        TextView time;
    }

    private class RowItem{
        String name;
        String id;
        String time;

        public RowItem(String name, String id) {
            this(name, id, "");
        }

        public RowItem(String name, String id, String time) {
            this.name = name;
            this.id = id;
            this.time = time;
        }
    }
}
