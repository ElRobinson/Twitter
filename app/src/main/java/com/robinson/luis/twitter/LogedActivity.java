package com.robinson.luis.twitter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class LogedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loged);
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

        } else if (id == R.id.tweet){

        } else if(id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
