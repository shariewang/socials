package com.sharie.mdb.mdbsocials;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class Details extends AppCompatActivity implements View.OnClickListener {

    private String title, description, email, key, date;
    private int stars;
    private DatabaseReference database, starsRef;
    private FirebaseAuth mAuth;
    private FloatingActionButton star;
    private TextView content;
    private Toolbar toolbar;
    private FloatingActionButton info;
    private String url;
    private CollapsingToolbarLayout tool;
    private ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Gets extras for information about the current social
        title = getIntent().getExtras().getString("title");
        description = getIntent().getExtras().getString("descript");
        email = getIntent().getExtras().getString("email");
        key = getIntent().getExtras().getString("key");
        stars = getIntent().getExtras().getInt("stars");
        date = getIntent().getExtras().getString("date");
        url = getIntent().getExtras().getString("url");

        //Initialize UI elements
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tool = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        image = (ImageView) findViewById(R.id.imageView2);
        content = (TextView) findViewById(R.id.content);
        star = (FloatingActionButton) findViewById(R.id.star);
        info = (FloatingActionButton) findViewById(R.id.interested);

        star.setOnClickListener(this);
        info.setOnClickListener(this);

        setSupportActionBar(toolbar);
        setMessage();
        tool.setTitle(title);

        //Initialize FireBase references
        starsRef = FirebaseDatabase.getInstance().getReference("ideas/"+key+"/stars");
        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Gets download URL of image file from storage database
        FirebaseStorage.getInstance().getReferenceFromUrl("gs://mdb-socials-5cc85.appspot.com").child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadFilesTask().execute(uri.toString());
            }
        });
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.star:
                star.setImageResource(R.drawable.star);
                FirebaseUser cur = mAuth.getCurrentUser();

                //Adds self to list of people interested in this social
                if (cur != null)
                    database.child("interested").child(key).child(cur.getUid()).setValue(cur.getEmail());

                //Runs Transaction to update the star count for this social in real-time
                starsRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Integer cur = mutableData.getValue(Integer.class);
                        mutableData.setValue(cur+1);
                        return Transaction.success(mutableData);
                    }
                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
                });

                //Listens for changes in the star count and updates the number of people interested
                //on the details page accordingly
                starsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        stars = dataSnapshot.getValue(Integer.class);
                        setMessage();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                //Makes star icon appear clicked

                break;
            case R.id.info:
                Intent show_info = new Intent(getApplicationContext(),Interested.class);
                show_info.putExtra("key", key);
                startActivity(show_info);
                break;
        }
    }

    /*
    * @param none
    * @return none
    * Resets the message according to the star count of the social
    */
    public void setMessage(){
        String ans = "";
        ans += date + "\n";
        ans += description + "\n\n";
        ans += "Submitted by " + email+ "\n";
        if(stars == 1)
            ans += stars + " person is interested.";
        else
            ans += stars + " people are interested.";
        content.setText(ans);
    }

    //Runs AsyncTask to download image
    class DownloadFilesTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... strings) {
            try {return Glide.
                    with(getApplicationContext()).
                    load(strings[0]).
                    asBitmap().
                    into(100, 100). // Width and height
                    get();}
            catch (Exception e) {return null;}
        }

        protected void onProgressUpdate(Void... progress) {}

        protected void onPostExecute(Bitmap result) {
            Palette.PaletteAsyncListener paletteListener = new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette palette) {
                    int defaulto = 0x000000;
                    tool.setBackgroundColor(palette.getDarkMutedColor(defaulto));
                    star.setBackgroundColor(palette.getVibrantColor(defaulto));
                }
            };
            if (result != null && !result.isRecycled()) {
                Palette.from(result).generate(paletteListener);
            }
            image.setImageBitmap(result);
        }
    }
}
