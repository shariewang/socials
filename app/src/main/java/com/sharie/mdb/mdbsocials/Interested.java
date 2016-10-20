package com.sharie.mdb.mdbsocials;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class Interested extends AppCompatActivity {

    DatabaseReference ref;
    ArrayList<String> people = new ArrayList<>();
    InterestedAdapter interestedAdapter;
    RecyclerView recyclerView;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested);

        //UI elements
        key = getIntent().getExtras().getString("key");
        ref = FirebaseDatabase.getInstance().getReference("interested/"+key);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        interestedAdapter = new InterestedAdapter(getApplicationContext(), people);
        recyclerView.setAdapter(interestedAdapter);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            //Adds this user to the list of interested people and updates adapter
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                people.add(dataSnapshot.getValue(String.class));
                interestedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
