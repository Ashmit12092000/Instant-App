package com.project.tesla.Project.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tesla.Project.Activity.ShareActivity;
import com.project.tesla.Project.Activity.UserProfileActivity;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;
import com.project.tesla.databinding.ReelsLayoutBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReelsAdapter extends RecyclerView.Adapter<ReelsAdapter.MyViewHolder> {
    Context context;
    List<Post>  videoList;
    public ReelsAdapter(Context context, List<Post> videoList) {
        this.context = context;
        this.videoList = videoList;
    }
    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reels_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.setData(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ReelsLayoutBinding binding;

        Boolean isMuted=false;
        MediaPlayer mediaPlayer;
        String name;


        int stopPosition;
        public MyViewHolder(View x) {
            super(x);
            binding=ReelsLayoutBinding.bind(x);

        }
        @SuppressLint("ClickableViewAccessibility")
        void setData(Post post){
            binding.videoView.setVideoPath(post.getVideo_url());
            binding.reelsCaption.setText(post.getCaption_txt());
            binding.reelsShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ShareActivity.class);
                    intent.putExtra("postType",post.getPost_type());
                    intent.putExtra("video_uri",post.getVideo_url());
                    intent.putExtra("postID",post.getPost_id());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

                }
            });

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("users")
                    .child(post.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                name=snapshot.child("name").getValue(String.class);
                                String image_uri=snapshot.child("profile_image").getValue(String.class);
                                Glide.with(context).load(image_uri).placeholder(R.drawable.user).into(binding.reelsUserProfilePhoto);
                                binding.reelsUsername.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
            binding.reelsUsername.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(context, UserProfileActivity.class);
                            intent.putExtra("uid",post.getUid());
                            context.startActivity(intent);
                        }
                    });
            binding.reelsUserProfilePhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(context, UserProfileActivity.class);
                            intent.putExtra("uid",post.getUid());
                            context.startActivity(intent);
                        }
                    });

            binding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer=mp;
                    binding.progressBar.setVisibility(View.GONE);
                    mp.start();
                }
            });
            getlikedStatus(post.getVid());
            nrlikes(post.getVid());
            binding.reelsLikeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(binding.reelsLikeBtn.getTag().equals("not_liked")){
                        FirebaseDatabase.getInstance().getReference().child("likes").child(post.getVid())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(true);
                    }
                    else{
                        FirebaseDatabase.getInstance().getReference().child("likes").child(post.getVid())
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();
                    }
                }
            });
            binding.audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isMuted){
                        binding.audio.setImageResource(R.drawable.ic_unmute);
                        mediaPlayer.setVolume(1f,1f);
                        isMuted=false;
                    }
                    else{
                        binding.audio.setImageResource(R.drawable.ic_mute);
                        mediaPlayer.setVolume(0f,0f);
                        isMuted=true;
                    }
                }
            });

            binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
            binding.videoView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        binding.heartAnimation.setVisibility(View.GONE);
                        if(binding.reelsLikeBtn.getTag().equals("not_liked")){
                            binding.heartAnimation.setVisibility(View.VISIBLE);
                            binding.heartAnimation.playAnimation();
                            FirebaseDatabase.getInstance().getReference().child("likes").child(post.getVid())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(true);
                        }
                        else{
                            binding.heartAnimation.setVisibility(View.GONE);
                            binding.videoView.clearAnimation();
                            FirebaseDatabase.getInstance().getReference().child("likes").child(post.getVid())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .removeValue();
                        }
                        return super.onDoubleTap(e);
                    }
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent event) {
                        try {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                binding.imgPlayback.setVisibility(View.VISIBLE);
                            } else {
                                mediaPlayer.start();
                                binding.imgPlayback.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                });
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }



        private void getlikedStatus(String vid) {
            final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference likeref=FirebaseDatabase.getInstance().getReference("likes").child(vid);
            likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(firebaseUser.getUid()).exists()) {

                        binding.reelsLikeBtn.setImageResource(R.drawable.ic_heart);
                        binding.reelsLikeBtn.setTag("liked");
                    }
                    else{

                        binding.reelsLikeBtn.setImageResource(R.drawable.ic_unlike);
                        binding.reelsLikeBtn.setTag("not_liked");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private void nrlikes(String vid) {
            DatabaseReference likesref=FirebaseDatabase.getInstance().getReference().child("likes")
                    .child(vid);
            likesref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int likescount=(int)snapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
