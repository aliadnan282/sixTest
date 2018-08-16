package com.workout.sixpacksabs.view.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.ActivityDrawerBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.helper.ShadowTransformer;
import com.workout.sixpacksabs.in_app.util.SixPacksBilling;
import com.workout.sixpacksabs.interfaces.CloseAppListener;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.AnalyticsManager;
import com.workout.sixpacksabs.manager.CustomNotificationManager;
import com.workout.sixpacksabs.model.HeaderModel;
import com.workout.sixpacksabs.view.adapter.CardPagerAdapter;
import com.workout.sixpacksabs.view.fragment.AchievementsFragment;
import com.workout.sixpacksabs.view.fragment.CalendarFragment;
import com.workout.sixpacksabs.view.fragment.CategoryFragment;
import com.workout.sixpacksabs.view.fragment.RecipeFragment;
import com.workout.sixpacksabs.view.fragment.ShowExistDialog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

import static com.workout.sixpacksabs.helper.AppConstant.CLOCK_TYPE;
import static com.workout.sixpacksabs.helper.AppConstant.IS_ADS_DISABLED;
import static com.workout.sixpacksabs.helper.AppConstant.IS_FIRST_RUN;
import static com.workout.sixpacksabs.helper.AppConstant.NOTIFICATION_ENABLE;
import static com.workout.sixpacksabs.helper.AppConstant.PLAN_NUMBER;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_TIME;
import static com.workout.sixpacksabs.helper.AppConstant.TTS_UNMUTE;
import static com.workout.sixpacksabs.helper.AppConstant.USER_EEA;
import static com.workout.sixpacksabs.helper.AppConstant.mContext;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class DrawerActivity extends DaggerAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, CloseAppListener {
    private static final String TAG = DrawerActivity.class.getSimpleName();

    ActivityDrawerBinding activityDrawerBinding;
    TypedArray headerTitle;
    @Inject
    AppPreference appPreference;
    AppBarLayout.OnOffsetChangedListener appBarOffsetListener;
    TypedArray colors;
    boolean isDietPlan = false;
    @Inject
    SixPacksBilling sixPacksBilling;
    ConsentForm form;

    MenuItem menuItem;
    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private ShowExistDialog showExistDialog;
    private ConsentInformation consentInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDrawerBinding = DataBindingUtil.setContentView(this, R.layout.activity_drawer);
        setSupportActionBar(activityDrawerBinding.includeDrawer.toolbarHeader);
        activityDrawerBinding.includeDrawer.toolbarHeader.setTitle(getString(R.string.app_name));
        AdsManager.getInstance().showFacebookBannerAd(activityDrawerBinding.includeDrawer.adContainer);
        // In-App billing Helper class
        //sixPacksBilling = new SixPacksBilling(this);
        sixPacksBilling.onCreate();

        Intent intent = getIntent();
        if (intent.getExtras() != null)
            isDietPlan = intent.getIntExtra(PLAN_NUMBER, 1) == 0;

       // appPreference = AppPreference.getInstance(this);
        if (!appPreference.getBoolean(IS_FIRST_RUN)) {
            appPreference.setBoolean(TTS_UNMUTE, true);
            appPreference.setBoolean(NOTIFICATION_ENABLE, true);
            appPreference.setIntValue(SNOOZE_TIME, 300);  // Snooze time set by default 5 minutes
            Calendar calendar = Calendar.getInstance();
            CustomNotificationManager.getInstance(mContext).setNotification(calendar.get(HOUR_OF_DAY) + CLOCK_TYPE, calendar.get(MINUTE));
        }
        //AdsManager.getInstance().showFacebookInterstitialAd();

        if (!appPreference.getBoolean(getString(R.string.is_consent_first_time_requested)) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requestGoogleConsentForm();
        } else {
            AdsManager.getInstance().showFacebookInterstitialAd();
        }
        setHeaderViewPager();

        setContentViewPager();

        setScrimAndStatusBarScrimColor(0, colors);

        setToolbarBackgroundColor();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, activityDrawerBinding.drawerLayout, activityDrawerBinding.includeDrawer.toolbarHeader, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        activityDrawerBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        activityDrawerBinding.navView.setNavigationItemSelectedListener(this);

        // Initialize exit dialog to load ad in advance
        showExistDialog = new ShowExistDialog(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideMenuItemInNavMenuDrawer();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_remove_ad, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_remove_ad:
                sixPacksBilling.purchaseRemoveAds();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItem = menu.findItem(R.id.menu_remove_ad);

        if (appPreference.getBoolean(IS_ADS_DISABLED))
            menuItem.setVisible(false);
        else
            menuItem.setVisible(true);

        return true;
    }

    private void hideMenuItemInNavMenuDrawer() {

        if (appPreference.getBoolean(IS_ADS_DISABLED)) {
            activityDrawerBinding.navView.getMenu().setGroupVisible(R.id.grp_privacy, false);
        }
/*        MenuItem menuItem = activityDrawerBinding.navView.getMenu().findItem(R.id.menu_remove_ad);
        if (appPreference.getBoolean(IS_ADS_DISABLED)) {
            menuItem.setIcon(R.drawable.ic_premium);
            menuItem.setTitle(R.string.premium_title);
        }else{
            menuItem.setIcon(R.drawable.ic_remove_ads);
            menuItem.setTitle(R.string.remove_ads);
        }
        activityDrawerBinding.navView.invalidate();*/

    }

    private void requestGoogleConsentForm() {
        consentInformation = ConsentInformation.getInstance(this);
    /*    if (BuildConfig.DEBUG) {
            consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
            consentInformation.addTestDevice("BF1E10604107999DB67DA1C6570D3645");
        }*/
        consentInformation.requestConsentInfoUpdate(new String[]{getString(R.string.publisher_id)}, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d(TAG, consentStatus.name());
                // User's consent status successfully updated
                // check where user is located either not in EEA or in EEA
                // case 1 (not in EEA): if user is not located in EEA, no consent is required, you can request to Google Mobile Ads SDK
                // case 2 (in EEA): consent is required
                if (consentInformation.isRequestLocationInEeaOrUnknown()) {
                    // user is located in EEA, consent is required
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        appPreference.setBoolean(USER_EEA, true);
                        showGoogleConsentForm();
                    }
                } else {
                    appPreference.setBoolean(USER_EEA, false);
                }
                appPreference.setBoolean(getString(R.string.is_consent_first_time_requested), true);
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update
                Log.d(TAG, "onFailedToUpdateConsentInfo: " + errorDescription);
            }
        });
    }

    private void showGoogleConsentForm() {
        // consent not provided, collect consent using Google-rendered consent form
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getString(R.string.privacy_policy_url));
        } catch (MalformedURLException e) {
            Log.e(TAG, "onConsentInfoUpdated: " + e.getLocalizedMessage());
        }
        form = new ConsentForm.Builder(DrawerActivity.this, privacyUrl)
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d(TAG, "onConsentFormLoaded");
                        form.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d(TAG, "onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        Log.d(TAG, "onConsentFormClosed");
                        if (consentStatus == ConsentStatus.PERSONALIZED) {
                            appPreference.setBoolean(getString(R.string.npa), false);
                        } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                            appPreference.setBoolean(getString(R.string.npa), true);
                        } else if (userPrefersAdFree) {
                            sixPacksBilling.purchaseRemoveAds();
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                        Log.d(TAG, "onConsentFormError: " + errorDescription);
                    }
                })
                .build();
        form.load();
    }

    @Override
    public void onBackPressed() {
        if (activityDrawerBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityDrawerBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (activityDrawerBinding.includeDrawer.viewPager.getCurrentItem() == 0)
                showExistDialog.showDialog();
            else {
                activityDrawerBinding.includeDrawer.vpHeader.setCurrentItem(0);
                activityDrawerBinding.includeDrawer.viewPager.setCurrentItem(0);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            activityDrawerBinding.includeDrawer.vpHeader.setCurrentItem(0);
            activityDrawerBinding.includeDrawer.viewPager.setCurrentItem(0);

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(DrawerActivity.this, SettingActivity.class));
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        } else if (id == R.id.nav_share) {
            AnalyticsManager.getInstance().sendAnalytics("share_app", "share_app_visit");
            startActivity(AppUtils.getShareIntent());
        } else if (id == R.id.nav_rate_us) {
            AnalyticsManager.getInstance().sendAnalytics("rate_us", "rate_us_visit");
            startActivity(AppUtils.getLikeUsIntent());
        } else if (id == R.id.nav_more_app) {
            AnalyticsManager.getInstance().sendAnalytics("more_apps", "More_apps_visit");
            startActivity(AppUtils.getMoreAppIntent());

        } else if (id == R.id.in_app) {
            sixPacksBilling.purchaseRemoveAds();
        }

        activityDrawerBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setToolbarBackgroundColor() {
        appBarOffsetListener = new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(final AppBarLayout appBarLayout, int verticalOffset) {
                //Initialize the size of the scroll
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                //Check if the view is collapsed
                if (scrollRange + verticalOffset == 0) {
                    Log.d(TAG, "onOffsetChanged: color white");
                    activityDrawerBinding.includeDrawer.toolbarHeader.setTitle(headerTitle.getString(activityDrawerBinding.includeDrawer.viewPager.getCurrentItem()));
                    activityDrawerBinding.includeDrawer.toolbarHeader.setTitleTextColor(Color.WHITE);
                    activityDrawerBinding.includeDrawer.toolbarHeader.setNavigationIcon(R.drawable.ic_nav_white);
                    if (menuItem != null) {
                        menuItem.setIcon(R.drawable.ic_menu_remove_ad_white);
                    }

                } else {
                    activityDrawerBinding.includeDrawer.toolbarHeader.setNavigationIcon(R.drawable.ic_nav_black);
                    activityDrawerBinding.includeDrawer.toolbarHeader.setTitleTextColor(Color.BLACK);
                    activityDrawerBinding.includeDrawer.toolbarHeader.setTitle(getString(R.string.app_name));
                    if (menuItem != null)
                        menuItem.setIcon(R.drawable.ic_menu_remove_ad);

                }
            }
        };
        activityDrawerBinding.includeDrawer.appbarHeader.addOnOffsetChangedListener(appBarOffsetListener);

    }

    @Override
    protected void onDestroy() {
        sixPacksBilling.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sixPacksBilling.onActivityResult(requestCode, resultCode, data);
    }

    private void setHeaderViewPager() {

        TypedArray imgs = mContext.getResources().obtainTypedArray(R.array.categories_image);
        colors = mContext.getResources().obtainTypedArray(R.array.categories_color);
        headerTitle = mContext.getResources().obtainTypedArray(R.array.categories_title);
        mCardAdapter = new CardPagerAdapter();

        for (int i = 0; i < imgs.length(); i++) {
            mCardAdapter.addCardItem(new HeaderModel(headerTitle.getString(i), imgs.getResourceId(i, 0), colors.getColor(i, 0)));
        }
        // header pager model set
        activityDrawerBinding.includeDrawer.vpHeader.setAdapter(mCardAdapter);

        // Header pager animation
        mCardShadowTransformer = new ShadowTransformer(activityDrawerBinding.includeDrawer.vpHeader, mCardAdapter);
        mCardShadowTransformer.enableScaling(true);
        //  activityDrawerBinding.includeDrawer.vpHeader.setPageTransformer(false, mCardShadowTransformer);
        activityDrawerBinding.includeDrawer.vpHeader.setOffscreenPageLimit(4);
        activityDrawerBinding.includeDrawer.vpHeader.setPageMargin(0);


        activityDrawerBinding.includeDrawer.vpHeader.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setScrimAndStatusBarScrimColor(position, colors);

                Log.d(TAG, "onPageSelected: " + position);
                activityDrawerBinding.includeDrawer.viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setScrimAndStatusBarScrimColor(int position, TypedArray colors) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityDrawerBinding.includeDrawer.collapseBarHeader.setContentScrimColor(colors.getColor(position, 0));
            activityDrawerBinding.includeDrawer.collapseBarHeader.setStatusBarScrimColor(colors.getColor(position, 0));
        }
    }

    private void setContentViewPager() {
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        activityDrawerBinding.includeDrawer.viewPager.setAdapter(mPagerAdapter);
        //activityDrawerBinding.includeDrawer.viewPager.setPageTransformer(true, new FadePageTransformer());
        activityDrawerBinding.includeDrawer.viewPager.setOffscreenPageLimit(4);
        activityDrawerBinding.includeDrawer.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0 || position == 2)
                    activityDrawerBinding.includeDrawer.adContainer.setVisibility(View.GONE);
                else
                    activityDrawerBinding.includeDrawer.adContainer.setVisibility(View.VISIBLE);

                activityDrawerBinding.includeDrawer.vpHeader.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (isDietPlan)
            activityDrawerBinding.includeDrawer.viewPager.setCurrentItem(1);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void closeApp() {
        finish();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CategoryFragment.newInstance();
                case 1:
                    return RecipeFragment.newInstance();

                case 2:
                    return CalendarFragment.newInstance();

                case 3:
                    return AchievementsFragment.newInstance();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
