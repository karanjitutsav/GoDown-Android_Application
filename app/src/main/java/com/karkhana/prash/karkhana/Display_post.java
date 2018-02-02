package com.karkhana.prash.karkhana;


/* In this Activity we are actually goinf to show the ad posted by other user.
 * 1. Floating Button to add  new Post
  * 2. Slide Navigation Bar to Sign Out, view, Settings.
  * */
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karkhana.prash.karkhana.Adapter.AdAdapter;
import com.karkhana.prash.karkhana.JavaClasses.FirebaseHelper;
import com.karkhana.prash.karkhana.JavaClasses.New_Ad_Post;
import com.karkhana.prash.karkhana.Menu.Drawer.Fragments.Profile_settings;
import com.karkhana.prash.karkhana.activities.LoginActivity;
import com.karkhana.prash.karkhana.activities.PostNewAd;
import com.karkhana.prash.karkhana.JavaClasses.Profile_Data;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Display_post extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static String TAG = "DISPLAY_POST";

    //Key for Bundle
    private final static String key = "userData";
    private Profile_Data uInfo;
    Bundle bundle;

    //variables for photo upload
    private static final int GALLERY_REQUEST = 2;
    Uri uri;
    Uri resultUri;

    //Recyler View Adapter nd Object
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    FloatingActionButton fab;

    private List<New_Ad_Post> adItems;

    //Progress Dialog Variable
    ProgressDialog mProgressDialog;

    //Navigaton Name and Area of the user
    private TextView user_Name;
    private TextView user_area;
    private CircleImageView imageView;


    //Database Reference
    private FirebaseHelper firebaseHelper;
    private DatabaseReference mDatabase;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference mDatabaseUser;
    private String userID;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Floating button to start the Post new AD Activity
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getting the image using an Intent
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_PICK);
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });


        //finding the Side Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigATION View of the Side Displaying user Photo and Email Address
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        user_Name = headerView.findViewById(R.id.user_name);
        user_area = headerView.findViewById(R.id.user_area);
        imageView = (CircleImageView)headerView.findViewById(R.id.user_profile);


        //Database Reference for getting the Ad Posted and Populating he Recyler View
        mDatabase = FirebaseDatabase.getInstance().getReference().child("/AD Posts");
        mDatabase.keepSynced(true);


        adItems = new ArrayList<>(); //initializing the Array List for Objects

        //Getting the recyler view
        recyclerView = findViewById(R.id.recyler_ad_posts);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        //Getting the AdPost saved in the Firebase and Displaying it to the User in CardView
        DisplayRecyclerAdPost();
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdAdapter(adItems, Display_post.this);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0) {
                    fab.show();

                } else if (dy > 0) {
                    fab.hide();
                }
            }
        });
        // ending recyler view

        //Checking if the user is Currenty signed In or Not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed in
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
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


    @Override
    protected void onStart() {
        super.onStart();
        //Getting the suer Information from the Firebase and Storing it in an Object of Class Profile Data
        getUserNameArea();

    }

    private void DisplayRecyclerAdPost() {

        showProgressDialog();
        //Initializing list and Adding Items to the Array List


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                //Getting the Value in Database and Saving it to the Class using Object
                for(DataSnapshot postSnapshot: ds.getChildren()) {
                    New_Ad_Post ads = postSnapshot.getValue(New_Ad_Post.class);

                    Log.d("Display_post", "Object Ads: " + ads.getTitle());

                    adItems.add(ads);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Error Fetching Data", Toast.LENGTH_SHORT).show();
            }
        });


        hideProgressDialog();

    }


    private void getUserNameArea() {

        //Declaring new Reference Object of Firebase for the Profile Data to get the user Name and area
        //Here we area getting ths user name and area to display in the Navigation Header

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();

        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                uInfo = dataSnapshot.child("Users").child(userID).getValue(Profile_Data.class);
                Log.d("Display_post", "User Name: " + uInfo.getFirst_name());
                Log.d("Display_post", "User Area: " + uInfo.getArea());

                String name = uInfo.getFirst_name();
                String area = uInfo.getArea();
                user_Name.setText(name);
                user_area.setText(area);
                Uri uri = Uri.parse(uInfo.getProfilePic());
                Picasso.with(Display_post.this)
                        .load(uri)
                        .into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("Display_post", "Error Fetching Data From Server " + "Database Error");
            }
        });

    }



    //Closing the Navigation Bar on backPressed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Getting the Toolbar and Inflating it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_bar) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        android.support.v4.app.Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            //Starting the Post new Ad Activity
            startActivity(new Intent(getApplicationContext(), PostNewAd.class));

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.message_me) {

        } else if (id == R.id.user_profile) {
            mAuth = FirebaseAuth.getInstance();
            final FirebaseUser user = mAuth.getCurrentUser();
            String email = "";
            if (user != null) {
                email = user.getEmail();

            }

            bundle = new Bundle();
            bundle.putString("Email", email);
            bundle.putSerializable(key, uInfo);
            fragment =  new Profile_settings();
            fragment.setArguments(bundle);
        } else if (id == R.id.sign_out) {

            mAuth.signOut();
            startActivity(new Intent(Display_post.this, LoginActivity.class));
            finish();


        }

        if(fragment != null) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.framelayout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                String stringUri = resultUri.toString();
                Intent newPostImage = new Intent(getApplicationContext(), PostNewAd.class);
                newPostImage.putExtra("Image", stringUri);
                startActivity(newPostImage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(Display_post.this, error.toString() , Toast.LENGTH_SHORT).show();
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
