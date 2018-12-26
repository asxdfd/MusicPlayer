package com.example.lenovo.musicplayer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lenovo.musicplayer.activity.MainActivity;
import com.example.lenovo.musicplayer.R;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private EditText mEtUserName = null;
    private EditText mEtPassWord = null;
    private EditText mEtEmail = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mEtUserName = (EditText) view.findViewById(R.id.register_username);
        mEtPassWord = (EditText) view.findViewById(R.id.register_pwd);
        mEtEmail = (EditText) view.findViewById(R.id.register_email);

        Button mBtn = (Button) view.findViewById(R.id.register);
        mBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        String username = mEtUserName.getText().toString();
        String password = mEtPassWord.getText().toString();
        String email = mEtEmail.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "信息不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        /**
         * Bmob注册
         */
        final BmobUser user = new BmobUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.signUp(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                    ((MainActivity) getActivity()).onNavigationItemSelected(
                            ((NavigationView) getActivity().findViewById(R.id.nav_view)).getMenu().getItem(0));
                } else {
                    Toast.makeText(getActivity(), "注册失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
