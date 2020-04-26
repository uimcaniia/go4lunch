package com.uimainon.go4lunch.controllers.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.api.PreferenceHelper;
import com.uimainon.go4lunch.controllers.activities.ProfileActivity;

import java.util.Objects;

public class SettingFragment extends Fragment {


    private CheckBox monday;
    private CheckBox tuesday;
    private CheckBox wednesday;
    private CheckBox thursday;
    private CheckBox friday;
    private CheckBox saturday;
    private CheckBox sunday;
    private int hour;
    private int minute;
    private  TimePicker mTimepicker;
    private String idUser;
    private Button mButtonDeleteAccount;

    com.getbase.floatingactionbutton.FloatingActionButton mAddButton;

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        monday = (CheckBox)rootView.findViewById(R.id.notification_monday);
        tuesday = (CheckBox)rootView.findViewById(R.id.notification_tuesday);
        wednesday = (CheckBox)rootView.findViewById(R.id.notification_wednesday);
        thursday = (CheckBox)rootView.findViewById(R.id.notification_thursday);
        friday = (CheckBox)rootView.findViewById(R.id.notification_friday);
        saturday = (CheckBox)rootView.findViewById(R.id.notification_saturday);
        sunday = (CheckBox)rootView.findViewById(R.id.notification_sunday);
        configBtnDeleteAccount(rootView);
        configBtnvalidPref(rootView);

        Bundle bundle=getArguments();
        assert bundle != null;
        this.idUser = bundle.getString("idUser");

        if(bundle.getInt("monday") == 1)
            this.monday.setChecked(true);
        if(bundle.getInt("tuesday") == 1)
            this.tuesday.setChecked(true);
        if(bundle.getInt("wednesday") == 1)
            this.wednesday.setChecked(true);
        if(bundle.getInt("thursday") == 1)
            this.thursday.setChecked(true);
        if(bundle.getInt("friday") == 1)
            this.friday.setChecked(true);
        if(bundle.getInt("saturday") == 1)
            this.saturday.setChecked(true);
        if(bundle.getInt("sunday") == 1)
            this.sunday.setChecked(true);
      //  System.out.println( this.tuesday);
       this.hour = bundle.getInt("hour");
        this.minute = bundle.getInt("minute");
        initHour(rootView, hour, minute);

        return rootView;
    }

    private void configBtnDeleteAccount(View rootView) {
        mButtonDeleteAccount = (Button)rootView.findViewById(R.id.confirm_delete_profil);
        mButtonDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.popup_message_confirmation_delete_account)
                        .setPositiveButton(R.string.popup_message_choice_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((ProfileActivity) Objects.requireNonNull(getActivity())).deleteUserFromFirebase();
                            }
                        })
                        .setNegativeButton(R.string.popup_message_choice_no, null)
                        .show();

            }
        });
    }

    private void initHour(View drawer, int hour, int minute) {
/*        System.out.println(hour);*/
        mTimepicker = drawer.findViewById(R.id.timePicker);
        mTimepicker.setIs24HourView(true);
        mTimepicker.setCurrentHour(hour);
        mTimepicker.setCurrentMinute(minute);
        mTimepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hour, int minute) {
                hour = hour;
                minute = minute;
            }
        });
    }

    private void configBtnvalidPref(View rootView) {
        mAddButton = rootView.findViewById(R.id.btn_valid_preference);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                System.out.println(mTimepicker.getCurrentHour());*/
                PreferenceHelper.updatePreferenceNotification("hour", idUser, mTimepicker.getCurrentHour());
                PreferenceHelper.updatePreferenceNotification("minute", idUser, mTimepicker.getCurrentMinute());
                if(monday.isChecked()){
                    PreferenceHelper.updatePreferenceNotification("monday", idUser, 1);
                }else{
                    PreferenceHelper.updatePreferenceNotification("monday", idUser, 0);
                }
                if(tuesday.isChecked()){
                    PreferenceHelper.updatePreferenceNotification("tuesday", idUser, 1);
                }else{
                    PreferenceHelper.updatePreferenceNotification("tuesday", idUser, 0);
                }
                if(wednesday.isChecked()){
                    PreferenceHelper.updatePreferenceNotification("wednesday", idUser, 1);
                }else{
                    PreferenceHelper.updatePreferenceNotification("wednesday", idUser, 0);
                }
                if(thursday.isChecked()){
                    PreferenceHelper.updatePreferenceNotification("thursday", idUser, 1);
                }else{
                    PreferenceHelper.updatePreferenceNotification("thursday", idUser, 0);
                }
                if(friday.isChecked()){
                    PreferenceHelper.updatePreferenceNotification("friday", idUser, 1);
                }else{
                    PreferenceHelper.updatePreferenceNotification("friday", idUser, 0);
                }
                if(saturday.isChecked()){
                    PreferenceHelper.updatePreferenceNotification("saturday", idUser, 1);
                }else{
                    PreferenceHelper.updatePreferenceNotification("saturday", idUser, 0);
                }
                if(sunday.isChecked()){
                    PreferenceHelper.updatePreferenceNotification("sunday", idUser, 1);
                }else{
                    PreferenceHelper.updatePreferenceNotification("sunday", idUser, 0);
                }

            }
        });
    }

}
