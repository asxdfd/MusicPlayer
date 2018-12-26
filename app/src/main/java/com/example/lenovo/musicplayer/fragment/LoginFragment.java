package com.example.lenovo.musicplayer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.musicplayer.activity.MainActivity;
import com.example.lenovo.musicplayer.R;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginFragment extends Fragment implements View.OnClickListener{
    private EditText mEtUserName = null;
    private EditText mEtPassWord = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEtUserName = (EditText) view.findViewById(R.id.login_username);
        mEtPassWord = (EditText) view.findViewById(R.id.login_pwd);

        Button mBtn = (Button) view.findViewById(R.id.btn_login);
        mBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        final String username = mEtUserName.getText().toString();
        final String password = mEtPassWord.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getActivity(), "用户名密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        BmobUser user = MainActivity.getUser();
        user.setUsername(username);
        user.setPassword(password);
        user.login(new SaveListener<BmobUser>(){
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if(e == null) {
                    Toast.makeText(getActivity(),bmobUser.getUsername() + "已登录", Toast.LENGTH_SHORT).show();
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null) {
                        NavigationView navigationView = activity.findViewById(R.id.nav_view);
                        navigationView.getMenu().getItem(1).setVisible(false);
                        navigationView.getMenu().getItem(2).setVisible(false);
                        navigationView.getMenu().getItem(3).setVisible(true);
                        TextView user = (TextView) activity.findViewById(R.id.info_username);
                        user.setText(bmobUser.getUsername());
                        TextView email = (TextView) activity.findViewById(R.id.info_email);
                        email.setText(bmobUser.getEmail());
                        activity.onNavigationItemSelected(navigationView.getMenu().getItem(0));
                        setCache(username, password);
                    }
                } else {
                    Toast.makeText(getActivity(),"登录失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setCache(String username, String password) {
        MainActivity activity = (MainActivity) getActivity();
        File file = new File(activity.getCacheDir().getAbsolutePath() + "/user" + System.currentTimeMillis() + ".cac");
        System.out.println(file.getAbsolutePath());
        System.out.println(Arrays.toString(activity.getCacheDir().list()));
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(username + "\t" + password);
            fw.flush();
            fw.close();
        } catch (Exception e) {
            Log.d(MainActivity.class.getSimpleName(), "write " + file.getAbsolutePath() + " data failed!");
            e.printStackTrace();
        }
    }
}
