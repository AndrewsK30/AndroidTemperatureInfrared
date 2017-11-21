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
    private Float mTempMed;
    private Float mTempMin;
    private Float mTempMax;
    @Setter
    private Runnable mCleanCallBack;
    @Setter
    private Runnable mAddCallBack;
    protected ArrayList<Float> mTemperaturaArray = new ArrayList<>();


    public void addTemperature(Float temperature){

        if (temperature == null) return;

        mTemperaturaArray.add(temperature);
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
        mTemperaturaArray = new ArrayList<>();
        if(mCleanCallBack != null) mCleanCallBack.run();
    }

    private void calculateTemps(){
        Iterator<Float> temps = mTemperaturaArray.iterator();
        mTempMed = 0f;
        while (temps.hasNext()){
            Float atual = temps.next();
            mTempMed+= atual;

            if (mTempMin == null || atual < mTempMin){
                mTempMin = atual;
            }

            if (mTempMax == null || atual > mTempMax){
                mTempMax = atual;
            }
        }

        mTempMed /= mTemperaturaArray.size();

    }

}
