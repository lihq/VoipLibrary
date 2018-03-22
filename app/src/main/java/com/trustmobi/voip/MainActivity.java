package com.trustmobi.voip;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.trustmobi.voip.bean.DisplayInfo;
import com.trustmobi.voip.callback.DisplayCallback;
import com.trustmobi.voip.callback.NarrowCallback;

public class MainActivity extends AppCompatActivity {
    EditText edit_domain;
    EditText edit_username;
    EditText edit_pwd;
    EditText edit_to;
    private int REQUEST_CODE = 1000;
    EditText stun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_domain = findViewById(R.id.domain);
        edit_username = findViewById(R.id.username);
        edit_pwd = findViewById(R.id.password);
        edit_to = findViewById(R.id.to);
        stun = findViewById(R.id.stun);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有权限.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                    ) {
                //已经禁止提示了
                Toast.makeText(MainActivity.this, "您已禁止该权限，需要重新开启。", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CONTACTS
                        },
                        REQUEST_CODE);

            }

        } else {
            //可以了
        }

        VoipHelper.getInstance().setNarrowCallback(new NarrowCallback() {
            @Override
            public void openSystemWindow() {
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(MainActivity.this);
                localBuilder.setTitle("提示！");
                localBuilder.setIcon(R.mipmap.ic_launcher);
                localBuilder.setMessage("拨打电话中需要开启悬浮窗权限才可以在界面上显示悬浮窗");
                localBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        //确定
                        SettingsCompat.manageDrawOverlays(MainActivity.this);
                    }
                });
                localBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        //取消
                    }
                });

                /***
                 * 设置点击返回键不会消失
                 * */
                localBuilder.setCancelable(false).create();

                localBuilder.show();
            }
        });
    }

    public void openVoip(View view) {
        VoipHelper.getInstance().setDisplayCallback(new DisplayCallback() {
            @Override
            public DisplayInfo getDisplayInfo() {
                DisplayInfo displayInfo = new DisplayInfo();
                displayInfo.setNickName(edit_to.getText().toString().trim());
                displayInfo.setAvatar("http://img3.duitang.com/uploads/item/201505/23/20150523165343_M2dsr.jpeg");
                return displayInfo;
            }
        });
        VoipHelper.getInstance().setDebug(true);
        VoipHelper.getInstance()
                .setDomain(edit_domain.getText().toString().trim())
                .setStunServer(stun.getText().toString().trim())
                .setUserName(edit_username.getText().toString().trim())
                .setPassword(edit_pwd.getText().toString().trim())
                .startVoip(this);
    }

    public void closeVoip(View view) {
        VoipHelper.getInstance().stopVoip(this);
    }

    public void call(View view) {
        VoipHelper.getInstance().callAudio(this, edit_to.getText().toString().trim());
    }

    public void video(View view) {
        VoipHelper.getInstance().callVideo(this, edit_to.getText().toString().trim());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SettingsCompat.REQUEST_SYSTEM_ALERT_WINDOW) {
            if (SettingsCompat.canDrawOverlays(this)) {
                LinphoneService.instance().createNarrowView();
            } else {

            }
        }
    }

}
