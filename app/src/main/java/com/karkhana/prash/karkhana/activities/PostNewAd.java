package com.karkhana.prash.karkhana.activities;

//In this Activity we are going to do the following
// 1. Let user choose one phone
// 2. Set titile, Descriptiomn for the photo
// 3. Upload it to the databse and update the user profile with the uploaded photo with description
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karkhana.prash.karkhana.Display_post;
import com.karkhana.prash.karkhana.JavaClasses.New_Ad_Post;
import com.karkhana.prash.karkhana.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostNewAd extends AppCompatActivity {

    //variables for photo upload
    private static final int GALLERY_REQUEST = 2;
    private Uri uri = null;
    private Uri resultUri= null;
    private double mPhotoUploadProgress = 0;

    //textid and button
    private ImageButton imgButton;
    EditText title_name;
    EditText description;
    Spinner Category;
    EditText AreaOfPost;
    private ImageView imgView;
    EditText PriceOfItem;
    static int count;

    //firebse contexts
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference newPost;
    private DatabaseReference mDatabaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        title_name = (EditText) findViewById(R.id.title_for_post);
        description = (EditText) findViewById(R.id.description_for_post);
        Category = (Spinner) findViewById(R.id.category_of_post);
        imgView = (ImageView) findViewById(R.id.post_image);
        PriceOfItem = findViewById(R.id.Price_for_item);
        AreaOfPost = (EditText) findViewById(R.id.Area_of_Post);


        Button PostBottonSubmit = findViewById(R.id.post_submit_btn);
        PostBottonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostButtonSubmitted(v);
            }
        });
        //Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        //Database Reference
        database = FirebaseDatabase.getInstance();
        newPost = database.getReference("/AD Posts");  //we get reference to the Child Ad Post in Database to store the new post

        //We get the Image Uri when User presses the Camera Button on the Display Post
        resultUri = Uri.parse(getIntent().getStringExtra("Image"));
        if (resultUri != null) {

            //We use Picasso we Load into ImageView
            Picasso.with(getApplicationContext())
                    .load(resultUri)
                    .into(imgView);

        }
    }

    public void ImageButtonClicked(View view) {

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
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null &&data.getData() != null){

            uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);



        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Picasso.with(getApplicationContext())
                        .load(resultUri)
                        .into(imgView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(PostNewAd.this, error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void PostButtonSubmitted(View view) {

        //We get the Text Typed by the User on the TextFields
        final String Title = title_name.getText().toString().trim();
        final String title_Des = description.getText().toString().trim();
        final String Cat = Category.toString().trim();
        final String Area = AreaOfPost.getText().toString().trim();
        final String Price = PriceOfItem.getText().toString().trim();

        if(!TextUtils.isEmpty(Title) && !TextUtils.isEmpty(title_Des) &&!TextUtils.isEmpty(Price) && resultUri != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);

            //bringing up the progress dailog
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the user unique ID
            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //we Get Reference to the User's Saved Data on the Database
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            //Getting a reference to the DATABASE storage for the photo
            final StorageReference ref = storageReference.child("Photos").child(user_id).child(resultUri.getLastPathSegment() );


            final DatabaseReference upload_Newpost = newPost.push(); //This is wil create unique key for each Post -- newPost - reference to AdPost in Database

            //adding a onSuccess Listener and Updating the userDatabase
            ref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();

                       final Uri downloadurl = taskSnapshot.getDownloadUrl(); //geting the url to the image just uploaded
                       Toast.makeText(PostNewAd.this,"Uploaded Successful",Toast.LENGTH_SHORT).show();




                    upload_Newpost.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String key = dataSnapshot.getKey(); //getting the unique key of the particular post to use later for user to save the post
                            //We put all the information in a object and save all at once

                            final New_Ad_Post ad_Details = new New_Ad_Post(Title, title_Des, Price, Cat, downloadurl.toString(), user_id, Area, key);

                            //adding the values to the users Database
                            upload_Newpost.setValue(ad_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        title_name.setText("");
                                        description.setText("");
                                        PriceOfItem.setText("");
                                        AreaOfPost.setText("");

                                        startActivity(new Intent(PostNewAd.this, Display_post.class));
                                        finish();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            Log.d("PostNewD", "Error Saving Data In Firebase");
                        }
                    });


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PostNewAd.this,"Upload Failed",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            if(progress - 15 > mPhotoUploadProgress)
                            {
                                progressDialog.setMessage("Upload Progress " + String.format("%.0f", progress) + "%");
                                mPhotoUploadProgress = progress;
                            }

                        }
                    });
        }else
            Toast.makeText(getApplicationContext(), "Fill all Box", Toast.LENGTH_SHORT).show();
    }
}
