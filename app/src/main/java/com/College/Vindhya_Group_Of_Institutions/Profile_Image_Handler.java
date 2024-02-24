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
public class Profile_Image_Handler {
    private ActivityResultLauncher<Intent> galleryLauncher;
    private StorageReference storageReference;
    private String userId;
    public Profile_Image_Handler(AppCompatActivity activity, String userId) {
        this.userId = userId;
        storageReference = FirebaseStorage.getInstance().getReference();
        setupGalleryLauncher(activity);
    }

    public void loadProfileImage(ImageView profileImageView) {
        StorageReference storeRef = storageReference.child("Profile_Images/" + userId);

        storeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            profileImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(e ->
                Toast.makeText(profileImageView.getContext(), "Unable to load Profile Image!" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupGalleryLauncher(AppCompatActivity activity) {
        galleryLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri imageUri = data.getData();
                    uploadImageToFirebase(imageUri,activity);
                }
            }
        });
    }

    public void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(openGallery);
    }
    private void uploadImageToFirebase(Uri imageUri,AppCompatActivity activity) {
        StorageReference fileRef = storageReference.child("Profile_Images/" + userId);


        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Assuming you have a reference to the ImageView in your activity
                ImageView profileImageView = activity.findViewById(R.id.imgProfile);
                if (profileImageView != null) {
                    profileImageView.setBackgroundColor(Color.TRANSPARENT);
                    Picasso.get().load(uri).into(profileImageView);
                }
            });
            Toast.makeText(activity, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(activity, "Image Not Uploaded! " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
