package com.example.s141680.appdevelopment_post;

import android.media.Image;

/**
 * Created by s141680 on 8-3-2017.
 * by Manning Zhang
 */
public class Post {

    //constructors:----------------------------------------------------------------------------------
    public Post(){               };    //empty constructor

    public Post(String title, String foodType, String expiredDate){
        setTitle(title);
        setFoodType(foodType);
        setExpiredDate(expiredDate);
    }
    //----------------------------------------------------------------------------------------------

    //Variables contained in each post==============================================================
    //Variables have to be filled by users:
    private String title;   //compulsory
    private String foodType;  //compulsory
    private String expiredDate;   //compulsory
    private Image foodPic;  //optional
    private String description;   //optional

    //variables will be automatically generated:
    private String author;
    private String publishDate;
    private String postID;
    //==============================================================================================


    //All set&get functions for private variables===================================================
    //set functions:
    public void setTitle(String title) {
        this.title = title;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public void setFoodPic(Image foodPic) {
        this.foodPic = foodPic;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    //get functions:
    public String getTitle() {
        return title;
    }

    public String getFoodType() {
        return foodType;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public Image getFoodPic() {
        return foodPic;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getPostID() {
        return postID;
    }

    //==============================================================================================


}
