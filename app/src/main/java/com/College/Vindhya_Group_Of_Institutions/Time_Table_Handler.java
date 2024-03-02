package com.College.Vindhya_Group_Of_Institutions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Time_Table_Handler {
    Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private final StorageReference storageReference;

    private final Progress_Dialog progressDialog;

    private boolean isImageSelected = false;
    public Time_Table_Handler(AppCompatActivity activity) {
        storageReference = FirebaseStorage.getInstance().getReference();
        setupGalleryLauncher(activity);

        progressDialog = new Progress_Dialog(activity);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
    }

//    public void loadProfileImage(ImageView profileImageView) {
//        StorageReference storeRef = storageReference.child("Profile_Images/" );
//
//        storeRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            profileImageView.setBackgroundColor(Color.TRANSPARENT);
//            Picasso.get().load(uri).into(profileImageView);
//        }).addOnFailureListener(e ->
//                Toast.makeText(profileImageView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
//    }

    private void setupGalleryLauncher(AppCompatActivity activity) {
        galleryLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    imageUri = data.getData();
                    ImageView profileImageView = activity.findViewById(R.id.imgPhoto);
                    if (profileImageView != null) {
                        profileImageView.setBackgroundColor(Color.TRANSPARENT);
                        Picasso.get().load(imageUri).into(profileImageView);
                        isImageSelected = true;
                    }
                }
            }
        });
    }

    public void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(openGallery);

    }

    public void uploadImageToFirebase(AppCompatActivity activity, String depart, String year) {

        progressDialog.show();

        StorageReference fileRef = storageReference.child(depart+"/"+year);


        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();

            Toast.makeText(activity, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();


        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            String errorMessage = "Image Not Uploaded! " + e.getMessage();
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
        });
    }

    public boolean isImageSelected() {
        return isImageSelected;
    }

}
