package com.michaelt.bikeapplication.activities;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.michaelt.bikeapplication.R;
import java.io.InputStream;
import java.net.URL;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ViewBikeActivity extends Activity {

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 500;

    private Context mContext;
    private int mBikeLeftDelta;
    private int mBikeTopDelta;
    private int mLogoLeftDelta;
    private int mLogoTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    @InjectView(R.id.image_bike_inspect_logo) ImageView mLogoImage;
    @InjectView(R.id.image_bike_large) ImageView mBikeImage;
    @InjectView(R.id.text_bike_details) TextView mBikeDetails;
    @InjectView(R.id.text_bike_description) TextView mDescriptionView;
    @InjectView(R.id.button_add_to_cart) Button mAddToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bike);
        mContext = this;
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        final int bikeTop = bundle.getInt("bikeTop");
        final int bikeLeft = bundle.getInt("bikeLeft");
        final int logoTop = bundle.getInt("logoTop");
        final int logoLeft = bundle.getInt("logoLeft");
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
                    mBikeLeftDelta = bikeLeft - screenLocation[0];
                    mBikeTopDelta = bikeTop - screenLocation[1];

                    mLogoImage.getLocationOnScreen(screenLocation);
                    mLogoLeftDelta = logoLeft - screenLocation[0];
                    mLogoTopDelta = logoTop - screenLocation[1];

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
     * Creates an Alert Dialog that informs the user that an item has been added to
     * the non-existing cart
     */
    @OnClick(R.id.button_add_to_cart)
    public void addToCart() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("This bike has been added to your shopping cart!");
        AlertDialog alert = builder.create();
        alert.show();
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
    private void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * 1.2);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mBikeImage.setPivotX(0);
        mBikeImage.setPivotY(0);
        mBikeImage.setScaleX(mWidthScale);
        mBikeImage.setScaleY(mHeightScale);
        mBikeImage.setTranslationX(mBikeLeftDelta);
        mBikeImage.setTranslationY(mBikeTopDelta);

        mLogoImage.setPivotX(0);
        mLogoImage.setPivotY(0);
        mLogoImage.setScaleX(mWidthScale);
        mLogoImage.setScaleY(mHeightScale);
        mLogoImage.setTranslationX(mLogoLeftDelta);
        mLogoImage.setTranslationY(mLogoTopDelta);

        //We'll fade the text in later
        mDescriptionView.setAlpha(0);

        // Animate scale and translation to go from thumbnail to full size
        mLogoImage.animate()
                .setDuration(duration)
                .scaleX(1)
                .scaleY(1)
                .translationX(0)
                .translationY(0)
                .alpha(1)
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
    private void runExitAnimation(final Runnable endAction) {
        final long duration = (long) (ANIM_DURATION * 1.1);

        mBikeDetails.animate().alpha(0);
        mAddToCartButton.animate().alpha(0);
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
                                        .translationX(mBikeLeftDelta)
                                        .translationY(mBikeTopDelta)
                                        .withEndAction(endAction);
                                mLogoImage.animate()
                                        .setDuration(duration)
                                        .scaleX((float) .5)
                                        .scaleY((float) .5)
                                        .translationX(mLogoLeftDelta)
                                        .translationY(mLogoTopDelta);
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
