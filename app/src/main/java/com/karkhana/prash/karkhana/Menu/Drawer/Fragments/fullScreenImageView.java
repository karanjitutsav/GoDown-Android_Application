package com.karkhana.prash.karkhana.Menu.Drawer.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.facebook.drawee.view.SimpleDraweeView;
import com.karkhana.prash.karkhana.R;

public class fullScreenImageView extends Fragment {



    private View mDecorView;
    SimpleDraweeView draweeView;
    Uri uri;

    public fullScreenImageView() {
        // Required empty public constructor
    }

    public static fullScreenImageView newInstance(String param1) {
        fullScreenImageView fragment = new fullScreenImageView();
        Bundle args = new Bundle();
        args.putString("Uri", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String uirImage = getArguments().getString("Uri");

        uri = Uri.parse(uirImage);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mDecorView = inflater.inflate(R.layout.frescofullscreenimage, container, false);
        return mDecorView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         draweeView =view.findViewById(R.id.my_image_view);

        draweeView.setImageURI(uri);
        //hideSystemUI();


    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }



}
