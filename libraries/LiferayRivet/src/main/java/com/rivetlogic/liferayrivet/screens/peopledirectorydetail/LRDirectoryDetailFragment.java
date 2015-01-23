package com.rivetlogic.liferayrivet.screens.peopledirectorydetail;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rivetlogic.liferayrivet.R;
import com.rivetlogic.liferayrivet.component.CircularImageView;
import com.rivetlogic.liferayrivet.screens.peopledirectorylist.User;
import com.rivetlogic.liferayrivet.component.RoundedTransformation;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by lorenz on 1/16/15.
 */
public class LRDirectoryDetailFragment extends Fragment {

    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRDirectoryDetailFragment_styleResId";
    private static final String KEY_USER = "com.rivetlogic.liferay.screens.login.LRDirectoryDetailFragment_user";

    private int styleResId;
    private User user;
    private CircularImageView imageView;
    private ImageView frame;

    public static LRDirectoryDetailFragment newInstance(int styleResId, User user) {
        LRDirectoryDetailFragment fragment = new LRDirectoryDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_STYLE_ID, styleResId);
        args.putSerializable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public LRDirectoryDetailFragment() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_USER)) {
            user = (User) args.getSerializable(KEY_USER);
        }

        @SuppressWarnings("ResourceType")
        TypedArray a = getActivity().getApplicationContext().obtainStyledAttributes(R.style.lrThemeLoginViewDefault, R.styleable.LRLoginView);
        setStyledAttributes(a);
        if (styleResId > 0) {
            a = getActivity().getApplicationContext().obtainStyledAttributes(styleResId, R.styleable.LRLoginView);
            setStyledAttributes(a);
        }
        a.recycle();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lr_fragment_directory_detail, null);

        imageView = (CircularImageView)v.findViewById(R.id.lr_fragment_directory_detail_image);
        frame = (ImageView) v.findViewById(R.id.lr_fragment_directory_detail_image_background);

        Picasso.with(getActivity()).load(SettingsUtil.getServer() + user.portraitUrl)
                .placeholder(R.drawable.ic_list_image_default)
                .error(R.drawable.ic_list_image_default)
                .resizeDimen(R.dimen.detail_image_size, R.dimen.detail_image_size)
                .into(target);

        TextView name = (TextView) v.findViewById(R.id.lr_fragment_directory_detail_name);
        name.setText(user.fullName);

        TextView email = (TextView) v.findViewById(R.id.lr_fragment_directory_detail_email);
        email.setText(user.emailAddress);

     //   TextView title = (TextView) v.findViewById(R.id.lr_fragment_directory_detail_title);
      //  title.setText(user.jobTitle);

        TextView screenName = (TextView) v.findViewById(R.id.lr_fragment_directory_detail_screen_name);
        screenName.setText(user.screenName);

     //   TextView city = (TextView) v.findViewById(R.id.lr_fragment_directory_detail_city);
     //   city.setText(user.city);

        TextView phone = (TextView) v.findViewById(R.id.lr_fragment_directory_detail_phone);
        phone.setText(user.userPhone);

        TextView skype = (TextView) v.findViewById(R.id.lr_fragment_directory_detail_skype);
        skype.setText(user.skypeName);


        RelativeLayout buttonSkype = (RelativeLayout) v.findViewById(R.id.lr_fragment_directory_detail_skype_button);
        buttonSkype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri skypeUri = Uri.parse("skype:"+user.skypeName+"?call&video=true");
                initiateSkypeUri(getActivity(), skypeUri);
            }
        });

        RelativeLayout buttonEmail = (RelativeLayout) v.findViewById(R.id.lr_fragment_directory_detail_email_button);
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", user.emailAddress, null));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email)));
            }
        });

        RelativeLayout buttonPhone = (RelativeLayout) v.findViewById(R.id.lr_fragment_directory_detail_phone_button);
    //    if(!isTelephonyEnabled())
    //        buttonPhone.setVisibility(View.GONE);
        buttonPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ user.userPhone));
                startActivity(intent);
            }
        });


        ImageView emailIcon = (ImageView) v.findViewById(R.id.lr_fragment_directory_detail_email_icon);
        emailIcon.setColorFilter(getResources().getColor(R.color.carrot), PorterDuff.Mode.SRC_ATOP);

        ImageView phoneIcon = (ImageView) v.findViewById(R.id.lr_fragment_directory_detail_phone_icon);
        phoneIcon.setColorFilter(getResources().getColor(R.color.carrot), PorterDuff.Mode.SRC_ATOP);

        return v;
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);
            Bitmap bm = blurRenderScript(bitmap);
            frame.setImageBitmap(bm);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blurRenderScript(Bitmap smallBitmap) {
        Bitmap output = Bitmap.createBitmap(smallBitmap.getWidth(), smallBitmap.getHeight(), smallBitmap.getConfig());
        RenderScript rs = RenderScript.create(getActivity());
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation inAlloc = Allocation.createFromBitmap(rs, smallBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_GRAPHICS_TEXTURE);
        Allocation outAlloc = Allocation.createFromBitmap(rs, output);
        script.setRadius(25);
        script.setInput(inAlloc);
        script.forEach(outAlloc);
        outAlloc.copyTo(output);
        rs.destroy();
        return output;
    }

    private boolean isTelephonyEnabled(){
        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY;
    }

    public void initiateSkypeUri(Context myContext, Uri mySkypeUri) {

        // Make sure the Skype for Android client is installed.
        if (!isSkypeClientInstalled(myContext)) {
            goToMarket(myContext);
            return;
        }

        Intent skype = new Intent("android.intent.action.VIEW");
        skype.setData(mySkypeUri);
       // startActivity(skype);


        // Create the Intent from our Skype URI.
      //  Uri skypeUri = Uri.parse(mySkypeUri);
     //   Intent myIntent = new Intent(Intent.ACTION_VIEW, skypeUri);

        // Restrict the Intent to being handled by the Skype for Android client only.
        skype.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
        skype.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Initiate the Intent. It should never fail because you've already established the
        // presence of its handler (although there is an extremely minute window where that
        // handler can go away).
        startActivity(skype);

        return;
    }

    public boolean isSkypeClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo("com.skype.raider", PackageManager.GET_ACTIVITIES);
        }
        catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }

    public void goToMarket(Context myContext) {
        Uri marketUri = Uri.parse("market://details?id=com.skype.raider");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myIntent);

        return;
    }

    private void setStyledAttributes(TypedArray a) {
        try {

        } finally {

        }
    }

}