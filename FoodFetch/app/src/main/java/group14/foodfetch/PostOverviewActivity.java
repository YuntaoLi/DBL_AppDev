package group14.foodfetch;

/*
 * Created by tomas on 15-3-2017.
 * This Activity shows the foodbank all the available posts
 * and allows them to use search between all the posts
 * by typing a keyword in the search bar
 */

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PostOverviewActivity extends ListActivity implements View.OnClickListener {

    //list stuff
    private ListView mListView;
    private ArrayList<Post> retrievedPosts;
    private ArrayList<Donor> retrievedDonors;
    private ArrayList<String> postItems;
    private ArrayAdapter<String> adapter;
    ValueEventListener databaseListener;

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
        setContentView(R.layout.activity_overview);

        firebaseAuthentication(); // make sure user is logged in, else redirect to login
        componentSetup();
        AddSearchListener();
        fillList();
        Log.v("VALUE", Arrays.toString(retrievedPosts.toArray()));
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

    public void componentSetup(){

        //Arrays
        retrievedPosts = new ArrayList<Post>();
        postItems = new ArrayList<String>();
        retrievedDonors = new ArrayList<Donor>();

        //List
        mListView = (ListView) findViewById(android.R.id.list);
        adapter = new MyListAdapter(this, R.layout.list_item, postItems);
        mListView.setAdapter(adapter);

        //button
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        //search
        searchTextHolder = (EditText) findViewById(R.id.text_search);

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

        for (Post post : retrievedPosts) {
            if (post.getAcceptence() == "false") { //if it's already accapted, then don't show
                String postInfo = post.getTitle() + "\n" + post.getFoodType() + "\n" + post.getExpiredDate() + "\n" + post.getPublishID();
                postItems.add(postInfo);
            }
        }
        adapter.notifyDataSetChanged();
        Log.v("VALUE", "fillList: "+Arrays.toString(retrievedPosts.toArray()));
    }

    public void retrievePosts(DataSnapshot dataSnapshot){
        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

        if (map.get("doner").toString().equals("true")) {
            Log.v("VALUE", "this is a donor");
            if (map.get("publish") != null) {
                Log.v("Value", "this is the publish: " + map.get("publish").toString());
                ArrayList<HashMap<String, String>> tempArray = (ArrayList<HashMap<String, String>>)map.get(("publish"));
                for (HashMap<String, String> post : tempArray) {
                    Log.v("Value", "this is the acceptence: " + post.get("acceptence"));
                    if(post.get("acceptence").equals("false")) { //post hasn't been accepted yet
                        Log.v("Value", "this is the title: " + post.get("title"));
                        Post tempPost = new Post(post.get("title"), post.get("foodType"), post.get("expiredDate"), post.get("publishID"));
                        retrievedPosts.add(tempPost);
                    }
                }
            }
        }
        fillList();
    }


    //==============================================================================================
    //button clicking

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLogout:/*remove it in the future*/
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            //case R.id.button?
        }

    }

    public void acceptPost(int position){ //not working yet
        String[] splitter = postItems.get(position).split(" - ");
        String postID = splitter[1];
        String userID = splitter[2];
        int temp = Integer.parseInt(postID)-1;
        String RealPostID = String.valueOf(temp);
        Log.v("VALUE", "postID en userID" +postID + userID );
        mConditionRef.child(userID).child("publish").child(RealPostID).child("acceptence").setValue("true");
    }

    //==============================================================================================
    //Search

    public void AddSearchListener() {
        searchTextHolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence searchQuery, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence searchQuery, int i, int i1, int i2) {
                if(searchQuery.toString().equals("")){
                    fillList();

                }
                else{
                    fillList();
                    searchItem(searchQuery.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void searchItem(String textToSearchFor){
        Iterator<String> iter = postItems.iterator();
        while (iter.hasNext()) {
            String str = iter.next();

            if(!str.toLowerCase().contains(textToSearchFor.toLowerCase())) {

                iter.remove();
            }
        }
        adapter.notifyDataSetChanged();
    }

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
                viewHolder.button = (Button) convertView.findViewById(R.id.list_item_button);
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Post number "+ position + "accepted", Toast.LENGTH_SHORT).show();
                        acceptPost(position);
                        postItems.remove(position);
                        adapter.notifyDataSetChanged();
                    }

                });
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
        Button button;
    }
}
