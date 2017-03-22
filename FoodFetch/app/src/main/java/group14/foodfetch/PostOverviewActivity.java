package group14.foodfetch;

/*
 * Created by tomas on 15-3-2017.
 * This Activity shows the foodbank all the available posts
 * and allows them to use search between all the posts
 * by typing a keyword in the search bar
 */

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class PostOverviewActivity extends ListActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fillPostsInitially();
        setContentView(R.layout.activity_overview);

        //whatever is contained within this list is displayed in the ListView
        String[] posts = {"Post 1", "Post 2", "Post 3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, posts);
        getListView().setAdapter(adapter);
        //fillPostsInitially();  //Call on create to fill the post overview

    }

    //fills the posts list when the app is started
    void fillPostsInitially() {
    }

    //fills the posts list after a search request has been done
    void SearchPosts() {
    }
}
