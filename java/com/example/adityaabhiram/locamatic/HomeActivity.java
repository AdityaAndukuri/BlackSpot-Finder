package com.example.adityaabhiram.locamatic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.geofire.example.Example;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth firebaseAuth;
    NavigationView navigationView;
    FragmentManager fm;
    Fragment fg;
    MenuItem alarm_item;
    int alarm_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this,SelectUserActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fm=getSupportFragmentManager();
        if(fg==null){
            onNavigationItemSelected(navigationView.getMenu().getItem(4).setChecked(true));
            //SpotMapsFragment spotMapsFragment = new SpotMapsFragment();
            //FragmentManager manager = getSupportFragmentManager();

            fg=new SpotMapsFragment();
            fm.beginTransaction().replace(R.id.MainLayout,fg).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(),SelectUserActivity.class));
            return true;
        }
        else if (id == R.id.action_alarm) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notify) {
            // Handle the camera action
            Spots_list spotslist = new Spots_list();
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction().replace(R.id.MainLayout,spotslist).addToBackStack(null).commit();
        } else if (id == R.id.nav_res) {
               //  Intent in = new Intent(getApplicationContext(),CheckMaps.class);
                 //startActivity(in);

        } else if (id == R.id.nav_unres) {
            Minspotlist spotMapsFragment = new Minspotlist();
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction().replace(R.id.MainLayout,spotMapsFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_addspot) {
            SpotMapsFragment spotMapsFragment = new SpotMapsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStack();
            manager.beginTransaction().replace(R.id.MainLayout,spotMapsFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_edit) {
            Profile driving = new Profile();
            FragmentManager manager = getSupportFragmentManager();

            manager.popBackStack();
            manager.beginTransaction().replace(R.id.MainLayout,driving).addToBackStack(null).commit();

        } else if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(),SelectUserActivity.class));
        } else if(id==R.id.nav_drive){
            Driving driving = new Driving();
            FragmentManager manager = getSupportFragmentManager();

            manager.popBackStack();
            manager.beginTransaction().replace(R.id.MainLayout,driving).addToBackStack(null).commit();




        }
        else if(id==R.id.nav_progress){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Driving.REQUEST_LOCATION){
            driving.onActivityResult(requestCode, resultCode, data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/
}
