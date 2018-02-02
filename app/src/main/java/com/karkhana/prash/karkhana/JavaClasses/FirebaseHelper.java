package com.karkhana.prash.karkhana.JavaClasses;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by prash on 1/10/2018.
 */

public class FirebaseHelper {

    //Database Reference
    private FirebaseDatabase firebaseDatabase;
    private static FirebaseAuth mAuth;
    private static DatabaseReference myRef;
    private static String userID;


    static ArrayList<String> userInfo = new ArrayList<>();

    private Boolean saved = false;

    public FirebaseHelper() {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
    }

    public FirebaseHelper(DatabaseReference userRef) {
        myRef = userRef;

    }


    public Boolean SaveUser(Profile_Data profile_data){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        }
        try{

            myRef.child("Users").child(userID).setValue(profile_data);
            return true;
        }catch (DatabaseException e){
            e.printStackTrace();
            return false;
        }


    }

    private static void fetchData(DataSnapshot ds){


            Profile_Data userData = ds.getValue(Profile_Data.class);

        if (userData != null) {
            userInfo.add(userData.getFirst_name());
            userInfo.add(userData.getAddress());
            userInfo.add(userData.getArea());
            userInfo.add(userData.getPhoneNumber());
            userInfo.add(userData.getCity());

        }
    }


    public static void getUserData(){

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("Display_post", "Error Fetching Data From Server " + "Database Error");
            }
        });

    }

    public static ArrayList<String> getUserInfo() {
        return userInfo;
    }


}
