package com.laodev.translate.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.laodev.translate.MainActivity;
import com.laodev.translate.R;
import com.laodev.translate.utils.Constants;
import com.laodev.translate.utils.FuncUtils;
import com.laodev.translate.utils.SharedPrefManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private ProgressDialog progressDlg;

    private LinearLayout llt_back, llt_submit;

    private EditText edt_name, edt_email, edt_comment;

    private void initEvents() {

        llt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        llt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        getSupportActionBar().hide();

        progressDlg = ProgressDialog.show(this, "", "Please wait...");
        progressDlg.dismiss();

        initUIView();
        initEvents();
    }

    private void sendComment() {

        String device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, String> params = new HashMap<>();
        params.put("comment_user_id", device_id);
        params.put("comment_name", edt_name.getText().toString());
        params.put("comment_email", edt_email.getText().toString());
        params.put("comment_content", edt_comment.getText().toString());

        progressDlg.show();
        OkHttpUtils.post().url(Constants.sendCommentRequest())
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int i) {
                        progressDlg.dismiss();
                        FuncUtils.showToast(CommentActivity.this, getString(R.string.error_retry));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressDlg.dismiss();
                        onBackPressed();
                    }
                });
    }

    private void initUIView() {

        llt_back = findViewById(R.id.llt_comment_back);
        llt_submit = findViewById(R.id.llt_comment_submit);

        edt_name = findViewById(R.id.edt_commen_name);
        edt_email = findViewById(R.id.edt_commen_email);
        edt_comment = findViewById(R.id.edt_comment);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
