package com.example.andrewskuhndasilva.mymeasure.data;

import java.util.ArrayList;
import java.util.Iterator;
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
    @Setter
    private Runnable mCleanCallBack;
    @Setter
    private Runnable mAddCallBack;
    protected int mNumberOfItens = 0;


    public void addTemperature(Float temperature){

        if (temperature == null ) return;

        mNumberOfItens++;
        mTempAtual = temperature;
        calculateTemps();
        if (mAddCallBack != null) mAddCallBack.run();
    }

    public void addTemperature(String  temperature){
        this.addTemperature(Float.valueOf(temperature));
    }

    public void clean(){
        mTempAtual = 0f;
        mTempMed = 0f;
        mTempMin = 0f;
        mTempMax = 0f;
        mNumberOfItens = 0;
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


        mTempMed /= mNumberOfItens;

    }

}
