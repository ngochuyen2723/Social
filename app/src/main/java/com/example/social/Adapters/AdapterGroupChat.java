package com.example.social.Adapters;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social.Models.ModelGroupChat;
import com.example.social.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.Date;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChat>{

    private static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private ArrayList<ModelGroupChat> modelGroupChatList;
    private String groupId;

    private FirebaseAuth firebaseAuth;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChatList, String groupId) {
        this.context = context;
        this.modelGroupChatList = modelGroupChatList;
        this.groupId = groupId;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layouts
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent, false);
            return new HolderGroupChat(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent, false);
            return new HolderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        // get data
        ModelGroupChat model = modelGroupChatList.get(position);
        String timestamp = model.getTimestamp();
        String message = model.getMessage();    // if text message then contain message, if image message then contain url of the image storaged in firebase storage
        String senderUid = model.getSender();
        String messageType = model.getType();

        // convert time stamp to dd/mm/YYYY hh:mm am/pm
        Long timeStamp = Long.parseLong(modelGroupChatList.get(position).getTimestamp());
        Date time = new Date((long) timeStamp);
        String pattern = "yyyy-MM-dd HH:MM aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        // set data
        if (messageType.equals("text")){
            // text message, hide messageIv, show messageTv
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageVv.setVisibility(View.GONE);
            holder.messageTv.setText(message);
        } else {
            String[] message1 = message.split(",");
            if (messageType.equals("image")){
                // image message
                holder.messageTv.setVisibility(View.GONE);
                holder.messageVv.setVisibility(View.GONE);
                holder.messageIv.setVisibility(View.VISIBLE);

                Picasso.get().load(message1[0]).placeholder(R.drawable.ic_image_black).into(holder.messageIv);
            } else if (messageType.equals("video")) {
                // image message
                holder.messageTv.setVisibility(View.GONE);
                holder.messageIv.setVisibility(View.GONE);
                holder.messageVv.setVisibility(View.VISIBLE);
                holder.messageVv.setVideoPath(message1[0]);
                //holder.messageVv.requestFocus();
                holder.messageVv.seekTo(1);
                holder.messageVv.pause();


            } else if (messageType.equals("document")){
                holder.messageTv.setVisibility(View.VISIBLE);
                holder.messageIv.setVisibility(View.GONE);
                holder.messageVv.setVisibility(View.GONE);
                holder.messageTv.setTypeface(holder.messageTv.getTypeface(), Typeface.ITALIC);
                holder.messageTv.setPaintFlags(holder.messageTv.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
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

        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (messageType.equals("text")){
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

        holder.timeTv.setText(simpleDateFormat.format(time).toString());

        setUserName(model, holder);

    }

    private void setUserName(ModelGroupChat model, HolderGroupChat holder) {
        // get sender info from uid in model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = "" + ds.child("name").getValue();

                            holder.nameTv.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
        String[] message =modelGroupChatList.get(position).getMessage().split(",");
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


        String msgTimeStamp = modelGroupChatList.get(position).getTimestamp();
        //DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups");
        if (!modelGroupChatList.get(position).getType().equals("text")){
            String path = (modelGroupChatList.get(position).getMessage().split(","))[0];
            FirebaseStorage.getInstance().getReferenceFromUrl(path).delete();
        }
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups").
                child(groupId).child("Messages");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {

                    if (ds.child("sender").getValue().equals(myUID)) {

                        ds.getRef().removeValue();


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
    public int getItemViewType(int position) {
        if (modelGroupChatList.get(position).getSender().equals(firebaseAuth.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return modelGroupChatList.size();
    }

    class HolderGroupChat extends RecyclerView.ViewHolder{

        private TextView nameTv, messageTv, timeTv;
        private ImageView messageIv;
        private VideoView messageVv;
        LinearLayout messageLayout;

        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            messageIv = itemView.findViewById(R.id.messageIv);
            messageVv = itemView.findViewById(R.id.messageVv);
            messageLayout = itemView.findViewById(R.id.messagegroupLayout);
        }
    }
}
