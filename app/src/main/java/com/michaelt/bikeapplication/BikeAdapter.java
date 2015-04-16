package com.michaelt.bikeapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;

/**
 * Created by Michael on 4/15/2015.
 */
public class BikeAdapter extends BaseAdapter {

    private Context mContext;
    List<Bike> mBikeList;
    NumberFormat nf;

    public BikeAdapter(Context theContext, List<Bike> theBikeList) {
        mContext = theContext;
        mBikeList = theBikeList;
    }

    @Override
    public int getCount() {
        return mBikeList.size();
    }

    @Override
    public Object getItem(int i) {
        return mBikeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Bike bike = mBikeList.get(i);
        view = LayoutInflater.from(mContext).inflate(R.layout.bike_list_item, null);

        TextView brandName = (TextView) view.findViewById(R.id.text_brand_name);
        TextView modelName = (TextView) view.findViewById(R.id.text_model_name);
        TextView price = (TextView) view.findViewById(R.id.text_price);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_bike);

        Picasso.with(mContext)
               .load(bike.getImageURL())
               .into(imageView);

        brandName.setText(bike.getBrandName());
        modelName.setText(bike.getModelName());

        nf = DecimalFormat.getCurrencyInstance(Locale.US);
        price.setText(nf.format(bike.getPrice()));
        price.setTextColor(Color.RED);

        return view;
    }
}
