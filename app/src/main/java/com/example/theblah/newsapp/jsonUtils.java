package com.example.theblah.newsapp;

import com.example.theblah.newsapp.models.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by TheBlah on 6/26/2017.
 */

public class jsonUtils {
    public static ArrayList<NewsItem> parseJSON(String json) throws JSONException {
        ArrayList<NewsItem> result = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("articles");

        for(int i = 0; i < items.length(); i++){
            JSONObject item = items.getJSONObject(i);
            NewsItem newsItem = new NewsItem(
                    item.getString("author"),
                    item.getString("title"),
                    item.getString("description"),
                    item.getString("url"),
                    item.getString("urlToImage"),
                    item.getString("publishedAt"));
            result.add(newsItem);
        }
        return result;
    }
}
