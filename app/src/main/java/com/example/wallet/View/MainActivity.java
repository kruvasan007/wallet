package com.example.wallet.View;

import static com.example.wallet.App.userData;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallet.Adpter.CardAdapter;
import com.example.wallet.Model.Card;
import com.example.wallet.R;
import com.example.wallet.ViewModel.CardViewModel;
import com.example.wallet.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.BusinessObjectMetadata;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CardAdapter cardsAdapter;
    private Session searchSession;
    private RecyclerView cardsRecycler;
    private LinearSnapHelper snapHelper;
    private CardViewModel cardViewModel;
    private ImageView addButton;
    private String CATEGORY = "all";
    int PERMISSION_ID = 44;
    FusedLocationProviderClient locationProviderClient;
    private ArrayList<Card> cardArrayList;
    private EditText input;
    private Location userLocation;
    private Card newCard;
    private int radius = 10000;
    private FusedLocationProviderClient locationClient;
    private ImageView addButtonBar;
    private LinearLayout magazineTypeButton;
    private ImageView aboutButton;
    private LinearLayout restaurantTypeBytton;
    private LinearLayout allTypeButton;
    private ImageView updateButton;
    private MapView mapview;
    private Point userPoint;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        //initialization MapKit
        MapKitFactory.initialize(this);
        SearchFactory.initialize(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapview = binding.mapView;
        userPoint = new Point(userData.userLocation.getLatitude(), userData.userLocation.getLongitude());
        setUserPoint();
        mapview.getMap().move(
                new CameraPosition(userPoint, 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);

        initRecycler();
        addButton = binding.addButton;
        addButtonBar = binding.addButtonBottom;
        magazineTypeButton = binding.marketTypeButton;
        restaurantTypeBytton = binding.restaurantTypeButton;
        allTypeButton = binding.allTypeButton;
        aboutButton = binding.faqButton;

        addButton.setOnClickListener(v -> {
            newCard = new Card();
            startScanningBarcode();
        });
        addButtonBar.setOnClickListener(v -> {
            newCard = new Card();
            startScanningBarcode();
        });
        magazineTypeButton.setOnClickListener(v -> {
            CATEGORY = "??????????????";
            getTasks(CATEGORY);
        });
        restaurantTypeBytton.setOnClickListener(v -> {
            CATEGORY = "????????????????";
            getTasks(CATEGORY);
        });
        allTypeButton.setOnClickListener(v -> {
            CATEGORY = "all";
            getTasks(CATEGORY);
        });
        aboutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            onStop();
        });
        updateButton = binding.updateButton;
        updateButton.setOnClickListener(v -> {
            cardArrayList.sort(Comparator.comparing(Card::getDistance));
            cardsAdapter.notifyDataSetChanged();
        });
    }

    private void setUserPoint() {
        mapview.getMap().getMapObjects().addPlacemark(userPoint).setIcon(ImageProvider.fromBitmap(getIconFromDrawables(getDrawable(R.drawable.user))));
    }

    private Bitmap getIconFromDrawables(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void getNearbyOrganization(Card card) {
        SearchManager searchManager = SearchFactory.getInstance().createSearchManager(
                SearchManagerType.ONLINE);
        Point userPositionPoint = new Point(userData.userLocation.getLatitude(), userData.userLocation.getLongitude());
        Geometry point = Geometry.fromPoint(userPositionPoint);
        SearchOptions options = new SearchOptions().setGeometry(true).setUserPosition(userPositionPoint);
        searchSession = searchManager.submit(card.getNameCard(), point, options, new Session.SearchListener() {
            @Override
            public void onSearchResponse(@NonNull Response response) {
                List<GeoObjectCollection.Item> sortResponse = response.getCollection().getChildren();
                if (sortResponse.size() != 0) {
                    if (Objects.requireNonNull(sortResponse.get(0).getObj()).getMetadataContainer().getItem(BusinessObjectMetadata.class).getDistance() != null) {
                        card.setDistance(Objects.requireNonNull(sortResponse.get(0).getObj().getMetadataContainer().getItem(BusinessObjectMetadata.class).getDistance()).getValue());
                        mapview.getMap().getMapObjects().addPlacemark(Objects.requireNonNull(sortResponse.get(0).getObj()).getGeometry().get(0).getPoint()).setIcon(ImageProvider.fromBitmap(getIconFromDrawables(getDrawable(R.drawable.marker))));
                    } else
                        card.setDistance(100000.0);
                } else {
                    card.setDistance(100000.0);
                }
            }

            @Override
            public void onSearchError(@NonNull Error error) {
                Log.e("E", "ERROR GET DATA");
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        userData.userLocation = location;
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            userData.userLocation = locationResult.getLastLocation();
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                               @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private void initRecycler() {
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cardArrayList = new ArrayList<>();
        getTasks(CATEGORY);
        cardsRecycler = binding.cardRecycler;
        cardsAdapter = new CardAdapter(this, cardArrayList);
        cardsRecycler.setAdapter(cardsAdapter);
        snapHelper = new LinearSnapHelper();
        cardsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View newSnapPosition = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (newSnapPosition != null && dy < 2) {
                    if (layoutManager.getPosition(newSnapPosition) != cardsAdapter.getLastSnapPosition()) {
                        cardsAdapter.setSnapPosition(layoutManager.getPosition(newSnapPosition));
                    }
                }
            }
        });
        cardsRecycler.post(() -> cardsAdapter.notifyDataSetChanged());
        cardsRecycler.setLayoutManager(layoutManager);
        cardsRecycler.setItemAnimator(new DefaultItemAnimator());
        snapHelper.attachToRecyclerView(cardsRecycler);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                cardViewModel.deleteCard(cardArrayList.get(viewHolder.getAdapterPosition()).getNameCard());
                cardArrayList.remove(viewHolder.getAdapterPosition());
                cardsAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                mapview.getMap().getMapObjects().clear();
                getTasks(CATEGORY);
                setUserPoint();
            }
        }).attachToRecyclerView(cardsRecycler);

    }

    private void getTasks(String type) {
        cardViewModel.listenActivitiesResponse(type).observe(this, items -> {
            cardArrayList.clear();
            cardArrayList.addAll(items);
            for (Card card : items) {
                getNearbyOrganization(card);
            }
            cardsAdapter.notifyDataSetChanged();
        });
    }

    private void startScanningBarcode() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this).setOrientationLocked(true);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                newCard.setBarcode(result.getContents());
                Toast.makeText(this, "Scanned!", Toast.LENGTH_SHORT).show();
                getTypeOfCard(newCard);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getTypeOfCard(Card card) {
        final CharSequence[] charSequence = new CharSequence[]{"??????????????", "????????????????", "????????????"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("?????? ??????????")
                .setSingleChoiceItems(charSequence, 0, (dialog, which) -> {
                })
                .setPositiveButton("??????????????", (dialog, which) -> {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    card.setType(String.valueOf(charSequence[selectedPosition]));
                    dialog.dismiss();
                    getNameOfCard();
                })
                .create().show();
    }

    private void getNameOfCard() {
        input = new EditText(this);
        input.setMaxWidth(15);
        input.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        new MaterialAlertDialogBuilder(this)
                .setTitle("?????? ??????????")
                .setView(input)
                .setPositiveButton("ok", (dialog, which) -> {
                    newCard.setNameCard(String.valueOf(input.getText()));
                    cardViewModel.createCard(newCard);
                })
                .show();
    }

    @Override
    protected void onStop() {
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        MapKitFactory.getInstance().onStart();
        super.onStart();
    }
}
