package com.laodev.translate.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.laodev.translate.MainActivity;
import com.laodev.translate.R;
import com.laodev.translate.SQLite.DBManager;
import com.laodev.translate.classes.GeneralClasses.AdvertiseCls;
import com.laodev.translate.classes.GeneralClasses.ColumnCls;
import com.laodev.translate.classes.GeneralClasses.LanguageCls;
import com.laodev.translate.views.PurchasaeActivity;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FuncUtils {

    static String TAG = "ProcessImage";

    public static boolean connStatus = false;
    public static String device_id = "";
    public static boolean expired = false;

    private static String u_laosounds = "";
    private static String u_appinfos = "";
    private static String u_languages = "";
    private static String u_advers = "";
    private static String u_lastupdated = "";

    public static LanguageCls gLanguageInputCls = new LanguageCls();
    public static LanguageCls gLanguageOutputCls = new LanguageCls();

    public static boolean checkNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else return false;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void showToast(Context context, String text) {
        DynamicToast.make(context, text, context.getResources().getDrawable(R.drawable.ic_alert), context.getResources().getColor(R.color.colorWhite), context.getResources().getColor(R.color.colorBlue), Toast.LENGTH_LONG).show();
    }

    public static boolean isInternetWorking(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()){
                    return true;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()){
                    return true;
                }
        }
        return false;
    }

    public static void getConnStatusFromServer(OnTestConnCallBack callback) {
        Map<String, String> params = new HashMap<>();
        params.put("test_conn", "test_conn");

        OkHttpUtils.post().url(Constants.sendTestConnRequest())
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int i) {
                        callback.onSuccess(false);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            String resp = new JSONObject(response).getString("result");
                            if(resp.equals("test_conn")){
                                callback.onSuccess(true);
                            }else {
                                callback.onSuccess(false);
                            }
                        } catch (JSONException e) {
                            callback.onSuccess(false);
                        }
                    }
                });
    }

    public static void onBindAudio(String fName, OnBindAudioCallback callback){
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(fName));

            byte[] data = new byte[fileInputStream.available()];
            fileInputStream.read(data);
            fileInputStream.close();

            byte[] audio = new byte[Constants.getPrefixForAudio().length + data.length];
            for (int i = 0; i < audio.length; ++i){
                audio[i] = i < Constants.getPrefixForAudio().length ? Constants.getPrefixForAudio()[i] : data[i - Constants.getPrefixForAudio().length];
            }
            callback.onSuccess(audio);
        } catch (IOException e) {
            callback.onError(e.getMessage());
        }
    }

    public static void createPopUpMenu(PopupMenu popup, int category, OnCLickPopUpItem clicker) {
        int init = 0;
        for (LanguageCls language: Constants.getLanguages()) {
            popup.getMenu().add(0, init + 1, 1,
                    FuncUtils.menuIconWithText(Constants.lst_menu_flags.get(init), language.language_title));
            init++;
        }
        popup.setOnMenuItemClickListener(item -> {
            int ind = 0;
            for(LanguageCls language: Constants.getLanguages()){
                if(item.getTitle().toString().trim().equals(language.language_title)){
                    clicker.onClickPopUpItem(category, ind);
                    break;
                } ind++;
            }
            return true;
        });
        popup.dismiss();
    }

    public static CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, 40, 40);
        SpannableString sb = new SpannableString("    " + title);
        sb.setSpan(new ImageSpan(r, ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public static void sendDicData(String[] dic_data, SendDicDataCallback callback) {

        Map<String, String> params = new HashMap<>();
        params.put("source", dic_data[0]);
        params.put("sourceKey", dic_data[1]);
        params.put("translation", dic_data[2]);
        params.put("translationKey", dic_data[3]);

        OkHttpUtils.post().url(Constants.sendDicDataRequest())
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int i) {
                        callback.onError(e.getMessage());
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        callback.onSuccess();
                    }
                });
    }

    public static void loadDataWithOnline(Context context, LoadAppInfoErrorCallback callback) {

        OkHttpUtils.post().url(Constants.getLaoSoundRequest())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int i) {
                        FuncUtils.connStatus = false;
                        setAppDataWithOffline(context, callback);
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONArray lao_sounds = new JSONObject(response).getJSONArray("result");
                            u_laosounds = lao_sounds.toString();
                            Constants.laoSounds.clear();
                            for (int i = 0; i < lao_sounds.length(); i++) {
                                try {
                                    Constants.laoSounds.add(lao_sounds.getJSONObject(i).getString("name"));
                                    if(i == lao_sounds.length()-1){
                                        loadAppInfoFromServer(context, callback);
                                    }
                                } catch (JSONException e) {
                                    setAppDataWithOffline(context, callback);
                                }
                            }
                        } catch (JSONException e) {
                            setAppDataWithOffline(context, callback);
                        }
                    }
                });
    }

    private static void loadAppInfoFromServer(Context context, LoadAppInfoErrorCallback callback) {

        Map<String, String> params = new HashMap<>();
        params.put("device_id", Constants.getDeviceId());

        OkHttpUtils.post().url(Constants.getAppInfoRequest())
            .params(params)
            .build()
            .execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e, int i) {
                    setAppDataWithOffline(context, callback);
                }
                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject result = new JSONObject(response).getJSONObject("result");
                        setInformationWithJSONArray(context, result, callback);
                    } catch (JSONException e) {
                        setAppDataWithOffline(context, callback);
                    }
                }
            });
    }

    private static void setInformationWithJSONArray(Context context, JSONObject jsonObject, LoadAppInfoErrorCallback callback) {
        try {
            expired = jsonObject.getString("expired").equals("true");

            JSONObject app_info = jsonObject.getJSONObject("info");
            u_appinfos = app_info.toString();
            setAppInfoValuesWithJsonObject(app_info);

            JSONArray languages = jsonObject.getJSONArray("lang");
            u_languages = languages.toString();
            setLanguageClassesWithJsonArray(languages);

            JSONArray adver_array = jsonObject.getJSONArray("adver");
            u_advers = adver_array.toString();
            setAdverClassesWithJsonArray(adver_array);

            setFlagsAndInOutCls(context);
            getDicDataWithOnline(context, callback);

            if(expired){
                callback.onExpired();
            }else {
                callback.onSuccess();
            }

        } catch (JSONException e) {
            setAppDataWithOffline(context, callback);
        }
    }

    private static void saveSharedAppInfo() {
        SharedPrefManager.setExpiredVal(expired);
        SharedPrefManager.setJSONArrayLaoSounds(u_laosounds);
        SharedPrefManager.setJSONObjectAppInfo(u_appinfos);
        SharedPrefManager.setJSONArrayLanguages(u_languages);
        SharedPrefManager.setJSONArrayAdvers(u_advers);
        SharedPrefManager.setLastUpdated(u_lastupdated);
    }

    public static void getDicDataWithOnline(Context context, LoadAppInfoErrorCallback callback) {

        Map<String, String> params = new HashMap<>();
        params.put("updated", SharedPrefManager.getLastUpdated());

        OkHttpUtils.post().url(Constants.getAllDicDataRequest())
            .params(params)
            .build()
            .execute(new StringCallback() {
                @Override
                public void onError(okhttp3.Call call, Exception e, int i) {
                    setAppDataWithOffline(context, callback);
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        u_lastupdated = obj.getString("updatedDate");
                        setDicDataToSQLite(context, obj.getJSONArray("result"), callback);
                    } catch (JSONException e) {
                        setAppDataWithOffline(context, callback);
                    }
                }
            });
    }

    private static void setDicDataToSQLite(Context context, JSONArray jsonArr, LoadAppInfoErrorCallback callback){

        DBManager dbManager = new DBManager(context);
        dbManager.open();
        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject object = jsonArr.getJSONObject(i);

                ColumnCls colNew = new ColumnCls();
                colNew.column_id = Integer.parseInt(object.getString("id"));
                colNew.dic_id = Integer.parseInt(object.getString("dic_id"));
                colNew.lang_id = Integer.parseInt(object.getString("lang_id"));
                colNew.content = object.getString("content");
                colNew.link = object.getString("link");
                colNew.regdate = object.getString("regdate");
                colNew.other = object.getString("other");
                dbManager.insert(colNew);
            }
//            dbManager.removeAll();
            dbManager.close();
            saveSharedAppInfo();
        }catch (JSONException e) {
            dbManager.close();
            setAppDataWithOffline(context, callback);
        }
    }

    private static void setAppInfoValuesWithJsonObject(JSONObject object){

        try {
            Constants.setVersion(object.getString("version"));
            Constants.setAboutUs(object.getString("about_us"));
            Constants.setCopyRight(object.getString("copy_right"));
            Constants.setAPIKey(object.getString("android"));
            Constants.setRegDate(object.getString("regdate"));
            Constants.setOther(object.getString("other"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setLanguageClassesWithJsonArray(JSONArray languages) {
        try {
            List<LanguageCls> languageList = new ArrayList<>();
            for (int i = 0; i < languages.length(); i++) {
                JSONObject obj = languages.getJSONObject(i);
                LanguageCls languageCls = new LanguageCls(i, obj);
                languageList.add(languageCls);
            }
            Constants.setLanguages(languageList);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void setAdverClassesWithJsonArray(JSONArray advers) {
        try {
            List<AdvertiseCls> advertiseClses = new ArrayList<>();
            for (int i = 0; i < advers.length(); i++) {
                JSONObject obj = advers.getJSONObject(i);
                AdvertiseCls advertiseCls = new AdvertiseCls(obj);
                advertiseClses.add(advertiseCls);
            }
            Constants.setAdvers(advertiseClses);
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setAppDataWithOffline(Context context, LoadAppInfoErrorCallback callback){

        if(SharedPrefManager.getExpiredVal()) expired = true;
        else expired = false;

        JSONArray lao_sounds = createLaoSoundsJSONArrayWithOffline(callback);
        u_laosounds = lao_sounds.toString();
        setLaoSoundsWithJsonArray(lao_sounds);

        JSONObject appInfo = createAppInfoJSONObject(callback);
        u_appinfos = appInfo.toString();
        setAppInfoValuesWithJsonObject(appInfo);

        JSONArray languages = createLanguageClassesJSONArrayWithOffline(callback);
        u_languages = languages.toString();
        setLanguageClassesWithJsonArray(languages);

        JSONArray adver_array = createAdversJSONArrayWithOffline(callback);
        u_advers = adver_array.toString();
        setAdverClassesWithJsonArray(adver_array);

        setFlagsAndInOutCls(context);
        saveSharedAppInfo();
        if(expired){
            callback.onExpired();
        }else {
            callback.onSuccess();
        }
    }

    private static void setLaoSoundsWithJsonArray(JSONArray laoSoundsArr){

        try {
            Constants.laoSounds.clear();
            for (int i = 0; i < laoSoundsArr.length(); i++) {
                JSONObject object = laoSoundsArr.getJSONObject(i);
                Constants.laoSounds.add(object.getString("name"));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray createLaoSoundsJSONArrayWithOffline(LoadAppInfoErrorCallback callback){

        JSONArray jsonArray = null;

        String jsonarray_lao_sounds = SharedPrefManager.getJSONArrayLaoSounds();
        if(jsonarray_lao_sounds.equals("")){
            String[] sounds = {
                    "{\"id\":\"4\",\"name\":\"Phutai\",\"other\":\"0\"}",
                    "{\"id\":\"3\",\"name\":\"Pakse\",\"other\":\"0\"}",
                    "{\"id\":\"2\",\"name\":\"Loungprabang\",\"other\":\"0\"}",
                    "{\"id\":\"1\",\"name\":\"Vientiane\",\"other\":\"0\"}"
            };
            jsonArray = new JSONArray();
            try {
                for (String s : sounds) {
                    JSONObject sound = new JSONObject(s);
                    jsonArray.put(sound);
                }

            } catch (JSONException e) {}
        }else{
            try {
                jsonArray = new JSONArray(jsonarray_lao_sounds);
            } catch (JSONException e) {}
        }

        return jsonArray;
    }

    private static JSONObject createAppInfoJSONObject(LoadAppInfoErrorCallback callback) {

        JSONObject appObject = null;

        String app_info = SharedPrefManager.getJSONObjectAppInfo();
        if(app_info.equals("")){
            app_info = "{\"id\":\"1\",\"version\":\"1.0\",\"about_us\":\"Kigabyte Solutions Laos\",\"copy_right\":\"Kigabyte CopyRight@2020\",\"android\":\"Your_KEY\",\"ios\":\"Your_KEY\",\"regdate\":\"2020-06-15 06:44:06\",\"other\":\"Translate Me Lao \"}";
        }

        try {
            appObject = new JSONObject(app_info);
        } catch (JSONException e) {}
        return appObject;
    }

    private static void setFlagsAndInOutCls(Context context){
        File file = new File(Constants.getRootFlagPath());
        if (!file.exists()) {
            file.mkdirs();
        }

        Constants.lst_menu_flags.clear();
        for (int i = 0; i < Constants.getLanguages().size(); i++) {
            String flagName = Constants.getRootFlagPath() + Constants.getLanguages().get(i).flag + ".png";
            Drawable drawable = Constants.GetImage(context, Constants.getLanguages().get(i).flag);
            Constants.lst_menu_flags.add(drawable);
            Bitmap icon = ((BitmapDrawable)Constants.GetImage(context, Constants.getLanguages().get(i).flag)).getBitmap();

            File flgFile = new File(flagName);
            if (!flgFile.exists()) {
                try{
                    OutputStream stream = new FileOutputStream(flagName);
                    icon.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                }catch (IOException e) {}
            }
            Constants.getLanguages().get(i).flag = flagName;
        }

        String in_lang = "lo", out_lang = "en";
        if(!SharedPrefManager.getSharedPrefLanguageIn().equals("")){
            in_lang = SharedPrefManager.getSharedPrefLanguageIn();
        }
        if(!SharedPrefManager.getSharedPrefLanguageOut().equals("")){
            out_lang = SharedPrefManager.getSharedPrefLanguageOut();
        }
        setGlobalInOutCls(in_lang, out_lang);
    }

    private static void setGlobalInOutCls(String in_lang, String out_lang){
        for (int i=0; i < Constants.getLanguages().size(); i++){
            LanguageCls language = Constants.getLanguages().get(i);
            if(language.text_scann_lang_code.toLowerCase().equals(in_lang.toLowerCase())){
                FuncUtils.gLanguageInputCls = language;
                SharedPrefManager.setSharedPrefLanguageIn(in_lang.toLowerCase());
            }
            if(language.text_scann_lang_code.toLowerCase().equals(out_lang.toLowerCase())){
                FuncUtils.gLanguageOutputCls = language;
                SharedPrefManager.setSharedPrefLanguageOut(out_lang.toLowerCase());
            }
        }
        SharedPrefManager.setLoginStatus();
    }

    private static JSONArray createLanguageClassesJSONArrayWithOffline(LoadAppInfoErrorCallback callback){

        JSONArray jsonArray = null;
        String json_arr_languages = SharedPrefManager.getJSONArrayLanguages();
        if(json_arr_languages.equals("")){
            jsonArray = new JSONArray();
            String[] languages = {
                    "{\"id\":\"1\",\"name\":\"ລາວ\",\"surname\":\"lo\",\"voice_in_key\":\"lo-LA\",\"voice_out_key\":\"th-TH\",\"voice_type\":\"th-TH-Standard-A\",\"trans_key\":\"LAO\",\"regdate\":\"2020-06-15 07:24:43\",\"other\":\"lao\"}",
                    "{\"id\":\"2\",\"name\":\"조선어\",\"surname\":\"ko\",\"voice_in_key\":\"ko\",\"voice_out_key\":\"ko-KR\",\"voice_type\":\"ko-KR-Standard-A\",\"trans_key\":\"ko\",\"regdate\":\"2020-06-15 07:24:43\",\"other\":\"korean\"}",
                    "{\"id\":\"3\",\"name\":\"English\",\"surname\":\"en\",\"voice_in_key\":\"en\",\"voice_out_key\":\"en-US\",\"voice_type\":\"en-US-Standard-C\",\"trans_key\":\"en\",\"regdate\":\"2020-06-15 07:24:07\",\"other\":\"english\"}",
                    "{\"id\":\"4\",\"name\":\"中文\",\"surname\":\"zh\",\"voice_in_key\":\"zh\",\"voice_out_key\":\"cmn-CN\",\"voice_type\":\"cmn-CN-Standard-A\",\"trans_key\":\"zh-CN\",\"regdate\":\"2020-06-15 07:24:43\",\"other\":\"chinese\"}",
                    "{\"id\":\"5\",\"name\":\"日本語\",\"surname\":\"ja\",\"voice_in_key\":\"ja\",\"voice_out_key\":\"ja-JP\",\"voice_type\":\"ja-JP-Standard-A\",\"trans_key\":\"ja\",\"regdate\":\"2020-06-15 07:24:43\",\"other\":\"japanese\"}",
                    "{\"id\":\"6\",\"name\":\"Pусский\",\"surname\":\"ru\",\"voice_in_key\":\"ru\",\"voice_out_key\":\"ru-RU\",\"voice_type\":\"ru-RU-Standard-A\",\"trans_key\":\"ru\",\"regdate\":\"2020-06-18 08:27:14\",\"other\":\"russian\"}",
                    "{\"id\":\"7\",\"name\":\"Tiếng Việt\",\"surname\":\"vi\",\"voice_in_key\":\"vi\",\"voice_out_key\":\"vi-VN\",\"voice_type\":\"vi-VN-Standard-A\",\"trans_key\":\"vi\",\"regdate\":\"2020-06-18 08:27:14\",\"other\":\"vietnam\"}",
                    "{\"id\":\"8\",\"name\":\"ไทย\",\"surname\":\"th\",\"voice_in_key\":\"th\",\"voice_out_key\":\"th-TH\",\"voice_type\":\"th-TH-Standard-A\",\"trans_key\":\"th\",\"regdate\":\"2020-06-18 08:28:48\",\"other\":\"thailand\"}",
                    "{\"id\":\"9\",\"name\":\"Français\",\"surname\":\"fr\",\"voice_in_key\":\"fr\",\"voice_out_key\":\"fr-FR\",\"voice_type\":\"fr-FR-Standard-A\",\"trans_key\":\"fr\",\"regdate\":\"2020-06-18 08:28:48\",\"other\":\"french\"}",
                    "{\"id\":\"10\",\"name\":\"Deutsch\",\"surname\":\"de\",\"voice_in_key\":\"de\",\"voice_out_key\":\"de-DE\",\"voice_type\":\"de-DE-Standard-A\",\"trans_key\":\"de\",\"regdate\":\"2020-06-18 08:30:46\",\"other\":\"germany\"}",
                    "{\"id\":\"11\",\"name\":\"العربية\",\"surname\":\"ar\",\"voice_in_key\":\"ar\",\"voice_out_key\":\"ar-XA\",\"voice_type\":\"ar-XA-Standard-A\",\"trans_key\":\"ar\",\"regdate\":\"2020-06-19 03:21:47\",\"other\":\"arabic\"}",
                    "{\"id\":\"12\",\"name\":\"Italiano\",\"surname\":\"it\",\"voice_in_key\":\"it\",\"voice_out_key\":\"it-IT\",\"voice_type\":\"it-IT-Standard-A\",\"trans_key\":\"it\",\"regdate\":\"2020-06-19 03:21:47\",\"other\":\"italian\"}",
                    "{\"id\":\"13\",\"name\":\"Español\",\"surname\":\"es\",\"voice_in_key\":\"es\",\"voice_out_key\":\"es-ES\",\"voice_type\":\"es-ES-Standard-A\",\"trans_key\":\"es\",\"regdate\":\"2020-06-19 03:23:17\",\"other\":\"spanish\"}",
                    "{\"id\":\"14\",\"name\":\"Português\",\"surname\":\"pt\",\"voice_in_key\":\"pt\",\"voice_out_key\":\"pt-PT\",\"voice_type\":\"pt-PT-Standard-A\",\"trans_key\":\"pt\",\"regdate\":\"2020-06-19 03:23:17\",\"other\":\"portugal\"}"
            };
            try {
                for (String language : languages) {
                    JSONObject object = new JSONObject(language);
                    jsonArray.put(object);
                }
            } catch (JSONException e) {}

        }else{
            try {
                jsonArray = new JSONArray(json_arr_languages);
            } catch (JSONException e) {}
        }
        return jsonArray;
    }

    private static JSONArray createAdversJSONArrayWithOffline(LoadAppInfoErrorCallback callback){

        JSONArray jsonArray = null;
        String json_arr_advers = SharedPrefManager.getJSONArrayAdvers();
        if(json_arr_advers.equals("")){
            String[] advers = {
                    "{\"id\":\"4\",\"title\":\"LAO TELECOM\",\"sub_title\":\"Telephone services in Lao PDR\",\"link\":\"https:\\/\\/laotel.com\",\"regdate\":\"2020-06-19 03:15:15\",\"other\":\"\"}",
                    "{\"id\":\"3\",\"title\":\"Unitel\",\"sub_title\":\"Telecommunications services in the Lao PDR\",\"link\":\"https:\\/\\/unitel.com.la\",\"regdate\":\"2020-06-19 03:15:15\",\"other\":\"\"}",
                    "{\"id\":\"2\",\"title\":\"Lao Ecommerce\",\"sub_title\":\"Ecommerce for native in Laos\",\"link\":\"http:\\/\\/www.kigabyte.la\\/\",\"regdate\":\"2020-06-19 03:15:15\",\"other\":\"\"}",
                    "{\"id\":\"1\",\"title\":\"TourLao\",\"sub_title\":\"App for Lao Tourist\",\"link\":\"http:\\/\\/tourlao.daxiao-itdev.com\\/\",\"regdate\":\"2020-06-19 03:15:15\",\"other\":\"\"}"
            };
            jsonArray = new JSONArray();
            try {
                for (String s : advers) {
                    JSONObject adver = new JSONObject(s);
                    jsonArray.put(adver);
                }
            } catch (JSONException e) {}
        }else {
            try {
                jsonArray = new JSONArray(json_arr_advers);
            } catch (JSONException e) {}
        }
        return jsonArray;
    }

    public static void sendPurchaseRequest(PurchasaeActivity context) {

        Map<String, String> params = new HashMap<>();
        params.put("device_id", Constants.getDeviceId());

        OkHttpUtils.post().url(Constants.sendPurchaseRequest())
                .params(params)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int i) {
                        Toast.makeText(context.getApplicationContext(), R.string.error_retry, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray purchaseRequest = obj.getJSONArray("result");
                            Toast.makeText(context.getApplicationContext(), "Request sent successfully.", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            Toast.makeText(context.getApplicationContext(), R.string.error_retry, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void exitDialog(MainActivity mainActivity) {

        AlertDialog.Builder alertdlg = null;
        if(alertdlg == null){
            alertdlg = new AlertDialog.Builder(mainActivity);
            alertdlg.setIcon(R.drawable.ic_launcher_round);
            alertdlg.setTitle(R.string.app_name);
            alertdlg.setMessage(R.string.string_message_exit);
            alertdlg.setPositiveButton(R.string.string_message_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    mainActivity.moveTaskToBack(true);
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

    public interface GetDirectoryCallback {
        void getDirectoryFromUrl(String result);
    }

    public interface OnBindAudioCallback {
        void onSuccess(byte[] b);
        void onError(String e);
    }

    public interface SendDicDataCallback {
        void onSuccess();
        void onError(String e);
    }

    public interface LoadAppInfoErrorCallback {
        void onSuccess();
        void onExpired();
    }

    public interface OnCLickPopUpItem {
        void onClickPopUpItem(int category, int index);
    }

    public interface OnTestConnCallBack {
        void onSuccess(boolean result);
    }
}
