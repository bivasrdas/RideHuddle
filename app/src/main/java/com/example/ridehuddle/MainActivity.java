package com.example.ridehuddle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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
        setContentView(R.layout.activity_main);
        Intent mainActivity = new Intent(MainActivity.this, DisplayGroupsActivity.class);
        startActivity(mainActivity);
        this.finish();
    }
}

