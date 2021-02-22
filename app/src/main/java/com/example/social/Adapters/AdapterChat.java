package com.example.social.Adapters;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.social.Activities.ThereProfileActivity;
import com.example.social.Models.ModelChat;
import com.example.social.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;
    FirebaseUser fUser;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String message = chatList.get(position).getMessage();
//        String timeStamp = chatList.get(position).getTimestamp();
        Long timeStamp = Long.parseLong(chatList.get(position).getTimestamp());


        String type = chatList.get(position).getType();
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH  );
//        cal.setTimeInMillis(Long.parseLong(timeStamp));
//        String dateTime = DateFormat.format("dd/MM/yyy hh:mm aa", cal).toString();
        Date time = new Date((long) timeStamp);
        String pattern = "yyyy-MM-dd HH:MM aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (type.equals("text")){
            // text message
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageIv.setVisibility(View.GONE);
            holder.messageVv.setVisibility(View.GONE);
            holder.messageTv.setText(message);

        } else {
            String[] message1 = message.split(",");
            if (type.equals("image")){
                // image message
                holder.messageTv.setVisibility(View.GONE);
                holder.messageVv.setVisibility(View.GONE);
                holder.messageIv.setVisibility(View.VISIBLE);

                Picasso.get().load(message1[0]).placeholder(R.drawable.ic_image_black).into(holder.messageIv);
            } else if (type.equals("video")) {
                // image message
                holder.messageTv.setVisibility(View.GONE);
                holder.messageIv.setVisibility(View.GONE);
                holder.messageVv.setVisibility(View.VISIBLE);
                holder.messageVv.setVideoPath(message1[0]);
                //holder.messageVv.requestFocus();
                holder.messageVv.seekTo(1);
                holder.messageVv.pause();


            } else if (type.equals("document")){
                holder.messageTv.setVisibility(View.VISIBLE);
                holder.messageIv.setVisibility(View.GONE);
                holder.messageVv.setVisibility(View.GONE);
                holder.messageTv.setTypeface(holder.messageTv.getTypeface(), Typeface.ITALIC);
                holder.messageTv.setPaintFlags(holder.messageTv.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
                holder.messageTv.setGravity(Gravity.RIGHT);
                holder.messageTv.setText(message1[1]);
            }
        }
        holder.messageVv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(holder.messageVv);
                holder.messageVv.setMediaController(mediaController);
            }
        });

        holder.timeTv.setText(simpleDateFormat.format(time).toString());

        try {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_default_img).into(holder.profileIv);
        } catch (Exception e){
            holder.profileIv.setImageResource(R.drawable.ic_default_img_white);
        }

        // click to show delete dialog
        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (type.equals("text")){
                    checkDelete(position);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setItems(new String[]{"Download", "Delete"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which ==0){
                                downloadFile(position);
                            }
                            if (which ==1){
                                checkDelete(position);
                            }
                        }
                    });
                    builder.create().show();
                }


                return false;
            }
        });

        if (position == chatList.size() - 1) {
            if (chatList.get(position).isSeen()) {
                holder.isSeenTv.setText("Seen");
            } else {
                holder.isSeenTv.setText("Delivered");
            }
        } else {
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }
    private void checkDelete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this message?");
        // delete button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMesage(position);
            }
        });

        // cancel delete button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dimiss dialog
                dialog.dismiss();
            }
        });
        // create and show dialog
        builder.create().show();
    }

    private void downloadFile(int position) {
        String[] message =chatList.get(position).getMessage().split(",");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(message[0]);
        storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String fileDirectory = Environment.DIRECTORY_DOWNLOADS;
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(message[0]);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(""+fileDirectory,""+message[1]);
                downloadManager.enqueue(request);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteMesage(int position) {
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String msgTimeStamp = chatList.get(position).getTimestamp();
        if (!chatList.get(position).getType().equals("text")){
            String path = (chatList.get(position).getMessage().split(","))[0];
            FirebaseStorage.getInstance().getReferenceFromUrl(path).delete();
        }
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    if (ds.child("sender").getValue().equals(myUID)) {

                        ds.getRef().removeValue();
                        //chatList.remove(position);

                        Toast.makeText(context, "Message deleted...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "You can delete only your messages...", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView profileIv, messageIv;
        VideoView messageVv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLayout; //for click listener to show delete

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageIv = itemView.findViewById(R.id.messageIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            messageVv = itemView.findViewById(R.id.messageVv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
