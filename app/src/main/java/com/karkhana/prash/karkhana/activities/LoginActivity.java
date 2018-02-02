package com.karkhana.prash.karkhana.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karkhana.prash.karkhana.Display_post;
import com.karkhana.prash.karkhana.MainDiplayActivity;
import com.karkhana.prash.karkhana.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    //Text Field
    private EditText UserName;
    private EditText Password;

    //Button Field
    Button Login;
    Button Sign_up;

    //Contexts
    ProgressBar progressBar;
    private String userID;
    public ProgressDialog mProgressDialog;



    //add Firebase Database stuff

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserName = (EditText) findViewById(R.id.login_email);
        Password = (EditText)findViewById(R.id.login_password);
        Login = (Button)findViewById(R.id.btn_login);
        Sign_up = (Button)findViewById(R.id.btn_sign_up);
        progressBar = (ProgressBar)findViewById(R.id.Progressbar);

        mAuth = FirebaseAuth.getInstance(); //getting an Instance of the FireBase



        Login.setOnClickListener(this);    //Setting Click Listener
        Sign_up.setOnClickListener(this);  //Setting Click Listener

        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();   //Getting Current User
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("/Users");  //Getting Reference to the Child User of the Firebase
        mDatabase.keepSynced(true);   //Keeps the Database Synced

        //Checking if the user is not null
        if (user != null) {
            userID = user.getUid();
        }

        //Checking if the user is Currenty signed In or Not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed in
                    startActivity(new Intent(getApplicationContext(), SignupActivity.class)); //if the user is Null we direct to Sign Up Part
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }
                // ...
            }
        };




    }

    //Button Method to implement different Buttons
    @Override
    public void onClick(View view) {

        if(view == Login){
            signinUser();
        }


        else if(view == Sign_up ) {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        }

    }


    //Signing in User when the user presses the Login Button
    private void signinUser() {
        String username = UserName.getText().toString().trim();   //getting the email of the user type in
        String password = Password.getText().toString().trim();  //getting the password typed by the user

        //we Check is the text field is not Empty
        if (!validateForm(username, password)) {
            return;
        }


        showProgressDialog();

        //Firebase Method to Signin the User
        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {
                    hideProgressDialog();
                    Intent intent = new Intent(getApplicationContext(), Display_post.class);  //We direct to the Main Activity of the User is Signed In
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   //This Clears all the Activity that is Open
                    startActivity(intent);

                }
                else{
                    hideProgressDialog();
                    makeToast(task.getException().getMessage());
                }

            }
        });




    }

    //Method to check if it is a Valid email Address and the textfield isn't empty
    private boolean validateForm(String username, String password) {
        if(username.isEmpty()) {
            UserName.setError("Email is Required");
            UserName.requestFocus();
            return false;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            UserName.setError("Enter a Valid Email");
            UserName.requestFocus();
            return false;

        }
        else if(password.isEmpty()){
            Password.setError("Password Required");
            Password.requestFocus();
            return false;
        }
        else if(password.length() < 6){
            Password.setError("Password less than 6 characters");
            Password.requestFocus();
            return false;
        }
      return true;
    }


    //During the start of the Application we will get the user Current User status if
    //the user is LOgged in, we will continue to Display Activity, to display Ads
    @Override
    public void onStart() {
        super.onStart();

        //Firebase Object to get User Status
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    //Class to Update the UI by checking the user Status
    private void updateUI(FirebaseUser currentUser) {

        hideProgressDialog();
        if( currentUser != null){
            startActivity(new Intent(getApplicationContext(), Display_post.class));

            finish();
            makeToast("Signed In");
        }

        else
        {
            Login.setVisibility(View.VISIBLE);
            UserName.setVisibility(View.VISIBLE);
            Password.setVisibility(View.VISIBLE);
            Sign_up.setVisibility(View.VISIBLE);

        }

    }


    //THe Following Method will hide or show Progress Bar Programm

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




    public void makeToast(String toast){
        Toast.makeText(LoginActivity.this, toast,
                Toast.LENGTH_SHORT).show();

    }
}

