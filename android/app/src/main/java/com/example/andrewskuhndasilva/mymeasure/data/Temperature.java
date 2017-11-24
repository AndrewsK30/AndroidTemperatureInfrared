package com.example.andrewskuhndasilva.mymeasure.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by andrewskuhndasilva on 12/11/2017.
 */
@NoArgsConstructor
@Getter
@Accessors(prefix = "m")
public class Temperature {

    private Float mTempAtual;
    private Float mTempMed = .0f;
    private Float mTempMin;
    private Float mTempMax;
    private List<Float> mTempHolder = new ArrayList<>();
    @Setter
    private Runnable mCleanCallBack;
    @Setter
    private Runnable mAddCallBack;
    protected int mNumberOfItens = 0;

    public void addTemperature(Float temperature){

        assert temperature != null;

        if(mTempHolder.size() < 20){

            mTempHolder.add(temperature);
            if (mAddCallBack != null) mAddCallBack.run();
            return;
        }else{
            mTempAtual = medOfHolderTemp();
            mTempAtual += 0.6f;
            mTempHolder.clear();
        }

        mNumberOfItens++;
        calculateTemps();
        if (mAddCallBack != null) mAddCallBack.run();
    }

    public void addTemperature(String  temperature){
        try {
            this.addTemperature(Float.valueOf(temperature));
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }

    public void clean(){
        mTempAtual = null;
        mTempMed = 0f;
        mTempMin = null;
        mTempMax = null;
        mNumberOfItens = 0;
        mTempHolder = new ArrayList<>();
        if(mCleanCallBack != null) mCleanCallBack.run();
    }

    private void calculateTemps(){
        mTempMed+= mTempAtual;

        if (mTempMin == null || mTempAtual < mTempMin){
            mTempMin = mTempAtual;
        }

        if (mTempMax == null || mTempAtual > mTempMax){
            mTempMax = mTempAtual;
        }
    }

    private float medOfHolderTemp(){
        float medTemp = 0;
        for (Float temp: mTempHolder) {
            medTemp += temp;
        }
        BigDecimal bd = new BigDecimal(medTemp/mTempHolder.size());
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public Float getTempMed(){
        if(mNumberOfItens == 0) return .0f;
        BigDecimal bd = new BigDecimal(mTempMed/mNumberOfItens);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}
