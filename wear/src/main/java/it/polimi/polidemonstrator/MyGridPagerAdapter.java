package it.polimi.polidemonstrator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.GridPagerAdapter;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.List;

import it.polimi.polidemonstrator.businesslogic.MeasurementClass;

/**
 * Created by saeed on 7/7/2016.
 */
public class MyGridPagerAdapter extends GridPagerAdapter {

    private final Context mContext;
    int resource;
    List<MeasurementClass> listMeasurementClass;

    public MyGridPagerAdapter(Context mContext, int resource, List<MeasurementClass> listMeasurementClass) {
        this.mContext = mContext;
        this.resource=resource;
        this.listMeasurementClass=listMeasurementClass;
    }

    @Override
    public int getRowCount() {
        return listMeasurementClass.size();

    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int row, int col) {
        final View view = LayoutInflater.from(mContext).inflate(resource, container, false);
        final TextView textView = (TextView) view.findViewById(R.id.tVMeasurementValueLatestValue);
        final TextView textView2=(TextView) view.findViewById(R.id.tVMeasurementLabel);
        final CircledImageView imageView = (CircledImageView) view.findViewById(R.id.imgvMeasurementImage);

        String text=listMeasurementClass.get(row).getSensorClassSensorLatestValue()+listMeasurementClass.get(row).getSensorClassMeasurementUnit();
        textView.setText(text);
        textView2.setText(listMeasurementClass.get(row).getSensorClassLabel());
        imageView.setImageResource(listMeasurementClass.get(row).getSensorClassImage());
        view.setTag(R.id.TAG_Measurement_ID,listMeasurementClass.get(row).getSensorClasseId());
        view.setTag(R.id.TAG_Measurement_Label,listMeasurementClass.get(row).getSensorClassLabel());
        view.setTag(R.id.TAG_Measurement_Unit,listMeasurementClass.get(row).getSensorClassMeasurementUnit());
        view.setOnClickListener(new viewOnClickListener());
        view.setLongClickable(true);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int row, int col, Object view) {
        container.removeView((View)view);

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    private class viewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String measurementClassId=(String)v.getTag(R.id.TAG_Measurement_ID);
            String measurementClassLabel=(String)v.getTag(R.id.TAG_Measurement_Label);
            String measurementClassMeasurementUnit=(String)v.getTag(R.id.TAG_Measurement_Unit);
            Bundle basket=new Bundle();
            basket.putString("measurementClassId",measurementClassId);
            basket.putString("measurementClassLabel",measurementClassLabel);
            basket.putString("measurementClassMeasurementUnit",measurementClassMeasurementUnit);
            Intent openChartActivity = new Intent("android.intent.action.MEASUREMENTDETAIL");
            openChartActivity.putExtras(basket);
            mContext.startActivity(openChartActivity);
        }
    }



}
