package com.example.apptrail4;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

public class AdapteChatList extends  RecyclerView.Adapter<AdapteChatList.ChatListHolder>{

    Context context;
    List<ModelUser>  userList;
    private HashMap<String,String> lastMessageMap,LastMessagetypeMap;

    public AdapteChatList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
        LastMessagetypeMap = new HashMap<>();
    }

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup viewgroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist,viewgroup,false);
        return new ChatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListHolder holder, int i) {

        final String samnevalekiUID = userList.get(i).getUid();
        String userImagestr = userList.get(i).getImage();
        String userNamestr = userList.get(i).getDisplayname();
       String lastMessagestr = lastMessageMap.get(samnevalekiUID);
       String theLastMessage_type_str = LastMessagetypeMap.get(samnevalekiUID);


       holder.username.setText(userNamestr);


        if (lastMessagestr == null || lastMessagestr.equals("default")){

            holder.lastmessage.setVisibility(View.GONE);
            holder.Image_Message.setVisibility(View.GONE);

        }
        else {

            if (lastMessagestr.equals("Image_hai_hgashjflyhoifheoyfhoiahfiof7q9084hrue67ryef697t02y7tv7ty28063yt8y8yf")){
                holder.lastmessage.setVisibility(View.GONE);
                holder.Image_Message.setVisibility(View.VISIBLE);
            }
            else {

                holder.lastmessage.setVisibility(View.VISIBLE);
                holder.Image_Message.setVisibility(View.GONE);
                holder.lastmessage.setText(lastMessagestr);
            }
        }



//        assert theLastMessage_type_str != null;
//        if (theLastMessage_type_str.equals("image") || theLastMessage_type_str.equals("default")){
//
//           holder.lastmessage.setVisibility(View.GONE);
//           holder.Image_Message.setVisibility(View.VISIBLE);
//
//       }
//        if (theLastMessage_type_str.equals("text")){
//
//            if (lastMessagestr == null || lastMessagestr.equals("default")){
//
//                holder.lastmessage.setVisibility(View.GONE);
//                holder.Image_Message.setVisibility(View.GONE);
//
//            }
//            else {
//
//                holder.lastmessage.setVisibility(View.VISIBLE);
//                holder.Image_Message.setVisibility(View.GONE);
//                holder.lastmessage.setText(lastMessagestr);
//            }
//
//       }
//        else {
//
//            holder.lastmessage.setVisibility(View.VISIBLE);
//            holder.Image_Message.setVisibility(View.GONE);
//            holder.lastmessage.setText("na image na msg something else");
//
//
//
//        }


       try {
           Glide.with(context).load(userImagestr).circleCrop().into(holder.profilePic);

       }
       catch (Exception e)
       {
               Glide.with(context).load(R.drawable.user_icon).circleCrop().into(holder.profilePic);

    }
       if (userList.get(i).getOnlineStatus().equals("Online")){

     holder.onlineStatusCircle.setImageResource(R.drawable.circle_online);
       }
       else {
           holder.onlineStatusCircle.setVisibility(View.GONE);
       }

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent gotochat = new Intent(context,Personal_Chat_Activity.class);
                gotochat.putExtra("samnevaleuserkiUid",samnevalekiUID);
                context.startActivity(gotochat);
           }
       });






    }

    public void setLastMessageMap(String userId, String lastMessage){

        lastMessageMap.put(userId,lastMessage);

    }

    public void setLastMessagetypeMap(String userId, String theLastMessage_type_str) {

        LastMessagetypeMap.put(userId,theLastMessage_type_str);
    }

    @Override
    public int getItemCount() {
    return userList.size();
    }




    class ChatListHolder extends RecyclerView.ViewHolder{

        ImageView profilePic,onlineStatusCircle;
        TextView username,lastmessage,Image_Message;

        public ChatListHolder( @NonNull View itemView) {
            super(itemView);

         profilePic =  itemView.findViewById(R.id.profilePicId);
        onlineStatusCircle  =  itemView.findViewById(R.id.onlineStatusCircleID);
         username =  itemView.findViewById(R.id.usernameId);
         lastmessage =  itemView.findViewById(R.id.lastmessageId);
         Image_Message =  itemView.findViewById(R.id.image_Massage_Id);

         Image_Message.setVisibility(View.GONE);

        }



    }
}
