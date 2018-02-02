package com.karkhana.prash.karkhana.JavaClasses;

import java.io.Serializable;

/**
 * Created by prash on 12/30/2017.
 */

public class New_Ad_Post implements Serializable{

    private String Title, Description, Price, Category, Image, First_Name, UserId, PostId, Area;

    public New_Ad_Post(){

    }

    public New_Ad_Post(String title, String description, String price, String category, String image, String UserId, String postId, String area) {
        Title = title;
        Description = description;
        Price = price;
        Category = category;
        Image = image;
        this.UserId = UserId;
        PostId = postId;
        Area = area;


    }

    public String getFirst_Name() {
        return First_Name;
    }

    public void setFirst_Name(String first_Name) {
        First_Name = first_Name;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public String getCategory() {
        return Category;
    }

    public String getImage() {
        return Image;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
