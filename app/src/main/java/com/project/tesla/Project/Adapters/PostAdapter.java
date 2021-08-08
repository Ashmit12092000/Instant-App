package com.project.tesla.Project.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.project.tesla.Project.Activity.CommentsActivity;
import com.project.tesla.Project.Activity.EditPost;
import com.project.tesla.Project.Activity.MainActivity;
import com.project.tesla.Project.Activity.ShareActivity;
import com.project.tesla.Project.Activity.UserProfileActivity;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;
import com.project.tesla.databinding.PostlayoutBinding;

import de.hdodenhof.circleimageview.CircleImageView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{
    List<Post> posts;
    Context context;
    DatabaseReference likeRef;
    SimpleDateFormat dateFormat;
    String convTime;
    private Long tim;
    String dp_image_uri;

    public PostAdapter(Context context,List<Post> posts) {
        this.posts = posts;
        this.context = context;
        this.setHasStableIds(true);
    }

    public PostAdapter() {
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.postlayout,parent,false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post=posts.get(position);
        holder.setIsRecyclable(false);
        likeRef=FirebaseDatabase.getInstance().getReference("likes");
        holder.binding.postcaption.setText(post.getCaption_txt());

        holder.binding.userName.setText(post.getUsername());
        holder.user_dp(holder.binding.profileImg,post.getUid());
        holder.binding.postmenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.postmenubtn(post);
            }
        });
        holder.commentsbtnclick(post.getPost_id(),post.getUid(),post.getImage_uri(),post.getCaption_txt(),post.getUsername());
        holder.timeofpost(post.getPost_id());
        holder.getlikedStatus(post.getPost_id());
        holder.nrlikes(post.getPost_id());
        holder.doubleClickLike(post.getPost_id());
        holder.clicktoprofilePage(post.getPost_id(),post.getUid());
        Glide.with(context).load(post.getImage_uri()).placeholder(R.drawable.photo).into(holder.binding.postImg);
        holder.binding.likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.binding.likebutton.getTag().equals("not_liked")){
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

        FirebaseDatabase.getInstance().getReference().child("Post_comments").child(post.getPost_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()!=0){

                            holder.getLastComments(holder.binding.commentedUsername,holder.binding.commentedMsg,post.getPost_id());
                            holder.binding.commentsLayout.setVisibility(View.VISIBLE);
                        }
                        else{
                            holder.binding.commentsLayout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        holder.itemView.setTag(posts.get(position));


    }





    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;

    }

    public  class  PostViewHolder extends  RecyclerView.ViewHolder{
        DatabaseReference likeref;


        PostlayoutBinding binding;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=PostlayoutBinding.bind(itemView);
            binding.animationView.setVisibility(View.GONE);
        }
        public void getlikedStatus(String postkey) {
            final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            likeref=FirebaseDatabase.getInstance().getReference("likes").child(postkey);
            likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(firebaseUser.getUid()).exists()) {

                        binding.likebutton.setImageResource(R.drawable.ic_heart);
                        binding.likebutton.setTag("liked");
                    }
                    else{

                        binding.likebutton.setImageResource(R.drawable.ic_unlike);
                        binding.likebutton.setTag("not_liked");
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private void timeofpost(String post_id) {
            FirebaseDatabase.getInstance().getReference()
                    .child("Posts")
                    .child(post_id)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                tim = snapshot.child("timestamp").getValue(long.class);
                                dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                String time=dateFormat.format(new Date(tim));
                                String timediff=convTimetoText(time);
                                binding.Timeofpost.setText(timediff);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
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

        public void user_dp(CircleImageView profileImg, String uid) {
            FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                dp_image_uri=snapshot.child("profile_image").getValue(String.class);
                                Glide.with(context).asBitmap().load(dp_image_uri).centerCrop().into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        profileImg.setImageBitmap(resource);
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        public void getLastComments(TextView commentedUsername, TextView commentedMsg, String post_id) {
                DatabaseReference commentsRef=FirebaseDatabase.getInstance().getReference().child("Post_comments").child(post_id);
                commentsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String lastcommentMsg=snapshot.child("lastMessage").getValue(String.class);
                            String senderID=snapshot.child("senderID").getValue(String.class);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("users")
                                    .child(senderID)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String username=snapshot.child("name").getValue(String.class);
                                            commentedUsername.setText("@"+username);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            commentedMsg.setText(lastcommentMsg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        }

        public void doubleClickLike(String post_id) {
            binding.postImg.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        //Toast.makeText(context, "onDoubleTap", Toast.LENGTH_SHORT).show();
                        //selectImage(nView);
                        binding.animationView.setVisibility(View.GONE);
                        if(binding.likebutton.getTag().equals("not_liked")){
                            binding.animationView.setVisibility(View.VISIBLE);
                            binding.animationView.playAnimation();
                            FirebaseDatabase.getInstance().getReference().child("likes").child(post_id)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(true);
                        }
                        else{
                            binding.animationView.setVisibility(View.GONE);
                            FirebaseDatabase.getInstance().getReference().child("likes").child(post_id)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .removeValue();
                        }
                        return super.onDoubleTap(e);
                    }
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent event) {
                        // Log.d("onSingleTapConfirmed", "onSingleTap");
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

        public void nrlikes(String post_id) {
            DatabaseReference likesref=FirebaseDatabase.getInstance().getReference().child("likes")
                    .child(post_id);
            likesref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int likescount=(int)snapshot.getChildrenCount();
                    binding.postLikesCount.setText(likescount+" Likes");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        public void commentsbtnclick(String post_id, String uid, String image_uri, String caption_txt, String username) {
            binding.postCommentsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, CommentsActivity.class);

                    intent.putExtra("postID",post_id);
                    intent.putExtra("uid",uid);
                    intent.putExtra("post_img",image_uri);
                    intent.putExtra("time",dateFormat.format(new Date(tim)));
                    intent.putExtra("username",username);
                    intent.putExtra("userDP",dp_image_uri);
                    intent.putExtra("caption",caption_txt);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    ((MainActivity)context).finish();
                }
            });
        }

        public void clicktoprofilePage(String post_id, String uid) {
            binding.userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, UserProfileActivity.class);
                    intent.putExtra("uid",uid);
                    context.startActivity(intent);


                }
            });
        }

        public void postmenubtn(Post post) {
            RoundedBottomSheetDialog bottomSheetDialog=new RoundedBottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

            LinearLayout edit = bottomSheetDialog.findViewById(R.id.Edit);
            LinearLayout delete = bottomSheetDialog.findViewById(R.id.deletelinear);
            LinearLayout share = bottomSheetDialog.findViewById(R.id.share);
            LinearLayout report = bottomSheetDialog.findViewById(R.id.reportLayout);

            bottomSheetDialog.show();
            if(FirebaseAuth.getInstance().getUid().equals(post.getUid())){
                edit.setVisibility(View.VISIBLE);
            }
            else{
                edit.setVisibility(View.GONE);
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure want to delete this Post?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("Posts")
                                                .child(post.getPost_id())
                                                .removeValue();
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("PostCount")
                                                .child(post.getUid())
                                                .child(post.getPost_id())
                                                .removeValue();
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("Post_comments")
                                                .child(post.getPost_id())
                                                .removeValue();
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("likes")
                                                .child(post.getPost_id())
                                                .removeValue();
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("No",null);
                    AlertDialog alert = builder.create();
                    alert.setTitle("Delete Post");
                    alert.show();
                    bottomSheetDialog.dismiss();
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, EditPost.class);
                    intent.putExtra("postID",post.getPost_id());
                    intent.putExtra("postImg",post.getImage_uri());
                    intent.putExtra("captionText",post.getCaption_txt());
                    intent.putExtra("uid",post.getUid());
                    context.startActivity(intent);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ShareActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("image_uri",post.getImage_uri());
                    intent.putExtra("postID",post.getPost_id());
                    intent.putExtra("postType",post.getPost_type());
                    context.startActivity(intent);
                }
            });
        }
    }
}

