package com.example.beaconapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BlueCatsSDK;
import com.example.beaconapp.databinding.FragmentProductBinding;
import com.example.beaconapp.models.Product;
import com.example.beaconapp.models.User;
import com.example.beaconapp.models.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductFragment extends Fragment {

    FragmentProductBinding binding;

    ProductAdapter productAdapter;

    ArrayList<Product> productArrayList = new ArrayList<>();

    ApplicationPermissions mPermissions;

    BCBeaconManager beaconManager = new BCBeaconManager();

    static String current_beacon = null;

    static String new_beacon = null;

    static int count = 0;

    static HashMap<String, String> data = new HashMap<>(); // serial number - region

    IProducts am;

    User user;

    static {
        // Add beacons data
        data.put("B100", "produce");
        data.put("B200", "grocery");
        data.put("B300", "lifestyle");
    }

    private final BCBeaconManagerCallback mBeaconManagerCallback = new BCBeaconManagerCallback()
    {
        @Override
        public void didRangeBlueCatsBeacons( final List<BCBeacon> beacons ) {
            super.didRangeBlueCatsBeacons(beacons);

            if(beacons.size() <= 0){ // if no beacons detected then change to global
                if(current_beacon != null){
                    current_beacon = null;
                    updateProducts(null);
                }
                return;
            }

            HashMap<BCBeacon.BCProximity, ArrayList<String>> groups = new HashMap<>();
            BCBeacon.BCProximity min = BCBeacon.BCProximity.BC_PROXIMITY_FAR;

            for(BCBeacon beacon : beacons){ // find nearest beacon and minimum distance proximity
                ArrayList<String> group = groups.getOrDefault(beacon.getProximity(), new ArrayList<>());
                group.add(beacon.getName());
                groups.put(beacon.getProximity(), group);

                Log.d("ddd", "didRangeBlueCatsBeacons: " + beacon.getName() + " - " + beacon.getProximity());
                if(beacon.getProximity().getValue() < min.getValue()) min = beacon.getProximity();
            }

            if(min == BCBeacon.BCProximity.BC_PROXIMITY_FAR){ // if min distance is far, then change to global
                if(current_beacon != null){
                    current_beacon = null;
                    updateProducts(null);
                }
                return;
            }

            ArrayList<String> closest_beacons = groups.get(min);
            if(current_beacon == null || !closest_beacons.contains(current_beacon)) { // if current closest beacon is already in nearest beacons then skip update

                if(new_beacon != null && !closest_beacons.contains(new_beacon)){
                    count = 0;
                    new_beacon = closest_beacons.get(0);
                    return;
                }

                if(new_beacon == null){
                    count = 0;
                    new_beacon = closest_beacons.get(0);
                }

                if(count++ > 4){ // only update if count is more than 4 else skip
                    current_beacon = new_beacon;
                    new_beacon = null;
                    count = 0;
                    updateProducts(data.get(current_beacon)); // change product list

                }


            }
        }

    };

    @Override
    public void onResume() { // 2
        super.onResume();
        BlueCatsSDK.didEnterForeground();

        beaconManager.registerCallback(mBeaconManagerCallback);
    }

    @Override
    public void onPause() { // 3
        super.onPause();

        BlueCatsSDK.didEnterBackground();

        beaconManager.unregisterCallback(mBeaconManagerCallback);
    }

    public void updateProducts(String region){
        am.getProducts(new MainActivity.Return() {
            @Override
            public void response(@NonNull String response) {

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Product[] products = gson.fromJson(response, Product[].class);

                productArrayList.clear();
                productArrayList.addAll(Arrays.asList(products));
                productAdapter.notifyDataSetChanged();


                if(current_beacon == null)
                    binding.textView.setText("Global");
                else
                    binding.textView.setText("Aisle " + current_beacon);

                if(region != null) am.alert("Updated List!");
            }

            @Override
            public boolean showDialog() {
                return true;
            }

            @Override
            public void error(@NonNull String response) {
            }
        }, region);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) { // 1

        getActivity().setTitle(R.string.products);

        mPermissions = new ApplicationPermissions(getActivity());
        mPermissions.verifyPermissions();

        final Map<String, String> options = new HashMap<>();
        options.put(BlueCatsSDK.BC_OPTION_DISCOVER_BEACONS_NEARBY, "true");
        BlueCatsSDK.setOptions(options);

        BlueCatsSDK.startPurringWithAppToken(getContext(), Utils.BEACON_TOKEN);

        binding = FragmentProductBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        user = am.getUser();

        am.profile(new MainActivity.Return() {
            @Override
            public void response(@NonNull String response) {

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String token = user.getToken();

                user = gson.fromJson(response, User.class);

                binding.name.setText(user.getFullname());
                user.setToken(token);
                am.setUser(user);
            }

            @Override
            public boolean showDialog() {
                return false;
            }

            @Override
            public void error(@NonNull String response) {
            }
        });

        binding.name.setText(user.getFullname());

        binding.productView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.productView.setLayoutManager(llm);

        productAdapter = new ProductAdapter(user, productArrayList);
        binding.productView.setAdapter(productAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.productView.getContext(), llm.getOrientation());
        binding.productView.addItemDecoration(dividerItemDecoration);

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.setUser(null);
                am.sendLoginView();
            }
        });

        updateProducts(null);

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IProducts) {
            am = (IProducts) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK) {
            if (mPermissions != null) {
                mPermissions.verifyPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mPermissions != null) {
            mPermissions.onRequestPermissionResult(requestCode, permissions, grantResults);
        }
    }

    public interface IProducts {
        void alert(String msg);
        User getUser();
        void setUser(User user);
        void sendLoginView();
        void profile(MainActivity.Return response);
        void getProducts(MainActivity.Return response, String region);
    }
}