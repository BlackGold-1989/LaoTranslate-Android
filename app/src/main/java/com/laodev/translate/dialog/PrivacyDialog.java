package com.laodev.translate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adjust.sdk.webbridge.AdjustBridge;
import com.laodev.translate.R;
import com.laodev.translate.utils.FuncUtils;

public class PrivacyDialog extends Dialog {

    private AppCompatActivity mContext;
    private LinearLayout llt_network;
    private WebView webview;
    private Button btn_cancel, btn_ok;

    private PrivacyDialogCallback dialogCallback;

    private void initEvent() {

        btn_ok.setOnClickListener(v -> {
            if(FuncUtils.connStatus){
                dialogCallback.onClockOk();
            }
        });

        btn_cancel.setOnClickListener(v -> dialogCallback.onClockCancel());
    }

    public PrivacyDialog(@NonNull AppCompatActivity context, PrivacyDialogCallback callback) {
        super(context);

        mContext = context;
        dialogCallback = callback;

        setContentView(R.layout.dialog_privacy);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setTitle(null);
        setCanceledOnTouchOutside(true);

        initUIDialog();
    }

    private void initUIDialog() {

        llt_network = findViewById(R.id.llt_not_connected);
        webview = findViewById(R.id.webview_privacy);

        btn_ok = findViewById(R.id.btn_dialog_ok);
        btn_cancel = findViewById(R.id.btn_dialog_cencel);

        if(FuncUtils.connStatus){
            llt_network.setVisibility(View.GONE);
            webview.setVisibility(View.VISIBLE);
            btn_ok.setBackgroundResource(R.drawable.blue_stroke_12);
        }else{
            llt_network.setVisibility(View.VISIBLE);
            webview.setVisibility(View.GONE);
            btn_ok.setBackgroundResource(R.drawable.grey_12);
        }

        initEvent();
    }

    public void setPrivacyLink(String url){

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new WebViewClient());

        AdjustBridge.registerAndGetInstance(mContext.getApplication(), webview);
        try {
            webview.loadUrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface PrivacyDialogCallback {
        void onClockOk();
        void onClockCancel();
    }

}
