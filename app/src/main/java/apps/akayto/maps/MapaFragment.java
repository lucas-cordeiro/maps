package apps.akayto.maps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.wearable.Asset;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import apps.akayto.maps.Helper.Convert;
import apps.akayto.maps.Helper.Maps;
import apps.akayto.maps.Helper.Permissoes;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private String[] permissoes = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private LocationManager locationManager;
    private boolean first;

    public MapaFragment() {
        // Required empty public constructor
        first = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);

        setHasOptionsMenu(true);

        //Toast.makeText(getContext(), "HardwareAccelerated: "+view.isHardwareAccelerated(), Toast.LENGTH_SHORT).show();

        if (getContext() != null)
            //Google Play Services
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            Toast.makeText(getContext(), "Connectado!", Toast.LENGTH_LONG);
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(LocationServices.API)
                    .build();


        //Objeto responsável por gerenciar a localização do usuário
        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mMap != null) {
            switch ((item.getItemId())) {
                case R.id.action_location:
                    LatLng location = new LatLng(-23.5489, -46.6388);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                    break;

                case R.id.action_location_direction:
                    Toast.makeText(getContext(), "Mostra a rota", Toast.LENGTH_SHORT).show();
                    LatLng latLng1 = mMap.getCameraPosition().target;
                    LatLng latLng2 = new LatLng(-23.5489, -46.6388);

                    PolylineOptions line = new PolylineOptions();
                    line.add(latLng1);
                    line.add(latLng2);
                    line.color(Color.BLUE);

                    Polyline polyline = mMap.addPolyline(line);
                    polyline.setGeodesic(true);

                    break;

                case R.id.action_mapa_normal:
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;

                case R.id.action_mapa_satelite:
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;

                case R.id.action_mapa_terreno:
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    break;

                case R.id.action_mapa_hibrido:
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                case R.id.action_zoom_in:
                    Toast.makeText(getContext(), "+ Zoom", Toast.LENGTH_SHORT).show();
                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
                    break;

                case R.id.action_zoom_out:
                    Toast.makeText(getContext(), "- Zoom", Toast.LENGTH_SHORT).show();
                    mMap.animateCamera(CameraUpdateFactory.zoomOut());
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean aceitou = true;
        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Permissões");
                builder.setMessage("É necessário aceitar as permissões");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                aceitou = false;
                break;
            }
        }

        if (aceitou)
            localizacaoUsuario();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("Localização", "onMapReady");
        //Validar Permissões
        if (Permissoes.validarPermissoes(permissoes, this, 1)) {
            localizacaoUsuario();
        }
    }


    private void localizacaoUsuario() {
        Log.d("Localização", "localizacaoUsuario");
        /*Parâmetros
         * 1-Provedor da localização
         * 2-Tempo mínimo entre atualizações de localização
         * 3-Distância mínima entre atualizações e localização
         * 4-Location Linstener*/




        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("Localização", "check localizacaoUsuario");
            long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
            long MIN_TIME_BW_UPDATES = 1000 * 10;
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this);

        }else{
            Log.d("Localização", "no check localizacaoUsuario");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Localização", "location: "+location.toString());
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        mMap.clear();
        LatLng localUsuario = new LatLng(latitude, longitude);
        Maps.adicionarMarcador(mMap, localUsuario, "Usuário", first);

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {

            /*
            Por lat e lon
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
             *"Unnamed Road,
             * 20300 Pan de Azúcar,
             * Departamento de Maldonado,
             * Uruguai"],
             * feature=Unnamed Road,
             * admin=Departamento de Maldonado,
             * sub-admin=null,
             * locality=Pan de Azúcar,
             * thoroughfare=Unnamed Road,
             * postalCode=20300,
             * countryCode=UY,
             * countryName=Uruguai,
             * hasLatitude=true,
             * latitude=-34.7885212,
             * hasLongitude=true,l
             * ongitude=-55.233568399999996,
             * phone=null,
             * url=null,
             * extras=null
             * */

            //Com um endereço
            List<Address> addresses = geocoder.getFromLocationName("15060-280", 1);
            /*
            * "R. da Trindade,
             * Vila Ideal,
              * São José do Rio Preto - SP,
              * 15060-280, Brasil",
              * feature=15060-280,
              * admin=São Paulo,
              * sub-admin=São José do Rio Preto,
              * locality=null,
              * thoroughfare=Rua da Trindade,
              * postalCode=15060-280,
              * countryCode=BR,
              * countryName=Brasil,
              * hasLatitude=true,
              * latitude=-20.8010454,
              * hasLongitude=true,
              * longitude=-49.3617,
              * phone=null,
              * url=null,
              * extras=null]
            * */
            if(addresses!=null && addresses.size() > 0)
            Log.d("Localização", "Endereço: "+addresses.get(0).toString());


        } catch (IOException e) {
            e.printStackTrace();
        }
        first = false;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
