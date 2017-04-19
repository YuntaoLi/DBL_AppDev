package group14.foodfetch;

/*
 * Created by tomas on 15-3-2017.
 * This Activity shows the foodbank all the available announcements
 * and allows them to use search between all the announcements
 * by typing a keyword in the search bar
 */

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AnnouncementOverviewActivity extends ListActivity implements View.OnClickListener {

    //list stuff
    private ListView mListView;
    private ArrayList<Announcement> retrievedPosts;
    private ArrayList<Donor> retrievedDonors;
    private ArrayList<String> postItems;
    private ArrayAdapter<String> adapter;
    ValueEventListener databaseListener;
    private final Handler handler = new Handler(); //to fix thé bug maybe
    private String userAndPostID;

    //search
    private EditText searchTextHolder;
    private ArrayList<String> tempSearchArray;

    //logout
    private Button buttonLogout;/*need to be removed*/

    //firebase db essentials
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef;

    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_overview);

        firebaseAuthentication(); // make sure user is logged in, else redirect to login
        componentSetup();
        fillList();
        //checkConnection();
        Log.v("VALUE", Arrays.toString(retrievedPosts.toArray()));
        possibleBugFixer(2000);
    }

    //==============================================================================================
    //helpers

    public void firebaseAuthentication(){

        //firebaseAuth property*
        firebaseAuth = FirebaseAuth.getInstance();

        //get the user
        currentUser = firebaseAuth.getCurrentUser();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
/*
    public void checkConnection() {
        String s = FirebaseDatabase.getInstance().getReference(".info/connected").toString();
        Log.v("value", "this is the ref"+s);
    }
*/
    public void componentSetup(){

        //Arrays
        retrievedPosts = new ArrayList<Announcement>();
        postItems = new ArrayList<String>();

        //List
        mListView = (ListView) findViewById(android.R.id.list);
        adapter = new MyListAdapter(this, R.layout.list_item_announcement, postItems);
        mListView.setAdapter(adapter);

        //button //is now refresh
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        //Database reference
        mConditionRef = mRootRef; //refer to current users posts

        mConditionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                /* this is different from MyPosts*/
                retrievePosts(dataSnapshot);

                Log.v("Value", "this is the postItems: " + postItems);

                Log.v("VALUE", "Rertrieved posts after Listener: "+Arrays.toString(retrievedPosts.toArray()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                //retrievePosts(dataSnapshot);  //this should be actually used

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Post removedPost = dataSnapshot.getValue(Post.class);
                //retrievedPosts.remove(removedPost);
                //retrievePosts(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //retrievePosts(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fillList() {
        postItems.clear();

        for (Announcement announcement : retrievedPosts) {
            String postInfo = announcement.getTitle() + "\n" + announcement.getFoodType() + "\n" + announcement.getPublishDate();
            postItems.add(postInfo);
        }
        adapter.notifyDataSetChanged();
        Log.v("VALUE", "fillList: "+Arrays.toString(retrievedPosts.toArray()));
    }

    public void retrievePosts(DataSnapshot dataSnapshot){
        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

        if (map.get("doner").toString().equals("false")) {
            Log.v("VALUE", "this is a foodbank");
            if (map.get("publish") != null) {
                Log.v("Value", "this is the publish: " + map.get("publish").toString());
                ArrayList<HashMap<String, String>> tempArray = (ArrayList<HashMap<String, String>>)map.get(("publish"));
                for (HashMap<String, String> announcement : tempArray) {
                    Log.v("Value", "this is the acceptence: " + announcement.get("acceptence"));

                    Log.v("Value", "this is the title: " + announcement.get("title"));
                    Announcement tempPost = new Announcement(announcement.get("title"), announcement.get("foodType"), announcement.get("publishDate"));
                    retrievedPosts.add(tempPost);

                }
            }
        }
        fillList();
    }

    public void possibleBugFixer(int time){ //change searchText in a delayed fashion to sync with Database?
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fillList();
            }
        }, time);

    }


    //==============================================================================================
    //button clicking

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLogout: //is now refresh
                fillList();
                break;
        }

    }

    //==============================================================================================
    //Search

    //==============================================================================================
    //structure of the list

    private class MyListAdapter extends ArrayAdapter<String> { //custom adapter (uses list_item.xml)
        private int layout;
        public MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.list_item_thumbnail);
                viewHolder.title = (TextView) convertView.findViewById(R.id.list_item_text);
                convertView.setTag(viewHolder);
                }
            else {
                mainViewholder = (ViewHolder) convertView.getTag();
                mainViewholder.title.setText(getItem(position));

            }
                return convertView;
        }
    }

    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
    }
}
