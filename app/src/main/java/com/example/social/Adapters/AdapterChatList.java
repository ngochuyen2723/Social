package com.example.social.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.social.Activities.AddPostActivity;
import com.example.social.Activities.ChatActivity;
import com.example.social.Activities.DashboardActivity;
import com.example.social.Activities.GroupInfoActivity;
import com.example.social.Activities.ThereProfileActivity;
import com.example.social.Fragments.ChatListFragment;
import com.example.social.Models.ModelUser;
import com.example.social.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends RecyclerView.Adapter<AdapterChatList.MyHolder> {

    Context context;
    List<ModelUser> userList;   // get user info
    private HashMap<String, String> lastMessageMap;

    // constructor


    public AdapterChatList(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout row_chatlist.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // get data
        String hisUid = userList.get(position).getUid();
        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String lastMessage = lastMessageMap.get(hisUid);

        // set data
        holder.nameTv.setText(userName);
        if (lastMessage == null || lastMessage.equals("default")){
            holder.lastMessageTv.setVisibility(View.GONE);
        } else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }

        try {
            Picasso.get().load(userImage).placeholder(R.drawable.ic_default_img_white).into(holder.profileIv);
        } catch (Exception e){
            holder.profileIv.setImageResource(R.drawable.ic_default_img);
        }

        // set online status of other users in chatlist

        if (!userList.isEmpty()){
            if (userList.get(position).getOnlineStatus().equals("online")){
                // online
                holder.onlineStatusIv.setImageResource(R.drawable.circle_online);
            } else {
                // offline
                holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);
            }
        }



        // handle click of user in chatlist
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start chat activity with that user
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });
        holder.infoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open group chat
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid",hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        // views of row_chatlist.xml
        ImageView profileIv, onlineStatusIv, infoIv;
        TextView nameTv, lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            // init views
            profileIv = itemView.findViewById(R.id.profileIv);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            infoIv = itemView.findViewById(R.id.infoIv);
        }
    }
}
