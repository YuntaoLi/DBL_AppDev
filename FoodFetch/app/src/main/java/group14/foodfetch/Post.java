package group14.foodfetch;

import android.graphics.Bitmap;

/**
 * Created by s141680 on 8-3-2017.
 * by Manning Zhang
 */
public class Post extends Publish{

    //constructors:---------------------------------------------------------------------------------
    public Post(String title, String foodType, String expiredDate){
        setTitle(title);
        setFoodType(foodType);
        setExpiredDate(expiredDate);
        setAcceptence(false);
        setPublishID(1);  ///should be removed by a generator function later
    }
    //----------------------------------------------------------------------------------------------

}
