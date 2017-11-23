package com.example.andrewskuhndasilva.mymeasure;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class MainActivity extends AppCompatActivity {

    private UsbHandler mHandler = new UsbHandler(this);
    private UsbService usbService;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };
    @Getter
    private Temperature mTemperature = new Temperature();
    private HorizontalBarChart mBarChart;
    private Menu mMenu;
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;
    private boolean isOnNoUSB = true;
    private ConstraintLayout mNoUsb;
    private CustomSeekBar bar;
    private boolean isRedingTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNoUsb = findViewById(R.id.no_usb_frame);
        bar = findViewById(R.id.seek_arc);
        mBarChart = findViewById(R.id.chart);
        this.chartConfig();
    }

    private void chartConfig(){
        mBarChart.setPinchZoom(false);
        mBarChart.setDoubleTapToZoomEnabled(false);
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
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setNoDataText("Na há nenhum dado");
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

    private void requestTemperature(){
        if (usbConnection != null && isRedingTemp) { // if UsbService was correctly binded, Send data
            usbService.write(new String("0").getBytes());
        }
    }


    private void handleTemperatureRead(){
        MenuItem menuItem = mMenu.findItem(R.id.readTemperature);
        if (isRedingTemp){
            isRedingTemp= false;
            menuItem.setTitle(R.string.read_desactive);
        }else{
            menuItem.setTitle(R.string.read_active);
            isRedingTemp = true;
            requestTemperature();
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


    public void drawTemperatures(){
        try{
            bar.setProgress(mTemperature.getTempAtual());
        }catch (Exception e){

        }
        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(1f, mTemperature.getTempMax()));
        entries.add(new BarEntry(2f, mTemperature.getTempMed()));
        entries.add(new BarEntry(3f, mTemperature.getTempMin()));

        BarDataSet dataset = new BarDataSet(entries,"Temperaturas");
        BarData data = new BarData(dataset);

        mBarChart.setData(data);
        mBarChart.invalidate();

        requestTemperature();
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
