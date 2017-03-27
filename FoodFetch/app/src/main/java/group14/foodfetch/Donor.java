package group14.foodfetch;

import android.graphics.Bitmap;

/**
 * Created by s143969 on 3/26/2017.
 */
/*Donor class*/
public class Donor extends User {

    public Donor(Post post) {
        setDoner(true);
        setPublish(post);
    }


}
