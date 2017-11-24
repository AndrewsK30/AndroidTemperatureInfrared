package com.example.andrewskuhndasilva.mymeasure.data;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by andrews.silva on 24/11/2017.
 */
public class TemperatureTest {
    @Test
    public void addTemperature() throws Exception {
        Temperature temperature = new Temperature();
        Float[] temps ={22.02f,24.02f};
        temperature.addTemperature(temps[0]);
        temperature.addTemperature(temps[1]);

        Assert.assertEquals(2,temperature.getNumberOfItens());
        Assert.assertEquals(temps[1],temperature.getTempAtual());
        Assert.assertEquals(temps[0],temperature.getTempMin());
        Assert.assertEquals(temps[1],temperature.getTempMax());
        Assert.assertEquals(Float.valueOf((temps[1]+temps[0])/2),temperature.getTempMed());
    }

    @Test
    public void addTemperature1() throws Exception {

        Temperature temperature = new Temperature();
        String[] temps ={"22.02f","24.02f"};
        temperature.addTemperature(temps[0]);
        temperature.addTemperature(temps[1]);

        Assert.assertEquals(2,temperature.getNumberOfItens());
        Assert.assertEquals(Float.valueOf(temps[1]),temperature.getTempAtual());
        Assert.assertEquals(Float.valueOf(temps[0]),temperature.getTempMin());
        Assert.assertEquals(Float.valueOf(temps[1]),temperature.getTempMax());
        Assert.assertEquals(Float.valueOf((Float.valueOf(temps[1])+Float.valueOf(temps[0]))/2),temperature.getTempMed());
    }

    @Test
    public void clean() throws Exception {
        Temperature temperature = new Temperature();
        Float[] temps ={22.02f,24.02f};
        temperature.addTemperature(temps[0]);
        temperature.addTemperature(temps[1]);
        temperature.clean();
        Float float_0 = new Float(.0f);
        Assert.assertEquals(0,temperature.getNumberOfItens());
        Assert.assertEquals(float_0,temperature.getTempAtual());
        Assert.assertEquals(float_0,temperature.getTempMin());
        Assert.assertEquals(float_0,temperature.getTempMax());
        Assert.assertEquals(float_0,temperature.getTempMed());



    }

}