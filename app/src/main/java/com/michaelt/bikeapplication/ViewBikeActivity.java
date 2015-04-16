package com.michaelt.bikeapplication;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ViewBikeActivity extends Activity {

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;

    Context mContext;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;
    @InjectView(R.id.image_bike_inspect_logo) ImageView mLogoImage;
    @InjectView(R.id.image_bike_large) ImageView mBikeImage;
    @InjectView(R.id.text_bike_details) TextView mBikeDetails;
    @InjectView(R.id.text_bike_description) TextView mDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bike);
        mContext = this;
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        final int bikeTop = bundle.getInt("top");
        final int bikeLeft = bundle.getInt("left");
        final String bikeModel = bundle.getString("model");
        final String bikeBrand = bundle.getString("brand");
        final String bikePrice = bundle.getString("cost");
        final String bikeDescription = bundle.getString("description");

        mLogoImage.setImageResource(R.drawable.logo);
        mBikeImage.setImageResource(R.drawable.bike);
        mBikeDetails.setText(bikeBrand + " " + bikeModel + " - $" + bikePrice);
        mDescriptionView.setText(bikeDescription);



        if (savedInstanceState == null) {
            ViewTreeObserver observer = mBikeImage.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mBikeImage.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    mBikeImage.getLocationOnScreen(screenLocation);
                    mLeftDelta = bikeLeft - screenLocation[0];
                    mTopDelta = bikeTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) 300 / 1776;
                    mHeightScale = (float) 300 / 1080;

                    runEnterAnimation();

                    return true;
                }
            });
        }

    }

    /**
     * Retrieves an image from a image URL and returns a Drawable
     * @param theURL The image URL
     * @return The image as a Drawable
     */
    private static Drawable getDrawableFromURL(String theURL) {
        try {
            InputStream is = (InputStream) new URL(theURL).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the pictue is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * 1.2);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mBikeImage.setPivotX(0);
        mBikeImage.setPivotY(0);
        mBikeImage.setScaleX(mWidthScale);
        mBikeImage.setScaleY(mHeightScale);
        mBikeImage.setTranslationX(mLeftDelta);
        mBikeImage.setTranslationY(mTopDelta);

        mLogoImage.setPivotX(0);
        mLogoImage.setPivotY(0);
        mLogoImage.setScaleX(mWidthScale);
        mLogoImage.setScaleY(mHeightScale);
        mLogoImage.setTranslationX(0);
        mLogoImage.setTranslationY(0);

        //We'll fade the text in later
        mDescriptionView.setAlpha(0);

        // Animate scale and translation to go from thumbnail to full size
        mLogoImage.animate()
                .setDuration(duration)
                .scaleX(1)
                .scaleY(1)
                .translationX(0)
                .translationY(0)
                .setInterpolator(sDecelerator);

        mBikeImage.animate()
                .setDuration(duration)
                .scaleX(1)
                .scaleY(1)
                .translationX(0)
                .translationY(mLogoImage.getHeight())
                .setInterpolator(sDecelerator)
                .withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        mDescriptionView.setTranslationY(-mDescriptionView.getHeight());
                        mDescriptionView.animate()
                                        .setDuration(duration/2)
                                        .translationY(mBikeImage.getHeight() + mLogoImage.getHeight())
                                        .alpha(1)
                                        .setInterpolator(sDecelerator);
                    }
                });
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     * when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction) {
        final long duration = (long) (ANIM_DURATION * 1.1);

        mBikeDetails.animate().alpha(0);
        // First, slide/fade text out of the way
        mDescriptionView.animate()
                        .translationY(-mDescriptionView.getHeight())
                        .alpha(0)
                        .setDuration(duration / 2)
                        .setInterpolator(sAccelerator)
                        .withEndAction(new Runnable() {
                            public void run() {
                                // Animate image back to thumbnail size/location
                                mBikeImage.animate()
                                        .setDuration(duration)
                                        .scaleX(mWidthScale)
                                        .scaleY(mHeightScale)
                                        .translationX(mLeftDelta)
                                        .translationY(mTopDelta)
                                        .withEndAction(endAction);
                                mLogoImage.animate()
                                        .setDuration(duration)
                                        .scaleX((float) .5)
                                        .scaleY((float) .5)
                                        .translationX(0)
                                        .translationY(0);
                            }
                        });


    }

    /**
     * Overriding this method allows us to run our exit animation first, then exiting
     * the activity when it is complete.
     */
    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

}
