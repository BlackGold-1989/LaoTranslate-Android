package com.laodev.translate.classes.GeneralClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class LanguageCls {
    public int language;
    public String language_title;
    public String text_scann_lang_code;
    public String text_out_lang_code;
    public String flag;

    public String voice_in_lang_code;
    public String voice_out_lang_code;
    public String voice_out_type_code;

    public LanguageCls() {
        this.language = 0;
        this.language_title = "";
        this.text_scann_lang_code = "";
        this.flag = "";

        this.text_out_lang_code = "";
        this.voice_in_lang_code = "";
        this.voice_out_lang_code = "";
        this.voice_out_type_code = "";
    }

    public LanguageCls(int index, JSONObject obj) {
        try {
            language = index;
            language_title = obj.getString("name");
            text_scann_lang_code = obj.getString("surname");
            flag = "logo_" + obj.getString("surname") + "_256";

            text_out_lang_code = obj.getString("trans_key");
            voice_in_lang_code = obj.getString("voice_in_key");
            voice_out_lang_code = obj.getString("voice_out_key");
            voice_out_type_code = obj.getString("voice_type");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LanguageCls(int index, String lang, String scan) {

        language = index;
        language_title = lang;
        text_scann_lang_code = scan;
        flag = "logo_" + scan + "_256";

        text_out_lang_code = "";
        voice_in_lang_code = "";
        voice_out_lang_code = "";
        voice_out_type_code = "";
    }

}
