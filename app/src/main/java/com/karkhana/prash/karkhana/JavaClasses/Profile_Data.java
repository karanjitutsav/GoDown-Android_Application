package com.karkhana.prash.karkhana.JavaClasses;

import android.content.Context;
import android.net.Uri;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by prash on 12/22/2017.
 */

public class Profile_Data implements Serializable{

    private String First_name;
    private String Address;
    private String Area;
    private String City;
    private String PhoneNumber;
    private Long DateTime;
    private String profilePic;

    public Profile_Data() {

    }

    public Profile_Data(String first_name, String address, String area, String city, String phoneNumber, Long datetime, String pp) {
        First_name = first_name;
        Address = address;
        Area = area;
        City = city;
        PhoneNumber = phoneNumber;
        DateTime = datetime;
        profilePic = pp;
    }



    public String getFirst_name() {
        return First_name;
    }

    public void setFirst_name(String first_name) {
        First_name = first_name;
    }


    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public Long getmDateTime() {
        return DateTime;
    }

    public void setmDateTime(Long mDateTime) {
        this.DateTime = mDateTime;
    }

    public String getDateTimeFormatted(Context context){

        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy",
                context.getResources().getConfiguration().locale);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(DateTime));

    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
