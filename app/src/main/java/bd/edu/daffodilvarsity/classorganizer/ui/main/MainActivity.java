package bd.edu.daffodilvarsity.classorganizer.ui.main;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.AddActivity;
import bd.edu.daffodilvarsity.classorganizer.ui.search.SearchRefinedActivity;
import bd.edu.daffodilvarsity.classorganizer.service.NotificationRestartJobIntentService;
import bd.edu.daffodilvarsity.classorganizer.ui.base.BaseDrawerActivity;
import bd.edu.daffodilvarsity.classorganizer.ui.settings.SettingsActivity;
import bd.edu.daffodilvarsity.classorganizer.ui.setup.SetupActivity;
import bd.edu.daffodilvarsity.classorganizer.utils.CourseUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import bd.edu.daffodilvarsity.classorganizer.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseDrawerActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_INTRO = 6060;
    private static final int REQUEST_CODE_REFRESHABLE_ACTIVITY = 4208;

    private static final String CURRENT_PROMOTION = "Facebook_Page";

    private boolean alarmRecreated = false;
    private boolean isActivityRunning = false;
    private boolean updateDialogueBlocked = false;
    private DayFragmentPagerAdapter adapter;

    private MainViewModel mViewModel;
//    private AdView adView;

    @BindView(R.id.main_splash)
    ConstraintLayout mMainSplash;
    @BindView(R.id.main_holder)
    CoordinatorLayout mMainHolder;
    @BindView(R.id.main_appbar)
    AppBarLayout mAppbar;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));//status bar or the time bar at the top
        }
        if (PreferenceGetter.isFirstTimeLaunch()) {
            startActivityForResult(new Intent(this, SetupActivity.class), REQUEST_CODE_INTRO);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        //Setting drawer up
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadData();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasNotification = preferences.getBoolean("notification_preference", true);
        if (hasNotification) {
            if (!alarmRecreated) {
                NotificationRestartJobIntentService.enqueueWork(this, new Intent(this, NotificationRestartJobIntentService.class));
            }
        }



        //And load adz
//        if (adView == null) {
//            adView = (AdView) findViewById(R.id.adView);
//        }
//        adView.loadAd(new AdRequest.Builder().build());
        showOneTimePromotionalDialog();


    }

    private void revealMainView() {
        Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mMainHolder.setVisibility(View.VISIBLE);
                mMainSplash.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMainSplash.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        ViewUtils.circularRevealView(mMainHolder, mMainSplash, animatorListener, true);
    }

    @Override
    public void onBackPressed() {
        if (isActivityRunning) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
                finishAffinity();
            }
        } else {
            super.onBackPressed();
            finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_button) {
            Intent intent = new Intent(this, AddActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.search_button_main) {
            Intent intent = new Intent(this, SearchRefinedActivity.class);
            startActivityForResult(intent, REQUEST_CODE_REFRESHABLE_ACTIVITY);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_REFRESHABLE_ACTIVITY);
        } else if (id == R.id.nav_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text_extra));
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_title)));
        } else if (id == R.id.nav_send) {
            composeEmail();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        isActivityRunning = true;


        if (updateDialogueBlocked) {
            updateDialogueBlocked = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityRunning = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                // Finished the intro
                PreferenceGetter.setFirstTimeLaunch(false);
                mMainHolder.post(this::revealMainView);
                mViewModel.loadRoutine();
            } else {
                // Cancelled the intro. You can then e.g. finish this activity too.
                finish();
            }
        } if (requestCode == REQUEST_CODE_REFRESHABLE_ACTIVITY) {
            mViewModel.loadRoutine();
        }
    }

    public void loadData() {
        //Load Data
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        adapter = new DayFragmentPagerAdapter(this, getSupportFragmentManager(), new HashMap<>());
        mViewPager.setAdapter(adapter);
        mViewModel.getRoutineListListener().observe(this, listResource -> {
            if (listResource != null) {
                switch (listResource.getStatus()) {
                    case ERROR:
                        hideLoading();
                        break;
                    case LOADING:
                        break;
                    case SUCCESSFUL:
                        //Setting current date and tab
                        hideLoading();
                        adapter.updateData(listResource.getData());
                        Calendar calendar = Calendar.getInstance();
                        Date date = calendar.getTime();
                        String currentDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
                        for (int i = 0; i < adapter.getCount(); i++) {
                            if (adapter.getPageTitle(i).toString().equalsIgnoreCase(currentDay)) {
                                mViewPager.setCurrentItem(i);
                            }
                        }
                        // Find the tab layout that shows the tabs
                        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                        // Connect the tab layout with the view pager. This will
                        //   1. Update the tab layout when the view pager is swiped
                        //   2. Update the view pager when a tab is selected
                        //   3. Set the tab layout's tab names with the view pager's adapter's titles
                        //      by calling onPageTitle()
                        tabLayout.setupWithViewPager(mViewPager);

                }
            }
        });
    }



    //Checking if activity is in state loss
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        isActivityRunning = false;
    }


    //This function provides the skeleton of the suggestion email
    public void composeEmail() {
        String message = "Your suggestions: ";
        String subject = "Suggestions for DIU Class Organizer";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.auth_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /*Method to display snackbar properly*/
    public void showSnackBar(Activity activity, String message) {
        if (isActivityRunning) {
            View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    public boolean isActivityRunning() {
        return isActivityRunning;
    }

    public void showUpgradeDialogue() {
        PrefManager prefManager = new PrefManager(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Semester!");
        builder.setMessage("The routine was updated as per " + CourseUtils.getInstance(this).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()) + " semester.\n" +
                "Note: Your level and term will automatically get updated based on current selection and your modifications will be reset.");
        builder.setPositiveButton("OK", (dialog, which) -> {

            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void showOneTimePromotionalDialog() {
//        if (CURRENT_PROMOTION.equalsIgnoreCase(prefManager.getExpiredPromotion())) {
//            return;
//        }
//        MaterialDialog dialog = new MaterialDialog.Builder(this)
//                .title("Connect with us on Facebook!")
//                .content("Did you know Class Organizer is now on Facebook?\n " +
//                        "Like Class Organizer's Facebook page to get all the latest news and updates.")
//                .positiveText("Visit Facebook")
//                .negativeText("Cancel")
//                .onPositive((dialog1, which) -> {
//                    startActivity(getOpenFacebookIntent(this));
//                    prefManager.setExpiredPromotion(CURRENT_PROMOTION);
//                    dialog1.dismiss();
//                })
//                .onNegative((dialog12, which) -> dialog12.dismiss())
//                .checkBoxPrompt("Don't remind me again", false, (buttonView, isChecked) -> {
//                    if (isChecked) {
//                        prefManager.setExpiredPromotion(CURRENT_PROMOTION);
//                    }
//                })
//                .build();
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            if (!dialog.isShowing() && isActivityRunning()) {
//                dialog.show();
//            }
//        }, 1500);
//

    }

    private void hideLoading() {
        mMainHolder.post(this::revealMainView);
        mAppbar.setVisibility(View.VISIBLE);

    }




}
