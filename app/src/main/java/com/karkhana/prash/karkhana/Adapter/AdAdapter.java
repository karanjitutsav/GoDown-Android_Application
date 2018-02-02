package com.karkhana.prash.karkhana.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.karkhana.prash.karkhana.JavaClasses.New_Ad_Post;
import com.karkhana.prash.karkhana.R;
import com.karkhana.prash.karkhana.activities.ItemClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prash on 1/3/2018.
 */

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.ViewHolder> {

    private List<New_Ad_Post> Ad_Items;
    private Context context;
    Bundle bundle;
     String refkey;
    //Key for Bundle
    private final static String key = "userData";

    DatabaseReference mDatabase;


    public ImageView ad_Image;

    public AdAdapter(List<New_Ad_Post> ad_Items, Context context) {
        Ad_Items = ad_Items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ad_cardview_row, parent, false);
        return new ViewHolder(view, context, Ad_Items);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final New_Ad_Post adItem = Ad_Items.get(position);

        //holder.post_desc.setText(adItem.getDescription());

        refkey = adItem.getUserId();
        setImage(adItem);


    }

    private void setImage(New_Ad_Post adItem) {
        Picasso.with(context)
                .load(adItem.getImage())
                .into(ad_Image);
    }


    @Override
    public int getItemCount() {
        return Ad_Items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView post_title;
        //public TextView post_desc;
        List<New_Ad_Post> post_details = new ArrayList<>();
        Context ctx;

        public ViewHolder(View itemView, Context ctx, List<New_Ad_Post> post_details) {
            super(itemView);


            this.post_details = post_details;
            this.ctx = ctx;
            itemView.setOnClickListener(this);
            //post_title = itemView.findViewById(R.id.post_title);
            //post_desc = itemView.findViewById(R.id.post_description);
            ad_Image = itemView.findViewById(R.id.post_ad_image);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();

            New_Ad_Post postDetail = this.post_details.get(position);



            Intent item_click = new Intent(this.ctx, ItemClick.class);
            /*item_click.putExtra("Title", postDetail.getTitle());
            item_click.putExtra("Description", postDetail.getDescription());
            item_click.putExtra("Image", postDetail.getImage());
            item_click.putExtra("Cat", postDetail.getCategory());*/
            item_click.putExtra(key, postDetail);
            item_click.putExtra("PushKey", refkey);
            this.ctx.startActivity(item_click);

        }
    }
}
