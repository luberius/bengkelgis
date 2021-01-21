package com.example.bengkelgis_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListNearbyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DataAdapter> datas;
    private OnItemClickListener onItemClickListener;

    public ListNearbyAdapter(Context context, ArrayList<DataAdapter> datas, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.datas = datas;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_nearby, parent, false);
            TextView textName = convertView.findViewById(R.id.textName);
            TextView textAddr = convertView.findViewById(R.id.textAddr);
            TextView textDistance = convertView.findViewById(R.id.textDistance);

            textName.setText(datas.get(position).getImageTitle());
            textAddr.setText(datas.get(position).getAlamat());

            double distance = datas.get(position).getDistance();

            String labelDistance = round(distance) + " m";
            if (distance > 999) {
                labelDistance = round((distance / 1000)) + " km";
            }

            textDistance.setText(labelDistance);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(datas.get(position), position);
            }
        });

        return convertView;
    }

    public static double round(double value) {
        double scale = Math.pow(10, 2);
        return Math.round(value * scale) / scale;
    }

    public interface OnItemClickListener {
        void onItemClick(DataAdapter data, int pos);
    }
}
