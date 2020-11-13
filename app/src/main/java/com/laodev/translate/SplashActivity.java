package com.laodev.translate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.laodev.translate.utils.FuncUtils;
import com.laodev.translate.utils.PermissionsUtil;
import com.laodev.translate.utils.SharedPrefManager;
import com.laodev.translate.views.PurchasaeActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 451;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();

        new SharedPrefManager(SplashActivity.this);

        if (PermissionsUtil.hasPermissions(this)) {
            loadData();
        } else {
            ActivityCompat.requestPermissions(this, PermissionsUtil.permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @SuppressLint("HardwareIds")
    private void loadData(){
        if(SharedPrefManager.getDeviceId().equals("")){
            FuncUtils.device_id = Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPrefManager.setDeviceId(FuncUtils.device_id);
        }else{
            FuncUtils.device_id = SharedPrefManager.getDeviceId();
        }

        FuncUtils.getConnStatusFromServer(result -> {
            FuncUtils.connStatus = result;
            if(FuncUtils.connStatus){
                FuncUtils.loadDataWithOnline(SplashActivity.this, new FuncUtils.LoadAppInfoErrorCallback() {
                    @Override
                    public void onSuccess() {
                        gotoMain();
                    }

                    @Override
                    public void onExpired() {
                        gotoPurchaseRequest();
                    }
                });
            }else {
                FuncUtils.setAppDataWithOffline(SplashActivity.this, new FuncUtils.LoadAppInfoErrorCallback() {
                    @Override
                    public void onSuccess() {
                        gotoMain();
                    }

                    @Override
                    public void onExpired() {
                        gotoPurchaseRequest();
                    }
                });
            }
        });
    }

    private void gotoPurchaseRequest() {
        startActivity(new Intent(SplashActivity.this, PurchasaeActivity.class));
        finish();
    }

    public void gotoMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionsUtil.permissionsGranted(grantResults)) {
            loadData();
        } else {
            ActivityCompat.requestPermissions(this, PermissionsUtil.permissions, PERMISSION_REQUEST_CODE);
        }
    }

}
