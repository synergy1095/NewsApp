package com.example.theblah.newsapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.theblah.newsapp.Utility.Constants;
import com.example.theblah.newsapp.models.NewsItem;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by TheBlah on 6/26/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerAdapterViewHolder> {

    private Cursor cursor;
    private Context context;
    private ItemClickListener listener;

    private static final String TAG = "RecyclerViewAdapter";

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public RecyclerViewAdapter(Cursor cursor, ItemClickListener listener) {
        this.listener = listener;
        this.cursor = cursor;
    }

    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //storing context of mainactivity
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item,
                parent,
                false
        );
        return new RecyclerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public interface ItemClickListener {
        void onItemClick(int clickedItemIndex);
    }

    class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView description;
        private TextView date;
        private ImageView thumb;

        public RecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            //added imageview for thumbnail
            thumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            itemView.setOnClickListener(this);
        }

        //set views to data retrieved from db
        public void bind(int pos) {
            cursor.moveToPosition(pos);
            title.setText(cursor.getString(cursor.getColumnIndex(Constants.NewsTable.COLUMN_NAME_TITLE)));
            description.setText(cursor.getString(cursor.getColumnIndex(Constants.NewsTable.COLUMN_NAME_DESCRIPTION)));
            date.setText(cursor.getString(cursor.getColumnIndex(Constants.NewsTable.COLUMN_NAME_PUBLISHED_DATE)));

            String imageURL = cursor.getString(cursor.getColumnIndex(Constants.NewsTable.COLUMN_NAME_IMAGE_URL));
            Log.d(TAG, imageURL);

            if(imageURL != null){
                Glide.with(context)
                        .load(imageURL)
                        .into(thumb);
            }
        }

        //on click handler for when item is clicked
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos);
        }
    }
}
