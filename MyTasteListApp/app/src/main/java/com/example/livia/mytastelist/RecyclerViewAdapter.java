package com.example.livia.mytastelist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by livia on 30/11/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<String> title ;
    private List<String> poster ;
    private List<String> id ;
    private Context context;
    private String username ;

    public RecyclerViewAdapter(Context context, List<String> title, List<String> poster, List<String> id, String username) {
        this.title = title;
        this.context = context;
        this.poster = poster ;
        this.id = id ;
        this.username = username ;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_view, null);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        rcv.setUsername(username);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        String title = this.title.get(position) ;
        if(title.length() > 17) title = title.substring(0,17) + "..." ;
        holder.title.setText(title);
        holder.setId(id.get(position)) ;
        new DownloadImageTask(holder.poster).execute(poster.get(position));
    }

    @Override
    public int getItemCount() {
        return this.title.size();
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}