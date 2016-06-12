package info.androidhive.materialtabs.fragments;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.MapsActivity;


public class OneFragment extends Fragment {


    private Button Enter_btn;
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_one, container, false);
        //setEnter_btn((Button) v.findViewById(R.id.enter_to_shift_button));
        return v;
    }
    public Button getEnter_btn() {
        return Enter_btn;
    }

    public void setEnter_btn(Button enter_btn) {
        Enter_btn = enter_btn;
    }
    public void set_Enter_Button_text(String str){
        Enter_btn= (Button)getView().findViewById(R.id.enter_to_shift_button);
        Enter_btn.setText(str.toString());
    }


}
