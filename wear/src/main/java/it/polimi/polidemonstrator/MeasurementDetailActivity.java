package it.polimi.polidemonstrator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;

import java.util.ArrayList;

/**
 * Created by saeed on 7/5/2016.
 */
public class MeasurementDetailActivity extends Activity {

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_measurement_details);
        context=this;

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new myWatcthOnLayoutInflatedListener());

    }

    private class myWatcthOnLayoutInflatedListener implements WatchViewStub.OnLayoutInflatedListener {
        @Override
        public void onLayoutInflated(WatchViewStub watchViewStub) {

            // in this example, a LineChart is initialized from xml
            LineChart lineChart = (LineChart) watchViewStub.findViewById(R.id.chart);
            lineChart.animateXY(1500, 1500);
            lineChart.setBackgroundColor(Color.WHITE);

            lineChart.setDescription("");

            ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
            Entry c1e1 = new Entry(100.000f, 0); // 0 == quarter 1

            valsComp1.add(c1e1);
            Entry c1e2 = new Entry(50.000f, 1); // 1 == quarter 2 ...
            valsComp1.add(c1e2);
            Entry c1e3 = new Entry(150.000f, 2); // 1 == quarter 2 ...
            valsComp1.add(c1e3);
            Entry c1e4 = new Entry(30.000f, 3); // 1 == quarter 2 ...
            valsComp1.add(c1e4);
            Entry c1e5 = new Entry(130.000f, 4); // 1 == quarter 2 ...
            valsComp1.add(c1e5);

            LineDataSet setComp1 = new LineDataSet(valsComp1, "");
            setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
            setComp1.setLineWidth(6.000f);
            setComp1.setValueTextSize(18);



            // use the interface ILineDataSet
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(setComp1);
            ArrayList<String> xVals = new ArrayList<String>();
            xVals.add(""); xVals.add(""); xVals.add(""); xVals.add("");xVals.add("");

            LineData lineData = new LineData(xVals, dataSets);
            lineChart.setData(lineData);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setEnabled(false);

            YAxis yAxis = lineChart.getAxisRight();
            yAxis.setEnabled(false);

            lineChart.setTouchEnabled(false);

            lineChart.invalidate();


        }
    }
}
