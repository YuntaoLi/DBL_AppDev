package group14.foodfetch;


import android.graphics.Bitmap;

/**
 * Created by s141680 on 27-3-2017.
 */

public class FoodBank  extends User {

/*Foodbank class*/
    //    public String usrname;
//    public String usrid;
    public Announcement announcement;

    public FoodBank(Announcement announcement) {
//        this.usrname = usrname;
//        this.usrid = usrid;
        this.announcement = announcement;
    }
}