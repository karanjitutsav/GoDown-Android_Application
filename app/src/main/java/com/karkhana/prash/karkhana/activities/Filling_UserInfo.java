package com.karkhana.prash.karkhana.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karkhana.prash.karkhana.JavaClasses.FirebaseHelper;
import com.karkhana.prash.karkhana.JavaClasses.Profile_Data;
import com.karkhana.prash.karkhana.MainDiplayActivity;
import com.karkhana.prash.karkhana.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Filling_UserInfo extends AppCompatActivity {

    private EditText FirstName;
    private Button next;
    private EditText Address;
    private EditText PhoneNumber;
    private EditText City;
    private EditText Area;

    ImageView profilePic;
    public ProgressDialog mProgressDialog;
    private String userID;

    //Obejct to get Firebase Helper Methods
    FirebaseHelper firebaseHelper;

    //variables for photo upload
    private static final int GALLERY_REQUEST = 2;
    Uri resultUri;

    //add Firebase Database stuff
    private DatabaseReference mFirebaseDatabase;
    //firebse contexts
    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filling__user_info);

        FirstName = findViewById(R.id.First_Name);
        Address = findViewById(R.id.Address);
        PhoneNumber = findViewById(R.id.PhoneNumber);
        City = findViewById(R.id.City);
        Area = findViewById(R.id.Area);
        next = findViewById(R.id.next);
        profilePic= findViewById(R.id.profilePic);


        //getting the user unique ID
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();

        }
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseHelper = new FirebaseHelper(mFirebaseDatabase);

        //Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();


        Intent email = getIntent();

        if(email != null){

            String Email = email.getStringExtra("Email");
        }



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                //Declaring Strings for the User Information
                final String name = FirstName.getText().toString().trim();
                final String address = Address.getText().toString().trim();
                final String phoneNumber = PhoneNumber.getText().toString().trim();
                final String city = City.getText().toString().trim();
                final String area = Area.getText().toString().trim();

                if(name.isEmpty() || !Character.isAlphabetic(name.charAt(0))) {
                    FirstName.setError("Name Required");
                    FirstName.requestFocus();
                    return;

                }

                if(area.isEmpty()) {
                    Area.setError("Name Required");
                    Area.requestFocus();
                    return;

                }
                if(phoneNumber.length() < 10 || phoneNumber.isEmpty())
                {
                    PhoneNumber.setError("Invalid Phone Number");
                    PhoneNumber.requestFocus();
                    return;
                }

                //Getting a reference to the DATABASE storage for the photo from User's Photo
                if(resultUri.getLastPathSegment() != null){

                    //saving the reference to the Database
                final StorageReference ref = storageReference.child("Profile Photo").child(userID).child(resultUri.getLastPathSegment() );


                Log.d("Filling User Info", "Dsiaplying name of user: " + name);

                //We set a Listener of the photo is saved properly

                ref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadurl = taskSnapshot.getDownloadUrl();

                        //Putting Details of the User in an Object and Storing in Firebase
                        final Profile_Data userInformation = new Profile_Data(name, address, area, city, phoneNumber, System.currentTimeMillis(), downloadurl.toString());

                        //Calling a Firebase Helper Function to Directly save User Information
                        Boolean save = firebaseHelper.SaveUser(userInformation);


                        //if it is saved then we set a Flag and call next activity
                        if(save) {
                            FirstName.setText("");
                            Address.setText("");
                            PhoneNumber.setText("");
                            City.setText("");
                            Area.setText("");

                            hideProgressDialog();
                            Intent mainActivity = new Intent(Filling_UserInfo.this, MainDiplayActivity.class);
                            mainActivity.putExtra("name", userInformation.getFirst_name());
                            startActivity(mainActivity);
                            finish();
                        }else{
                            hideProgressDialog();
                            makeToast("Check all the Fields");
                        }
                    }
                });
                }else
                    makeToast("Select Profile Picture");
            }
        });


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
        Toast.makeText(Filling_UserInfo.this, toast,
                Toast.LENGTH_SHORT).show();
        toast = " ";

    }



//We call this Function when user presses the Profile Photo Option
    public void updateUserProfilePhoto(View view) {

        //getting the image using an Intent
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting the image botton to the selected image
        //We check if the result code is okay and request code matches our request Code
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null &&data.getData() != null){

            Uri uri = data.getData();
            //After we get the Image Uri we use CropImage Activity to Crop the Image
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)  //Setting aspect ratio as Circle
                    .start(this);



        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            //if the Result Code is Okay from CropImage Activity we Proceed to load into the Image View
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.with(getApplicationContext())
                        .load(resultUri)
                        .into(profilePic);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(Filling_UserInfo.this, error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
    }
}
