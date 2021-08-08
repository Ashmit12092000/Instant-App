package com.project.tesla.Project.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.project.tesla.Project.Activity.CommentsActivity;
import com.project.tesla.Project.Activity.MainActivity;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;
import com.project.tesla.databinding.CustomDialogBinding;
import com.project.tesla.databinding.UserPostsGridLayoutBinding;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserGridPostsAdapter extends RecyclerView.Adapter<UserGridPostsAdapter.UserGridPostsViewHolder> {

    Context context;
    ArrayList<Post> postList;
    String name;
    String profile_img;
    String dp_image_uri;
    String convTime;
    String likes_count;
    int comments_count;

    DatabaseReference likeref;
    SimpleExoPlayer exoPlayer;

    public UserGridPostsAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public UserGridPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_posts_grid_layout,parent,false);
        return new UserGridPostsViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull UserGridPostsViewHolder holder, int position) {
        Post post=postList.get(position);
        Long time=post.getTimestamp();
        holder.binding.gridPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.commentsbtnclick(post.getPost_id(),post.getUid(),post.getImage_uri(),post.getCaption_txt(),post.getUsername(),time);
            }
        });

        if(post.getPost_type().equals("video")){
            try {
                holder.binding.gridPostImg.setVisibility(View.GONE);
                holder.binding.videoplayer.setVisibility(View.VISIBLE);
                TrackSelector trackSelector=new com.google.android.exoplayer2.trackselection.DefaultTrackSelector();

                exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

                String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
                Uri videouri = Uri.parse(post.getVideo_url());

                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videouri);

                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(false);

                holder.binding.videoplayer.setPlayer(exoPlayer);
            } catch (Exception e) {

                Log.e("TAG", "Error : " + e.toString());
            }
        }else{
            holder.binding.gridPostImg.setVisibility(View.VISIBLE);
            holder.binding.videoplayer.setVisibility(View.GONE);
            Glide.with(context).load(post.getImage_uri()).placeholder(R.drawable.photo).into(holder.binding.gridPostImg);
        }

        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(post.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            name=snapshot.child("name").getValue(String.class);
                            profile_img=snapshot.child("profile_image").getValue(String.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.binding.gridPostImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null);
                CustomDialogBinding custombinding=CustomDialogBinding.bind(view);
                
                
                
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(custombinding.getRoot())
                        .setCancelable(true)
                        .create();

                Glide.with(context).load(post.getImage_uri()).into(custombinding.customDiagPostImg);
                custombinding.customDiagUserName.setText(name);
                Glide.with(context).load(profile_img).placeholder(R.drawable.photo).into(custombinding.customDiagProfileImg);
                Long tim=post.getTimestamp();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                String timetext=convTimetoText(dateFormat.format(new Date(tim)));
                custombinding.customDiagTimeofpost.setText(timetext);
                holder.getlikedStatus(post.getPost_id(),custombinding.customDiagLikebutton);
                holder.nrlikes(post.getPost_id(),custombinding.customDiagLikesCount);
                try {
                    FirebaseDatabase.getInstance().getReference("Post_comments")
                            .child(post.getPost_id())
                            .child("comments")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    comments_count = (int) snapshot.getChildrenCount();
                                    custombinding.customDiagCommentsCount.setText(String.valueOf(comments_count));
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                custombinding.customDiagLikebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(custombinding.customDiagLikebutton.getTag().equals("not_liked")){
                            FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPost_id())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(true);
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference().child("likes").child(post.getPost_id())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .removeValue();
                        }
                    }
                });
                dialog.show();
                return false;
            }
        });

    }

    private String convTimetoText(String time) {
        try
        {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date past = format.parse(time);
            Date now = new Date();
            long second= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minute=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hour=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long day=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
//
//          System.out.println(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " milliseconds ago");
//          System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
//          System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
//          System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");

            if (second < 60) {
                convTime = second + " Seconds " + "Ago";
            } else if (minute < 60) {
                convTime = minute + " Minutes "+"Ago";
            } else if (hour < 24) {
                convTime = hour + " Hours "+"Ago";
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + "Ago";
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + "Ago";
                } else {
                    convTime = (day / 7) + " Week " + "Ago";
                }
            } else if (day < 7) {
                convTime = day+" Days "+"Ago";
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }
        return  convTime;
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class UserGridPostsViewHolder extends  RecyclerView.ViewHolder{
        UserPostsGridLayoutBinding binding;
        
        public UserGridPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=UserPostsGridLayoutBinding.bind(itemView);

        }

        public void getlikedStatus(String postkey, ImageView likeimg) {
            final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            likeref=FirebaseDatabase.getInstance().getReference("likes").child(postkey);
            likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(firebaseUser.getUid()).exists()) {

                        likeimg.setImageResource(R.drawable.ic_heart);
                        likeimg.setTag("liked");
                    }
                    else{
                        likeimg.setImageResource(R.drawable.ic_unlike);
                        likeimg.setTag("not_liked");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        public void nrlikes(String post_id, TextView LikesCount) {
            DatabaseReference likesref=FirebaseDatabase.getInstance().getReference().child("likes")
                    .child(post_id);
            likesref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int likescount=(int)snapshot.getChildrenCount();
                    LikesCount.setText(likescount+" Likes");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private String convTimetoText(String time) {
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date past = format.parse(time);
                Date now = new Date();
                long second= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
                long minute=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
                long hour=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
                long day=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
//
//          System.out.println(TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " milliseconds ago");
//          System.out.println(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " minutes ago");
//          System.out.println(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hours ago");
//          System.out.println(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days ago");

                if (second < 60) {
                    convTime = second + " Seconds " + "Ago";
                } else if (minute < 60) {
                    convTime = minute + " Minutes "+"Ago";
                } else if (hour < 24) {
                    convTime = hour + " Hours "+"Ago";
                } else if (day >= 7) {
                    if (day > 360) {
                        convTime = (day / 360) + " Years " + "Ago";
                    } else if (day > 30) {
                        convTime = (day / 30) + " Months " + "Ago";
                    } else {
                        convTime = (day / 7) + " Week " + "Ago";
                    }
                } else if (day < 7) {
                    convTime = day+" Days "+"Ago";
                }
            }
            catch (Exception j){
                j.printStackTrace();
            }
            return  convTime;
        }

        public void commentsbtnclick(String post_id, String uid, String image_uri, String caption_txt, String username, Long time) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        dp_image_uri = snapshot.child("profile_image").getValue(String.class);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    Intent intent=new Intent(context, CommentsActivity.class);

                    intent.putExtra("postID",post_id);
                    intent.putExtra("uid",uid);
                    intent.putExtra("post_img",image_uri);
                    intent.putExtra("time",dateFormat.format(new Date(time)));
                    intent.putExtra("username",username);
                    intent.putExtra("userDP",dp_image_uri);
                    intent.putExtra("caption",caption_txt);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    ((MainActivity)context).finish();

        }
    }
}
