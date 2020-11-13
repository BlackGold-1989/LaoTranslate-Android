package com.laodev.translate.classes.gcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: Changemyminds.
 * Date: 2018/6/22.
 * Description:
 * Reference:
 */
public class VoiceList {
    private static final String TAG = VoiceList.class.getName();

    private List<IVoiceListener> mVoiceListeners = new ArrayList<>();

    public VoiceList() {
    }

    public void start() {
        new Thread(() -> {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Config.VOICES_ENDPOINT)
                    .addHeader(Config.API_KEY_HEADER, Config.API_KEY)
                    .build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    onSuccess(response.body().string());
                } else {
                    throw new IOException(String.valueOf(response.code()));
                }
            } catch (IOException IoEx) {
                IoEx.printStackTrace();
                onError(IoEx.getMessage());
            }
        }).start();
    }

    public void addVoiceListener(IVoiceListener voiceListener) {
        mVoiceListeners.add(voiceListener);
    }

    private void onSuccess(String text) {
        for (IVoiceListener voiceListener : mVoiceListeners) {
            voiceListener.onResponse(text);
        }
    }

    private void onError(String error) {
        for (IVoiceListener voiceListener : mVoiceListeners) {
            voiceListener.onFailure(error);
        }
    }

    public interface IVoiceListener {
        void onResponse(String text);
        void onFailure(String error);
    }
}
