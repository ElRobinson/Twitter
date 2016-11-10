package com.robinson.luis.twitter;

import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LogedActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter arrayAdapter;

    private ArrayList<String> users;
    private ArrayList<String> userids;
    private ArrayList<String> follow;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference ref = db.getReference();

    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    private String myUid;
    private String myName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged);

        follow = new ArrayList<>();
        userids = new ArrayList<>();
        users = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,users);
        listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()){
                    follow.add(userids.get(position));
                } else {
                    follow.remove(follow.indexOf(userids.get(position)));
                }

                    ref.child("users").child(myUid).child("following").setValue(follow);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
        } else {
            myUid = user.getUid();
            ref.child("users").child(myUid).child("name").addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myName = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            userids.clear();
            users.clear();
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!dataSnapshot.child("uid").getValue(String.class).equals(myUid)){
                        users.add(dataSnapshot.child("name").getValue(String.class));
                        userids.add(dataSnapshot.child("uid").getValue(String.class));
                        arrayAdapter.notifyDataSetChanged();
                        refreshList();
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
            ref.child("users").addChildEventListener(childEventListener);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    follow.clear();
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        follow.add(data.getValue(String.class));
                    }
                    refreshList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.child("users").child(myUid).child("following").addValueEventListener(valueEventListener);
        }
    }

    public void refreshList(){
        for(String uid:userids){
            if(follow.contains(uid)){
                listView.setItemChecked(userids.indexOf(uid),true);
            } else {
                listView.setItemChecked(userids.indexOf(uid),false);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu; add itens to act bar if it is present.
        getMenuInflater().inflate(R.menu.menu_twitter,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.feed){

            Intent i = new Intent(getApplicationContext(),MyFeed.class);
            i.putStringArrayListExtra("following",follow);
            startActivity(i);

        } else if (id == R.id.tweet){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Send a Tweet");
            final EditText contentTweet = new EditText(this);
            builder.setView(contentTweet);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog,int which){
                    Map<String,Object> tweet = new HashMap<String, Object>();
                    tweet.put("msg",contentTweet.getText().toString());
                    tweet.put("uid",myUid);
                    tweet.put("data", -1*System.currentTimeMillis());
                    tweet.put("name",myName);
                    ref.child("tweets").push().setValue(tweet);

                    Toast.makeText(getApplicationContext(),"Your tweet was send",Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){
                    dialog.cancel();
                }
            });

            builder.show();

            return true;



        } else if(id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
