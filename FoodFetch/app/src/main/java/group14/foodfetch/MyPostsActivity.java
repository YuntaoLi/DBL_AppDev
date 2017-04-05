package group14.foodfetch;

/*
 * Created by tomas on 15-3-2017.
 * This Activity shows the foodbank all the available posts
 * and allows them to use search between all the posts
 * by typing a keyword in the search bar
 * additional line for testing PULL
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
import java.util.Iterator;
import java.util.List;

public class MyPostsActivity extends ListActivity implements View.OnClickListener {

    //list stuff
    private ListView mListView;
    private ArrayList<Post> retrievedPosts;
    private ArrayList<String> postItems;
    private ArrayAdapter<String> adapter;
    private final Handler handler = new Handler(); //to fix thé bug maybe

    //search
    private EditText searchTextHolder;

    //logout
    private Button buttonLogout;/*need to be removed*/

    //firebase db essentials
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef;
    private boolean userIsLoggedIn = false; //for testing requirements

    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        firebaseAuthentication(); // make sure user is logged in, else redirect to login
        componentSetup();
        AddSearchListener();
        fillList();
        Log.v("VALUE", Arrays.toString(retrievedPosts.toArray()));
        Log.v("VALUE", "Retrieved posts should be finished"+Arrays.toString(retrievedPosts.toArray()));
        Log.v("VALUE", "PostItems should be filled: "+Arrays.toString(postItems.toArray()));
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
        else {
            userIsLoggedIn = true; //for testing requirement
        }
        printLoginStatus();
    }

    public void printLoginStatus(){
        if (userIsLoggedIn) {
            Log.v("VALUE", "userIsLoggedIn: true");
            Log.v("VALUE", "Current User ID: "+currentUser.getUid());
        }
        else {
            Log.v("VALUE", "userIsLoggedIn: false");
        }
    }

    public void componentSetup(){

        //Arrays
        retrievedPosts = new ArrayList<Post>();
        postItems = new ArrayList<String>();

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
        mConditionRef = mRootRef.child(currentUser.getUid() + "/publish"); //refer to current users posts

        mConditionRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Post post = dataSnapshot.getValue(Post.class);
                        retrievedPosts.add(post);
                        Log.v("VALUE", "RetrievedPosts During OnChildAdded: "+Arrays.toString(retrievedPosts.toArray()));
                        Log.v("VALUE", "PostItems during OnChildAdded: "+Arrays.toString(postItems.toArray()));


                    }


                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Post removedPost = dataSnapshot.getValue(Post.class);
                retrievedPosts.remove(removedPost);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.v("VALUE", "end of setup"+Arrays.toString(postItems.toArray()));
    }

    public void fillList() {
        postItems.clear();

        for (Post post : retrievedPosts) {
            String postInfo = post.getTitle()+"\n"+post.getFoodType()+"\n"+post.getExpiredDate();
            postItems.add(postInfo);
        }
        //Log.v("VALUE", postItems.get(0));
        adapter.notifyDataSetChanged();
        Log.v("VALUE", "happens during fillList: "+Arrays.toString(retrievedPosts.toArray()));
    }

    public void possibleBugFixer(){ //change searchText in a delayed fashion to sync with Database?
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchTextHolder.setText("/");
            }
        }, 4000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchTextHolder.setText("");
            }
        }, 4000);
    }

    //==============================================================================================
    //button clicking

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLogout:/*remove it in the future*/
                firebaseAuth.signOut();
                userIsLoggedIn = false;
                printLoginStatus();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            //case R.id.button?
        }

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
                    Log.v("VALUE", "Happens on equals nothing: "+Arrays.toString(retrievedPosts.toArray()));
                    Log.v("VALUE", "OnChildAdded: "+Arrays.toString(postItems.toArray()));
                }
                else{
                    fillList();
                    searchItem(searchQuery.toString());
                    Log.v("VALUE", "OnChildAdded: "+Arrays.toString(postItems.toArray()));
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
                        Toast.makeText(getContext(), "Post accepted", Toast.LENGTH_SHORT).show();
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
                Log.v("VALUE", "");
                return convertView;
        }
    }

    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
        Button button;
    }
}
