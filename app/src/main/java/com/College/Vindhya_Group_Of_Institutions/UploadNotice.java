package com.College.Vindhya_Group_Of_Institutions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class UploadNotice extends AppCompatActivity {

    // Constants

    // UI Elements
    private CardView addImage;
    private ImageView noticeImageView;
    private EditText noticeTitle;
    private Button uploadNoticeBtn;

    // Firebase
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // Bitmap for image
    private Bitmap bitmap;

    // Progress Dialog
    private ProgressDialog progressDialog;

    // Activity Result Launcher
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);

        // Firebase Initialization
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notice");
        storageReference = FirebaseStorage.getInstance().getReference().child("Notice");

        // Progress Dialog Initialization
        progressDialog = new ProgressDialog(this);

        // Initialize UI Elements
        addImage = findViewById(R.id.addimage);
        noticeImageView = findViewById(R.id.noticeImageView);
        noticeTitle = findViewById(R.id.noticeTitle);
        uploadNoticeBtn = findViewById(R.id.uploadNoticeBtn);

        // Upload Notice Button Click Listener
        uploadNoticeBtn.setOnClickListener(v -> uploadNotice());

        // Gallery Launcher Initialization
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    noticeImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Add Image Click Listener
        addImage.setOnClickListener(v -> openGallery());
    }

    // Method to upload notice
    private void uploadNotice() {
        String title = noticeTitle.getText().toString().trim();
        if (title.isEmpty()) {
            noticeTitle.setError("Empty!");
            noticeTitle.requestFocus();
            return;
        }
        if (bitmap == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Uploading ....");
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageData = baos.toByteArray();
        String imageName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference filePath = storageReference.child(imageName);
        UploadTask uploadTask = filePath.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            uploadData(title, downloadUrl);
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(UploadNotice.this, "Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Method to upload notice data
    private void uploadData(String title, String downloadUrl) {
        String uniqueKey = databaseReference.push().getKey();
        if (uniqueKey == null) {
            Toast.makeText(this, "Failed to generate unique key", Toast.LENGTH_SHORT).show();
            return;
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String date = dateFormat.format(cal.getTime());
        String time = timeFormat.format(cal.getTime());
        NoticeData noticeData = new NoticeData(title, date, time, downloadUrl, uniqueKey);
        databaseReference.child(title).setValue(noticeData)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNotice.this, "Notice Uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNotice.this, "Failed to upload notice: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Method to open gallery for image selection
    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickImage);
    }
}
