package com.karkhana.prash.karkhana.Menu.Drawer.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.karkhana.prash.karkhana.JavaClasses.New_Ad_Post;
import com.karkhana.prash.karkhana.JavaClasses.Profile_Data;
import com.karkhana.prash.karkhana.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Profile_settings extends Fragment {

    //Key for Bundle
    private final static String key = "userData";
    private Profile_Data user;
    //Variables
    private CircleImageView Profile_Photo;
    private TextView User_Name;
    private TextView User_PhoneNum;
    private TextView User_Email;


    public static Profile_settings newInstance(Profile_Data user){
        Profile_settings fragment = new Profile_settings();
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, user);
        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        user = (Profile_Data)getArguments().getSerializable(key);


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_settings, null);



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Profile_Photo = view.findViewById(R.id.user_profile_photo);
        User_Name = view.findViewById(R.id.profilesettings_username);
        User_Email = view.findViewById(R.id.profilesettings_userEmail);
        User_PhoneNum = view.findViewById(R.id.profilesettings_userPhone);

        //Here we get the values from the bundle that was just passed from the Activity

        Picasso.with(getContext()).load(user.getProfilePic()).into(Profile_Photo);
        User_Name.setText(user.getFirst_name());
        User_Email.setText(getArguments().getString("Email"));
        User_PhoneNum.setText(user.getPhoneNumber());

       // Log.d("Profile Settings", "User Email from the Profile Data Class: "+ userData.getFirst_name());

        getActivity().onBackPressed();
    }


}
