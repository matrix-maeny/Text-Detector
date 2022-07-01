package com.matrix_maeny.textdetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.matrix_maeny.textdetector.databinding.ActivityScannerBinding;

import java.io.IOException;

public class ScannerActivity extends AppCompatActivity {

    private ActivityScannerBinding binding;

    private Bitmap imageBitmap = null;
    private final int IMAGE_CAPTURE_CODE = 1;
    private final int GALLERY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.snapBtn.setOnClickListener(v -> {
            captureImage();
        });

        binding.galleryBtn.setOnClickListener(v -> {
            getFromGallery();
        });

        binding.detectBtn.setOnClickListener(v -> {
            detectText();
        });
    }

    private void getFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }

    private void detectText() {
        if (imageBitmap == null) {
            Toast.makeText(this, "Please take the picture first", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(ScannerActivity.this);
        progressDialog.setMessage("Please wait while processing");
        progressDialog.show();
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);

        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        Task<Text> task = recognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {

                StringBuilder result = new StringBuilder();

                for (Text.TextBlock block : text.getTextBlocks()) {
                    String blockText = block.getText();
                    Point[] blockCornerPoints = block.getCornerPoints();
                    Rect blockRect = block.getBoundingBox();

                    for (Text.Line line : block.getLines()) {
                        String lineText = line.getText();
                        Point[] lineCornerPoints = line.getCornerPoints();
                        Rect lineRect = line.getBoundingBox();

                        for (Text.Element element : line.getElements()) {
                            String elementText = element.getText();
                            result.append(elementText).append(" ");
                        }


                    }
                }

                progressDialog.dismiss();
//                        binding.resultTv.setText(result);
                Intent intent = new Intent(ScannerActivity.this, ResultTextActivity.class);
//                        intent.putExtra("result",result.toString());
                intent.putExtra("result", result.toString());
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScannerActivity.this, "failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, IMAGE_CAPTURE_CODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                imageBitmap = (Bitmap) bundle.get("data");
                binding.capturedIv.setImageBitmap(imageBitmap);
            }
        }

        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    binding.capturedIv.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    imageBitmap = null;
                }
            }
        }
    }
}