package com.rivetlogic.mobilepeopledirectory.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.rivetlogic.mobilepeopledirectory.R;
import com.rivetlogic.mobilepeopledirectory.data.DataAccess;
import com.rivetlogic.mobilepeopledirectory.data.IDataAccess;
import com.rivetlogic.mobilepeopledirectory.utilities.Utilities;
import com.rivetlogic.mobilepeopledirectory.view.CircularImageView;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.model.User;
import com.rivetlogic.mobilepeopledirectory.view.HexagonImageView;
import com.rivetlogic.mobilepeopledirectory.view.StickyParallaxScrollView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by lorenz on 1/16/15.
 */
public class DirectoryDetailFragment extends Fragment {

    private static final String KEY_STYLE_ID = "com.rivetlogic.liferay.screens.login.LRDirectoryDetailFragment_styleResId";
    private static final String KEY_USER = "com.rivetlogic.liferay.screens.login.LRDirectoryDetailFragment_user";

    private DirectoryDetailFragmentCallback listener;
    private int styleResId;
    private User user;
    private IDataAccess da;
    private HexagonImageView imageView;
    private ImageView frame;
    private int iconColor;
    private Toolbar toolbar;
    private StickyParallaxScrollView stickyScrollView;
    private LinearLayout lnrMain,lnrParallaxContainer;
    private int lastMainFrameHeight = 0;
    private FrameLayout frmParallaxConainer;
    private View v;
    private FrameLayout frmFloatingButton;
    private TextView txtProfileJobTitle;
    private TextView txtProfileLocation;

    public interface DirectoryDetailFragmentCallback {
        void logout();
    }

    public static DirectoryDetailFragment newInstance(int styleResId, User user) {
        DirectoryDetailFragment fragment = new DirectoryDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_STYLE_ID, styleResId);
        args.putSerializable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    public DirectoryDetailFragment() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (DirectoryDetailFragmentCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(KEY_USER)) {
            user = (User) args.getSerializable(KEY_USER);
        }

        da = DataAccess.getInstance(getActivity());
        styleResId = R.style.LRThemeUserDetailDefault;
        setStyledAttributes();
        if (args != null && args.containsKey(KEY_STYLE_ID)) {
            styleResId = args.getInt(KEY_STYLE_ID);
            if (styleResId > 0)
                setStyledAttributes();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.fragment_directory_detail, null);
        lnrMain = (LinearLayout)v.findViewById(R.id.mainContainer);
        lnrParallaxContainer = (LinearLayout)v.findViewById(R.id.lnrParallaxContainer);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        TextView tv = (TextView)toolbar.findViewById(R.id.toolbar_title);
        tv.setText(null);
        toolbar.inflateMenu(R.menu.menu_detail);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_about:

                        break;
                    case R.id.action_logout:
                        listener.logout();
                        break;

                }
                return false;
            }
        });

        imageView = (HexagonImageView)v.findViewById(R.id.fragment_directory_detail_image);
        frame = (ImageView) v.findViewById(R.id.fragment_directory_detail_image_background);
        frame.setImageResource(R.drawable.trans_bg);
        Picasso.with(getActivity()).load(SettingsUtil.getServer() + user.portraitUrl)
                .placeholder(R.drawable.ic_list_image_default)
                .error(R.drawable.ic_list_image_default)
                .resizeDimen(R.dimen.detail_image_size, R.dimen.detail_image_size)
                .into(target);
        stickyScrollView = (StickyParallaxScrollView)v.findViewById(R.id.StickyScrollView);
        stickyScrollView.setImageView(frame);

        ViewTreeObserver observer = lnrMain.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                int tempHeight = lnrMain.getHeight();
                FrameLayout frameLayout = (FrameLayout)v.findViewById(R.id.frmImageContainer);
                if(lastMainFrameHeight != tempHeight) {
                    stickyScrollView.setInitParams(frameLayout.getLayoutParams().height,
                            lnrMain.getHeight(),frameLayout,lnrParallaxContainer);
                    lastMainFrameHeight = tempHeight;
                }

            }
        });


        frmFloatingButton = (FrameLayout)v.findViewById(R.id.frmFloatingButton);
        final FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.fab);
        button.setIcon(user.favorite ? R.drawable.ic_star_white_36dp : R.drawable.ic_star_outline_white_36dp);
        button.setStrokeVisible(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.favorite = !user.favorite;
                da.updateUser(user);
                button.setIcon(user.favorite ? R.drawable.ic_star_white_36dp : R.drawable.ic_star_outline_white_36dp);
                Utilities.showTost(getActivity(), user.favorite ? R.string.user_added_favorites : R.string.user_removed_favorites);
            }
        });

        TextView name = (TextView) v.findViewById(R.id.fragment_directory_detail_name);
        name.setText(user.fullName);

        TextView email = (TextView) v.findViewById(R.id.fragment_directory_detail_email);
        email.setText(user.emailAddress);

        TextView screenName = (TextView) v.findViewById(R.id.fragment_directory_detail_screen_name);
        screenName.setText(user.screenName);

        TextView phone = (TextView) v.findViewById(R.id.fragment_directory_detail_phone);
        phone.setText(user.userPhone);

        TextView skype = (TextView) v.findViewById(R.id.fragment_directory_detail_skype);
        skype.setText(user.skypeName);

        RelativeLayout buttonSkype = (RelativeLayout) v.findViewById(R.id.fragment_directory_detail_skype_button);
        buttonSkype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri skypeUri = Uri.parse("skype:"+user.skypeName+"?call&video=true");
                initiateSkypeUri(getActivity(), skypeUri);
            }
        });

        RelativeLayout buttonEmail = (RelativeLayout) v.findViewById(R.id.fragment_directory_detail_email_button);
        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", user.emailAddress, null));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.email)));
            }
        });

        RelativeLayout buttonPhone = (RelativeLayout) v.findViewById(R.id.fragment_directory_detail_phone_button);
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


        ImageView emailIcon = (ImageView) v.findViewById(R.id.fragment_directory_detail_email_icon);
        emailIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);

        ImageView phoneIcon = (ImageView) v.findViewById(R.id.fragment_directory_detail_phone_icon);
        phoneIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);

        TextView location = (TextView) v.findViewById(R.id.fragment_directory_detail_location);
        location.setText(user.city);

        TextView txtJobTitle = (TextView) v.findViewById(R.id.fragment_directory_detail_profile_job_title);
        txtJobTitle.setText(user.jobTitle);

        TextView txtLoction = (TextView) v.findViewById(R.id.fragment_directory_detail_profile_location);
        txtLoction.setText(user.city);

        getStarWithAnimation(frmFloatingButton);

        return v;
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Bitmap bm = blurRenderScript(bitmap);
                frame.setImageBitmap(bitmap);
                setLocked(frame);
            }
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
        if (!isSkypeClientInstalled(myContext)) {
            goToMarket(myContext);
            return;
        }
        Intent skype = new Intent("android.intent.action.VIEW");
        skype.setData(mySkypeUri);
        skype.setComponent(new ComponentName("com.skype.raider", "com.skype.raider.Main"));
        skype.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void setStyledAttributes() {
        TypedArray a = getActivity().getApplicationContext().obtainStyledAttributes(styleResId, R.styleable.LRUserDetailView);
        if (a != null) {
            try {
                iconColor = a.getColor(R.styleable.LRUserDetailView_lrScreenUserDetailIconColor, iconColor);

            } finally {
                a.recycle();
            }
        }

    }

    private void getStarWithAnimation( FrameLayout lnrGame13) {

        int duration = 100;
        long animDuration = 700;
        int currentDelay = duration + 50;

        for (int i = 0; i < lnrGame13.getChildCount(); i++) {
            final View childView = ((ViewGroup) lnrGame13).getChildAt(i);
            // childView.setVisibility(View.VISIBLE);
            childView.setVisibility(View.INVISIBLE);

            ObjectAnimator View1 = ObjectAnimator.ofPropertyValuesHolder(childView, PropertyValuesHolder.ofFloat("scaleX", .5f, 1f),
                    PropertyValuesHolder.ofFloat("scaleY", .5f, 1f));
            View1.setDuration(animDuration);
            View1.setStartDelay(currentDelay);
            View1.setInterpolator(new OvershootInterpolator(3));

            View1.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {
                    // TODO Auto-generated method stub
                    childView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub

                }
            });

            View1.start();
            currentDelay += 200;
        }
    }

    public static void  setLocked(ImageView v)
    {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        v.setColorFilter(cf);
//        v.setAlpha(150);   // 128 = 0.5
    }
}