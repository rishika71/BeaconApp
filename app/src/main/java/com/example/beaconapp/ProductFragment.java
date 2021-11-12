package com.example.beaconapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.beaconapp.databinding.FragmentProductBinding;
import com.example.beaconapp.models.Product;
import com.example.beaconapp.models.User;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ProductFragment extends Fragment {

    FragmentProductBinding binding;

    IProducts am;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.products);
        binding = FragmentProductBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        user = am.getUser();

        binding.productView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        binding.productView.setLayoutManager(llm);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.productView.getContext(), llm.getOrientation());
        binding.productView.addItemDecoration(dividerItemDecoration);

        am.getProducts(new MainActivity.Return() {
            @Override
            public void response(@NonNull String response) {

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Product[] products = gson.fromJson(response, Product[].class);

                ArrayList<Product> productArrayList = new ArrayList<>(Arrays.asList(products));

                binding.productView.setAdapter(new ProductAdapter(user, productArrayList));
            }

            @Override
            public boolean showDialog() {
                return true;
            }

            @Override
            public void error(@NonNull String response) {
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.setUser(null);
                am.sendLoginView();
            }
        });

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

    public interface IProducts {
        User getUser();
        void setUser(User user);
        void sendLoginView();
        void getProducts(MainActivity.Return response);
    }
}