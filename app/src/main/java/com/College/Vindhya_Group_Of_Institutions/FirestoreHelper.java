package com.College.Vindhya_Group_Of_Institutions;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {



    public static void storeArrayListInFirestore(JSONArray jsonArray, String collectionName, String documentId, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to store the data
        Map<String, Object> data = new HashMap<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract RollNumber and Percentage from each JSON object
                String rollNumber = jsonObject.getString("RollNumber");
                String percentage = jsonObject.getString("Percentage");
                // Add the data to the map
                data.put(rollNumber, percentage);
            }

            // Add the map to Firestore as a new document
            db.collection(collectionName)
                    .document(documentId)
                    .set(data)
                    .addOnSuccessListener(aVoid -> {
                        // Document added successfully
                        Toast.makeText(context, "Data Stored Successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle Firestore write errors
                        Toast.makeText(context, "Error storing data. Please try again.", Toast.LENGTH_SHORT).show();
                    });
        } catch (JSONException e) {

            Toast.makeText(context, "Error parsing data. Please check the JSON array format.", Toast.LENGTH_SHORT).show();
        }
    }


}
