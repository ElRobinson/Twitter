package com.robinson.luis.twitter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFeed extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,String>> tweetData = new ArrayList<>();
    private ArrayList<String> following;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference();

    private ChildEventListener childEventListener;
    private Query queryRef;
    private String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feed);

        listView = (ListView) findViewById(R.id.listView);
        tweetData = new ArrayList<>();
        simpleAdapter  = new SimpleAdapter(this,tweetData,android.R.layout.simple_list_item_2,new String[]{"content","username"},new int[]{android.R.id.text1,android.R.id.text2});

        listView.setAdapter(simpleAdapter);

        Intent i = getIntent();
        following = i.getStringArrayListExtra("following");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        } else {
            myUid = user.getUid();
        }

        tweetData.clear();

        queryRef = ref.child("tweets").orderByChild("data");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (following.contains(dataSnapshot.child("uid").getValue(String.class))|| dataSnapshot.child("uid").getValue(String.class).equals(myUid)){
                    Map<String,String> tweet = new HashMap<>(2);
                    tweet.put("content",dataSnapshot.child("msg").getValue(String.class));
                    tweet.put("username",dataSnapshot.child("name").getValue(String.class));
                    tweetData.add(tweet);
                    simpleAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        queryRef.removeEventListener(childEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        queryRef.removeEventListener(childEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id== R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
