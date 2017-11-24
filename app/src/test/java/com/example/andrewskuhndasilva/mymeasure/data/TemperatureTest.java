package com.example.andrewskuhndasilva.mymeasure.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by andrewskuhndasilva on 24/11/2017.
 */
public class TemperatureTest {
    @Test
    public void addTemperature() throws Exception {

        List<Float> temps = Arrays.asList(22.33f,22.43f,22.43f,22.43f,21.43f,22.53f,22.33f,23.13f,22.03f,20.43f,21.43f,21.43f);
        List<Float> temps2 = Arrays.asList(22.33f,22.43f,22.43f,22.43f,21.43f,22.53f,22.33f,23.13f,22.03f,20.43f);
        Temperature temperature = new Temperature();

        assert temps.size() > 9;
        temps.forEach(temperature::addTemperature);
        Float med = temps2.stream().reduce(0f,(v,s) -> v+s)/10;
        Assert.assertEquals(new Float(med+0.2f),temperature.getTempAtual());



    }

}