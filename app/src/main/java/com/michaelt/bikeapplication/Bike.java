package com.michaelt.bikeapplication;

/**
 * Created by Michael on 4/15/2015.
 */
public class Bike {

    String mBrandName;
    String mModelName;
    double mPrice;
    String mImageURL;
    String mDescription;

    public Bike(String theBrandName, String theModelName, double thePrice, String theImageURL) {
        mBrandName = theBrandName;
        mModelName = theModelName;
        mPrice = thePrice;
        mImageURL = theImageURL;
        mDescription = "Caution: This bike will get you hooked. It packs all of our race hardtail experience into a light, fast, " +
                        "race-ready bike that pairs the right wheel size with each frame size. Nothing beats the efficiency, simplicity, " +
                        "and straight-up fun of an this bike in a 29er or 27.5. Great for XC racing, marathons, 24-hour racing, or " +
                        "simply shredding singletrack.";
    }

    /**
     *  Getters and Setters
     */
    public void setBrandName(String theBrandName) { mBrandName = theBrandName; }

    public void setModelName(String theModelName) { mModelName = theModelName; }

    public void setImageURL(String theImageURL) { mImageURL = theImageURL; }

    public void setPrice(double thePrice) { mPrice = thePrice; }

    public void setmDescription(String theDescription) { mDescription = theDescription; }

    public String getBrandName() { return mBrandName; }

    public String getModelName() { return mModelName; }

    public String getImageURL() { return mImageURL; }

    public double getPrice() { return mPrice; }

    public String getDescription() { return mDescription; }
}
