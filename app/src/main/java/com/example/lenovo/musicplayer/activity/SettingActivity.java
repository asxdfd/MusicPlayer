package com.example.lenovo.musicplayer.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lenovo.musicplayer.R;

import java.io.File;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ((Button) findViewById(R.id.btn_clear)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_exit)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_clear:
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
                if (file != null) {
                    file.delete();
                }
                Toast.makeText(SettingActivity.this, "缓存已清除", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_exit:
                System.exit(0);
                break;
        }
    }
}
