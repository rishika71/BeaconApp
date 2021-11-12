package com.example.beaconapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.beaconapp.databinding.FragmentRegisterBinding;
import com.example.beaconapp.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

public class RegisterFragment extends Fragment {

    FragmentRegisterBinding binding;

    IRegister am;

    String fullname, address, email, pass;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IRegister) {
            am = (IRegister) context;
        } else {
            throw new RuntimeException(context.toString());
        }
    }

    public interface IRegister {

        void setUser(User user);

        void alert(String msg);

        void goBack();

        void sendProductsView();

        void register(com.example.beaconapp.MainActivity.Return response, String... data);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Register");

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.cancelButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.goBack();
            }
        });

        binding.registerButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullname = binding.edittext.getText().toString();
                address = binding.edittext4.getText().toString();
                email = binding.edittext5.getText().toString();
                pass = binding.edittext6.getText().toString();

                if(email.isEmpty() || fullname.isEmpty() || address.isEmpty() || pass.isEmpty()){
                    am.alert("Please enter all values for registering!");
                    return;
                }


                am.register(new com.example.beaconapp.MainActivity.Return() {
                    @Override
                    public void response(@NotNull String response) {
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        User user = gson.fromJson(response, User.class);
                        am.setUser(user);
                        am.sendProductsView();
                    }

                    @Override
                    public boolean showDialog() {
                        return true;
                    }

                    @Override
                    public void error(@NotNull String response) {
                    }
                }, fullname, address, email, pass);

            }
        });

        return view;
    }
}