package com.project.tesla.Project.Adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tesla.Project.Cryptography.DecodingMessages;
import com.project.tesla.Project.Model.Message;
import com.project.tesla.R;
import com.project.tesla.databinding.RecieverSampleChatBinding;
import com.project.tesla.databinding.SendersSampleChatBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter{
    Context context;
    List<Message> messages;
    String senderRoom;
    String receiverRoom;
    SimpleExoPlayer exoPlayer;
    FirebaseDatabase db=FirebaseDatabase.getInstance();
    final int ITEM_SENT=1;
    final int ITEM_RECEIVED=2;
    private int appNameRes=R.string.app_name;

    public MessageAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == ITEM_SENT){
            View view= LayoutInflater.from(context).inflate(R.layout.senders_sample_chat,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.reciever_sample_chat,parent,false);
            return new RecieverViewHolder(view);
        }
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        Message message =messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderID())){
            return  ITEM_SENT;
        }
        else {
            return ITEM_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.setIsRecyclable(false);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;

            if (message.getMessage().equals("post")) {
                viewHolder.binding.isseen.setVisibility(View.GONE);
                viewHolder.binding.videoChatBox1.setVisibility(View.GONE);
                viewHolder.binding.senderTextBox.setVisibility(View.GONE);
                viewHolder.binding.pdfSenderBox.setVisibility(View.GONE);
                viewHolder.binding.senderChatBox.setVisibility(View.VISIBLE);
                viewHolder.binding.imageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.getImage_uri())
                        .apply(new RequestOptions().override(500,500))
                        .centerCrop()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.binding.imageView);
                db.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Long tim = snapshot.child("timestamp").getValue(Long.class);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    viewHolder.binding.imageChatMsgTime.setText(dateFormat.format(new Date(tim)));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            } else if (message.getMessage().equals("camera")) {
                viewHolder.binding.isseen.setVisibility(View.GONE);
                viewHolder.binding.videoChatBox1.setVisibility(View.GONE);
                viewHolder.binding.senderTextBox.setVisibility(View.GONE);
                viewHolder.binding.pdfSenderBox.setVisibility(View.GONE);
                viewHolder.binding.senderChatBox.setVisibility(View.VISIBLE);
                viewHolder.binding.imageView.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.getImage_uri())
                        .apply(new RequestOptions().override(500,500))
                        .centerCrop()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.binding.imageView);
                db.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Long tim = snapshot.child("timestamp").getValue(Long.class);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    if (message.getMessage().equals("photo")) {
                                        viewHolder.binding.imageChatMsgTime.setText(dateFormat.format(new Date(tim)));
                                    }
                                    else {
                                        viewHolder.binding.textMsgTime.setText(dateFormat.format(new Date(tim)));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            } else if (message.getMessage().equals("video")) {
                viewHolder.binding.isseen.setVisibility(View.GONE);
                viewHolder.binding.senderChatBox.setVisibility(View.GONE);
                viewHolder.binding.senderTextBox.setVisibility(View.GONE);
                viewHolder.binding.pdfSenderBox.setVisibility(View.GONE);
                viewHolder.binding.videoChatBox1.setVisibility(View.VISIBLE);
                try {

                    TrackSelector trackSelectorDef = new DefaultTrackSelector();
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);

                    String userAgent = Util.getUserAgent(context, context.getString(appNameRes));
                    DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
                    Uri videouri = Uri.parse(message.getVideo_uri());

                    MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videouri);  // creating a media source
                    ;

                    exoPlayer.prepare(mediaSource);
                    exoPlayer.setPlayWhenReady(false);

                    viewHolder.binding.videoplayer.setPlayer(exoPlayer);
                } catch (Exception e) {

                    Log.e("TAG", "Error : " + e.toString());
                }
                db.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Long tim = snapshot.child("timestamp").getValue(Long.class);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    viewHolder.binding.videochatMsgTime.setText(dateFormat.format(new Date(tim)));

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
            else if(message.getMessage().equals("pdf")){
                viewHolder.binding.isseen.setVisibility(View.GONE);
                viewHolder.binding.videoChatBox1.setVisibility(View.GONE);
                viewHolder.binding.senderChatBox.setVisibility(View.GONE);
                viewHolder.binding.senderTextBox.setVisibility(View.GONE);
                viewHolder.binding.pdfSenderBox.setVisibility(View.VISIBLE);
                viewHolder.binding.filename.setText(" "+message.getFilename());
                //viewHolder.binding.message.setText(message.getMessage());
                db.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Long tim = snapshot.child("timestamp").getValue(Long.class);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    viewHolder.binding.pdfMsgTime.setText(dateFormat.format(new Date(tim)));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

            else {
                viewHolder.binding.isseen.setVisibility(View.GONE);

                viewHolder.binding.videoChatBox1.setVisibility(View.GONE);
                viewHolder.binding.senderChatBox.setVisibility(View.GONE);
                viewHolder.binding.pdfSenderBox.setVisibility(View.GONE);
                viewHolder.binding.senderTextBox.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setText(DecodingMessages.decode(message.getMessage()));
                db.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Long tim = snapshot.child("timestamp").getValue(Long.class);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    viewHolder.binding.textMsgTime.setText(dateFormat.format(new Date(tim)));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
            /*if(position==messages.size()-1){
                viewHolder.binding.isseen.setVisibility(View.VISIBLE);
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("chat_info");
                ref.child(message.getSenderID())
                        .child(message.getRecieverID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String msg_Seen=snapshot.child("msg_seen").getValue(String.class);
                                    if(msg_Seen==null){
                                        Toast.makeText(context, "Msg seen null", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(msg_Seen.equals("true"))
                                    {
                                        viewHolder.binding.isseen.setText("Seen");
                                    }
                                    else
                                    {
                                        viewHolder.binding.isseen.setText("Delivered");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }


            /*viewHolder.binding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatPhotoViewer.class);
                    intent.putExtra("image_uri", message.getImage_uri());
                    intent.putExtra("chat_photo_id", "1");
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.pdfSenderBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, PDFViewer.class);
                    intent.putExtra("pdf_url",message.getPdf_uri());
                    intent.putExtra("uid",message.getPdf_uri());
                    context.startActivity(intent);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null);
                    DeleteLayoutBinding binding = DeleteLayoutBinding.bind(view);
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();
                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage(EncodeMessages.encode("This message is removed."));
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageID()).setValue(message);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(message.getMessageID()).setValue(message);
                            dialog.dismiss();
                        }
                    });
                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage(EncodeMessages.encode("This message is removed."));
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageID()).setValue(null);
                            dialog.dismiss();
                        }
                    });
                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return false;
                }
            });*/
        }
        else {
            RecieverViewHolder viewHolder = (RecieverViewHolder)holder;

            if(message.getMessage().equals("post")){
                viewHolder.binding.rcvVideoLayout.setVisibility(View.GONE);
                viewHolder.binding.txtmsgLayout.setVisibility(View.GONE);
                viewHolder.binding.pdfinearLayout.setVisibility(View.GONE);
                viewHolder.binding.rcvImageMsgLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.imageView2.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.getImage_uri())
                        .apply(new RequestOptions().override(500,500))
                        .centerCrop()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.binding.imageView2);
            }
            else if (message.getMessage().equals("camera")){
                viewHolder.binding.rcvVideoLayout.setVisibility(View.GONE);
                viewHolder.binding.txtmsgLayout.setVisibility(View.GONE);
                viewHolder.binding.pdfinearLayout.setVisibility(View.GONE);
                viewHolder.binding.rcvImageMsgLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.imageView2.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.getImage_uri())
                        .apply(new RequestOptions().override(500,500))
                        .centerCrop()
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.binding.imageView2);
            }
            else if (message.getMessage().equals("video")){
                viewHolder.binding.rcvImageMsgLayout.setVisibility(View.GONE);
                viewHolder.binding.txtmsgLayout.setVisibility(View.GONE);
                viewHolder.binding.pdfinearLayout.setVisibility(View.GONE);
                viewHolder.binding.rcvVideoLayout.setVisibility(View.VISIBLE);
                try {

                    TrackSelector trackSelectorDef = new DefaultTrackSelector();
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);

                    String userAgent = Util.getUserAgent(context, context.getString(appNameRes));
                    DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,userAgent);
                    Uri videouri = Uri.parse(message.getVideo_uri());

                    MediaSource mediaSource = new  ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videouri);
                    exoPlayer.prepare(mediaSource);
                    exoPlayer.setPlayWhenReady(false);

                    viewHolder.binding.videoplayer1.setPlayer(exoPlayer);
                } catch (Exception e) {

                    Log.e("TAG", "Error : " + e.toString());
                }

            }
            else if(message.getMessage().equals("pdf")){
                viewHolder.binding.rcvImageMsgLayout.setVisibility(View.GONE);
                viewHolder.binding.txtmsgLayout.setVisibility(View.GONE);
                viewHolder.binding.rcvVideoLayout.setVisibility(View.GONE);
                viewHolder.binding.pdfinearLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.filename.setText(""+message.getFilename());
                db.getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(message.getMessageID())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Long tim = snapshot.child("timestamp").getValue(Long.class);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                    viewHolder.binding.pdfMsgTime.setText(dateFormat.format(new Date(tim)));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
            else {
                viewHolder.binding.rcvImageMsgLayout.setVisibility(View.GONE);
                viewHolder.binding.rcvVideoLayout.setVisibility(View.GONE);
                viewHolder.binding.pdfinearLayout.setVisibility(View.GONE);
                viewHolder.binding.txtmsgLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.recieverMessage.setText(DecodingMessages.decode(message.getMessage()));
            }
            db.getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Long tim = snapshot.child("timestamp").getValue(Long.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                if (message.getMessage().equals("photo")) {
                                    viewHolder.binding.rcvVideoLayout.setVisibility(View.GONE);
                                    viewHolder.binding.txtmsgLayout.setVisibility(View.GONE);
                                    viewHolder.binding.rcvImageMsgLayout.setVisibility(View.VISIBLE);
                                    viewHolder.binding.rcvImageMsgTime.setText(dateFormat.format(new Date(tim)));
                                }
                                else if (message.getMessage().equals("video")){
                                    viewHolder.binding.rcvVideoLayout.setVisibility(View.VISIBLE);
                                    viewHolder.binding.txtmsgLayout.setVisibility(View.GONE);
                                    viewHolder.binding.rcvImageMsgLayout.setVisibility(View.GONE);
                                    viewHolder.binding.videoRcvMsgTime.setText(dateFormat.format(new Date(tim)));
                                }
                                else {
                                    viewHolder.binding.rcvVideoLayout.setVisibility(View.GONE);
                                    viewHolder.binding.txtmsgLayout.setVisibility(View.VISIBLE);
                                    viewHolder.binding.rcvImageMsgLayout.setVisibility(View.GONE);
                                    viewHolder.binding.txtRcvMsgTime.setText(dateFormat.format(new Date(tim)));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            /*viewHolder.binding.imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ChatPhotoViewer.class);
                    intent.putExtra("image_uri",message.getImage_uri());
                    context.startActivity(intent);
                }
            });
            viewHolder.binding.pdfinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, PDFViewer.class);
                    intent.putExtra("pdf_url",message.getPdf_uri());
                    context.startActivity(intent);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View view =LayoutInflater.from(context).inflate(R.layout.delete_layout,null);
                    DeleteLayoutBinding binding = null;
                    AlertDialog dialog = new AlertDialog.Builder(context)
                            .setTitle("Delete Message")
                            .setView(binding.getRoot())
                            .create();
                    binding.everyone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage(EncodeMessages.encode("This message is deleted"));
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageID()).setValue(message);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(message.getMessageID()).setValue(message);
                            dialog.dismiss();
                        }
                    });
                    binding.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            message.setMessage(EncodeMessages.encode("This message is removed."));
                            FirebaseDatabase.getInstance().getReference()
                                    .child("chats")
                                    .child(senderRoom)
                                    .child("messages")
                                    .child(message.getMessageID()).setValue(null);
                            dialog.dismiss();
                        }
                    });
                    binding.cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    return false;
                }
            });*/


        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    public  class SenderViewHolder extends RecyclerView.ViewHolder{
        SendersSampleChatBinding binding;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=SendersSampleChatBinding.bind(itemView);
        }
    }
    public  class RecieverViewHolder extends RecyclerView.ViewHolder{
        RecieverSampleChatBinding binding;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding =RecieverSampleChatBinding.bind(itemView);
        }
    }
}
