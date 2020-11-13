package com.laodev.translate.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.laodev.translate.R;
import com.laodev.translate.utils.Constants;
import com.laodev.translate.utils.SharedPrefManager;

public class SettingsActivity extends AppCompatActivity {

    private Switch swch_admob;
    private LinearLayout llt_review, llt_comment, llt_privacy, llt_change_sound, llt_update;
    private TextView txt_about_us, txt_sound;
    private ImageView imgBack;

    private void initEvents() {

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llt_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appName = getPackageName();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
        });

        txt_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String call_number = "tel:" + Constants.getAboutUs();
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse(call_number));
//                startActivity(intent);

                String url = "http://www.kigabyte.la";
                Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                if (i.resolveActivity(getPackageManager()) == null) {
                    i.setData(Uri.parse(url));
                }
                startActivity(i);
            }
        });

        swch_admob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swch_admob.isChecked()){
                    SharedPrefManager.setSharedPrefAdEnable();

                }else{
                    SharedPrefManager.unSetSharedPrefAdEnable();
                }
            }
        });

        llt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, CommentActivity.class));
            }
        });

        llt_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://privacy.daxiao-itdev.com/txmelao/";
                Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                if (i.resolveActivity(getPackageManager()) == null) {
                    i.setData(Uri.parse(url));
                }
                startActivity(i);
            }
        });

        llt_change_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(SettingsActivity.this, txt_sound);
                for (int i = 0; i < Constants.laoSounds.size(); i++) {
                    String soundName = Constants.laoSounds.get(i);
                    popup.getMenu().add(0, i + 1, 1, menuIconWithText(getDrawable(R.drawable.ic_app_icon), soundName));

                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        txt_sound.setText(item.getTitle().toString());
                        SharedPrefManager.setSharedPrefLaoSound(item.getTitle().toString());
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, 0, 0);
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().hide();

        ProgressDialog progressDlg = ProgressDialog.show(this, "", "Please wait...");
        progressDlg.dismiss();

        initUIView();
        initEvents();
    }

    private void initUIView() {
        imgBack = findViewById(R.id.img_settings_back);

        llt_review = findViewById(R.id.llt_setting_review);

        TextView txt_app_name = findViewById(R.id.txt_settings_appname);
        TextView txt_db_version = findViewById(R.id.txt_settings_db_version);
        TextView txt_policy = findViewById(R.id.txt_settings_policy);
        txt_about_us = findViewById(R.id.txt_settings_contact);
        txt_sound = findViewById(R.id.txt_settings_sound);
        TextView txt_copyright = findViewById(R.id.txt_settings_copy_right);

        llt_comment = findViewById(R.id.llt_settings_comment);
        llt_privacy = findViewById(R.id.llt_settings_privacy);
        llt_change_sound = findViewById(R.id.llt_change_sound);
        llt_update = findViewById(R.id.llt_settings_update);

        swch_admob = findViewById(R.id.switch_advertise);
        if(SharedPrefManager.getSharedPrefAdEnable().equals("checked")){
            swch_admob.setChecked(true);
        }else{
            swch_admob.setChecked(false);
        }

        txt_app_name.setText(Constants.getOther());
        txt_db_version.setText(Constants.getVersion());
        txt_about_us.setText(Constants.getAboutUs());
        txt_copyright.setText(Constants.getCopyRight());

        txt_about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(txt_about_us.getText().toString()));
                startActivity(i);
            }
        });

        if(Constants.getVersion().equals(SharedPrefManager.getVersionNumber())){
            llt_update.setVisibility(View.GONE);
        }else{
            llt_update.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
