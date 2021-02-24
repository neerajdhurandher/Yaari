package com.example.apptrail4;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter_Show_Comment extends RecyclerView.Adapter<Adapter_Show_Comment.CommentHolder>   {

  Context context;
  List<Model_Show_Comment> commentList;

    public Adapter_Show_Comment(Context context, List<Model_Show_Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_show_comment,viewGroup,false);
        return new  CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int i) {

        String commentTxt = commentList.get(i).getCommenttxt();
        String cId = commentList.get(i).getcId();
        String Time = commentList.get(i).getTime();
        String uid = commentList.get(i).getuUID();
        String uNAme = commentList.get(i).getuName();
        String uDp = commentList.get(i).getuDp();


        // covert timeStamp to normal time formate
        Calendar calendar =  Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(Time));
        final String dateTime = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

        // set data

        holder.comment_User_Name.setText(uNAme);
        holder.commentTime.setText(dateTime);
        holder.commentTxt.setText(commentTxt);

        try {
            Glide.with(context).load(uDp).circleCrop().into(holder.comment_User_Dp);

        }
        catch (Exception e) {
            Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.comment_User_Dp);

        }






    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }





    class CommentHolder extends RecyclerView.ViewHolder {

        ImageView comment_User_Dp;
        TextView comment_User_Name,commentTxt,commentTime;

        public CommentHolder(@NonNull View itemView) {
            super(itemView);

            comment_User_Name = itemView.findViewById(R.id.usernameS);
            comment_User_Dp = itemView.findViewById(R.id.useriamgeS);
            commentTxt = itemView.findViewById(R.id.commentTxtId);
            commentTime = itemView.findViewById(R.id.comment_time);


        }
    }



}
