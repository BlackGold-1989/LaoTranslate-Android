package com.laodev.translate.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;

import com.laodev.translate.classes.GeneralClasses.AdvertiseCls;
import com.laodev.translate.classes.GeneralClasses.LanguageCls;
import com.laodev.translate.classes.GeneralClasses.TextCls;

import java.util.ArrayList;
import java.util.List;

import static android.provider.Settings.*;

public class Constants {

    private static String device_id = "";

    public static final int ENTRY_LANGUAGE = 0;
    public static final int OUTPUT_LANGUAGE = 1;
    public static final int OUTPUT_VOICE = 2;

    public static String APP_PATH = Environment.getExternalStorageDirectory() + "/RecognizeTextOCR/";

    private static final String base_url = "https://translate.laodev.info/";

    private static final String request_app_info = base_url + "Backend/trans_get_info";
    private static final String request_lao_sounds = base_url + "Backend/trans_get_lao";
    private static final String request_dic_data = base_url + "Backend/trans_get_dict_data";
    private static final String request_add_comment = base_url + "Backend/trans_add_comment";
    private static final String request_get_all_dic = base_url + "Backend/trans_get_all_dic";
    private static final String request_purchase = base_url + "Backend/trans_send_request";
    private static final String request_send_dic_data = base_url + "Backend/trans_update_dic";
    private static final String sound_link = base_url + "uploads/sounds/";
    private static final String connection_test = base_url + "Backend/trans_test_connection";

    private static final String path_root = "/TxMeLao/";

    private static final String path_flags = path_root + "Flags/";
    private static final String path_audios = path_root + "Audios/";
    private static final String path_texts = path_root + "Texts/";

    private static String api_key = "";
    private static String version = "";
    private static String about_us = "";
    private static String copy_right = "";
    private static String regdate = "";
    private static String other = "";

    public static Uri cropedImageUti;
    public static String capturedText = "";

    public static String string_translating = "Translating ...";
    public static String[] string_unknown = {
            "ບໍ່ ຮູ້",
            "알수 없는 단어",
            "Unknown",
            "未知",
            "わからない",
            "Неизвестно",
            "không xác định",
            "ไม่ ทราบ",
            "Inconnue",
            "Onbekend",
            "مجهول",
            "Sconosciuto"
    };

    private static String str_privacy = "http://privacy.laodev.info/txmelao/";
//    public static String privacy_check_default_string = "un_checked";

    public static String getPrivacyURL() {
        return str_privacy;
    }

    private static byte[] prefixData = {0, 0, 0, 28, 102, 116, 121, 112, 77, 52, 65, 32, 0, 0, 0, 0};

    public static List<Drawable> lst_menu_flags = new ArrayList<>();
    public static List<String> laoSounds = new ArrayList<>();
    private static List<LanguageCls> languageClses = new ArrayList<>();
    private static List<AdvertiseCls> advertiseClses = new ArrayList<>();

    public static String getDeviceId(){ return device_id;}

    public static String getAppInfoRequest() {
        return request_app_info;
    }

    public static String getLaoSoundRequest() { return request_lao_sounds;}

    public static String getDictionaryDataRequest() { return request_dic_data;}

    public static String sendDicDataRequest() { return request_send_dic_data;}

    public static String getAllDicDataRequest() {
        return request_get_all_dic;
    }

    public static String sendCommentRequest() {
        return request_add_comment;
    }

    public static String sendPurchaseRequest() { return request_purchase; }

    public static String getSoundServerLink(){ return sound_link; }

    public static String sendTestConnRequest(){ return connection_test; }

    public static String getRootFlagPath(){ return Environment.getExternalStorageDirectory().toString() + path_flags;}

    public static String getRootAudioPath(){return Environment.getExternalStorageDirectory().toString() + path_audios;}

    public static String getRootTextPath(){ return Environment.getExternalStorageDirectory().toString() + path_texts;}

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable GetImage(Context c, String imageName) {
        return c.getResources().getDrawable(c.getResources().getIdentifier(imageName, "drawable", c.getPackageName()));
    }

    public static String getVersion(){
        return version;
    }

    public static void setVersion(String _versin){ version = _versin;}

    public static String getAboutUs(){
        return about_us;
    }

    public static void setAboutUs(String _about_us){
        about_us = _about_us;
    }

    public static String getCopyRight(){
        return copy_right;
    }

    public static void setCopyRight(String _copy_right){
        copy_right = _copy_right;
    }

    public static String getAPIKey(){
        return api_key;
    }

    public static void setAPIKey(String _api_key){
        api_key = _api_key;
    }

    public static void setRegDate(String _regdate){
        regdate = _regdate;
    }

    public static void setLanguages(List<LanguageCls> _languages){
        languageClses.clear();
        languageClses.addAll(_languages);
    }

    public static LanguageCls getLanguages(int index){
        return languageClses.get(index);
    }

    public static List<LanguageCls> getLanguages(){ return languageClses;}

    public static int getLanguagesCount(){
        return languageClses.size();
    }

    public static void setAdvers(List<AdvertiseCls> _advers){
        advertiseClses.clear();
        advertiseClses.addAll(_advers);
    }

    public static byte[] getPrefixForAudio(){
        return prefixData;
    }

    public static AdvertiseCls getAdver(int index){
        return advertiseClses.get(index);
    }

    public static int getAdversCount(){
        return advertiseClses.size();
    }

    public static String getOther(){
        return other;
    }

    public static void setOther(String _other){
        other = _other;
    }
}
