package com.rivetlogic.mobilepeopledirectory.fragment;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.rivetlogic.liferayrivet.util.SettingsUtil;
import com.rivetlogic.mobilepeopledirectory.R;
import com.rivetlogic.mobilepeopledirectory.data.DataAccess;
import com.rivetlogic.mobilepeopledirectory.data.IDataAccess;
import com.rivetlogic.mobilepeopledirectory.model.User;
import com.rivetlogic.mobilepeopledirectory.utilities.Utilities;
import com.rivetlogic.mobilepeopledirectory.view.CircularImageView;
import com.rivetlogic.mobilepeopledirectory.view.CustomScrollView;
import com.rivetlogic.mobilepeopledirectory.view.DetailRow;
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
    private CircularImageView circleImage;
    private ImageView frame;
    private int iconColor;

    private FrameLayout toolbarContainer;
    private TextView toolbarTitle;
    private Drawable toolbarBackground;
    private View headerContainer;
    private int mLastDampedScroll;
    private FloatingActionButton fab;
    private boolean fabVisible;

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
        View v = inflater.inflate(R.layout.fragment_directory_detail, null);

        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbarContainer = (FrameLayout) v.findViewById(R.id.toolbar_container);
        toolbarTitle = (TextView) v.findViewById(R.id.title);
        toolbarTitle.setText(user.fullName);
        toolbarBackground = toolbarContainer.getBackground();
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

        headerContainer = v.findViewById(R.id.header_container);
        final CustomScrollView scrollView = (CustomScrollView) v.findViewById(R.id.observable_scrollview);

        fabVisible = true;
        circleImage = (CircularImageView) v.findViewById(R.id.fragment_directory_detail_image);
        frame = (ImageView) v.findViewById(R.id.fragment_directory_detail_image_background);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        frame.setColorFilter(cf);

        Picasso.with(getActivity()).load(SettingsUtil.getServer() + user.portraitUrl)
                .placeholder(R.drawable.ic_list_image_default)
                .error(R.drawable.ic_list_image_default)
                .resizeDimen(R.dimen.detail_image_size, R.dimen.detail_image_size)
                .into(target);

        LinearLayout contactContainer = (LinearLayout) v.findViewById(R.id.contact_container);
        updateData(contactContainer);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setImageResource(user.favorite ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_outline_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.favorite = !user.favorite;
                da.updateUser(user);
                fab.setImageResource(user.favorite ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_outline_white_24dp);
                Utilities.showTost(getActivity(), user.favorite ? R.string.user_added_favorites : R.string.user_removed_favorites);
            }
        });

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                int position = headerContainer.getMinimumHeight() - toolbarContainer.getMinimumHeight();
                scrollView.scrollTo(0, position);
                updateToolbarHeight(position);
                hideFAB();
                scrollView.setOnScrollChangedListener(scrollChangedListener);
            }
        });

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ValueAnimator realSmoothScrollAnimation = ValueAnimator.ofInt(scrollView.getScrollY(), 0);
                realSmoothScrollAnimation.setDuration(500);
                realSmoothScrollAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int scrollTo = (Integer) animation.getAnimatedValue();
                        scrollView.scrollTo(0, scrollTo);
                    }
                });
                realSmoothScrollAnimation.start();
            }
        }, 250);

        return v;
    }

    CustomScrollView.OnScrollChangedListener scrollChangedListener = new CustomScrollView.OnScrollChangedListener() {
        @Override
        public void onScrollChanged(int mScrollX, int mScrollY) {
            onScroll(mScrollY);
        }
    };

    private void updateData(LinearLayout contactContainer) {
        int color = getResources().getColor(R.color.primary);
        if (!TextUtils.isEmpty(user.userPhone)) {
            DetailRow newRow = new DetailRow(getActivity());
            newRow.setIconColor(color);
            newRow.setIcon(R.drawable.ic_call_grey600_24dp);
            newRow.setTitle(R.string.phone);
            newRow.setData(user.userPhone);
            newRow.setBackgroundRes(contactContainer.getChildCount() % 2 == 0 ? R.color.black_05 : R.color.black_10);
            contactContainer.addView(newRow);
            //  newRow.setOnClickListener(buttonPhoneListener);
        }

        if (!TextUtils.isEmpty(user.userPhone)) {
            DetailRow newRow = new DetailRow(getActivity());
            newRow.setIcon(R.drawable.ic_message_grey600_24dp);
            newRow.setTitle(R.string.sms);
            newRow.setData(user.userPhone);
            newRow.setIconColor(color);
            newRow.setBackgroundRes(contactContainer.getChildCount() % 2 == 0 ? R.color.black_05 : R.color.black_10);
            contactContainer.addView(newRow);
            //   newRow.setOnClickListener(buttonMessageListener);
        }

        if (!TextUtils.isEmpty(user.emailAddress)) {
            DetailRow newRow = new DetailRow(getActivity());
            newRow.setIcon(R.drawable.ic_email_grey600_24dp);
            newRow.setTitle(R.string.email);
            newRow.setData(user.emailAddress);
            newRow.setIconColor(color);
            newRow.setBackgroundRes(contactContainer.getChildCount() % 2 == 0 ? R.color.black_05 : R.color.black_10);
            contactContainer.addView(newRow);
            //  newRow.setOnClickListener(buttonEmailListener);
        }

        if (!TextUtils.isEmpty(user.skypeName)) {
            DetailRow newRow = new DetailRow(getActivity());
            newRow.setIcon(R.drawable.ic_skype_24dp);
            newRow.setTitle(R.string.skype);
            newRow.setData(user.skypeName);
            newRow.setBackgroundRes(contactContainer.getChildCount() % 2 == 0 ? R.color.black_05 : R.color.black_10);
            contactContainer.addView(newRow);
            //   newRow.setOnClickListener(buttonSkypeListener);
        }

        if (!TextUtils.isEmpty(user.city)) {
            DetailRow newRow = new DetailRow(getActivity());
            newRow.setIcon(R.drawable.ic_place_grey600_24dp);
            newRow.setTitle(R.string.location);
            newRow.setData(user.city);
            newRow.setIconColor(color);
            newRow.setBackgroundRes(contactContainer.getChildCount() % 2 == 0 ? R.color.black_05 : R.color.black_10);
            contactContainer.addView(newRow);
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;
        try {
            height -= toolbarContainer.getMinimumHeight();
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height -= getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {

        }
        contactContainer.setMinimumHeight(height);
    }

    View.OnClickListener buttonPhoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + user.userPhone));
            startActivity(intent);
        }
    };

    View.OnClickListener buttonMessageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.setType("vnd.android-dir/mms-sms");
            intent.setData(Uri.parse("sms:" + user.userPhone));
            startActivity(intent);
        }
    };

    View.OnClickListener buttonEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", user.emailAddress, null));
            startActivity(Intent.createChooser(emailIntent, getString(R.string.email)));
        }
    };

    View.OnClickListener buttonSkypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri skypeUri = Uri.parse("skype:" + user.skypeName + "?call&video=true");
            initiateSkypeUri(getActivity(), skypeUri);
        }
    };

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            circleImage.setImageBitmap(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Bitmap bm = blurRenderScript(bitmap);
                frame.setImageBitmap(bm);
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
        script.setRadius(15);
        script.setInput(inAlloc);
        script.forEach(outAlloc);
        outAlloc.copyTo(output);
        rs.destroy();
        return output;
    }

    private boolean isTelephonyEnabled() {
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null && tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    public void initiateSkypeUri(Context myContext, Uri mySkypeUri) {
        if (!isSkypeClientInstalled(myContext)) {
            goToMarket();
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
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }

    public void goToMarket() {
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

    public void onScroll(int scrollPosition) {
        int headerHeight = headerContainer.getHeight() - toolbarContainer.getMinimumHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0)
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;

        updateToolbarHeight(scrollPosition);
        updateToolbarTransparency(ratio);
        updateFABVisibility(ratio);

        updateCircleImageTransparency(ratio);
        updateParallaxEffect(scrollPosition);
    }

    private void updateToolbarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        toolbarBackground.setAlpha(newAlpha);
        toolbarContainer.setBackground(toolbarBackground);
    }

    private void updateCircleImageTransparency(float scrollRatio) {
        circleImage.setAlpha(1f - scrollRatio * 1.5f);
    }

    private void updateToolbarHeight(float scrollPosition) {
        int newHeight = (int) (headerContainer.getHeight() - scrollPosition);
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolbarContainer.getLayoutParams();
        if (newHeight > toolbarContainer.getMinimumHeight()) {
            lp.height = newHeight;
        } else if (newHeight > 0) {
            lp.height = toolbarContainer.getMinimumHeight();
        }
        toolbarContainer.setLayoutParams(lp);
    }

    private void updateFABVisibility(float scrollRatio) {
        if (scrollRatio > .5) {
            hideFAB();
        } else {
            showFAB();
        }
    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        headerContainer.offsetTopAndBottom(-offset);
        mLastDampedScroll = dampedScroll;
    }

    private void showFAB() {
        if (!fabVisible) {
            fabVisible = !fabVisible;
            ViewPropertyAnimator.animate(fab).cancel();
            ViewPropertyAnimator.animate(fab).scaleX(1).scaleY(1).setDuration(200).start();
            ViewPropertyAnimator.animate(toolbarTitle).cancel();
            ViewPropertyAnimator.animate(toolbarTitle).scaleX(1.4f).scaleY(1.4f).setDuration(400).start();
        }
    }

    private void hideFAB() {
        if (fabVisible) {
            fabVisible = !fabVisible;
            ViewPropertyAnimator.animate(fab).cancel();
            ViewPropertyAnimator.animate(fab).scaleX(0).scaleY(0).setDuration(200).start();
            ViewPropertyAnimator.animate(toolbarTitle).cancel();
            ViewPropertyAnimator.animate(toolbarTitle).scaleX(1f).scaleY(1f).setDuration(400).start();

        }
    }

}