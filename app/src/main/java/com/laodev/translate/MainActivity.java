package com.laodev.translate;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;
import androidx.viewpager.widget.ViewPager;

import com.baoyz.actionsheet.ActionSheet;
import com.laodev.translate.SQLite.DBManager;
import com.laodev.translate.adapter.TextListAdapter;
import com.laodev.translate.classes.GeneralClasses.BannerSliderAdapter;
import com.laodev.translate.classes.GeneralClasses.ColumnCls;
import com.laodev.translate.classes.GeneralClasses.GoogleCloudTranslateOut;
import com.laodev.translate.classes.GeneralClasses.LanguageCls;
import com.laodev.translate.classes.GeneralClasses.SpeakerPlayer;
import com.laodev.translate.classes.GeneralClasses.TextCls;
import com.laodev.translate.dialog.PrivacyDialog;
import com.laodev.translate.presenter.MainActivityPresenter;
import com.laodev.translate.utils.Constants;
import com.laodev.translate.utils.CropImageRequest;
import com.laodev.translate.utils.FuncUtils;
import com.laodev.translate.utils.SharedPrefManager;
import com.laodev.translate.views.CropedImageActivity;
import com.laodev.translate.views.MainActivityView;
import com.laodev.translate.views.SettingsActivity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements MainActivityView, ActionSheet.ActionSheetListener{

    private final int REQ_CODE_VOICE_INPUT = 100;
    public final int REQ_CODE_MOVE_CROPIMAGE = 101;

    private DBManager dbManager;
    private MainActivityPresenter mPresenter;
    private PopupMenu inputPopup, outputPopup;

    private TextListAdapter textListAdapter;

    private ListView lstView;
    private List<TextCls> mTextLists = new ArrayList<>();

    private LinearLayout rlt_admob, llt_connStatus;
    private TextView txtBottombarEntry, txtBottombarOutput, txtConStatus;
    private ImageView imgFlagEntry, imgFlagOutput;
    private EditText edtMessage;

    private String textToTranslate = "", translatedText = "";
    private String detectedLanguage;

    private int adver_index = 0;
    private int voice_out_index;
    private int lastTextIndex = 0;

    private boolean isFirst = true;

    private SpeakerPlayer mSpeaker;
    private String speakerText = "";
    private boolean chkPresenter = false;

    private File inputFlag, outFlag;

    private int tryOnlineRequest = 0;

    private int longClickIndex;

    private LinearLayout llt_record;
    private ImageView img_record;
    PrivacyDialog privacy_dialog;

    private String[] string_unknown = {
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

    private BroadcastReceiver wifichangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {

            tryOnlineRequest = 0;
            if(FuncUtils.isInternetWorking(MainActivity.this)){
                setOnlineUI();
            }else{
                if(mTextLists.size() > 0){
                    for(int i=0; i< mTextLists.size(); i++){
                        if(mTextLists.get(i).text.equals(Constants.string_translating)){
                            mTextLists.remove(mTextLists.size()-1);
                            if(mTextLists.size()-1>0){
                                mTextLists.remove(mTextLists.size()-2);
                            }
                            refreshListView();
                        }
                    }
                }
                setOfflineUI();
            }
        }
    };

    private void setOnlineUI() {
        FuncUtils.getConnStatusFromServer(result -> {
            if(result){
                FuncUtils.connStatus = true;
                tryOnlineRequest = 0;

                img_record.setImageResource(R.drawable.ic_circle1);

                if(!isFirst){
                    txtConStatus.setText(R.string.string_online);
                    llt_connStatus.setBackgroundColor(getResources().getColor(R.color.colorGreenLight));
                    llt_connStatus.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    handler.postDelayed(() -> llt_connStatus.setVisibility(View.GONE), 3000);
                }
                if(!chkPresenter){
                    setPresenter();
                }
            }else{
                img_record.setImageResource(R.drawable.ic_circle1_cl);

                setOnlineUI();
                tryOnlineRequest++;
                if(tryOnlineRequest<10)
                    setOnlineUI();
                else tryOnlineRequest = 10;
            }
        });
    }

    private void setOfflineUI() {
        txtConStatus.setText(R.string.string_offline);
        llt_connStatus.setBackgroundColor(getResources().getColor(R.color.colorTextGreyLight));
        llt_connStatus.setVisibility(View.VISIBLE);
        FuncUtils.connStatus = false;
        isFirst = false;

        setOnlineUI();
    }

    private TextListAdapter.TextListAdapterCallback textAdapterListener = new TextListAdapter.TextListAdapterCallback() {

        @Override
        public void onClickSpeaker(int languageIndex, int textIndex) {
            if(FuncUtils.connStatus){
                voice_out_index = languageIndex;
                onPlaySpeaker(mTextLists.get(textIndex).text);
            }else {
                FuncUtils.showToast(MainActivity.this, getString(R.string.error_retry));
            }
        }

        @Override
        public void onLongClickEntryCellEvent(int index) {

            longClickIndex = index;
            ActionSheet.createBuilder(MainActivity.this, getSupportFragmentManager())
                    .setCancelButtonTitle("Cancel")
                    .setOtherButtonTitles("Copy", "Share")
                    .setCancelableOnTouchOutside(true)
                    .setListener(MainActivity.this).show();
        }

        @Override
        public void onPlayVoiceByURL(String fname) {
            if(FuncUtils.connStatus){
                if(fname.contains("goodmorning")){
                    onBindAudio(Constants.getRootAudioPath() + fname);
                }else{
                    fname = Constants.getSoundServerLink() + fname;
                    mSpeaker.playVoiceByURL(fname);
                }
            } else {
                FuncUtils.showToast(MainActivity.this, getString(R.string.error_retry));
            }
        }
    };

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        String text = mTextLists.get(longClickIndex).text;
        if(index == 0){
            ClipboardManager clipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text",text);
            clipboardManager.setPrimaryClip(clipData);
            FuncUtils.showToast(MainActivity.this, "Data Copied to Clipboard");
        }else{
            Intent sendInt = new Intent(Intent.ACTION_SEND);
            sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sendInt.putExtra(Intent.EXTRA_TEXT, text);
            sendInt.setType("text/plain");
            startActivity(Intent.createChooser(sendInt, "Share"));
        }
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancle) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        if(SharedPrefManager.getPrivacyCheck().equals("un_checked")){
            onShowPrivacyDialog();
        }
        initUIView();
        initData();
        initCustomADS();
        createPopUp();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUIView() {

        llt_connStatus = findViewById(R.id.llt_connstatus);
        txtConStatus = findViewById(R.id.txt_connstatus);

        rlt_admob = findViewById(R.id.rlt_admob);

        txtBottombarEntry = findViewById(R.id.txt_main_entry);
        txtBottombarOutput = findViewById(R.id.txt_main_output);

        imgFlagEntry = findViewById(R.id.img_main_flag_entry);
        imgFlagOutput = findViewById(R.id.img_main_flag_output);
        edtMessage = findViewById(R.id.edt_main_message);

        lstView = findViewById(R.id.list_main_texts);
        llt_record = findViewById(R.id.rlt_main_voice);
        img_record = findViewById(R.id.img_main_mic);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initData() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getApplicationContext().registerReceiver(wifichangeListener, intentFilter);

        mSpeaker = new SpeakerPlayer(MainActivity.this);

        dbManager = new DBManager(this);
        dbManager.open();

        voice_out_index = FuncUtils.gLanguageOutputCls.language;

        textListAdapter = new TextListAdapter(MainActivity.this, mTextLists, textAdapterListener);
        lstView.setAdapter(textListAdapter);
        refreshListView();

        txtBottombarEntry.setText(Constants.getLanguages(FuncUtils.gLanguageInputCls.language).language_title);
        txtBottombarOutput.setText(Constants.getLanguages(FuncUtils.gLanguageOutputCls.language).language_title);
        edtMessage.setText("");

        llt_connStatus.setVisibility(View.GONE);
        if(!FuncUtils.connStatus){
            txtConStatus.setText(R.string.string_offline);
            llt_connStatus.setBackgroundColor(getResources().getColor(R.color.colorTextGreyLight));
            llt_connStatus.setVisibility(View.VISIBLE);
        }

        inputFlag = new File(FuncUtils.gLanguageInputCls.flag);
        outFlag = new File(FuncUtils.gLanguageOutputCls.flag);

        Picasso.get().load(inputFlag).fit().centerCrop().into(imgFlagEntry);
        Picasso.get().load(outFlag).fit().centerCrop().into(imgFlagOutput);

        if(FuncUtils.connStatus){
            setPresenter();
        }
        speakerText = "";

        llt_record.setOnTouchListener((v, event) -> {

            switch (event.getAction()){
                case MotionEvent.ACTION_POINTER_DOWN:
                    img_record.setImageResource(R.drawable.ic_circle1_cl);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    img_record.setImageResource(R.drawable.ic_circle1);
                    break;
            }
            return false;
        });

        setAdmobVisibility();
    }

    private void setPresenter(){
        chkPresenter = true;
        mPresenter = new MainActivityPresenter(MainActivity.this);
        mPresenter.initGCPTTSSettings();
        mPresenter.initAndroidTTSSetting();
    }

    private void initCustomADS() {
        adver_index = 0;

        BannerSliderAdapter bannerAdapter = new BannerSliderAdapter(getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(bannerAdapter);

        Handler handler = new Handler();
        Runnable update = () -> {
            adver_index++;
            if (adver_index == Constants.getAdversCount()) adver_index = 0;
            viewPager.setCurrentItem(adver_index, true);
        };
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 2000, 2000);
    }

    private void setAdmobVisibility() {
        if(SharedPrefManager.getSharedPrefAdEnable().equals("checked")){
            rlt_admob.setVisibility(View.VISIBLE);
        }else{
            rlt_admob.setVisibility(View.GONE);
        }
    }

    private void createPopUp() {
        inputPopup = new PopupMenu(MainActivity.this, findViewById(R.id.llt_main_bottombar_left));
        outputPopup = new PopupMenu(MainActivity.this, findViewById(R.id.llt_main_bottombar_right));
        FuncUtils.createPopUpMenu(inputPopup, Constants.ENTRY_LANGUAGE, new FuncUtils.OnCLickPopUpItem() {
            @Override
            public void onClickPopUpItem(int category, int index) {
                setTextEntryStatus(category, index);
            }
        });
        FuncUtils.createPopUpMenu(outputPopup, Constants.OUTPUT_LANGUAGE, new FuncUtils.OnCLickPopUpItem() {
            @Override
            public void onClickPopUpItem(int category, int index) {
                setTextEntryStatus(category, index);
            }
        });
    }

    public void OnClickOCR(View view) {
        CropImageRequest.getCropImageRequest().start(MainActivity.this);
    }

    public void OnClickSettings(View view) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    public void onClickAdmobClose(View view) {
        rlt_admob.setVisibility(View.GONE);
        SharedPrefManager.unSetSharedPrefAdEnable();
    }

    public void onClickBottomLeft(View view) {
        inputPopup.show();
    }

    public void onClickBottomRight(View view) {
        outputPopup.show();
    }

    public void onClickExchange(View view) {

        LanguageCls tmpCls = FuncUtils.gLanguageInputCls;
        FuncUtils.gLanguageInputCls = FuncUtils.gLanguageOutputCls;
        FuncUtils.gLanguageOutputCls = tmpCls;

        voice_out_index = FuncUtils.gLanguageOutputCls.language;

        txtBottombarEntry.setText(Constants.getLanguages(FuncUtils.gLanguageInputCls.language).language_title);
        txtBottombarOutput.setText(Constants.getLanguages(FuncUtils.gLanguageOutputCls.language).language_title);

        inputFlag = new File(String.valueOf(Uri.parse(FuncUtils.gLanguageInputCls.flag)));
        outFlag = new File(String.valueOf(Uri.parse(FuncUtils.gLanguageOutputCls.flag)));

        Picasso.get().load(inputFlag).fit().centerCrop().into(imgFlagEntry);
        Picasso.get().load(outFlag).fit().centerCrop().into(imgFlagOutput);

        SharedPrefManager.setSharedPrefLanguageIn(FuncUtils.gLanguageInputCls.text_scann_lang_code);
        SharedPrefManager.setSharedPrefLanguageOut(FuncUtils.gLanguageOutputCls.text_scann_lang_code);
    }

    public void onClickLearnMore(View view) {
        if(FuncUtils.connStatus){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.getAdver(adver_index).link));
            startActivity(browserIntent);
        } else {
            FuncUtils.showToast(MainActivity.this, getString(R.string.error_retry));
        }
    }

    public void onClickAdmobInformation(View view) {

    }

    public void OnClickMic(View view) {
        if(FuncUtils.connStatus){
            promptSpeechInput();
        }else{
            FuncUtils.showToast(MainActivity.this, "Network not Connected.");
        }
    }

    public void OnClickSend(View view) {
        if(!edtMessage.getText().toString().equals("")){
            textToTranslate = edtMessage.getText().toString().trim().replaceAll ("(?m)^[ \t].*\r?\n", "");
            edtMessage.setText("");
            outInputedText(Constants.ENTRY_LANGUAGE);
        }
    }

    private void setTextEntryStatus(int category, int language_index) {
        LanguageCls lang = Constants.getLanguages(language_index);
        if(category == Constants.ENTRY_LANGUAGE){
            if(FuncUtils.gLanguageInputCls.language != language_index){
                FuncUtils.gLanguageInputCls = lang;
                txtBottombarEntry.setText(FuncUtils.gLanguageInputCls.language_title);

                inputFlag = new File(FuncUtils.gLanguageInputCls.flag);
                Picasso.get().load(inputFlag).fit().centerCrop().into(imgFlagEntry);
                SharedPrefManager.setSharedPrefLanguageIn(FuncUtils.gLanguageInputCls.text_scann_lang_code);
            }
        }else{
            if(FuncUtils.gLanguageOutputCls.language != language_index){
                FuncUtils.gLanguageOutputCls = lang;
                txtBottombarOutput.setText(FuncUtils.gLanguageOutputCls.language_title);
                outFlag = new File(FuncUtils.gLanguageOutputCls.flag);
                Picasso.get().load(outFlag).fit().centerCrop().into(imgFlagOutput);

                SharedPrefManager.setSharedPrefLanguageOut(FuncUtils.gLanguageOutputCls.text_scann_lang_code);
                voice_out_index = language_index;
                if (FuncUtils.connStatus){
                    setVoiceSettings();
                }
            }
        }
    }

    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, FuncUtils.gLanguageInputCls.voice_in_lang_code);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        try {
            startActivityForResult(intent, REQ_CODE_VOICE_INPUT);
        } catch (ActivityNotFoundException a) {

            AlertDialog.Builder alertdlg = null;
            if(alertdlg == null){
                alertdlg = new AlertDialog.Builder(MainActivity.this);
                alertdlg.setIcon(R.drawable.ic_launcher_round);
                alertdlg.setTitle(R.string.app_name);
                alertdlg.setMessage(R.string.string_message_voice_record);
                alertdlg.setPositiveButton(R.string.string_message_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String appPackageName = "com.google.android.googlequicksearchbox";
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
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
    }

    private void onPlaySpeaker(String text) {
        mPresenter.selectStyle(Constants.getLanguages(voice_out_index).voice_out_type_code);
        mPresenter.selectLanguage(Constants.getLanguages(voice_out_index).voice_out_lang_code);
        speakerText = text;
        mPresenter.startSpeak(speakerText);
    }

    private void onCheckSQLite(){
        String trimed_str = textToTranslate;
        ColumnCls col = dbManager.getColumnWithInputText(trimed_str.toLowerCase().replaceAll("[!@#$%^&*()|<>{}_+-.,]", ""));
        int langId1 = col.lang_id;
        int dic_id = col.dic_id;

        if(!col.content.equals("")){

            int langId = langId1 - 1;
            detectedLanguage = Constants.getLanguages(langId).text_scann_lang_code;
            setInputFlagWithLanguageCode();

            ColumnCls col1 = dbManager.getColumnIndexWithDicAndLangID(dic_id, FuncUtils.gLanguageOutputCls.language + 1);
            if(!col1.content.equals("")){
                translatedText = col1.content;
                TextCls translatedTextCls = new TextCls();
                translatedTextCls.text = translatedText;
                translatedTextCls.language = FuncUtils.gLanguageOutputCls.language;
                translatedTextCls.voiceType = FuncUtils.gLanguageOutputCls.voice_out_type_code;
                translatedTextCls.entryType = Constants.OUTPUT_VOICE;

                mTextLists.set(lastTextIndex, translatedTextCls);
                refreshListView();

                if(FuncUtils.connStatus){
                    onPlaySpeaker(translatedText);
                }
            } else {
                if(!FuncUtils.connStatus){

                    TextCls translatedTextCls = new TextCls();
                    translatedTextCls.text = string_unknown[FuncUtils.gLanguageOutputCls.language];
                    translatedTextCls.language = FuncUtils.gLanguageOutputCls.language;
                    translatedTextCls.voiceType = FuncUtils.gLanguageOutputCls.voice_out_type_code;
                    translatedTextCls.entryType = Constants.OUTPUT_VOICE;

                    mTextLists.set(lastTextIndex, translatedTextCls);
                    refreshListView();
                }else{
                    delayedCallForTrans();
                }
            }
        }
        else {
            if(!FuncUtils.connStatus){
                TextCls translatedTextCls = new TextCls();
                translatedTextCls.text = string_unknown[FuncUtils.gLanguageOutputCls.language];
                translatedTextCls.language = FuncUtils.gLanguageOutputCls.language;
                translatedTextCls.voiceType = FuncUtils.gLanguageOutputCls.voice_out_type_code;
                translatedTextCls.entryType = Constants.OUTPUT_VOICE;

                mTextLists.set(lastTextIndex, translatedTextCls);
                refreshListView();
            }else{
                delayedCallForTrans();
            }
        }
    }

    private void delayedCallForTrans() {
        if (FuncUtils.connStatus){
            new Handler().postDelayed(() -> translateOutTextByGoogle(), 1500);
        } else {
            FuncUtils.showToast(MainActivity.this, getString(R.string.error_retry));
        }
    }

    private void translateOutTextByGoogle(){
        try {
            GoogleCloudTranslateOut googleTranslate = new GoogleCloudTranslateOut(textToTranslate, (stringTranslated, detectedLang, result) -> {
                translatedText = stringTranslated;
                detectedLanguage = detectedLang;

                setPlaySpeakerAndRefresh();
            });
            googleTranslate.execute().get();

        } catch (ExecutionException | InterruptedException e) {
        }
    }

    private void setPlaySpeakerAndRefresh(){
        txtBottombarOutput.post(() -> {
            setInputFlagWithLanguageCode(); // set input class

            TextCls textClsInput = new TextCls();
            textClsInput.text = textToTranslate;
            textClsInput.entryType = Constants.ENTRY_LANGUAGE;
            textClsInput.language = FuncUtils.gLanguageInputCls.language;

            int ind = mTextLists.size() - 1;
            mTextLists.set(ind, textClsInput);
            refreshListView();

            if(!translatedText.equals("")){
                TextCls translatedTextCls = new TextCls();
                translatedTextCls.text = translatedText;
                translatedTextCls.language = FuncUtils.gLanguageOutputCls.language;
                translatedTextCls.voiceType = FuncUtils.gLanguageOutputCls.voice_out_type_code;
                translatedTextCls.entryType = Constants.OUTPUT_VOICE;

                mTextLists.set(lastTextIndex, translatedTextCls);
                refreshListView();

                if (FuncUtils.connStatus){
                    onPlaySpeaker(translatedText);

                    int src_index = FuncUtils.gLanguageInputCls.language;
                    int tgt_index = FuncUtils.gLanguageOutputCls.language;
//                    String trimed_str = textToTranslate.replaceAll("[!@#$%^&*()|<>{}_+-]", "");
                    String trimed_str = textToTranslate.replaceAll("[`~!@#$%^&*()-_=+\\|}{][:;\'\"?/><.,]", "");
                    String[] dic_data = {
                            trimed_str,
                            String.valueOf(src_index + 1),
                            translatedText,
                            String.valueOf(tgt_index + 1),
                    };
                    FuncUtils.sendDicData(dic_data, new FuncUtils.SendDicDataCallback() {
                        @Override
                        public void onSuccess() {
                            getUpdatedDicData();
                        }

                        @Override
                        public void onError(String e) {

                        }
                    });
                }
            }else{
                mTextLists.remove(mTextLists.size()-1);
                refreshListView();
            }
        });
    }

    private void setInputFlagWithLanguageCode(){
        if(!detectedLanguage.equals(FuncUtils.gLanguageInputCls.text_scann_lang_code)){
            for(LanguageCls languase: Constants.getLanguages()){
                if(languase.text_scann_lang_code.equals(detectedLanguage)){
                    FuncUtils.gLanguageInputCls = languase;
                    txtBottombarEntry.setText(FuncUtils.gLanguageInputCls.language_title);

                    inputFlag = new File(FuncUtils.gLanguageInputCls.flag);
                    Picasso.get().load(inputFlag).fit().centerCrop().into(imgFlagEntry);
                    SharedPrefManager.setSharedPrefLanguageIn(FuncUtils.gLanguageInputCls.text_scann_lang_code);

                    TextCls textClsInput = new TextCls();
                    textClsInput.text = textToTranslate;
                    textClsInput.entryType = Constants.ENTRY_LANGUAGE;
                    textClsInput.language = FuncUtils.gLanguageInputCls.language;

                    int ind = mTextLists.size() - 2;
                    mTextLists.set(ind, textClsInput);
                    refreshListView();
                }
            }
        }
    }

    private void refreshListView() {
        textListAdapter.notifyDataSetChanged();
        int lastIndex = mTextLists.size() - 1;
        lstView.setSelection(lastIndex);
    }

    private void getUpdatedDicData() {
        FuncUtils.getDicDataWithOnline(this, new FuncUtils.LoadAppInfoErrorCallback() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onExpired() {
            }
        });
    }

    private void onBindAudio(String fName){
        FuncUtils.onBindAudio(fName, new FuncUtils.OnBindAudioCallback() {
            @Override
            public void onSuccess(byte[] audio) {
                mSpeaker.playVoiceByBytes(audio);
            }
            @Override
            public void onError(String e) {}
        });
    }

    private void outInputedText(int type) {

        TextCls textClsInput = new TextCls();
        textClsInput.text = textToTranslate;
        textClsInput.entryType = type;
        textClsInput.language = FuncUtils.gLanguageInputCls.language;
        mTextLists.add(textClsInput);

        refreshListView();

        translatedText = "";

        TextCls translatedTextCls = new TextCls();
        translatedTextCls.text = "";
        translatedTextCls.language = FuncUtils.gLanguageOutputCls.language;
        mTextLists.add(translatedTextCls);
        lastTextIndex = mTextLists.size() - 1;

        refreshListView();

        onCheckSQLite();
    }

    private void setVoiceSettings() {
        mPresenter.selectStyle(Constants.getLanguages(voice_out_index).voice_out_type_code);
        mPresenter.selectLanguage(Constants.getLanguages(voice_out_index).voice_out_lang_code);
    }

    @Override
    public void onKey() {

    }

    @Override
    public void setAdapterLanguage(String[] languages) {
        return;
    }

    @Override
    public void setSpinnerLanguage(final String[] _languages) {
        mPresenter.selectLanguage(Constants.getLanguages(voice_out_index).voice_out_lang_code);
    }

    @Override
    public String getSelectedLanguageText() {
        return Constants.getLanguages(voice_out_index).voice_out_lang_code;
    }

    @Override
    public void setAdapterStyle(String[] styles) {

    }

    @Override
    public void setSpinnerStyle(final String[] styles) {
        mPresenter.selectStyle(Constants.getLanguages(voice_out_index).voice_out_type_code);
    }

    @Override
    public void clearUI() {

    }

    @Override
    public String getSelectedStyleText() {
        return Constants.getLanguages(voice_out_index).voice_out_type_code;
    }

    @Override
    public void setTextViewGender(String gender) {
    }

    @Override
    public void setTextViewSampleRate(String sampleRate) {
    }

    @Override
    public int getProgressPitch() {
        return 2200;
    }

    @Override
    public int getProgressSpeakRate() {
        return 72;
    }

    @Override
    public void makeToast(String text, boolean longShow) {

    }

    private void onShowPrivacyDialog() {

        privacy_dialog = new PrivacyDialog(this, new PrivacyDialog.PrivacyDialogCallback() {
            @Override
            public void onClockOk() {
                SharedPrefManager.setPrivacyCheck();
                onDismissPrivacyDlg();
            }

            @Override
            public void onClockCancel() {
                onDismissPrivacyDlg();

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        privacy_dialog.setPrivacyLink(Constants.getPrivacyURL());
        privacy_dialog.show();
    }

    private void onDismissPrivacyDlg(){
        privacy_dialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPresenter != null){
            mPresenter.pauseSpeak();
        }
    }

    @Override
    protected void onResume() {
        setAdmobVisibility();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mPresenter.disposeSpeak();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        FuncUtils.exitDialog(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_VOICE_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String input_str = result.get(0);
                    textToTranslate = input_str.trim().replaceAll ("[`~!@#$%^&*()-_=+\\|}{][:;\'\"?/><.,]", "");

                    if(!textToTranslate.equals("")){
                        outInputedText(Constants.ENTRY_LANGUAGE);
                    }
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Constants.cropedImageUti = result.getUri();
                    startActivityForResult(new Intent(MainActivity.this, CropedImageActivity.class), REQ_CODE_MOVE_CROPIMAGE);
                }
                break;

            case REQ_CODE_MOVE_CROPIMAGE:
                if(!Constants.capturedText.equals("")){
                    textToTranslate = Constants.capturedText.trim().replaceAll ("[`~!@#$%^&*()-_=+\\|}{][:;\'\"?/><.,]", "");
                    outInputedText(Constants.ENTRY_LANGUAGE);
                }
                break;
        }
    }
}
