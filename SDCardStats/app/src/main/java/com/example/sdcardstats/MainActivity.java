package com.example.sdcardstats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected static final String CUSTOM_ACTION = "com.example.sucharan.sdcardstats.CUSTOM_ACTION";
    protected static final String FILES_INFO = "files_info";
    protected static final String EXTN_INFO = "extn_info";
    protected static final String AVG_FILE_SIZE = "avg_file_size";

    private Intent serviceIntent;
    private MyReceiver myReceiver;

    private ArrayList<FileStats> fileStats;
    private HashMap<String, Integer> extensionOccurances;
    private double avgFileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent = new Intent(this, MyService.class);
        myReceiver = new MyReceiver();
        if(savedInstanceState != null){
            avgFileSize = savedInstanceState.getDouble(AVG_FILE_SIZE, 0);
            displayAverageFileSize();
            fileStats = savedInstanceState.getParcelableArrayList(FILES_INFO);
            extensionOccurances = (savedInstanceState.containsKey(EXTN_INFO))?(HashMap<String, Integer>)savedInstanceState.getSerializable(EXTN_INFO): null;
            displayFilesList();
            displayExtensionInfo();
        }
    }

    @Override
    public void onBackPressed() {
        if(serviceIntent != null){
            unregisterReceiver(myReceiver);
            stopService(serviceIntent);
        }
        super.onBackPressed();
    }

    public void stopScan(View view) {
        if(serviceIntent != null && myReceiver != null){
            try{
                unregisterReceiver(myReceiver);
            }catch (Exception e){
            }
            stopService(serviceIntent);
        }
        ((Button)findViewById(R.id.start_scan)).setEnabled(true);
    }

    public void startScan(View view) {
        ((Button)findViewById(R.id.start_scan)).setEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CUSTOM_ACTION);
        registerReceiver(myReceiver, filter);
        startService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                if(avgFileSize != 0 && fileStats != null && extensionOccurances != null) {
                    intent.putExtra(Intent.EXTRA_TEXT, "Avg File Size is:" + avgFileSize + "\n" + fileStats.toString() + "\n" + extensionOccurances.toString());
                    intent.setType("text/plain");
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Information is not available", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(AVG_FILE_SIZE, avgFileSize);
        outState.putSerializable(EXTN_INFO, extensionOccurances);
        outState.putParcelableArrayList(FILES_INFO, fileStats);
    }
    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(CUSTOM_ACTION)){
                ((Button)findViewById(R.id.start_scan)).setEnabled(true);
                fileStats = intent.getParcelableArrayListExtra(FILES_INFO);
                extensionOccurances = (HashMap<String, Integer>)intent.getSerializableExtra(EXTN_INFO);
                avgFileSize = intent.getDoubleExtra(MainActivity.AVG_FILE_SIZE, 0);
                displayFilesList();
                displayAverageFileSize();
                displayExtensionInfo();

            }
        }
    }

    private void displayFilesList(){
        LinearLayout filesList = (LinearLayout) findViewById(R.id.files_list);
        if(fileStats != null && fileStats.size()>0) {
            View view = new View(MainActivity.this);
            view.setBackgroundColor(Color.BLACK);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
            filesList.addView(view, lineParams);
            (findViewById(R.id.files_list_title)).setVisibility(View.VISIBLE);
            for (int i = 0; i < fileStats.size(); i++) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView name = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 3;
                name.setText(fileStats.get(i).getName());
                layout.addView(name, params);
                TextView otherInfo = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                params1.weight = 1;
                params1.setMargins(10, 0, 0, 0);
                otherInfo.setText(String.valueOf((int) (fileStats.get(i).getSize() / (1024 * 1024))) + " MB");
                layout.addView(otherInfo, params1);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                filesList.addView(layout, rowParams);
                View view1 = new View(MainActivity.this);
                view1.setBackgroundColor(Color.BLACK);
                LinearLayout.LayoutParams lineParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
                filesList.addView(view1, lineParams1);
            }
        }
    }

    private void displayExtensionInfo(){
        LinearLayout frequentExtensionsList = (LinearLayout) findViewById(R.id.most_frequent_file_extensions);
        if(extensionOccurances != null && extensionOccurances.size()>0) {
            (findViewById(R.id.most_frequent_file_extensions_title)).setVisibility(View.VISIBLE);
            View view = new View(MainActivity.this);
            view.setBackgroundColor(Color.BLACK);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
            frequentExtensionsList.addView(view, lineParams);
            for (Map.Entry<String, Integer> extension : extensionOccurances.entrySet()) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                TextView name = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 3;
                name.setText(extension.getKey());
                layout.addView(name, params);
                TextView otherInfo = new TextView(MainActivity.this);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                params1.weight = 1;
                params1.setMargins(10, 0, 0, 0);
                otherInfo.setText(String.valueOf(extension.getValue()));
                layout.addView(otherInfo, params1);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                frequentExtensionsList.addView(layout, rowParams);
                View view1 = new View(MainActivity.this);
                view1.setBackgroundColor(Color.BLACK);
                LinearLayout.LayoutParams lineParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2);
                frequentExtensionsList.addView(view1, lineParams1);
            }
        }
    }

    private void displayAverageFileSize(){
        String info = "<b>Average File Size:</b> " + String.valueOf((int)avgFileSize /(1024*1024))+" MB";
        ((TextView)findViewById(R.id.avg_file_size)).setText(Html.fromHtml(info));
    }
}
