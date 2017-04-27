package app.uocssafe.com.uocs_safe.Personal;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.uocssafe.com.uocs_safe.BaseActivity;
import app.uocssafe.com.uocs_safe.Helper.AppConfig;
import app.uocssafe.com.uocs_safe.Helper.Session;
import app.uocssafe.com.uocs_safe.R;
import app.uocssafe.com.uocs_safe.login_register.Login;
import de.hdodenhof.circleimageview.CircleImageView;

import static app.uocssafe.com.uocs_safe.BaseActivity.decodeBase64;

public class PersonalActivity extends AppCompatActivity implements MyReportAdapter.BottomSheetCallback{

    ImageView imageView,tabBg;
    CircleImageView profile_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    AppConfig config;
    BottomSheetBehavior bottomSheetBehavior;
    LinearLayout bottomsheet;
    TextView tvStatus, tvReason, tvActionTaken;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        config = new AppConfig();
        config.changeStatusBarColor(R.color.colorPrimary, PersonalActivity.this);
        session = new Session(PersonalActivity.this);

        imageView= (ImageView) findViewById(R.id.backdrop);
        tabBg= (ImageView) findViewById(R.id.tabBg);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(session.getUsername());

        if(session.getUserAvatar() == null)
            profile_image.setImageResource(R.drawable.head_1);
        else
            profile_image.setImageBitmap(decodeBase64(session.getUserAvatar()));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvReason = (TextView) findViewById(R.id.tvReason);
        tvActionTaken = (TextView) findViewById(R.id.tvActionTaken);
        bottomsheet = (LinearLayout) findViewById(R.id.bottom_sheet_status);

//        openBottomSheet = (OpenBottomSheet) PersonalActivity.this;

        Picasso.with(PersonalActivity.this).load(R.drawable.background).into(imageView);
//        Picasso.with(PersonalActivity.this).load(R.drawable.background).centerCrop().into(tabBg);
        tabBg.setImageResource(R.color.colorPrimary);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        tabLayout.setupWithViewPager(viewPager);

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet_status));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                bottomSheet.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(event.getAction() == MotionEvent.ACTION_DOWN)
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                        return true;
                    }
                });

            }
        });
    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyReportFragment(), "My Report");
        adapter.addFragment(new MyActivityFragment(), "My Activity");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onUserClick(String status, String reason, String action_taken) {
        Log.d("status138", status + " " + reason + " " + action_taken);

        if(reason.equals("null"))
            reason = "-";
        if(action_taken.equals("null"))
            action_taken = "-";

        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
            String statusformat = String.format("Status : %s", status);
            String reasonformat = String.format("Reason : %s", reason);
            String actionformat = String.format("Action Taken: %s", action_taken);
            tvStatus.setText(statusformat);
            tvReason.setText(reasonformat);
            tvActionTaken.setText(actionformat);
            bottomSheetBehavior.setPeekHeight(200);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
