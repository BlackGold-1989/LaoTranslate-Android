package com.laodev.translate.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.laodev.translate.R;
import com.laodev.translate.utils.FuncUtils;

public class PurchasaeActivity extends AppCompatActivity {

    private LinearLayout llt_contact, llt_request;

    private AlertDialog.Builder alertdlg;

    private TextView txt_deviceId, txtHideShow;
    private boolean enableShow = false;

    private void initEvents() {

        llt_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String call_number = "tel:" + Constants.getAboutUs();
                String call_number = "tel:" + "+8562096227257";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(call_number));
                startActivity(intent);
            }
        });

        llt_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FuncUtils.sendPurchaseRequest(PurchasaeActivity.this);
            }
        });

        txtHideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableShow = !enableShow;
                setShowEnable(enableShow);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        getSupportActionBar().hide();

        ProgressDialog progressDlg = ProgressDialog.show(this, "", "Please wait...");
        progressDlg.dismiss();

        initUIView();
        initEvents();
    }

    private void initUIView() {

        llt_contact = findViewById(R.id.llt_purchase_contact);
        llt_request = findViewById(R.id.llt_purchase_request);

        txt_deviceId = findViewById(R.id.txt_device_id);
        txtHideShow = findViewById(R.id.txt_hide);

        setShowEnable(enableShow);
    }

    private void setShowEnable(boolean b) {
        if(b){
            txtHideShow.setText("Hide");
            txt_deviceId.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        }else{
            txtHideShow.setText("Show");
            txt_deviceId.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void exitDialog() {

        if(alertdlg == null){
            alertdlg = new AlertDialog.Builder(this);
            alertdlg.setIcon(R.drawable.ic_launcher_round);
            alertdlg.setTitle(R.string.app_name);
            alertdlg.setMessage(R.string.string_message_exit);
            alertdlg.setPositiveButton(R.string.string_message_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });

            alertdlg.setNeutralButton(R.string.string_message_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

        alertdlg.show();
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }
}
