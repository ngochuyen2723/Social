package com.example.social.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.social.Fragments.ChatListFragment;
import com.example.social.Fragments.GroupChatsFragment;
import com.example.social.Fragments.HomeFragment;
import com.example.social.Fragments.NotificationsFragment;
import com.example.social.Fragments.ProfileFragment;
import com.example.social.Fragments.UsersFragment;
import com.example.social.Notifications.Token;

import com.example.social.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.firestore.auth.Token;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    //TextView mProfileTv;
    ActionBar actionBar;
    String myUID;
    SharedPreferences sp;
    private BottomNavigationView navigationView;
            //getSharedPreferences("SP_USER", MODE_PRIVATE);
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        editor = sp.edit();
        actionBar = getSupportActionBar();
        //actionBar.setTitle("Profile");
        firebaseAuth = FirebaseAuth.getInstance();
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        String nameft = getIntent().getStringExtra("nameft");
        if (nameft != null){
            navigationView.getMenu().getItem(3).setChecked(true);
            actionBar.setTitle("Group Chats");
            GroupChatsFragment fragmentgroups = new GroupChatsFragment();
            FragmentTransaction ftgroups = getSupportFragmentManager().beginTransaction();
            ftgroups.replace(R.id.content,fragmentgroups,"");
            ftgroups.commit();
        }
        else {
            navigationView.getMenu().getItem(0).setChecked(true);
            actionBar.setTitle("Home");
            HomeFragment fragmenthome = new HomeFragment();
            FragmentTransaction fthome = getSupportFragmentManager().beginTransaction();
            fthome.replace(R.id.content,fragmenthome,"");
            fthome.commit();
        }
        checkUserStatus();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
                            Token mToken = new Token(token);
                            reference.child(myUID).setValue(mToken);
                        }




                    }
                });
        //updateToken(FirebaseInstanceId.getInstance().getToken());

        //mProfileTv = findViewById(R.id.profileTv);
    }
    private  BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_home:
                    actionBar.setTitle("Home");
                    HomeFragment fragmenthome = new HomeFragment();
                    FragmentTransaction fthome = getSupportFragmentManager().beginTransaction();
                    fthome.replace(R.id.content,fragmenthome,"");
                    fthome.commit();
                    return true;
                case R.id.nav_profile:
                    actionBar.setTitle("Profile");
                    ProfileFragment fragmentprofile = new ProfileFragment();
                    FragmentTransaction ftprofile = getSupportFragmentManager().beginTransaction();
                    ftprofile.replace(R.id.content,fragmentprofile,"");
                    ftprofile.commit();
                    return true;
                case R.id.nav_users:
                    actionBar.setTitle("Users");
                    UsersFragment fragmentusers = new UsersFragment();
                    FragmentTransaction ftusers = getSupportFragmentManager().beginTransaction();
                    ftusers.replace(R.id.content,fragmentusers,"");
                    ftusers.commit();
                    return true;
                case R.id.nav_groups:
                   /* actionBar.setTitle("Chats");
                    ChatListFragment fragmentchats = new ChatListFragment();
                    FragmentTransaction ftchats = getSupportFragmentManager().beginTransaction();
                    ftchats.replace(R.id.content,fragmentchats,"");
                    ftchats.commit();*/
                    actionBar.setTitle("Group Chats");
                    GroupChatsFragment fragmentgroups = new GroupChatsFragment();
                    FragmentTransaction ftgroups = getSupportFragmentManager().beginTransaction();
                    ftgroups.replace(R.id.content,fragmentgroups,"");
                    ftgroups.commit();
                    return true;
                case R.id.nav_more:
                    showMoreOptions();
                    return true;
                /*case R.id.nav_groups:
                    actionBar.setTitle("Groups Chat");
                    GroupChatsFragment fragmentgroups = new GroupChatsFragment();
                    FragmentTransaction ftgroups = getSupportFragmentManager().beginTransaction();
                    ftgroups.replace(R.id.content,fragmentgroups,"");
                    ftgroups.commit();
                    return true;*/
                /*case R.id.nav_notification:
                    actionBar.setTitle("Notifications");
                    NotificationsFragment fragmentnotifications = new NotificationsFragment();
                    FragmentTransaction ftnotifications = getSupportFragmentManager().beginTransaction();
                    ftnotifications.replace(R.id.content,fragmentnotifications,"");
                    ftnotifications.commit();
                    return true;*/

            }
            return false;
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this, navigationView, Gravity.END);

        // items to show in menu
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Notifications");
        popupMenu.getMenu().add(Menu.NONE, 1, 0, "Chats");
        // menu cliks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0){
                    // notifications clicked

                    // users fragment transaction
                    /*actionBar.setTitle("Notifications");  // change actionbar title
                    NotificationsFragment fragment5 = new NotificationsFragment();
                    FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                    ft5.replace(R.id.content, fragment5, "");
                    ft5.commit();*/
                    actionBar.setTitle("Notifications");
                    NotificationsFragment fragmentnotifications = new NotificationsFragment();
                    FragmentTransaction ftnotifications = getSupportFragmentManager().beginTransaction();
                    ftnotifications.replace(R.id.content,fragmentnotifications,"");
                    ftnotifications.commit();

                } else if (id == 1){
                    // group chats clicked

                    // GroupChats fragment transaction
                 /*   actionBar.setTitle("Group Chats");  // change actionbar title
                    GroupChatsFragment fragment6 = new GroupChatsFragment();
                    FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
                    ft6.replace(R.id.content, fragment6, "");
                    ft6.commit();*/
                    /*actionBar.setTitle("Group Chats");
                    GroupChatsFragment fragmentgroups = new GroupChatsFragment();
                    FragmentTransaction ftgroups = getSupportFragmentManager().beginTransaction();
                    ftgroups.replace(R.id.content,fragmentgroups,"");
                    ftgroups.commit();*/
                    actionBar.setTitle("Chats");
                    ChatListFragment fragmentchats = new ChatListFragment();
                    FragmentTransaction ftchats = getSupportFragmentManager().beginTransaction();
                    ftchats.replace(R.id.content,fragmentchats,"");
                    ftchats.commit();

                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!=null){
            //mProfileTv.setText(user.getEmail());
            myUID = user.getUid();
           /* SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", myUID);
            editor.apply();*/
        }
        else{
            startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            editor.clear();
            editor.apply();
            finish();
        }
    }
    protected void onStart(){
        checkUserStatus();
        super.onStart();
    }
    /*public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }*/
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }
    public void updateToken (String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        reference.child(myUID).setValue(mToken);
    }
}