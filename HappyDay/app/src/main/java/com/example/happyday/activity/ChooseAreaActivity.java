package com.example.happyday.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happyday.R;
import com.example.happyday.db.HappyDayDB;
import com.example.happyday.model.Location;
import com.example.happyday.util.HttpCallbackListener;
import com.example.happyday.util.HttpUtil;
import com.example.happyday.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoqing on 2015/7/1.
 */
public class ChooseAreaActivity extends Activity {
    private ListView listView;
    private TextView titleText;

    private ArrayAdapter<String> adapter;
    private HappyDayDB happyDayDB;
    private Location selectedLocation = null;

    private List<String> datalist = new ArrayList<String>();
    private List<Location> locationList = new ArrayList<Location>();
    private ProgressDialog progressDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_area);

        // 将区域列表在list_view中显示出来
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title);
        happyDayDB = HappyDayDB.getInstance(this);
        selectedLocation = null;

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datalist);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedLocation = locationList.get(position);

                if (selectedLocation.getType() == Location.PROVINCE_TYPE) {
                    querySubLocations(Location.CITY_TYPE);
                }else if (selectedLocation.getType() == Location.CITY_TYPE){
                    querySubLocations(Location.COUNTRY_TYPE);
                } else if (selectedLocation.getType() == Location.COUNTRY_TYPE){
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("countryCode", selectedLocation.getCode());
                    startActivity(intent);
                }

            }
        });

        // 查询省份信息
        querySubLocations(Location.PROVINCE_TYPE);

    }


    private void querySubLocations(int type){

        int parent_id = -1;
        String code = null;

        if (selectedLocation != null){
            parent_id = selectedLocation.getId();
            code = selectedLocation.getCode();
        }

        locationList = happyDayDB.loadLocations(type, parent_id); // 先查db，如果db无法找到，就去服务器上进行相关的查询

        Log.d("HappyDay", "Get locationList Size - " + locationList.size());

        if (locationList.size() > 0){
            datalist.clear();
            for (Location location:locationList){
                datalist.add(location.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

            if (type == Location.PROVINCE_TYPE){
                titleText.setText("China");
            }else{
                titleText.setText(selectedLocation.getName());
            }
        }
        else{
            queryFromServer(code, type, parent_id);
        }


    }

    // 从服务器上获取相关内容
    private void queryFromServer(final String code, final int type, final int parent_id){
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        Log.d("HappyDay", "Get Address - " + address);

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                closeProgressDialog();
                boolean result = false;
                Log.d("HappyDay", "Get Response - " + response);
                result = Utility.handleLocationsResponse(happyDayDB, response, parent_id, type);

                // 后台服务在尝试获取到相关的城市信息
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            querySubLocations(type);
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "load failure", Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });

    }

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }


}
