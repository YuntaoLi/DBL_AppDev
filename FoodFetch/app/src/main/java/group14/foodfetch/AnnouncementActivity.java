package group14.foodfetch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

/**
 * Created by s141680 on 27-3-2017.
 */

public class AnnouncementActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {

    /*----------------------------------------------------------------------------------------*/
    //Menu essentials
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    /*----------------------------------------------------------------------------------------*/

    //All components used for a add post page: =====================================================
    private EditText input_title;
    //EditText input_foodType;
    private Spinner input_foodType;
    private Button addPicture;
    //TextView showPicName;
    private ImageView showPic;
    private EditText input_description;
    private Button publish;
    private FirebaseAuth firebaseAuth;

    /*firebase db essentials*/
    private DatabaseReference databaseReference;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String newTitle;
    private String newFoodType;
    private Bitmap newPicture;
    private String newDescription;

    //==============================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        /*Setup for toggle nav bar*/
        toolbar = (Toolbar)findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        //Nav listener
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        //Make home visible
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*-------------------------------------------------------------------------------------*/
        //Navgation view listener setup
        navigationView = (NavigationView) findViewById(R.id.nav_layout);
        // Setup drawer view
        setupDrawerContent(navigationView);

        //notify users which are required information
        Toast.makeText(this, "Please fill in all compulsory information (labelled by *)", Toast.LENGTH_SHORT ).show();

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
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> foodType_adapter = new ArrayAdapter<>(AnnouncementActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.foodTypeList));
        // Specify the layout to use when the list of choices appears
        foodType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        input_foodType.setAdapter(foodType_adapter);
        input_foodType.setOnItemSelectedListener(this);


/*
        addPicture = (Button) findViewById(R.id.picInput);
        addPicture.setOnClickListener(this);
        //showPicName = (TextView) findViewById(R.id.showPicName);
        //showPicName.setOnClickListener(this);
        showPic = (ImageView) findViewById(R.id.previewPic);
        //showPic.setOnClickListener(this);
*/

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
/*
    public void addPicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }
*/
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


        newDescription = input_description.getText().toString();



        if(newTitle.matches("") || newFoodType.matches("Select Food Type") ){

            Toast.makeText(this, "Please fill title / food type for your post", Toast.LENGTH_SHORT ).show();

        } else {

            Announcement newAnnouncement = new Announcement(newTitle, newFoodType);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
            newAnnouncement.setPublishDate(dateFormat.format(date));//need a function to get current time in format

            if (!newDescription.matches("")) {
                newAnnouncement.setDescription(newDescription);
            }
/*
            if (newPicture != null){
                newAnnouncement.setFoodPic(newPicture);
            }
*/
            pushToDatabase(newAnnouncement);   //pushToDatabase is used here to upload the newPost

            backToInitial(); //page will back to empty
        }
    }

    public void backToInitial(){
        input_title.setText("");
        input_foodType.setSelection(0);
/*
        newPicture = null;
        showPic.setImageBitmap(newPicture);
*/
        input_description.setText("");
    }

    /**
     * A function push the newPost to database, will be used in addPost()
     *
     */
    private User foodBank= new FoodBank();
    public void pushToDatabase(Announcement newAnnouncement){
        /*call an foodBank instance*/

        foodBank.setPublish(newAnnouncement);
        /*log to the current user*/
        FirebaseUser usr = firebaseAuth.getCurrentUser();
        newAnnouncement.setAuthor(usr.getEmail());
        newAnnouncement.setPublishID("ANO - " + foodBank.getPublish().size() + " - " + usr.getUid());
        /*save*/
        databaseReference.child(usr.getUid()).setValue(foodBank);
        Toast.makeText(this, "Published", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*
            case  R.id.picInput:
                addPicture(v);
                break;
                */
            case R.id.publish:
                addPost(v);
                break;
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*Drawer content is filled up here*/
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    /*Set up for menu buttons*/
    public void selectDrawerItem(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.navbuttonNewPost:
                startActivity(new Intent(this, PostActivity.class));
                break;
            case R.id.navbuttonMyPost:
                startActivity(new Intent(this, MyPostsActivity.class));
                break;
            case R.id.navbuttonAnnouncement:
                startActivity(new Intent(this, AnnouncementActivity.class));
                break;
            case R.id.navbuttonLogout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                startActivity(new Intent(this, PostActivity.class));
        }
        menuItem.setChecked(true);
        // Set action bar and close nav drawer
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }
}
