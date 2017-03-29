package group14.foodfetch;

import android.graphics.Bitmap;

/**
 * Created by s141680 on 8-3-2017.
 * by Manning Zhang
 */
public class Announcement extends Publish {

    //constructors:---------------------------------------------------------------------------------
    public Announcement(String title, String foodType){
        setTitle(title);
        setFoodType(foodType);
        //setPublishID(1);  ///should be replaced by a generator function later
    }
    //----------------------------------------------------------------------------------------------



}
