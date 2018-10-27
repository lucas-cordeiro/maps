package apps.akayto.maps.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import apps.akayto.maps.R;


public class Maps {

    public static Marker adicionarMarcador(GoogleMap map, LatLng latLng, String title, Bitmap bitmap, final Fragment fragment) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title(title).snippet("App Map");

        if (bitmap != null)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        Marker marker = map.addMarker(markerOptions);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                CardView cardView = (CardView) this.getInfoContents(marker);
                //cardView.setBackgroundColor(Color.WHITE);
                return cardView;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View view = fragment.getLayoutInflater().inflate(R.layout.map_cardview, null);
                CardView cardView = view.findViewById(R.id.mapCardView);
                TextView textViewTitle = view.findViewById(R.id.titleMarker);
                textViewTitle.setText(marker.getTitle());

                TextView textViewSnippet = view.findViewById(R.id.snippetMarker);

                if (marker.getSnippet() != null && marker.getSnippet().length() > 0) {
                    textViewSnippet.setText(marker.getSnippet());
                } else {
                    textViewSnippet.setVisibility(View.GONE);
                }

                return cardView;
            }
        });

        return marker;
    }

    public static void adicionarMarcador(GoogleMap map, LatLng latLng, String title) {
        map.addMarker(new MarkerOptions().position(latLng).title(title));
    }

    public static void adicionarMarcador(GoogleMap map, LatLng latLng, String title, boolean zoomCamera) {
        map.addMarker(new MarkerOptions().position(latLng).title(title));
        if(!zoomCamera)
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }


}
