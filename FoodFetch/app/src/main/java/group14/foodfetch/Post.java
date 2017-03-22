package group14.foodfetch;

import android.graphics.Bitmap;

/**
 * Created by s141680 on 8-3-2017.
 * by Manning Zhang
 */
public class Post {

    //constructors:---------------------------------------------------------------------------------
    public Post(String title, String foodType, String expiredDate){
        setTitle(title);
        setFoodType(foodType);
        setExpiredDate(expiredDate);
        setAcceptence(false);
        setPostID(1);  ///should be removed by a generator function later
    }
    //----------------------------------------------------------------------------------------------

    //Variables contained in each post==============================================================
    //Variables have to be filled by users:
    private String title;   //compulsory
    private String foodType;  //compulsory
    private String expiredDate;   //compulsory
    private Bitmap foodPic;  //optional
    private String description;   //optional

    //variables will be automatically generated:
    private String author;
    private String publishDate; //will be generated by the system when publish button is clicked
    private int postID;
    private boolean acceptence;
    //private boolean display; display is commented since it is the reverse of acceptence.
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

    public void setFoodPic(Bitmap foodPic) {
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

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public void setAcceptence(boolean acceptence) {
        this.acceptence = acceptence;
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

    public Bitmap getFoodPic() {
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

    public int getPostID() {
        return postID;
    }

    public boolean isAcceptence() {
        return acceptence;
    }


    //==============================================================================================


}
