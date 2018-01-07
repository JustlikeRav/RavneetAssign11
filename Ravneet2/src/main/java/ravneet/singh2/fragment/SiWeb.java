package ravneet.singh2.fragment;

import android.content.Context;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ravneet.singh2.R;

//Ravneet Singh
//n01148757
//Assignment 2

public class SiWeb extends Fragment {
    TextView cityTV, stateTV, codeTV;
    Button mButton;
    private RequestQueue requestQueue;
    EditText mEditText;
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.si_web, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());
        cityTV = mView.findViewById(R.id.city_tv);
        stateTV = mView.findViewById(R.id.state_tv);
        codeTV = mView.findViewById(R.id.zipcode_tv);
        mButton = mView.findViewById(R.id.webservice_btn);
        mEditText = mView.findViewById(R.id.zip_et);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
            }

            private void startService() {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        JsonObjectRequest request = new JsonObjectRequest("https://www.zipwise.com/webservices/zipinfo.php?key=xeet1pvj0w5ntz39&zip="
                                + mEditText.getText().toString() + "&format=json",
                                new Response.Listener<JSONObject>(){

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            cityTV.setText(response.getJSONObject("results").getJSONArray("cities").getJSONObject(0).getString("city"));
                                            stateTV.setText(response.getJSONObject("results").getString("state"));
                                            codeTV.setText(response.getJSONObject("results").getString("area_code"));

                                        } catch (JSONException e) {
                                            Toast.makeText(getActivity(), "dfhsbdfhg", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }


                                    }
                                }, new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                        requestQueue.add(request);

                    }
                });
            }
        });

        return mView;
    }
}
