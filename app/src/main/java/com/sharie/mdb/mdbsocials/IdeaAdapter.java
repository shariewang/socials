package com.sharie.mdb.mdbsocials;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<Idea> ideas;
    private String url;
    private Idea current;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://mdb-socials-5cc85.appspot.com");
    private StorageReference pathReference;


    public IdeaAdapter(Context context, ArrayList<Idea> ideas) {
        this.ideas = ideas;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_view, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        current = ideas.get(position);
        pathReference = storageRef.child(current.key+".jpg");
        storageRef.child(current.key+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                url = uri.toString();
                Glide.with(context).load(url).into(holder.imageView);
            }
        });

        //Sets details of the social
        holder.textView.setText(current.title);
        if(current.stars == 1)
            holder.interested.setText(current.stars+" person is interested");
        else
            holder.interested.setText(current.stars+" people are interested");
        holder.author.setText(current.email +":");

    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView, interested, author;
        ImageView imageView;

        public CustomViewHolder (View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.idea_name);
            this.imageView = (ImageView) view.findViewById(R.id.idea_pic);
            this.interested = (TextView) view.findViewById(R.id.num_interested);
            this.author = (TextView) view.findViewById(R.id.author);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int num = getAdapterPosition();
                    Idea current = ideas.get(num);

                    //Passes all details of this social into the Details class and opens it
                    Intent intent = new Intent(context,Details.class);
                    intent.putExtra("title", current.title);
                    intent.putExtra("descript", current.description);
                    intent.putExtra("email", current.email);
                    intent.putExtra("key", current.key);
                    intent.putExtra("stars", current.stars);
                    intent.putExtra("date", current.date);
                    intent.putExtra("url", url);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }


}