package com.example.tiresportapp.Vista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiresportapp.Casodeuso.ProductoActionCase;
import com.example.tiresportapp.Controlador.AdaptadorProductos;
import com.example.tiresportapp.Controlador.AdaptadorSucursales;
import com.example.tiresportapp.Controlador.CargaTablas;
import com.example.tiresportapp.Modelo.Sucursal;
import com.example.tiresportapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.osmdroid.views.MapController;

import java.util.ArrayList;

public class Sucursales extends AppCompatActivity implements OnMapReadyCallback {

    //declara un boton para contacto
    private Button contacto;

    Spinner spinner;
    MapView mapView;
    MapController myMapController;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    GoogleMap mMap;
    ArrayList<Sucursal> sucursales = new ArrayList<>();
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    boolean accesoConcedido;
    LatLng miUbicacion;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucursales);
        ActionBar barraMenu = getSupportActionBar();

        barraMenu.setDisplayShowHomeEnabled(true);
        barraMenu.setLogo(R.mipmap.ic_launcher);
        barraMenu.setTitle("Sucursales");
        barraMenu.setSubtitle("Elige una Sucursal");
        barraMenu.setDisplayUseLogoEnabled(true);
        barraMenu.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#313d8c")));


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) findViewById(R.id.mapa);
        mapView.onCreate(mapViewBundle);

        new ConsultaSucurasles().execute();

    }

    private boolean verificarAccesoGoggle() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result== ConnectionResult.SUCCESS){

            return  true;
        }else if(googleApiAvailability.isUserResolvableError(result)){

            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });
            dialog.show();
        }

        return false;
    }

    private void verificarPermisos() {

        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        accesoConcedido=true;

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(),"");
                        intent.setData(uri);
                        startActivity(intent);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        permissionToken.continuePermissionRequest();

                    }
                }).check();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menuPrincipal) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimnu, menuPrincipal);
        MenuItem carrito = menuPrincipal.findItem(R.id.carrito);
        carrito.setVisible(false);
        MenuItem buscar = menuPrincipal.findItem(R.id.buscar);
        buscar.setVisible(false);
        MenuItem sucursales = menuPrincipal.findItem(R.id.botonSucursales);
        sucursales.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.botonProductos:
                Intent principal = new Intent(getApplicationContext(), Catalogo.class);
                startActivity(principal);
                return true;

            case R.id.botonServicios:
                Intent servicios = new Intent(getApplicationContext(), com.example.tiresportapp.Vista.servicios.class);
                startActivity(servicios);
                return true;

            case R.id.botonSucursales:
                Intent sucursales = new Intent(getApplicationContext(), Sucursales.class);
                startActivity(sucursales);
                return true;

            case R.id.carrito:
                Toast.makeText(getApplicationContext(),
                        "El carrito se habilitara pronto", Toast.LENGTH_LONG).show();
                return true;

            case R.id.cambiMapa:
                seleccionTipoDeMapa();
                return true;

            default:
                return true;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {


        mMap = map;
        seleccionTipoDeMapa();
        mMap.getUiSettings().isMapToolbarEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);


        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                if(miUbicacion!=null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(miUbicacion, 15);
                    mMap.animateCamera(cameraUpdate);
                }else {
                    Toast.makeText(Sucursales.this, "No es posible encontrar tu ubicacion", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {

                LatLng ubicacion = new LatLng(sucursales.get(pos).getLatitud(), sucursales.get(pos).getLongitud());
                mMap.addMarker(new MarkerOptions().position(ubicacion).title(sucursales.get(pos).getCiudad()));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ubicacion,20);
                mMap.animateCamera(cameraUpdate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void verificarGps() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(4000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);
        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(Sucursales.this, "Gps disponible", Toast.LENGTH_SHORT).show();
                    actualizarUbicacion();

                } catch (ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(Sucursales.this, 101);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    }
                    if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {

                        Toast.makeText(Sucursales.this, "Configuracion no disponible", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

    }

    private void actualizarUbicacion() {


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(Sucursales.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                miUbicacion = new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude());


            }
        }, Looper.getMainLooper());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            if(resultCode==RESULT_OK){
                Toast.makeText(this, "Gps habilitado", Toast.LENGTH_SHORT).show();
            }if(resultCode==RESULT_CANCELED){
                Toast.makeText(this, "No es posible habilitar gps", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public  class ConsultaSucurasles extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Sucursales.this);
            progressDialog.setMessage("Cargando Información...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                Thread.sleep(1000);
                sucursales= CargaTablas.sucursalesCarga();

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            spinner = (Spinner) findViewById(R.id.spinnerSucursales);
            AdaptadorSucursales adapter = new AdaptadorSucursales(getApplicationContext(), sucursales);
            adapter.setDropDownViewResource(R.layout.itemsucursal);
            spinner.setAdapter(adapter);

            verificarPermisos();


            if(verificarAccesoGoggle()) {

                Toast.makeText(Sucursales.this, "Servicios de google disponibles", Toast.LENGTH_SHORT).show();
                mapView.getMapAsync(Sucursales.this);
                if(accesoConcedido){

                    verificarGps();

                }
            }else {
                Toast.makeText(Sucursales.this, "Servicios de google no disponibles", Toast.LENGTH_SHORT).show();
            }
            progressDialog.hide();

        }
    }

    private static final CharSequence[] MAP_TYPE_ITEMS =
            {"Carretera", "Hibrido", "Satelital", "Terreno"};

    private void seleccionTipoDeMapa() {

        final String fDialogTitle = "Selecciona el tipo de mapa";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(fDialogTitle);


        int checkItem = mMap.getMapType() - 1;


        builder.setSingleChoiceItems(
                MAP_TYPE_ITEMS,
                checkItem,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {



                        switch (item) {
                            case 1:
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                break;
                            case 2:
                                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                break;
                            case 3:
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                                break;
                            default:
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        }
                        dialog.dismiss();
                    }
                }
        );


        AlertDialog fMapTypeDialog = builder.create();
        fMapTypeDialog.setCanceledOnTouchOutside(true);
        fMapTypeDialog.show();
    }
}



