package lk.javainstitute.techstoreadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.javainstitute.techstoreadmin.adapter.OrderItemAdapter;
import lk.javainstitute.techstoreadmin.model.Order;
import lk.javainstitute.techstoreadmin.model.Product;

public class OrderItemsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker_current;
    private FirebaseFirestore firestore;
    private ArrayList<Product> items;
    private String orderId;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_items);

        firestore = FirebaseFirestore.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        String orderid = getIntent().getExtras().getString("orderID");

        TextView oId = findViewById(R.id.orderId);
        TextView odatetime = findViewById(R.id.datetime);
        TextView oemail = findViewById(R.id.email);
        TextView omobile = findViewById(R.id.phone);
        TextView address = findViewById(R.id.address);
        TextView total = findViewById(R.id.price);
        TextView status = findViewById(R.id.status);


//      ---------------------- LOAD ITEMS -----------------------------------
        items = new ArrayList<>();
        RecyclerView categoryView = findViewById(R.id.orderItemRecyclerView);
        OrderItemAdapter ordersAdapter = new OrderItemAdapter(items, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        categoryView.setLayoutManager(linearLayoutManager);
        categoryView.setAdapter(ordersAdapter);

        firestore.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                Order order = snapshot1.toObject(Order.class);

                                if (order.getId().equals(orderid)) {
                                    String orderId = snapshot1.getId();

                                    oId.setText(orderid.toString());
                                    oemail.setText(order.getEmail());
                                    omobile.setText(order.getMobile());
                                    address.setText(order.getAddress());
                                    status.setText(String.valueOf((order.getDeliver_status())).equals("1") ? "Delivered" : "Pending");
                                    odatetime.setText(order.getDate_time());
                                    total.setText("Rs." + order.getTotal() + ".00");

                                    latitude = Double.parseDouble(order.getLatitude());
                                    longitude = Double.parseDouble(order.getLongitude());

                                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                    mapFragment.getMapAsync(OrderItemsActivity.this);


                                    firestore.collection("orders/" + orderId + "/products").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot snapshot2 : task.getResult()) {
                                                            Product item = snapshot2.toObject(Product.class);
                                                            items.add(item);
                                                        }
                                                    }
                                                    ordersAdapter.notifyDataSetChanged();
                                                }
                                            });
                                    break;
                                }

                            }

                        }
                    }
                });

        findViewById(R.id.gmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String geoUri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Location+Marker)";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                intent.setPackage("com.google.android.apps.maps");

                PackageManager packageManager = getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please install google map", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void setDeliverLocation() {
        if (checkPermissions()) {
            LatLng latLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            MarkerOptions options = new MarkerOptions().title("Deliver Location").position(latLng);
            marker_current = map.addMarker(options);
            moveCamera(latLng);
        }
    }


    public void moveCamera(LatLng latLng) {
        CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(15f).build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        if (checkPermissions()) {
            map.setMyLocationEnabled(true);
            setDeliverLocation();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }


    private boolean checkPermissions() {
        boolean permission = false;
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permission = true;
        }

        return permission;
    }


}