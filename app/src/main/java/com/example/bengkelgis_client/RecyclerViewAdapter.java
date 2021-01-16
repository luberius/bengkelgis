package com.example.bengkelgis_client;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;

    String latitude, longitude;

    List<DataAdapter> dataAdapters;

    ImageLoader imageLoader;

    public RecyclerViewAdapter(List<DataAdapter> getDataAdapter, Context context){

        super();
        this.dataAdapters = getDataAdapter;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Viewholder, int position) {

        DataAdapter dataAdapterOBJ =  dataAdapters.get(position);

        imageLoader = ImageAdapter.getInstance(context).getImageLoader();

        imageLoader.get(dataAdapterOBJ.getImageUrl(),
                ImageLoader.getImageListener(
                        Viewholder.VollyImageView,//Server Image
                        R.mipmap.ic_launcher,//Before loading server image the default showing image.
                        android.R.drawable.ic_dialog_alert //Error image if requested image dose not found on server.
                )
        );

        Viewholder.VollyImageView.setImageUrl(dataAdapterOBJ.getImageUrl(), imageLoader);

        Viewholder.ImageTitleTextView.setText(dataAdapterOBJ.getImageTitle());

        Viewholder.Telp.setText(dataAdapterOBJ.getTelp());

        Viewholder.Email.setText(dataAdapterOBJ.getEmail());
        Viewholder.Alamat.setText(dataAdapterOBJ.getAlamat());
        Viewholder.Hari.setText(dataAdapterOBJ.getHari());
        Viewholder.Jam.setText(dataAdapterOBJ.getJam());
        Viewholder.Jamt.setText(dataAdapterOBJ.getJamt());
        longitude = dataAdapterOBJ.getLongitude();
        latitude = dataAdapterOBJ.getLatitude();

    }

    @Override
    public int getItemCount() {

        return dataAdapters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView ImageTitleTextView,Telp,Email,Alamat,Hari,Jam,Jamt;
        public Button BtnDirection;
        public NetworkImageView VollyImageView ;

        public ViewHolder(View itemView) {

            super(itemView);
            context = itemView.getContext();
            ImageTitleTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView) ;

            Telp = (TextView) itemView.findViewById(R.id.telp) ;
            Email = (TextView) itemView.findViewById(R.id.email) ;
            Alamat = (TextView) itemView.findViewById(R.id.alamat) ;
            Hari = (TextView) itemView.findViewById(R.id.hari) ;
            Jam = (TextView) itemView.findViewById(R.id.jam_buka) ;
            Jamt = (TextView) itemView.findViewById(R.id.jam_tutup) ;

            BtnDirection = (Button)itemView.findViewById(R.id.bSearch);
            BtnDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent;
                    //Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/place/" + Alamat.getText().toString());
                    //Uri gmmIntentUri = Uri.parse("https://www.google.com/maps?q="+latitude+","+longitude);
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
                    intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    intent.setPackage("com.google.android.apps.maps");

                    context.startActivity(intent);


                    Log.d("TAG", "onClick: " + Alamat.getText().toString());
                }
            });

            VollyImageView = (NetworkImageView) itemView.findViewById(R.id.VolleyImageView) ;
        }
    }
}
