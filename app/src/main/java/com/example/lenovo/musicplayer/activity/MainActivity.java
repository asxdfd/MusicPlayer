package com.example.lenovo.musicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.musicplayer.R;
import com.example.lenovo.musicplayer.fragment.HomeFragment;
import com.example.lenovo.musicplayer.fragment.LoginFragment;
import com.example.lenovo.musicplayer.fragment.MusicFragment;
import com.example.lenovo.musicplayer.fragment.RegisterFragment;
import com.example.lenovo.musicplayer.service.PlayService;
import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static BmobUser user = new BmobUser();
    private static boolean logout = false;
    protected PlayService mPlayService;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,
                    HomeFragment.getHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        startService(new Intent(this, PlayService.class));
        Bmob.initialize(this, "163858a36e4095d164e352750e1244c7");
        File path = new File(getCacheDir().getAbsolutePath());
        String[] fileList = path.list();
        String filename;
        File file = null;
        for (String aFileList : fileList) {
            filename = aFileList;
            if (filename.startsWith("user")) {
                file = new File(path, filename);
                break;
            }
        }
        if (file != null && !logout) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String userinfo = br.readLine();
                final String username = userinfo.split("\t")[0];
                final String password = userinfo.split("\t")[1];
                user.setUsername(username);
                user.setPassword(password);
                user.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if(e == null) {
                            Toast.makeText(MainActivity.this, bmobUser.getUsername() + "已登录", Toast.LENGTH_SHORT).show();
                            NavigationView navigationView = findViewById(R.id.nav_view);
                            navigationView.getMenu().getItem(1).setVisible(false);
                            navigationView.getMenu().getItem(2).setVisible(false);
                            navigationView.getMenu().getItem(3).setVisible(true);
                            TextView user = (TextView) findViewById(R.id.info_username);
                            user.setText(bmobUser.getUsername());
                            TextView email = (TextView) findViewById(R.id.info_email);
                            email.setText(bmobUser.getEmail());
                        } else {
                            Toast.makeText(MainActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,
                    HomeFragment.getHomeFragment()).commit();
        } else if (id == R.id.nav_login) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,
                    new LoginFragment()).commit();
        } else if (id == R.id.nav_register) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main,
                    new RegisterFragment()).commit();
        } else if (id == R.id.nav_logout) {
            logout = true;
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(1).setVisible(true);
            navigationView.getMenu().getItem(2).setVisible(true);
            navigationView.getMenu().getItem(3).setVisible(false);
            Toast.makeText(getApplicationContext(), "退出登录",
                    Toast.LENGTH_SHORT).show();
            TextView username = (TextView) findViewById(R.id.info_username);
            username.setText("未登录");
            TextView email = (TextView) findViewById(R.id.info_email);
            email.setText("");
            user.setUsername(null);
            user.setPassword(null);
            BmobUser.logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ServiceConnection mPlayServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.i(TAG, "play--->onServiceDisconnected");
            mPlayService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayService = ((PlayService.PlayBinder) service).getService();
            mPlayService.setOnMusicEventListener(mMusicEventListener);
            onChange(mPlayService.getPlayingPosition());
        }
    };

    private PlayService.OnMusicEventListener mMusicEventListener =
            new PlayService.OnMusicEventListener() {
                @Override
                public void onPublish(int progress) {
                    MainActivity.this.onPublish(progress);
                }

                @Override
                public void onChange(int position) {
                    MainActivity.this.onChange(position);
                }
            };

    public void allowBindService() {
        bindService(new Intent(this, PlayService.class),
                mPlayServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void allowUnbindService() {
        unbindService(mPlayServiceConnection);
    }

    public void onPublish(int progress) {

    }

    public void onChange(int position) {
        HomeFragment.getHomeFragment().getmMusicFragment().onPlay(position);
    }

    public PlayService getPlayService() {
        return mPlayService;
    }

    public static BmobUser getUser() {
        return user;
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PlayService.class));
        super.onDestroy();
    }

}
