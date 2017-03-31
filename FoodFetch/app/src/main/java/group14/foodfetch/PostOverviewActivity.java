package group14.foodfetch;

/*
 * Created by tomas on 15-3-2017.
 * This Activity shows the foodbank all the available posts
 * and allows them to use search between all the posts
 * by typing a keyword in the search bar
 */

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PostOverviewActivity extends ListActivity implements View.OnClickListener {

    private ListView mListView;
    private TextView textView;
    private ArrayList<String> postItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Button buttonLogout;/*need to be removed*/
    private ArrayList<Post> retrievedPosts;
    /*firebase db essentials*/
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    //private FirebaseUser currentUser;
    //private DatabaseReference mRootRef;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef;
    //DatabaseReference mConditionRef = mRootRef.child(currentUser.getUid()).child("/publish/0/author");
    //==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        /*firebaseAuth property*/
        firebaseAuth = FirebaseAuth.getInstance();
        /*Get the user*/
        currentUser = firebaseAuth.getCurrentUser();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        //mConditionRef = mRootRef.child("6QcH3VkfuKRm9jrhDa JUkxqk5kb2").child("/publish/0");
        mConditionRef = mRootRef.child(currentUser.getUid()+"/publish");

    //==============================================================================================

        retrievedPosts = new ArrayList<Post>();

        mListView = (ListView)findViewById(android.R.id.list);
        textView = (TextView)findViewById(R.id.textView3);
        //ArrayAdapter<ArrayList<String>> adapter = new ArrayAdapter<ArrayList<String>>(getListView().getContext(), android.R.layout.simple_list_item_1, postitems);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, postItems);
        mListView.setAdapter(adapter);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

    //==========================================================================================

    }

    @Override
    protected void onStart() {
        super.onStart();



        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clean the list
                postItems.clear();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {
                    Post post = child.getValue(Post.class);
                    retrievedPosts.add(post);
                    Log.v("value", "Example : " + post.getTitle());
                    String postInfo = post.getTitle()+"\n"+post.getFoodType()+"\n"+post.getExpiredDate();
                    postItems.add(postInfo);
                    adapter.notifyDataSetChanged();
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonLogout:/*remove it in the future*/
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }


    }

    //fills the posts list when the app is started
    void fillPostsInitially() {
    }

    //fills the posts list after a search request has been done
    void SearchPosts() {
    }

}
