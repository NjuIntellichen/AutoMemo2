package imagecup.nju.intellichens.automemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import imagecup.nju.intellichens.automemo.R;
import imagecup.nju.intellichens.automemo.util.User;

abstract public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setToolBar(int viewId){
        if(!User.isLogined()){
            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        setContentView(viewId);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView)headerView.findViewById(R.id.user_name);
        name.setText(User.getName());
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.nav_profile) {
            intent = new Intent(BaseActivity.this, ProfileActivity.class);
        } else if (id == R.id.nav_team) {
            intent = new Intent(BaseActivity.this, MyTeamActivity.class);
        } else if (id == R.id.nav_record) {
            intent = new Intent(BaseActivity.this, RecordActivity.class);
        } else if (id == R.id.nav_create) {
            intent = new Intent(BaseActivity.this, CreateTeamActivity.class);
        } else if (id == R.id.nav_search) {
            intent = new Intent(BaseActivity.this, SearchTeamActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        startActivity(intent);
        return true;
    }
}