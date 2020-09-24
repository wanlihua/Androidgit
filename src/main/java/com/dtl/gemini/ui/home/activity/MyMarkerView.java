
package com.dtl.gemini.ui.home.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.utils.DataUtil;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;
//            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
            tvContent.setText(DataUtil.doubleTwo(Double.parseDouble(ce.getHigh()+ "")));
        } else {
//            tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
            tvContent.setText(DataUtil.doubleTwo(Double.parseDouble(e.getY() + "")));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
