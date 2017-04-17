package group14.foodfetch;

import android.graphics.Bitmap;

/**
 * Created by s141680 on 8-3-2017.
 * by Manning Zhang
 */
public class Post extends Publish{

    public Post (){

    }

    //constructors:---------------------------------------------------------------------------------
    public Post(String title, String foodType, String expiredDate){
        setTitle(title);
        setFoodType(foodType);

        if (expiredDate != null) { //in case no date is found
            setExpiredDate(expiredDate);
        }
        else {
            setExpiredDate("No Date Found");
        }

        setAcceptence(false);
        //setPublishID(1);  ///should be replaced by a generator function later
    }
    //----------------------------------------------------------------------------------------------

}
