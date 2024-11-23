package com.example.ridehuddle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.ridehuddle.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
        {
            Intent intent = new Intent(MainActivity.this, FirebaseUIActivity.class);
            startActivity(intent);
            this.finish();
        }
        else {
            MyApp.getInstance().setUserId(currentUser.getUid());
            Log.d(TAG, "onCreate: "+currentUser.getUid());
            setContentView(R.layout.activity_main);
            Intent mainActivity = new Intent(MainActivity.this, DisplayGroupsActivity.class);
            startActivity(mainActivity);
            this.finish();
        }
    }
}

