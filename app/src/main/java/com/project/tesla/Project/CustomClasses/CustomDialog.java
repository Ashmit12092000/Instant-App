package com.project.tesla.Project.CustomClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.project.tesla.R;

public class CustomDialog extends Dialog implements View.OnClickListener {
    Context context;
    ImageView post_img,profile_img,like_img;
    RelativeLayout relativeLayout;
    TextView username,timeofpost,likescount;

    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context=context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        post_img=findViewById(R.id.custom_diag_post_img);
        profile_img=findViewById(R.id.custom_diag_profile_img);
        like_img=findViewById(R.id.custom_diag_likebutton);

        username=findViewById(R.id.custom_diag_userName);
        timeofpost=findViewById(R.id.custom_diag_Timeofpost);
        likescount=findViewById(R.id.custom_diag_likes_count);
        relativeLayout=findViewById(R.id.custom_diag_layout);
        profile_img.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
        profile_img.setOnClickListener(this);
        like_img.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {

    }
}
