package com.karkhana.prash.karkhana.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karkhana.prash.karkhana.MainDiplayActivity;
import com.karkhana.prash.karkhana.R;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";


    private EditText Password;
    private EditText Email;
    private ProgressBar progressBar;
    public ProgressDialog mProgressDialog;


    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    Button Login;
    Button Sign_up;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        Password = (EditText)findViewById(R.id.signup_password);

        progressBar = (ProgressBar)findViewById(R.id.signup_Progressbar);
        Sign_up = (Button) findViewById(R.id.btn_signUp);
        Email = (EditText)findViewById(R.id.signup_email);




        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();



        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser(view);
            }
        });



    }

    public void RegisterUser(View view) {
        final String username = Email.getText().toString().trim();  //Getting UserEmail and Password to String
        String password = Password.getText().toString().trim();


        //Validating the Type Field is not Empty
        if (!validateForm(username, password)) {
            return;
        }



     //here we are creating the field for the user

        showProgressDialog();


        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            makeToast("SignUp Successful");
                            sendEmailVerification();        //Sending a Verification Email to the user
                            Intent intent = new Intent(SignupActivity.this, Filling_UserInfo.class); //Directing to the activity to fill up Information
                            intent.putExtra("Email", username);
                            hideProgressDialog();
                            startActivity(intent);

                            finish();


                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.

                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                hideProgressDialog();
                                makeToast("Authentication failed.Already Registered");
                            }
                            else {
                                hideProgressDialog();
                                String Exception =task.getException().getMessage();
                                makeToast(Exception);
                            }

                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private boolean validateForm(String username, String password) {

        //Cheking if it is emppty or not a correct email address
        if(username.isEmpty()) {
            Email.setError("Email is Required");
            Email.requestFocus();
            return false;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            Email.setError("Enter a Valid Email");
            Email.requestFocus();
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

        return  true;
    }




    private void sendEmailVerification() {


        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {

            //if the Current user is not Null we send Verification Email through Firebase Method
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // [START_EXCLUDE]
                            // Re-enable button
                            if (task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(SignupActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // [END_EXCLUDE]
                        }
                    });
        }
    }



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
        Toast.makeText(SignupActivity.this, toast,
                Toast.LENGTH_SHORT).show();

    }
}
