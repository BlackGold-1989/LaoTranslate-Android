package com.laodev.translate.classes.GeneralClasses;

import android.os.AsyncTask;
import android.util.Log;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.laodev.translate.utils.Constants;
import com.laodev.translate.utils.FuncUtils;

public class GoogleCloudTranslateOut extends AsyncTask<String, Void, String> {

    private TranslateListener mLinstener;
    private String text;

    public GoogleCloudTranslateOut(String textToTranslate, TranslateListener listener) {
        text = textToTranslate;
        mLinstener = listener;
    }

    @Override
    protected String doInBackground(String... params){

        String translatedText = "", detectedLanguage = "";
        final String TARGET_LANGUAGE = FuncUtils.gLanguageOutputCls.text_out_lang_code;

        try {
            TranslateOptions options = TranslateOptions.newBuilder().setApiKey(Constants.getAPIKey()).build();
            Translate translate = options.getService();
            Detection detection = translate.detect(text);
            detectedLanguage = detection.getLanguage();
            if(detectedLanguage.toLowerCase().equals("lo")) detectedLanguage = "LAO";
            if(detectedLanguage.toLowerCase().equals("zh-cn")) detectedLanguage = "zh";

            if(!detectedLanguage.toLowerCase().equals(TARGET_LANGUAGE.toLowerCase())){
                Translation translation = translate.translate(text,
                        Translate.TranslateOption.sourceLanguage(detectedLanguage),
                        Translate.TranslateOption.targetLanguage(TARGET_LANGUAGE));
                translatedText = translation.getTranslatedText();

                mLinstener.onGetTranslatedText(translatedText, detectedLanguage, false);
            }else{
                translatedText = text;
                mLinstener.onGetTranslatedText(translatedText, detectedLanguage, false);
            }

            return translatedText;

        } catch (Exception e){
            translatedText = text;
            mLinstener.onGetTranslatedText(translatedText, detectedLanguage, false);
            return "";
        }
    }

    public interface TranslateListener {
        void onGetTranslatedText(String translatedText, String detectedLang, boolean result);
    }
}
