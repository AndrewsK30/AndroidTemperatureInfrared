package com.example.andrewskuhndasilva.mymeasure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.example.andrewskuhndasilva.mymeasure.data.Temperature;
import com.example.andrewskuhndasilva.mymeasure.handlers.UsbHandler;
import com.example.andrewskuhndasilva.mymeasure.service.UsbService;
import com.example.andrewskuhndasilva.mymeasure.service.UsbServiceConnection;
import com.example.andrewskuhndasilva.mymeasure.widget.CustomSeekBar;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class MainActivity extends AppCompatActivity {

    private final int TIMER_DELAY = 500;
    private UsbHandler mHandler = new UsbHandler(this);
    private final UsbServiceConnection usbConnection = new UsbServiceConnection(mHandler);
    @Getter
    private Temperature mTemperature = new Temperature();
    private Timer mTimer = new Timer();
    private boolean isTimerRunning;
    private HorizontalBarChart mBarChart;
    private Menu mMenu;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private boolean isOnNoUSB = true;
    private ConstraintLayout mNoUsb;
    private CustomSeekBar bar;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (usbConnection != null) { // if UsbService was correctly binded, Send data
                        usbConnection.sendMessage(new String("0"));
                    }
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNoUsb = findViewById(R.id.no_usb_frame);
        mBarChart = findViewById(R.id.chart);
        this.chartConfig();
    }

    private void chartConfig(){
        mBarChart.setPinchZoom(false);
        mBarChart.setDoubleTapToZoomEnabled(false);
        BarData data = new BarData(getDataSet());
        mBarChart.getXAxis().setDrawGridLines(false);
        mBarChart.getXAxis().setDrawAxisLine(false);
        mBarChart.getAxisRight().setDrawAxisLine(false);
        mBarChart.getAxisLeft().setDrawGridLines(false);
        mBarChart.getAxisLeft().setDrawAxisLine(false);
        mBarChart.getAxisRight().setDrawGridLines(false);
        mBarChart.getAxisLeft().setDrawLabels(false);
        mBarChart.getAxisRight().setDrawLabels(false);
        mBarChart.getXAxis().setDrawLabels(false);
        mBarChart.setDrawBorders(false);
        mBarChart.setData(data);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.animateXY(2000, 2000);
        mBarChart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.mTemperature.setAddCallBack(new Runnable() {
            @Override
            public void run() {
                drawTemperatures();
            }
        });
        this.mMenu = menu;
        menu.getItem(0).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.readTemperature:
                handleTemperatureRead();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void initTimer(){
        isTimerRunning = true;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask,0,TIMER_DELAY);
    }

    private void handleTemperatureRead(){
        MenuItem menuItem = mMenu.findItem(R.id.readTemperature);
        if (isTimerRunning){
            cancelTimer();
            menuItem.setTitle(R.string.read_desactive);
        }else{
            menuItem.setTitle(R.string.read_active);
            initTimer();
        }
    }

    private void showNoUsbFrame(){
        isOnNoUSB = true;
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        mNoUsb.setAnimation(inAnimation);
        mNoUsb.setVisibility(View.VISIBLE);
    }

    private void hideNoUsbFrame(){
        isOnNoUSB = false;
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        mNoUsb.setAnimation(outAnimation);
        mNoUsb.setVisibility(View.GONE);
    }

    private void cancelTimer(){
        isTimerRunning = false;
        mTimer.cancel();
        mTemperature.clean();
    }

    public void drawTemperatures(){
        bar.setProgress(mTemperature.getTempAtual());
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    private BarDataSet getDataSet() {
        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(1f, 4));
        entries.add(new BarEntry(2f, 1));
        entries.add(new BarEntry(3f, 2));

        BarDataSet dataset = new BarDataSet(entries,"Temperaturas");
        return dataset;
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "Disposivo pronto", Toast.LENGTH_SHORT).show();
                    mMenu.getItem(0).setEnabled(true);
                    if(isOnNoUSB) hideNoUsbFrame();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    if(!isOnNoUSB) showNoUsbFrame();
                    Toast.makeText(context, "Dispositivo não tem permisão", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_ATTACHED:
                    Toast.makeText(context, "Dispositivo conectado", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    if(!isOnNoUSB) showNoUsbFrame();
                    Toast.makeText(context, "Dispositivo desconectado", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
