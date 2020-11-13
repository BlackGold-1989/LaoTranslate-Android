package com.laodev.translate.classes.GeneralClasses;

public class TextCls {

    public String text;
    public int language;
    public String voiceType;
    public int entryType;
    public boolean enableCopyShare;
    public String voice_url;
    public boolean voice_byServer;

    public TextCls(){
        this.text = "";
        this.voiceType = "";
        this.language = 0;
        this.entryType = 0;
        this.enableCopyShare = false;
        voice_url = "";
        voice_byServer = false;
    }
}
