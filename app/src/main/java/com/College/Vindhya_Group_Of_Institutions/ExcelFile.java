package com.College.Vindhya_Group_Of_Institutions;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExcelFile extends AppCompatActivity {

    private ArrayList<PercentageModel> list = new ArrayList<>();
    private static final String TAG = "ExcelFileActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel_file);

        fetchDataFromSheet();
    }

    private void fetchDataFromSheet() {
        // Replace "YOUR_APP_SCRIPT_URL" with your Google Apps Script web app URL
        String url = "https://script.google.com/macros/s/AKfycbyivh6UoHQM5aD9dwgGacKgjVYZLaykJcOCsh9QIFTw1I_Dhz4MslKN3h_LBxLoV__sNg/exec";

        // Creating a JsonArrayRequest to fetch the JSON array response
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle the JSON array response
                        parseJsonResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching data", error);
                        Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_LONG).show();
                    }
                });

        // Creating a RequestQueue and adding the request to the queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void parseJsonResponse(JSONArray jsonArray) {
        try {
            // Clear existing data
            list.clear();

            // Process the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String rollNumber = jsonObject.getString("RollNumber");
                float percentage = (float) jsonObject.getDouble("Percentage");

                PercentageModel attendanceModel = new PercentageModel(rollNumber, percentage);
                list.add(attendanceModel);
            }

            // Now, 'list' contains the parsed data
            // You can use this list as needed (e.g., display in a RecyclerView)

            // Display a toast message indicating successful data retrieval
            Toast.makeText(getApplicationContext(), "Data retrieved successfully", Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            Toast.makeText(getApplicationContext(), "Error parsing JSON", Toast.LENGTH_LONG).show();
        }
    }
}
