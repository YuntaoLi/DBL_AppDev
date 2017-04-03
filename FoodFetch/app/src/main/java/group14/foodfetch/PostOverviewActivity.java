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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostOverviewActivity extends ListActivity implements View.OnClickListener {

    private ListView mListView;
    private Button buttonLogout;/*need to be removed*/
    private ArrayList<Post> retrievedPosts;
    private ArrayList<String> postItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private SearchView searchBar;

    /*firebase db essentials*/
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mConditionRef;

    //==============================================================================================
    //Authentication Process

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        /*firebaseAuth property*/
        firebaseAuth = FirebaseAuth.getInstance();
        /*Get the user*/
        currentUser = firebaseAuth.getCurrentUser();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mConditionRef = mRootRef.child(currentUser.getUid() + "/publish"); //refer to current users posts
        //onStart();
        adapter.notifyDataSetChanged();
    //==============================================================================================
    //components setup

        retrievedPosts = new ArrayList<Post>();

        mListView = (ListView) findViewById(android.R.id.list);
        adapter = new MyListAdapter(this, R.layout.list_item, postItems);
        mListView.setAdapter(adapter);

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(this);

        searchBar = (SearchView) findViewById(R.id.search_bar);
    }

    //==============================================================================================
    //filling the list

    @Override
    protected void onStart() {
        super.onStart();

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("value", "OnChange is starting");
                //clean the list
                postItems.clear();

                Iterable<DataSnapshot> postsSnapshot = dataSnapshot.getChildren();

                for (DataSnapshot postSnapshot : postsSnapshot) {
                    Post post = postSnapshot.getValue(Post.class);
                    retrievedPosts.add(post);
                    String postInfo = post.getTitle()+"\n"+post.getFoodType()+"\n"+post.getExpiredDate();
                    Log.v("value", "postInfo : " + postInfo);
                    postItems.add(postInfo);
                    Log.v("value", "List : " + postItems);

                }
                adapter.notifyDataSetChanged();
                Log.v("value", "OnChange is done");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("value", "Data change was cancelled");
            }
        });
        Log.v("value", "OnStart is done");

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


    //==============================================================================================
    //Search

    public void SearchPosts() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
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
                return convertView;
        }
    }

    public class ViewHolder {
        ImageView thumbnail;
        TextView title;
        Button button;
    }
}
