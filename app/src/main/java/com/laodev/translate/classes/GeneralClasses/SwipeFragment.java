package com.laodev.translate.classes.GeneralClasses;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.laodev.translate.R;
import com.laodev.translate.utils.Constants;

public class SwipeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View swipeView = inflater.inflate(R.layout.swipe_pager, container, false);

        TextView txt_top = swipeView.findViewById(R.id.txt_first);
        TextView txt_second = swipeView.findViewById(R.id.txt_second);
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        txt_top.setText(Constants.getAdver(position).title);
        txt_second.setText(Constants.getAdver(position).sub_title);
        return swipeView;
    }

    static SwipeFragment newInstance(int position) {
        SwipeFragment swipeFragment = new SwipeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        swipeFragment.setArguments(bundle);
        return swipeFragment;
    }
}
