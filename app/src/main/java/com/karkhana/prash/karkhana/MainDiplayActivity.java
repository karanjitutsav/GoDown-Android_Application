package com.karkhana.prash.karkhana;

/* This Activity is Greeting the user and Checking if the user has Verfified thier Email Address
* If the Email Address hasnot been Verfied then, the user will not be able to continue.*/
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainDiplayActivity extends AppCompatActivity {
    private Button Refresh;
    FirebaseAuth mAuth;
    private TextView Name;
    public ProgressDialog mProgressDialog;
    private String nameUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_diplay);

        Refresh = findViewById(R.id.Refresh);
        mAuth = FirebaseAuth.getInstance();


        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainDiplayActivity.class));

            }
        });


         nameUser = getIntent().getStringExtra("name");

        //starting new Intent when The Next Button is pressed
        findViewById(R.id.NEXT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainDiplayActivity.this, Display_post.class));
                finish();
            }
        });
    }


    //OnStart of the Application we get the user current status and update the UI
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser currentUser) {

        hideProgressDialog();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            // Name, email address, and profile photo Url
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            Name = (TextView) findViewById(R.id.getName);
            Name.setText(nameUser);

            //Checking if the email is verified
            boolean emailVerified = user.isEmailVerified();
            if (currentUser != null && emailVerified) {

                //if the email is verified than we display greetings and set the NEXT Button Clickable
                findViewById(R.id.VerifyText).setVisibility(View.GONE);
                findViewById(R.id.thankyou).setVisibility(View.VISIBLE);
                //findViewById(R.id.NEXT).setClickable(true);

            } else {
                //if the email is verified than we Verfiy Text and set the NEXT Button UnClickable
                findViewById(R.id.VerifyText).setVisibility(View.VISIBLE);
                findViewById(R.id.thankyou).setVisibility(View.GONE);
                //findViewById(R.id.NEXT).setClickable(false);
            }
        }
    }



    //Method to show the Process Dialog

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
