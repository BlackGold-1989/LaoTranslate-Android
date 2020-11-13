package com.laodev.translate.adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import com.laodev.translate.R;
import com.laodev.translate.classes.GeneralClasses.TextCls;
import com.laodev.translate.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class TextListAdapter extends BaseAdapter {

    private Context mContext;
    private List<TextCls> mDatas;

    private TextListAdapterCallback callback;

    public TextListAdapter(Context context, List<TextCls> _data, TextListAdapterCallback _callback) {
        this.mContext = context;
        this.mDatas = _data;
        this.callback = _callback;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(mContext).inflate(R.layout.list_texts, null);

        CardView cell = convertView.findViewById(R.id.card_cell);

        ImageView imgFlag = convertView.findViewById(R.id.img_text_cell_flag);
        TextView txtText = convertView.findViewById(R.id.txt_text);
        CardView cardViewSpeaker = convertView.findViewById(R.id.card_speaker);

        TextCls textCls = mDatas.get(position);
        txtText.setText(textCls.text);

        convertView.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        File flag = new File(Constants.getLanguages(textCls.language).flag);
        Picasso.get().load(flag).fit().centerCrop().into(imgFlag);

        if(textCls.text.equals("")){
            txtText.setTextColor(Color.BLACK);
            txtText.setText(Constants.string_translating);

            cell.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorGreyDestination));
            cardViewSpeaker.setVisibility(View.GONE);

            convertView.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }
        else{
            if(textCls.entryType == Constants.ENTRY_LANGUAGE || textCls.entryType == Constants.OUTPUT_LANGUAGE){
                txtText.setTextColor(Color.WHITE);
                cell.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                cardViewSpeaker.setVisibility(View.GONE);
            }else{
                txtText.setTextColor(Color.BLACK);
                cell.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorGreyDestination));
                boolean chk = false;
                for(int i=0; i < 12; i++){
                    if(txtText.getText().equals(Constants.string_unknown[i])) chk = true;
                }
                if(chk) cardViewSpeaker.setVisibility(View.GONE);
                else cardViewSpeaker.setVisibility(View.VISIBLE);

                cardViewSpeaker.setOnTouchListener((v, event) -> {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            cardViewSpeaker.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorBlue));
                            break;
                        case MotionEvent.ACTION_UP:
                            cardViewSpeaker.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                            if(textCls.voice_byServer){
                                callback.onPlayVoiceByURL(textCls.voice_url);
                            }else{
                                callback.onClickSpeaker(textCls.language, position);
                            }
                            break;
                    }
                    return true;
                });
            }

            cell.setOnLongClickListener(v -> {
                callback.onLongClickEntryCellEvent(position);
                return false;
            });
        }

        return convertView;
    }

    public interface TextListAdapterCallback {
        void onClickSpeaker(int languageIndex, int textIndex);
        void onLongClickEntryCellEvent(int index);
        void onPlayVoiceByURL(String url);
    }

}
