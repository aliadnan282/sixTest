package com.workout.sixpacksabs.manager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;

import java.util.List;

import static com.workout.sixpacksabs.helper.AppConstant.IS_ADS_DISABLED;


public class AdsManager {

    private static AdsManager manager;
    AppPreference appPreference;
    private InterstitialAd interstitialAd;
    // private com.facebook.ads.NativeAd nativeAd;
    private com.facebook.ads.InterstitialAd fbInterstitialAd;
    private String TAG = AdsManager.class.getName();


    private AdsManager() {
        appPreference = AppPreference.getInstance(AppConstant.mContext);

        if (!appPreference.getBoolean(IS_ADS_DISABLED)) {
            interstitialAd = new InterstitialAd(AppConstant.mContext);
            interstitialAd.setAdUnitId(AppConstant.mContext.getString(R.string.admob_interstitial_ad_unit));
            fbInterstitialAd = new com.facebook.ads.InterstitialAd(AppConstant.mContext,  AppConstant.mContext.getString(R.string.facebook_interstitial_ad_unit));
            // load the ads and cache them for later use
            loadInterstitialAd();
            loadFacebookInterstitialAd();
        }
    }

    public static AdsManager getInstance() {
        if (manager == null) {
            manager = new AdsManager();
        }
        return manager;
    }

    private AdRequest appendUserConsent() {
        AdRequest adRequest = null;
        if (appPreference.getBoolean(AppConstant.mContext.getString(R.string.npa))) {
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.mContext.getString(R.string.npa), "1");
            Log.d(TAG, "consent status: npa");
            adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, bundle).build();
        } else {
            Log.d(TAG, "consent status: pa");
            adRequest = new AdRequest.Builder().build();
        }
        return adRequest;
    }

    public void loadInterstitialAd() {
        if (AppUtils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            interstitialAd.loadAd(appendUserConsent());

            interstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Log.e(TAG, "onAdClosed");
                    // reload it and cache it for next time
                    loadInterstitialAd();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.e(TAG, "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.e(TAG, "onAdFailedToLoad");
                    // if failed to load then reload it again
                    loadInterstitialAd();
                }
            });
        }
    }

    public void showInterstitialAd() {
        if (!appPreference.getBoolean(IS_ADS_DISABLED)) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                loadInterstitialAd();
            }
        }
    }

    public void showAdMobLargeBanner(final AdView adView) {
        if (AppUtils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            if (adView != null) {

                adView.loadAd(appendUserConsent());
                adView.setAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.d("AdMobBanner", "onAdFailedToLoad");
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        Log.d("AdMobBanner", "onAdLoaded");
                        if (adView.getVisibility() == View.GONE) {
                            adView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }
    }

    public void loadNativeAppInstall(final FrameLayout nativeAppInstall) {
        if (AppUtils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            AdLoader adLoader = new AdLoader.Builder(AppConstant.mContext, AppConstant.mContext.getString(R.string.admob_native_ad_unit))
                    .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        @Override
                        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                            Log.e(TAG, "onNativeAppInstallAdLoaded");
                            NativeAppInstallAdView adView = (NativeAppInstallAdView)
                                    LayoutInflater.from(AppConstant.mContext).inflate(R.layout.ad_app_install, null);
                            populateNativeAppInstallAdView(nativeAppInstallAd, adView);
                            nativeAppInstall.removeAllViews();
                            nativeAppInstall.addView(adView);
                        }
                    })
                    .withAdListener(new AdListener() {

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            Log.e(TAG, "onNativeAppInstallAdFailedToLoad");
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (nativeAppInstall.getVisibility() == View.GONE) {
                                nativeAppInstall.setVisibility(View.VISIBLE);
                            }
                            Log.e(TAG, "onNativeAppInstallAdLoaded");
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Log.e(TAG, "onNativeAppInstallAdClosed");
                        }
                    }).build();
            adLoader.loadAd(appendUserConsent());
        }
    }

    private void populateNativeAppInstallAdView(NativeAppInstallAd nativeAppInstallAd, NativeAppInstallAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        // ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon().getDrawable());

        Picasso.get()
                .load(nativeAppInstallAd.getIcon().getUri())
                .into(((ImageView) adView.getIconView()));

        MediaView mediaView = adView.findViewById(R.id.appinstall_media);
        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mainImageView.setVisibility(View.GONE);
        } else {

            adView.setImageView(mainImageView);
            mediaView.setVisibility(View.GONE);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAppInstallAd.getImages();
            Picasso.get()
                    .load(images.get(0).getUri())
                    .into(mainImageView);
            // mainImageView.setImageDrawable(images.get(0).getUri());
        }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    public void loadNativeBannerAppInstall(final Context context, final FrameLayout nativeAppInstall) {
        if (AppUtils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_native_ad_unit))
                    .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        @Override
                        public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                            Log.e(TAG, "onNativeBannerAppInstallAdLoaded");
                            NativeAppInstallAdView adView = (NativeAppInstallAdView)
                                    LayoutInflater.from(context).inflate(R.layout.banner_ad_app_install, null);
                            populateNativeBannerAppInstallAdView(nativeAppInstallAd, adView);
                            nativeAppInstall.removeAllViews();
                            nativeAppInstall.addView(adView);
                        }
                    })
                    .withAdListener(new AdListener() {

                        @Override
                        public void onAdFailedToLoad(int i) {
                            super.onAdFailedToLoad(i);
                            Log.e(TAG, "onNativeBannerAppInstallAdFailedToLoad");
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            if (nativeAppInstall.getVisibility() == View.GONE) {
                                nativeAppInstall.setVisibility(View.VISIBLE);
                            }
                            Log.e(TAG, "onNativeBannerAppInstallAdLoaded");
                        }

                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Log.e(TAG, "onNativeBannerAppInstallAdClosed");
                        }
                    }).build();
            adLoader.loadAd(appendUserConsent());
        }
    }

    private void populateNativeBannerAppInstallAdView(NativeAppInstallAd nativeAppInstallAd, NativeAppInstallAdView adView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            public void onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                super.onVideoEnd();
            }
        });

        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
      /*  ((ImageView) adView.getIconView()).setImageDrawable(
                nativeAppInstallAd.getIcon().getDrawable());*/
        Picasso.get()
                .load(nativeAppInstallAd.getIcon().getUri())
                .into(((ImageView) adView.getIconView()));

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    public void loadFacebookInterstitialAd() {
        if (AppUtils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            fbInterstitialAd.setAdListener(new AbstractAdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    super.onError(ad, adError);
                    Log.d(TAG, "onError: facebook interstitial"+ adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    super.onAdLoaded(ad);
                    Log.d(TAG, "onAdLoaded: facebook interstitial"+ ad.toString());
                }

                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    super.onInterstitialDisplayed(ad);

                    Log.d(TAG, "facebook onInterstitialDisplayed: ");
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    super.onInterstitialDismissed(ad);
                    // reload it and cache it for next time
                    Log.d(TAG, "facebook onInterstitialDismissed: ");
                    loadFacebookInterstitialAd();
                }
            });
            fbInterstitialAd.loadAd();
        }
    }

    public void showFacebookInterstitialAd() {
        if (!appPreference.getBoolean(IS_ADS_DISABLED)) {
            if (fbInterstitialAd.isAdLoaded()) {
                try {
                    fbInterstitialAd.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                loadFacebookInterstitialAd();
            }
        }
    }

    public void showFacebookBannerAd(FrameLayout adContainer) {
        if (AppUtils.isNetworkAvailable() && !appPreference.getBoolean(IS_ADS_DISABLED)) {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(AppConstant.mContext, AppConstant.mContext.getString(R.string.facebook_banner_ad_unit), AdSize.BANNER_HEIGHT_50);
            // Add the ad view to your activity layout
            adContainer.addView(adView);
            // Request an ad
            adView.loadAd();
        }
    }
}
