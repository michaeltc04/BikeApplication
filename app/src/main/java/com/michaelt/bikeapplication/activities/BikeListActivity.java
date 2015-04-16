package com.michaelt.bikeapplication.activities;

import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import com.michaelt.bikeapplication.adapter.BikeAdapter;
import com.michaelt.bikeapplication.R;
import com.michaelt.bikeapplication.objects.Bike;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class BikeListActivity extends Activity {

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final int ANIM_DURATION = 600;

    private List<Bike> mBikeList;
    private BikeAdapter mBikeAdapter;
    private Context mContext;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    @InjectView(R.id.list_view_bikes) ListView mBikeListView;
    @InjectView(R.id.image_bike_list_logo) ImageView mLogoImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_list);
        mContext = this;
        ButterKnife.inject(this);

        Bundle bundle = getIntent().getExtras();
        final int logoTop = bundle.getInt("top");
        final int logoLeft = bundle.getInt("left");
        mLogoImage.setImageResource(R.drawable.logo);

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mLogoImage.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mLogoImage.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Find how far the image needs to move to get to its current position in the animation
                    int[] screenLocation = new int[2];
                    mLogoImage.getLocationOnScreen(screenLocation);
                    mLeftDelta = logoLeft - screenLocation[0];
                    mTopDelta = logoTop - screenLocation[1];

                    // How far down to scale the image from its default size
                    mWidthScale = (float) .5;
                    mHeightScale = (float) .5;

                    runEnterAnimation();

                    return true;
                }
            });
        }

        mBikeList = new ArrayList<Bike>();
        populateBikes();
        mBikeAdapter = new BikeAdapter(this, mBikeList);
        mBikeListView.setAdapter(mBikeAdapter);

        mBikeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //Get the current location of the bike image for animation use in the next activity
                int[] screenLocation = new int[2];
                mBikeListView.getChildAt(i).getLocationOnScreen(screenLocation);
                Bike bike = mBikeList.get(i);
                Intent intent = new Intent(mContext, ViewBikeActivity.class);

                //Pass along relevant information for which bike was selected
                intent.putExtra("brand", bike.getBrandName());
                intent.putExtra("model", bike.getModelName());
                intent.putExtra("cost", "" + bike.getPrice());
                intent.putExtra("imageURL", bike.getImageURL());
                intent.putExtra("bikeLeft", screenLocation[0]);
                intent.putExtra("bikeTop", screenLocation[1]);
                intent.putExtra("description", bike.getDescription());

                // Since we are moving 2 Views for this transition, we need to include the other
                // views properties as well.
                mLogoImage.getLocationOnScreen(screenLocation);
                intent.putExtra("logoLeft", screenLocation[0]);
                intent.putExtra("logoTop", screenLocation[1]);

                startActivity(intent);

                // Override transitions: we don't want the normal window animation in addition
                // to our custom one
                overridePendingTransition(0, 0);
            }
        });
    }



    /**
     * Generally this is where data would be retrieved from a server / database / string (JSON),
     * but here we will populate a local List (mBikeList) for simplicity.  Each bike object also
     * shares the same description.
     */
    private void populateBikes() {
        Bike bike;
        bike = new Bike("Trek", "X-Caliber", 789.99, "http://s7d4.scene7.com/is/image/TrekBicycleProducts/Asset_260734?wid=1440&hei=1080&fit=fit,1&fmt=png-alpha&qlt=30,1&op_usm=0,0,0,0&iccEmbed=0");
        mBikeList.add(bike);
        bike = new Bike("Salsa", "Fargo TI", 4299.00, "http://salsacycles.com/files/bikes/Fargo_Ti_15_sv_315x225.jpg");
        mBikeList.add(bike);
        bike = new Bike("Jamis", "Dakar AMT Pro", 4799.00,"http://www.myjamis.com/SSP%20Applications/JamisBikes/MyJamis/consumer/images/bikes_page/2015_bikes_page/15_dakarxctteam.jpg");
        mBikeList.add(bike);
        bike = new Bike("Salsa", "Bucksaw", 4999.00, "http://salsacycles.com/files/bikes/Bucksaw_1_15_34f_1440x960.jpg");
        mBikeList.add(bike);
        bike = new Bike("Bianchi", "Oltre XR2 DURA ACE Di2", 12799.99, "http://www.bianchiusa.com/fileadmin/_processed_/csm_YKBL1UB2_-_OLTRE_XR.2_DURA_ACE_Di2_Compact_9f7fffa4d4.jpg");
        mBikeList.add(bike);
        bike = new Bike("Jamis", "Dakar XCT Team", 6999.00, "http://www.jamisbikes.com/usa/images/15_dakarxctteam.jpg");
        mBikeList.add(bike);
        bike = new Bike("Trek", "Superfly FS", 2409.99, "http://s7d4.scene7.com/is/image/TrekBicycleProducts/Asset_264012?wid=1440&hei=1080&fit=fit,1&fmt=png-alpha&qlt=30,1&op_usm=0,0,0,0&iccEmbed=0");
        mBikeList.add(bike);
        bike = new Bike("Trek", "Lush Women's", 2099.99, "http://s7d4.scene7.com/is/image/TrekBicycleProducts/Asset_260748?wid=1440&hei=1080&fit=fit,1&fmt=png-alpha&qlt=30,1&op_usm=0,0,0,0&iccEmbed=0");
        mBikeList.add(bike);
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the picture is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION);

        // Set starting values for properties we're going to animate. These
        // values scale and position the full size version down to the thumbnail
        // size/location, from which we'll animate it back up
        mLogoImage.setPivotX(0);
        mLogoImage.setPivotY(0);
        mLogoImage.setScaleX(1);
        mLogoImage.setScaleY(1);
        mLogoImage.setTranslationX(mLeftDelta);
        mLogoImage.setTranslationY(mTopDelta);

        // Animate scale and translation to go from thumbnail to full size
        mLogoImage.animate()
                .setDuration(duration)
                .scaleX(mWidthScale)
                .scaleY(mHeightScale)
                .translationX(0)
                .translationY(0)
                .setInterpolator(sDecelerator);
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
        final long duration = (long) (ANIM_DURATION);

        mBikeListView.animate().setDuration(750).alpha(0);

        // Animate image back to thumbnail size/location
        mLogoImage.animate()
                .setDuration(duration)
                .scaleX(1)
                .scaleY(1)
                .translationX(mLeftDelta)
                .translationY(mTopDelta)
                .withEndAction(endAction);
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
