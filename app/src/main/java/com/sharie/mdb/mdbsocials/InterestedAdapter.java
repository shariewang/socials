package com.sharie.mdb.mdbsocials;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class InterestedAdapter extends RecyclerView.Adapter<InterestedAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<String> people;

    public InterestedAdapter(Context context, ArrayList<String> people) {
        this.people = people;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        String email = people.get(position);
        holder.textView.setText(email);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CustomViewHolder (View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.name);
        }
    }


}