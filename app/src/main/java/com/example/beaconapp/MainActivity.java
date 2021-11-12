package com.example.beaconapp;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;
import com.example.beaconapp.models.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoginFragment.ILogin, RegisterFragment.IRegister, ProductFragment.IProducts {

    private final OkHttpClient client = new OkHttpClient();
    public static final String BASE_URL  = "https://mysterious-beach-05426.herokuapp.com/"; // http://10.0.2.2:3000/
    private static final String BEACON_TOKEN = "06e8c088-fae4-419c-aeb6-c56e8def1c42";

    BCBeaconManager beaconManager;
    ProgressDialog dialog;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), BEACON_TOKEN);

        beaconManager = new BCBeaconManager();
        beaconManager.registerCallback( mBeaconManagerCallback );

        sendLoginView();

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        BlueCatsSDK.didEnterForeground();
        beaconManager.registerCallback( mBeaconManagerCallback );
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        BlueCatsSDK.didEnterBackground();
        beaconManager.unregisterCallback( mBeaconManagerCallback );
    }



    private BCBeaconManagerCallback mBeaconManagerCallback = new BCBeaconManagerCallback()
    {
        @Override
        public void didRangeBlueCatsBeacons( final List<BCBeacon> beacons ) {}

    };

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getUser() {
        return user;
    }

    public void sendLoginView(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerLayout, new LoginFragment())
                .commit();
    }

    @Override
    public void sendRegisterView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerLayout, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void sendProductsView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerLayout, new ProductFragment())
                .commit();
    }

    @Override
    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }

    public void clientToken(Return response){
        FormBody formBody = new FormBody.Builder()
                .add("customerId", user.getCustomerId())
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "product/clienttoken")
                .addHeader("x-jwt-token", user.getToken())
                .post(formBody)
                .build();
        sendRequest(request, response);
    }


    public void login(Return response, String... data){
        FormBody formBody = new FormBody.Builder()
                .add("email", data[0])
                .add("pass", data[1])
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "auth/login")
                .post(formBody)
                .build();
        sendRequest(request, response);
    }


    public void register(Return response, String... data){
        FormBody formBody = new FormBody.Builder()
                .add("fullname", data[0])
                .add("address", data[1])
                .add("email", data[2])
                .add("pass", data[3])
                .build();
        Request request = new Request.Builder()
                .url(BASE_URL + "auth/signup")
                .post(formBody)
                .build();
        sendRequest(request, response);
    }

    @Override
    public void getProducts(Return response) {
        Request request = new Request.Builder()
                .url(BASE_URL + "product/getAll")
                .addHeader("x-jwt-token", user.getToken())
                .build();
        sendRequest(request, response);
    }

    private void sendRequest(Request request, Return callback) {
        if(callback.showDialog()) toggleDialog(true, "Processing...");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if(callback.showDialog()) toggleDialog(false, null);
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(callback.showDialog()) toggleDialog(false, null);

                ResponseBody responseBody = response.body();

                String res_string;
                if (responseBody != null) {
                    res_string = responseBody.string();

                    if (response.isSuccessful()) {
                        runOnUiThread(() -> callback.response(res_string));
                    } else {
                        runOnUiThread(() -> {
                            try {
                                JSONObject jsonObject = new JSONObject(res_string);
                                if (jsonObject.has("message"))
                                    alert(jsonObject.getString("message"));
                            }catch (JSONException exc){
                            }
                        });
                        callback.error(res_string);
                    }
                }
            }
        });
    }

    @Override
    public void alert(String msg) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage(msg)
                .setPositiveButton("Okay", null)
                .show());
    }


    public void toggleDialog(boolean show) {
        toggleDialog(show, null);
    }

    public void toggleDialog(boolean show, String msg) {
        if (show) {
            dialog = new ProgressDialog(this);
            if (msg == null)
                dialog.setMessage("Loading...");
            else
                dialog.setMessage(msg);
            dialog.setCancelable(false);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

    interface Return{

        void response(@NotNull String response);

        void error(@NotNull String response);

        boolean showDialog();

    }


}