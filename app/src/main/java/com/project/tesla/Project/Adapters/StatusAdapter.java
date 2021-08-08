package com.project.tesla.Project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.devlomi.circularstatusview.CircularStatusView;
import com.project.tesla.Project.Activity.MainActivity;
import com.project.tesla.Project.Activity.UserProfileActivity;
import com.project.tesla.Project.Model.Status;
import com.project.tesla.Project.Model.User;
import com.project.tesla.Project.Model.UserStatus;
import com.project.tesla.R;
import com.project.tesla.databinding.ItemstatusBinding;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.OnStoryChangedCallback;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
    Context context;
    ArrayList<UserStatus> userStatuses;

    public StatusAdapter(Context context, ArrayList<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @NotNull
    @Override
    public StatusAdapter.StatusViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.itemstatus,parent,false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull StatusAdapter.StatusViewHolder holder, int position) {
        try
        {
            UserStatus userStatus = userStatuses.get(position);
            Status status = userStatus.getStatuses().get(position);
            Long tim = status.getTimestamp();
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            try {
                Glide.with(context).load(userStatus.getProfileImage()).into(holder.binding.image);
            } catch (Exception e){
                e.printStackTrace();
            }
            holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());

            holder.binding.circularStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ArrayList<MyStory> myStories = new ArrayList<>();
                        for (Status statu : userStatus.getStatuses()) {
                            myStories.add(new MyStory(statu.getImage_url()));
                        }


                        new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                                .setStoriesList(myStories) // Required
                                .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                .setTitleText(userStatus.getName())// Default is Hidden
                                .setSubtitleText(dateFormat.format(new Date(tim))) // Default is Hidden
                                .setTitleLogoUrl(userStatus.getProfileImage())// Default is Hidden
                                .setStoryClickListeners(new StoryClickListeners() {
                                    @Override
                                    public void onDescriptionClickListener(int position) {

                                    }

                                    @Override
                                    public void onTitleIconClickListener(int position) {
                                        Intent intent = new Intent(context, UserProfileActivity.class);
                                        intent.putExtra("uid", status.getSUid());
                                        context.startActivity(intent);
                                    }
                                })
                                .setOnStoryChangedCallback(new OnStoryChangedCallback() {
                                    @Override
                                    public void storyChanged(int position) {

                                    }
                                })
                                .build()
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            holder.binding.imageLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.binding.imageLayout.setVisibility(View.GONE);
                }
            }, (long) 8.64e+7);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public class StatusViewHolder extends  RecyclerView.ViewHolder {

        ItemstatusBinding binding;
        
        public StatusViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding=ItemstatusBinding.bind(itemView);
           
        }
    }
}
