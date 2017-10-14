package com.example.android.theguardiannewsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rean on 10/13/2017.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private final String AUTHOR_SEPERATOR = "|";
    private final String DATE_SEPERATOR = "T";
    private String title;
    private String author;
    private String date;

    public NewsAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        //If the list is null, do the inflater
        if (listItemView == null) {

            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }

        News newsAdapterItem = getItem(position);

        //If the title of the news article has has an author's name, it will split it and assign each value to its corresponded variable
        String newsTitle = newsAdapterItem.getTitle();
        if (newsTitle.contains(AUTHOR_SEPERATOR)) {
            String[] authorTitleArray = newsTitle.split("\\" +AUTHOR_SEPERATOR);
            title = authorTitleArray[0];
            author = authorTitleArray[1];
        } else {
            title = newsTitle;
            author = getContext().getString(R.string.no_author);
        }

        //It will split the time from the date and display only the date in the screen
        String newsDate = newsAdapterItem.getDate();
        if (newsDate.contains(DATE_SEPERATOR)) {
            String[] dateArray = newsDate.split(DATE_SEPERATOR);
            date = dateArray[0];
        } else {
            date = newsDate;
        }

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(title);

        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);
        sectionTextView.setText(newsAdapterItem.getSectionName());

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author_text_view);
        authorTextView.setText(author);

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        dateTextView.setText(date);

        return listItemView;
    }
}