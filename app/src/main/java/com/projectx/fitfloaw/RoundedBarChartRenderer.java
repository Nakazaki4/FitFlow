package com.projectx.fitfloaw;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class RoundedBarChartRenderer extends BarChartRenderer {
    private RectF mBarRect = new RectF();

    public RoundedBarChartRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mBarBorderPaint.setColor(dataSet.getBarBorderColor());
        mBarBorderPaint.setStrokeWidth(Utils.convertDpToPixel(dataSet.getBarBorderWidth()));

        final float phaseX = mAnimator.getPhaseX();
        final float phaseY = mAnimator.getPhaseY();

        // Initialize the buffer
        BarBuffer buffer = mBarBuffers[index];
        buffer.setPhases(phaseX, phaseY);
        buffer.setDataSet(index);
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()));
        buffer.setBarWidth(mChart.getBarData().getBarWidth());

        buffer.feed(dataSet);

        trans.pointValuesToPixel(buffer.buffer);

        // Define corner radius (adjust as needed)
        float cornerRadius = Utils.convertDpToPixel(8f);

        for (int j = 0; j < buffer.size(); j += 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2]))
                continue;

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j]))
                break;

            mBarRect.set(buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2], buffer.buffer[j + 3]);

            if (mBarRect.width() > Utils.convertDpToPixel(1)) {
                // Set the color of the bar
                mRenderPaint.setColor(dataSet.getColor(j / 4)); // Set color for the bar

                // Draw the rounded bar
                c.drawRoundRect(mBarRect, cornerRadius, cornerRadius, mRenderPaint);

                // Draw the border, if necessary
                if (dataSet.getBarBorderWidth() > 0) {
                    c.drawRoundRect(mBarRect, cornerRadius, cornerRadius, mBarBorderPaint);
                }
            }
        }
    }

}
