package com.laodev.translate.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefManager {

    private static SharedPreferences sharedPref;

    private static final String package_name = "com.laodev.translate";

    private static final String admob_enable = package_name + "admob_enable";
    private static final String language_in_key = package_name + "language_in";
    private static final String language_out_key = package_name + "language_out";
    private static final String lao_sound = package_name + "lao_sound";

    private static final String jsonarray_lao_sounds = "jsonarray_lao_sounds";
    private static final String jsonobject_app_info = "jsonobject_app_info";
    private static final String jsonarray_languages = "jsonarray_languages";
    private static final String jsonarray_advers = "jsonarray_advers";

    private static final String version_number = "version_number";
    private static final String last_updated = "last_updated";
    private static final String ISLOGIN = "is_logged_in";
    private static final String expired = "expired";

    private static final String device_id = "device_id";

    private static final String privacy_check = "privacy_showed";

    public SharedPrefManager (Context context) {
        sharedPref = context.getSharedPreferences(package_name, MODE_PRIVATE);
    }

    public static void setPrivacyCheck() {
        sharedPref.edit().putString(privacy_check, "checked").apply();
    }

    public static String getPrivacyCheck() {
        return sharedPref.getString(privacy_check, "un_checked");
    }

    public static void setDeviceId(String id) {
        sharedPref.edit().putString(device_id, id).apply();
    }

    public static String getDeviceId() {
        return sharedPref.getString(device_id, "");
    }

    public static void setLoginStatus() {
        sharedPref.edit().putBoolean(ISLOGIN, true).apply();
    }

    public static boolean getLoginStatus() {
        return sharedPref.getBoolean(ISLOGIN, false);
    }

    public static void setExpiredVal(boolean val) {
        sharedPref.edit().putBoolean(expired, val).apply();
    }

    public static boolean getExpiredVal() {
        return sharedPref.getBoolean(expired, false);
    }

    public static void setVersionNumber(String val) {
        sharedPref.edit().putString(version_number, val).apply();
    }

    public static String getVersionNumber() {
        return sharedPref.getString(version_number, "1.0");
    }

    public static void setLastUpdated(String updated) {
        sharedPref.edit().putString(last_updated, updated).apply();
    }

    public static String getLastUpdated() {
        return sharedPref.getString(last_updated, "");
    }

    public static void setSharedPrefAdEnable(){
        sharedPref.edit().putString(admob_enable, "checked").apply();
    }

    public static void unSetSharedPrefAdEnable(){
        sharedPref.edit().putString(admob_enable, "un_checked").apply();
    }

    public static String getSharedPrefAdEnable(){
        return sharedPref.getString(admob_enable, "checked");
    }

    public static void setSharedPrefLaoSound(String sound_name){
        sharedPref.edit().putString(lao_sound, sound_name).apply();
    }

    public static String getSetSharedPrefLaoSound(){
        return sharedPref.getString(lao_sound, "Vientiane");
    }

    public static void setSharedPrefLanguageIn(String value){
        sharedPref.edit().putString(language_in_key, value).apply();
    }

    public static String getSharedPrefLanguageIn(){
        return sharedPref.getString(language_in_key, "");
    }

    public static void setSharedPrefLanguageOut(String value){
        sharedPref.edit().putString(language_out_key, value).apply();
    }

    public static String getSharedPrefLanguageOut(){
        return sharedPref.getString(language_out_key, "");
    }

    public static void setJSONArrayLaoSounds(String value){
        sharedPref.edit().putString(jsonarray_lao_sounds, value).apply();
    }

    public static String getJSONArrayLaoSounds(){
        return sharedPref.getString(jsonarray_lao_sounds, "");
    }

    public static void setJSONObjectAppInfo(String value){
        sharedPref.edit().putString(jsonobject_app_info, value).apply();
    }

    public static String getJSONObjectAppInfo(){
        return sharedPref.getString(jsonobject_app_info, "");
    }

    public static void setJSONArrayLanguages(String value){
        sharedPref.edit().putString(jsonarray_languages, value).apply();
    }

    public static String getJSONArrayLanguages(){
        return sharedPref.getString(jsonarray_languages, "");
    }

    public static void setJSONArrayAdvers(String value){
        sharedPref.edit().putString(jsonarray_advers, value).apply();
    }

    public static String getJSONArrayAdvers(){
        return sharedPref.getString(jsonarray_advers, "");
    }

}
