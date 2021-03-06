package group14.foodfetch;

import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
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
    private Button announcement;
    private Button logOut;
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

        //notify users which are required information
        Toast.makeText(this, "Please fill in all compulsory information (labelled by *)",
                Toast.LENGTH_SHORT ).show();
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
        //Date picker dialog
        SelectedDateView = (TextView) findViewById(R.id.selected_date2);

        /*-------------------------------------------------------------------------------------*/
        //Navgation view listener setup
        navigationView = (NavigationView) findViewById(R.id.nav_layout);
        // Setup drawer view
        setupDrawerContent(navigationView);


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
        ArrayAdapter<String> foodType_adapter = new ArrayAdapter<>(PostActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.foodTypeList));
        // Specify the layout to use when the list of choices appears
        foodType_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        input_foodType.setAdapter(foodType_adapter);
        input_foodType.setOnItemSelectedListener(this);
/*
        input_expiredDate = (DatePicker) findViewById(R.id.expiredDateInput);
*/
        addPicture = (Button) findViewById(R.id.picInput);
        addPicture.setOnClickListener(this);
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
    /*-----------------Date Picker Dialog-------------------------------*/
    public static TextView SelectedDateView;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(),android.R.style.Theme_Holo_Light_Dialog,this, year,month, day);
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            SelectedDateView.setText("Selected Date: " + (month + 1) + "/" + day + "/" + year);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    /*------------------------------------------------------------*/

    /**all information should be saved after submit button is on clicked
     */
    public void addPost(View v){

        //get the input contents:
        newTitle = input_title.getText().toString();
        newFoodType = input_foodType.getSelectedItem().toString();
        //trimming the date from picker
        String test = SelectedDateView.getText().toString();
        String test2 = test.replace("Selected Date: ", "");
        newExpiredDate = test2;
        newDescription = input_description.getText().toString();


        //giving restriction to expired date: cannot be the day before today
        DateFormat dateFormat_date = new SimpleDateFormat("yyyy/MM/dd");
        Date date_today = new Date();

        System.out.println(newFoodType);
        if(newTitle.matches("") || newFoodType.matches("chooseType")||newExpiredDate.matches("")){

            Toast.makeText(this, "Please fill in correct title / food type / expired date for your post", Toast.LENGTH_SHORT ).show();

        } else {

            Post newPost = new Post(newTitle, newFoodType, newExpiredDate);
            DateFormat dateFormat_Time = new SimpleDateFormat("HH:mm:ss");
            //Date date = new Date();
            //System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
            newPost.setPublishDate(dateFormat_date.format(date_today) + " " +dateFormat_Time.format(date_today));//need a function to get current time in format

            if (!newDescription.matches("")) {
                newPost.setDescription(newDescription);
            }

            if (newPicture != null){
                newPost.setFoodPic(newPicture);
            }

            pushToDatabase(newPost);   //pushToDatabase is used here to upload the newPost
            backToInitial(date_today);  //page will back to empty
        }
    }


    public void backToInitial(Date today){
        input_title.setText("");
        input_foodType.setSelection(0);
        SelectedDateView.setText("");
        newPicture = null;
        showPic.setImageBitmap(newPicture);
        input_description.setText("");
        }

    /**
     * A function push the newPost to database, will be used in addPost()
     *
     */
    private User donor = new Donor();
    public void pushToDatabase(Post newPost){
        /*call an donor instance*/

        donor.setPublish(newPost);
        /*log to the current user*/
        FirebaseUser usr = firebaseAuth.getCurrentUser();
        newPost.setAuthor(usr.getEmail());
        newPost.setPublishID("POST - " + donor.getPublish().size() + " - " + usr.getUid());
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
                startActivity(new Intent(this, AnnouncementOverviewActivity.class));//Replace to the ann. overview
                break;
            case R.id.navbuttonLogout:
                firebaseAuth.signOut();
                System.out.println("userIsLoggedIn = false");
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
