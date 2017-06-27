package com.example.theblah.newsapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.theblah.newsapp.models.NewsItem;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by TheBlah on 6/26/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerAdapterViewHolder> {

    private ItemClickListener listener;
    private ArrayList<NewsItem> mData;

    public ArrayList<NewsItem> getData() {
        return mData;
    }

    public void setData(ArrayList<NewsItem> mData) {
        this.mData = mData;
    }

    public RecyclerViewAdapter(ArrayList<NewsItem> mData, ItemClickListener listener) {
        this.listener = listener;
        this.mData = mData;
    }

    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        if (mData == null)
            return 0;
        return mData.size();
    }

    public interface ItemClickListener {
        void onItemClick(int clickedItemIndex);
    }

    class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView description;
        private TextView date;

        public RecyclerAdapterViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            itemView.setOnClickListener(this);
        }

        public void bind(int pos) {
            NewsItem newsItem = mData.get(pos);
            title.setText(newsItem.getTitle());
            description.setText(newsItem.getDescription());
            date.setText(newsItem.getPublishedAt());
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos);
        }
    }
}
