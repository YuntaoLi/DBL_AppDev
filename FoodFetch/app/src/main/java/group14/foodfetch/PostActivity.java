package group14.foodfetch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    //All components used for a add post page: =====================================================
    private EditText input_title;
    //EditText input_foodType;
    private Spinner input_foodType;
    private DatePicker input_expiredDate;
    private Button addPicture;
    //TextView showPicName;
    private ImageView showPic;
    private EditText input_description;
    private Button publish;
    private Button buttonLogout;/*need to be removed*/
    private FirebaseAuth firebaseAuth;

    /*firebase db essentials*/
    private DatabaseReference databaseReference;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String newTitle;
    private String newFoodType;
    private String newExpiredDate;
    private Bitmap newPicture;
    private String newDescription;

    //==============================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        /*firebaseAuth property*/
        firebaseAuth = FirebaseAuth.getInstance();
        /*Get the user*/
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        /*Get data structure*/
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //link all components: =====================================================================
        input_title = (EditText) findViewById(R.id.titleInput);

        //input_foodType = (EditText) findViewById(R.id.foodTypeInput); EditText is changed to spinner
        input_foodType = (Spinner) findViewById(R.id.foodTypeInput);

        buttonLogout = (Button) findViewById(R.id. buttonLogout);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> foodType_adapter = new ArrayAdapter<>(PostActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.foodTypeList));
        // Specify the layout to use when the list of choices appears
        foodType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        input_foodType.setAdapter(foodType_adapter);
        input_foodType.setOnItemSelectedListener(this);

        input_expiredDate = (DatePicker) findViewById(R.id.expiredDateInput);

        addPicture = (Button) findViewById(R.id.picInput);
        addPicture.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        //showPicName = (TextView) findViewById(R.id.showPicName);
        //showPicName.setOnClickListener(this);
        showPic = (ImageView) findViewById(R.id.previewPic);
        //showPic.setOnClickListener(this);


        input_description = (EditText) findViewById(R.id.descriptionInput);

        publish = (Button) findViewById(R.id.publish);
        publish.setOnClickListener(this);
        //==========================================================================================
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        //TextView myText = (TextView) view;
        //Toast.makeText(this, "you Selected" + myText.getText(), Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // not required at this time
    }


    /**
     * function for button add Picture:
     * when button add picture is clicked, the application should provide a picture for user to pick
     */

    public void addPicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    //cannot test since there is an empty gallery in model phone
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            newPicture = (Bitmap) extras.get("data");
            showPic.setImageBitmap(newPicture);
        }
    }

    /**all information should be saved after submit button is on clicked
     */
    public void addPost(View v){

        //get the input contents:
        newTitle = input_title.getText().toString();
        newFoodType = input_foodType.getSelectedItem().toString();

        int newExpiredDate_year = input_expiredDate.getYear();
        int newExpiredDate_month = input_expiredDate.getMonth();
        int newExpiredDate_date = input_expiredDate.getDayOfMonth();
        newExpiredDate = newExpiredDate_date + " - " + newExpiredDate_month + " - " + newExpiredDate_year;

        newDescription = input_description.getText().toString();



        if(newTitle.matches("") || newFoodType.matches("Select Food Type") || newExpiredDate.matches("")){

            Toast.makeText(this, "Please fill title / food type / expired date for your post", Toast.LENGTH_SHORT ).show();

        } else {

            Post newPost = new Post(newTitle, newFoodType, newExpiredDate);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
            newPost.setPublishDate(dateFormat.format(date));//need a function to get current time in format

            if (!newDescription.matches("")) {
                newPost.setDescription(newDescription);
            }

            if (newPicture != null){
                newPost.setFoodPic(newPicture);
            }

            pushToDatabase(newPost);   //pushToDatabase is used here to upload the newPost
        }
    }

    /**
     * A function push the newPost to database, will be used in addPost()
     *
     */
    public void pushToDatabase(Post newPost){
        /*call an donor instance*/
        User donor = new Donor(newPost);
        /*log to the current user*/
        FirebaseUser usr = firebaseAuth.getCurrentUser();
        /*save*/
        databaseReference.child(usr.getUid()).setValue(donor);
        Toast.makeText(this, "Posted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.picInput:
                addPicture(v);
                break;
            case R.id.publish:
                addPost(v);
                break;
            case R.id.buttonLogout:/*remove it in the future*/
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }


    }
}
