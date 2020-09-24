package com.dtl.gemini.ui.home.activity;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import android.view.View;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.model.MData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class LineChartActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.chart1)
    LineChart chart;

    @Override
    public int getLayoutId() {
        return R.layout.activity_line_chart;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        initLineChart();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    private void initLineChart(){
        chart.setBackgroundColor(Color.WHITE);

        chart.getDescription().setEnabled(false);

        chart.setTouchEnabled(true);
        //启用右侧坐标
        chart.getAxisRight().setEnabled(true);
        //启用左侧坐标
        chart.getAxisLeft().setEnabled(false);
        chart.setDrawGridBackground(false);

        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        mv.setChartView(chart);
        chart.setMarker(mv);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        chart.setPinchZoom(true);

//        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
//        xAxis.setEnabled(false);
        // 上面第一行代码设置了false,所以下面第一行即使设置为true也不会绘制AxisLine
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

        xAxis.enableGridDashedLine(3f, 30f, 0f);
//        xAxis.disableGridDashedLine();
        YAxis yAxis = chart.getAxisLeft();
//        yAxis.setDrawAxisLine(true);
//        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yAxis.enableGridDashedLine(3f, 30f, 0f);
//        yAxis.disableGridDashedLine();

//        yAxis.setAxisMaximum(200f);
//        yAxis.setAxisMinimum(-50f);
        LimitLine llXAxis = new LimitLine(9f, "");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        LimitLine ll1 = new LimitLine(70f, "");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setLineColor(getResources().getColor(R.color.v2_btn));

        LimitLine ll2 = new LimitLine(20f, "");
        ll2.setLineWidth(0f);
        ll2.enableDashedLine(0f, 0f, 0f);
        ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(0f);
        ll2.setLineColor(getResources().getColor(R.color.transparent));

        yAxis.setDrawLimitLinesBehindData(true);
        xAxis.setDrawLimitLinesBehindData(true);
        yAxis.addLimitLine(ll1);
        yAxis.addLimitLine(ll2);
        setLineChartData(20, 200);
        chart.animateXY(2000, 2000);
        chart.setPinchZoom(true);
        Legend l = chart.getLegend();
        l.setForm(LegendForm.LINE);
    }

    private void setLineChartData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            float val = (float) (Math.random() * range) - 30;
            values.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            set1.notifyDataSetChanged();
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "");

            set1.setDrawIcons(false);

            set1.enableDashedLine(0f, 0f, 0f);
            set1.setColor(Color.WHITE);
            set1.setCircleColor(Color.TRANSPARENT);

            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);

            set1.setDrawCircleHole(false);

            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            set1.setValueTextSize(9f);

            set1.enableDashedHighlightLine(10f, 5f, 0f);

            set1.setDrawFilled(true);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return chart.getAxisLeft().getAxisMinimum();
                }
            });

            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.linechart_bg);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.WHITE);
            }
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            set1.setMode(set1.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                    ? LineDataSet.Mode.LINEAR
                    : LineDataSet.Mode.CUBIC_BEZIER);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }


    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
            R.id.btn8, R.id.btn9, R.id.btn10, R.id.btn11, R.id.btn12, R.id.btn13})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:///
                List<ILineDataSet> sets = chart.getData().getDataSets();
                for (ILineDataSet iSet : sets) {
                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }
                chart.invalidate();
                break;
            case R.id.btn2:
                List<ILineDataSet> sets2 = chart.getData().getDataSets();
                for (ILineDataSet iSet : sets2) {
                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawIcons(false);
                }
                chart.invalidate();
                break;
            case R.id.btn3:
                if (chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
                break;
            case R.id.btn4:///

                break;
            case R.id.btn5:///
                List<ILineDataSet> sets5 = chart.getData().getDataSets();
                for (ILineDataSet iSet : sets5) {
                    LineDataSet set = (LineDataSet) iSet;

                }
                chart.invalidate();
                break;
            case R.id.btn6:
                List<ILineDataSet> sets6 = chart.getData().getDataSets();
                for (ILineDataSet iSet : sets6) {
                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.CUBIC_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.CUBIC_BEZIER);
                }
                chart.invalidate();
                break;
            case R.id.btn7:
                List<ILineDataSet> sets7 = chart.getData().getDataSets();
                for (ILineDataSet iSet : sets7) {
                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.STEPPED
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.STEPPED);
                }
                chart.invalidate();
                break;
            case R.id.btn8:
                List<ILineDataSet> sets8 = chart.getData().getDataSets();
                for (ILineDataSet iSet : sets8) {
                    LineDataSet set = (LineDataSet) iSet;
                    set.setMode(set.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                            ? LineDataSet.Mode.LINEAR
                            : LineDataSet.Mode.HORIZONTAL_BEZIER);
                }
                chart.invalidate();
                break;
            case R.id.btn9:///
//                if (chart.isPinchZoomEnabled())
//                    chart.setPinchZoom(false);
//                else
                chart.setPinchZoom(true);
                chart.invalidate();
                break;
            case R.id.btn10:
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
                break;
            case R.id.btn11:
                chart.animateX(2000);
                break;
            case R.id.btn12:
                chart.animateY(2000, Easing.EaseInCubic);
                break;
            case R.id.btn13:///
                chart.animateXY(2000, 2000);
                break;
        }
    }
}
