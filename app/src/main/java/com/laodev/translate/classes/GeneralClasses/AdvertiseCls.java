package com.laodev.translate.classes.GeneralClasses;

import org.json.JSONException;
import org.json.JSONObject;

public class AdvertiseCls {

    public String title;
    public String sub_title;
    public String link;
    public String regdate;
    public String other;

    public AdvertiseCls(){
        this.title = "";
        this.sub_title = "";
        this.link = "";
        this.regdate = "";
        this.other = "";
    }
    
    public AdvertiseCls(JSONObject obj) {
        try {
            title = obj.getString("title");
            sub_title = obj.getString("sub_title");
            link = obj.getString("link");
            regdate = obj.getString("regdate");
            other = obj.getString("other");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
}
