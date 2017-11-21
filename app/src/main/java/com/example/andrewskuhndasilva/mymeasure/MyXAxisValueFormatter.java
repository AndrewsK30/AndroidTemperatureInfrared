package com.example.andrewskuhndasilva.mymeasure;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by andrewskuhndasilva on 19/11/2017.
 */

class MyXAxisValueFormatter implements IAxisValueFormatter {
    private String[] mValues;

    public MyXAxisValueFormatter(String[] values) {
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mValues[(int) value];
    }

}
