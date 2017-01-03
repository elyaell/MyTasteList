package com.example.livia.mytastelist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by livia on 30/11/16.
 */

public class RecyclerSuggestionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView title;
    public ImageView poster;
    public String id ;
    private final Context context ;
    private String username ;
    private int type ;

    public RecyclerSuggestionsHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setOnClickListener(this);
        title = (TextView)itemView.findViewById(R.id.grid_text);
        poster = (ImageView)itemView.findViewById(R.id.grid_image);
    }

    public void setId(String id){
        this.id = id ;
    }
    public void setType(int type){ this.type = type ; }

    @Override
    public void onClick(View view) {
        Intent myIntent ;
        if(type == 1) myIntent = new Intent(context, MovieDistance2.class);
        else myIntent = new Intent(context, MovieDistance.class) ;
        myIntent.putExtra("id_film", id) ;
        myIntent.putExtra("username", username) ;
        context.startActivity(myIntent); ;
        Toast.makeText(view.getContext(), "Clicked Movie = " + id, Toast.LENGTH_SHORT).show();
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
