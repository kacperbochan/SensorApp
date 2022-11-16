package com.example.sensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.app.PendingIntent.getActivity;
import static com.example.sensorapp.SensorDetailsActivity.EXTRA_SENSOR_TYPE_PARAMETAR;

public class SensorActivity extends AppCompatActivity {

    public static String KEY_EXTRA_SENSOR_TYPE;
    public static final String KEY_ARE_VISIBLE = "areVisible";
    public static final int SENSOR_DETAILS_ACTIVITY_REQUEST_CODE = 1;
    public static final int LOCATION_ACTIVITY_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private SensorManager sensorManager;
    private List<Sensor> sensorList;
    private SensorAdapter adapter;
    private boolean subtitleVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);

        recyclerView = findViewById(R.id.sensor_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        if (adapter == null){
            adapter = new SensorAdapter(sensorList);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        if(savedInstanceState != null){
            subtitleVisible = savedInstanceState.getBoolean(KEY_ARE_VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_ARE_VISIBLE, subtitleVisible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sensors_menu, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (subtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitles);
        } else {
            subtitleItem.setTitle(R.string.show_subtitles);
        }
        this.invalidateOptionsMenu();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.show_subtitle:
                subtitleVisible = !subtitleVisible;
                int count = sensorList.size();
                String subtitle = getString(R.string.sensors_count, count);
                if(!subtitleVisible){
                    subtitle = null;
                }
                this.getSupportActionBar().setSubtitle(subtitle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SensorHolder extends RecyclerView.ViewHolder {

        private Sensor sensor;
        private TextView nameTextView;
        private ImageView sensorImg;

        public SensorHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.sensor_list_item, parent, false));

            sensorImg = itemView.findViewById(R.id.sensor_item_icon);
            nameTextView = itemView.findViewById(R.id.sensor_item_name);
        }

        public void bind(Sensor sensor){
            this.sensor = sensor;

            nameTextView.setText(sensor.getName());
            View itemContainer = itemView.findViewById((R.id.list_item_sensor));
            if(sensor.getType()==Sensor.TYPE_LIGHT || sensor.getType()==Sensor.TYPE_RELATIVE_HUMIDITY){
                itemContainer.setBackgroundColor(getResources().getColor(R.color.blue));
                itemContainer.setOnClickListener(v-> {
                    Intent intent = new Intent(SensorActivity.this, SensorDetailsActivity.class);
                    intent.putExtra(EXTRA_SENSOR_TYPE_PARAMETAR, sensor.getType());
                    startActivityForResult(intent, SENSOR_DETAILS_ACTIVITY_REQUEST_CODE);
                });
            }
            if(sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                itemContainer.setBackgroundColor(getResources().getColor(R.color.blue));
                itemContainer.setOnClickListener(v-> {
                    Intent intent = new Intent(SensorActivity.this, LocationActivity.class);
                    startActivityForResult(intent, LOCATION_ACTIVITY_REQUEST_CODE);
                });
            }

            switch(sensor.getType()){
                case 1:
                    sensorImg.setImageResource(R.drawable.ic_sensor_accelerometer);
                    break;
                case 4:
                    sensorImg.setImageResource(R.drawable.ic_sensor_gyroscope);
                    break;
                case 5:
                    sensorImg.setImageResource(R.drawable.ic_sensor_light);
                    break;
                case 6:
                    sensorImg.setImageResource(R.drawable.ic_sensor_pressure);
                    break;
                case 8:
                    sensorImg.setImageResource(R.drawable.ic_sensor_proximity);
                    break;
                case 12:
                    sensorImg.setImageResource(R.drawable.ic_sensor_humidity);
                    break;
                case 18:
                    sensorImg.setImageResource(R.drawable.ic_sensor_step);
                    break;
                default:
                    sensorImg.setImageResource(R.drawable.ic_sensor_def);
                    break;
            }
            String log = String.format("Name: %s | Manufacturer: %s | MaxValue: %f", sensor.getName(), sensor.getVendor(), sensor.getMaximumRange());
            Log.i("SENSOR |", log);
        }
    }

    private class SensorAdapter extends RecyclerView.Adapter<SensorHolder> {

        private List<Sensor> sensors;

        public SensorAdapter(List<Sensor> sensors){ this.sensors = sensors; }

        @NonNull
        @Override
        public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new SensorHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
            Sensor sensor = sensors.get(position);
            holder.bind(sensor);
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }
    }
}
