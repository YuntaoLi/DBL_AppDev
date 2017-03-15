package group14.foodfetch;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PostActivity extends AppCompatActivity {

    //All components used for a add post page: =====================================================
    EditText input_title;
    EditText input_foodType;
    EditText input_expiredDate;
    Button addPic;
    EditText input_description;
    Button publish;
    //==============================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //link all components: =====================================================================
        input_title = (EditText) findViewById(R.id.titleInput);
        input_foodType = (EditText) findViewById(R.id.foodTypeInput);
        //input_expiredDate = (EditText) findViewById(R.id.expiredDateInput);
        addPic = (Button) findViewById(R.id.picInput);
        input_description = (EditText) findViewById(R.id.descriptionInput);
        publish = (Button) findViewById(R.id.publish);
        //==========================================================================================
    }



    /**all information should be saved after submit button is on clicked
     *
     * @param v
     * @throws IllegalArgumentException when compulsory information is left empty:
     *                         three compulsory variables are:  title, food type, expired date;
     */
    public void addPost(View v) throws IllegalArgumentException {


        //get the input contents:
        String newTitle = input_title.getText().toString();
        String newFoodType = input_foodType.getText().toString();
        String newExpiredDate = input_expiredDate.getText().toString();
        //here misses a image variable.
        String newDescription = input_description.getText().toString();



        if(newTitle == null || newFoodType == null || newExpiredDate==null){
            throw new IllegalArgumentException("you are missing compulsory information!");
        } else {

            Post newPost = new Post(newTitle, newFoodType, newExpiredDate);

            if (newDescription != null) {
                newPost.setDescription(newDescription);
            }

            Log.d("title", newPost.getTitle());
            Log.d("foodType", newPost.getFoodType());
            Log.d("expired date", newPost.getExpiredDate());
        }
    }



}
