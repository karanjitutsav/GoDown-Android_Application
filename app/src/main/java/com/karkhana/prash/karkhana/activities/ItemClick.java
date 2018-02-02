package com.karkhana.prash.karkhana.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karkhana.prash.karkhana.JavaClasses.New_Ad_Post;
import com.karkhana.prash.karkhana.JavaClasses.Profile_Data;
import com.karkhana.prash.karkhana.Manifest;
import com.karkhana.prash.karkhana.Menu.Drawer.Fragments.ImmersiveModeFragment;
import com.karkhana.prash.karkhana.Menu.Drawer.Fragments.fullScreenImageView;
import com.karkhana.prash.karkhana.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemClick extends AppCompatActivity{

    private TextView Titile, Description,userName;
    private ImageView imageView, profilepic;
    //Key for Bundle
    private final static String key = "userData";

    public  View decorView;

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;

    String SEND = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReciever, smsDeliveredReciever;

    String IdUser;

     Toolbar toolbar;

     //AlertDialog Box
     EditText messageToUser;
     String message;
     Button sendMessage;
     Button sendAutomqticMessage;

    Profile_Data userDataFromDatabase;
    New_Ad_Post postDetails;


    //Firebase Contexts
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabasePost;
    DatabaseReference mUserDatbase;
    public static final String FRAGTAG = "ImmersiveModeFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        decorView = getLayoutInflater().inflate(R.layout.activity_item_click, null);
        setContentView(decorView);

        toolbar = findViewById(R.id.toolbarItem);
        setSupportActionBar(toolbar);

        Titile = findViewById(R.id.PostTitle);
        Description = findViewById(R.id.PostDescription);
        imageView = findViewById(R.id.postImage);
        userName = findViewById(R.id.UserName);
        profilepic = findViewById(R.id.user_profile_pic);

        //These intents are used to broadcast a message to other user
        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SEND), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);


        //Firebase Objects Initialization
        mAuth = FirebaseAuth.getInstance();


        //Initializing Fresco
        Fresco.initialize(this);

        //calling a fragment for Immersive Mode
        if (getSupportFragmentManager().findFragmentByTag(FRAGTAG) == null ) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ImmersiveModeFragment fragment = new ImmersiveModeFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }

        //We get The Value put in the Intent while calling this activity
        Intent getItemClick = getIntent();

        if(getItemClick != null){


             postDetails = (New_Ad_Post) getItemClick.getSerializableExtra(key);
            Log.d("ItemClick", "Object Ads: " + postDetails.getTitle());



            //Toast.makeText(ItemClick.this, refKey, Toast.LENGTH_LONG).show();
            getSupportActionBar().setTitle(postDetails.getTitle());
            toolbar.setLogoDescription(postDetails.getTitle());

            //setting the Text Fields
            Titile.setText(postDetails.getTitle());
            Description.setText(postDetails.getDescription());
            Picasso.with(getApplicationContext())
                    .load(postDetails.getImage())
                    .into(imageView);


            imageView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {

                    hideSystemUI();
                    //Opening the image in another fragment
                    String uriImage = (postDetails.getImage());
                    Bundle bundle = new Bundle();
                    bundle.putString("Uri", uriImage);

                    //Initializing Fragment
                    Fragment fragment =  new fullScreenImageView();
                    fragment.setArguments(bundle);

                    //Fragment Manager to get Fragment Support
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    //Replacing the Layout of the main activity
                    fragmentTransaction.replace(R.id.relativeLayout, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            //displaying userinformation
            IdUser = postDetails.getUserId();

            displayUserInfo(IdUser);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ImmersiveModeFragment fragment = new ImmersiveModeFragment();
            transaction.add(fragment, FRAGTAG);
            transaction.commit();

        }
    }

    private void displayUserInfo(String idUser) {

        //This method will give us the information of the User who uploaded the post using the ID saved in the Database of the Post
        mUserDatbase = FirebaseDatabase.getInstance().getReference().child("/Users").child(idUser);

        mUserDatbase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userDataFromDatabase = dataSnapshot.getValue(Profile_Data.class);

                if (userDataFromDatabase != null) {
                    userName.setText(userDataFromDatabase.getFirst_name());
                    Picasso.with(getApplicationContext())
                            .load(userDataFromDatabase.getProfilePic())
                            .into(profilepic);

                    //Here we get the History of the User Member Joined the App
                    Long date = System.currentTimeMillis();
                    Long Date = userDataFromDatabase.getmDateTime();

                    Long UserMemberHistory = (date - Date);
                    UserMemberHistory /= (1000*60*60*24);

                    TextView dayofpost = findViewById(R.id.DayofPost);
                    dayofpost.setText(UserMemberHistory.toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("ItemClick", "Can't get User Information");
            }
        });

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.item_activity, menu);
        return true;
    }


    public void CalltheUser(View view) {

        //We use Intent to call System App to Direct to Phone App from saved Phone Number

        String PhNumber = userDataFromDatabase.getPhoneNumber();
        if (!PhNumber.isEmpty()) {
            String dail = "tel:" + PhNumber;
            Intent weIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(dail));

            //We check if the Intent is safe
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(weIntent, PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;
            if (isIntentSafe)
                startActivity(weIntent);
        }
    }

    public void MessageTheUser(View view) {

        //Building a Dialog Box to allow user send automatic message and type user's message
        AlertDialog.Builder dialog = new AlertDialog.Builder(ItemClick.this);
        View mView= getLayoutInflater().inflate(R.layout.dialoglayout, null);
        messageToUser = mView.findViewById(R.id.messagetoUser);
        message = messageToUser.getText().toString().trim();
        sendMessage = mView.findViewById(R.id.SendMessage);
        sendAutomqticMessage = mView.findViewById(R.id.automaticMessage);


        dialog.setView(mView);
        AlertDialog dialog1 = dialog.create();
        dialog1.show();


    }


    @Override
    protected void onResume() {
        super.onResume();

        //Pending Intent is used to check if the message is send
        smsSentReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               switch (getResultCode())
               {
                   case Activity.RESULT_OK:
                         makeToast("SMS Sent");
                         break;

                   case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                       makeToast("Generic Failure!");
                       break;
                   case SmsManager.RESULT_ERROR_NO_SERVICE:
                       makeToast("No Service!");
                       break;
                   case SmsManager.RESULT_ERROR_NULL_PDU:
                       makeToast("Null PDU");
                       break;
                   case SmsManager.RESULT_ERROR_RADIO_OFF:
                       makeToast("Radio OFF");
                       break;
               }
            }
        };

        smsDeliveredReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        makeToast("SMS Deliverd");
                        break;

                    case Activity.RESULT_CANCELED:
                        makeToast("SMS Not Delivered");
                        break;
                }
            }
        };

        registerReceiver(smsSentReciever, new IntentFilter(SEND));
        registerReceiver(smsDeliveredReciever, new IntentFilter(DELIVERED));
    }


    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsDeliveredReciever);
        unregisterReceiver(smsSentReciever);
    }


    public void sendAutomaticMessage(View view) {

        //This method is called when the user presses the automatic message button for User
        String automaticMessage = "Hi, I was interested in your ";
        automaticMessage += postDetails.getTitle();
        automaticMessage += " at your GoDown App.";

        if (!TextUtils.isEmpty(automaticMessage)) {
            String phNumber = userDataFromDatabase.getPhoneNumber();

            //We are checking if the Permission to send SMS has been Granted
            if(ContextCompat.checkSelfPermission(ItemClick.this, android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(ItemClick.this, new String[]{android.Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
            }else {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phNumber, null, automaticMessage, sentPI, deliveredPI);

                makeToast(automaticMessage);
                }
        }else
            makeToast("Permission Denied");
    }


    public void sendMessage(View view) {

        if (!TextUtils.isEmpty(message)) {
            String phNumber = userDataFromDatabase.getPhoneNumber();

            if(ContextCompat.checkSelfPermission(ItemClick.this, android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(ItemClick.this, new String[]{android.Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
            }else {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phNumber, null, message, sentPI, deliveredPI);

                makeToast(message);
            }
        }else
            makeToast("Permission Denied");
    }
    public void makeToast(String toast){
        Toast.makeText(ItemClick.this, toast,
                Toast.LENGTH_SHORT).show();

    }

    public void saveThePost(View view) {

        if(mAuth.getCurrentUser() != null)
                mUserDatbase = FirebaseDatabase.getInstance().getReference().child("/Users").child(mAuth.getCurrentUser().getUid());

        mUserDatbase.child("Saved Posts").setValue(postDetails.getPostId()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                     makeToast("Saved");
                else
                    makeToast("Try Again");

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR // hide nav bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

}
