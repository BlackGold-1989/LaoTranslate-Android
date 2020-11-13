package com.laodev.translate.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.laodev.translate.R;
import com.laodev.translate.utils.Constants;
import com.laodev.translate.utils.FuncUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CropedImageActivity extends AppCompatActivity{

    private EditText edtCropedText;
    private ProgressDialog progressDlg;

    private ImageView imgCropedImage;
    private Bitmap cropedImageBitmap;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private void writeToFile() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String fname = "TxMeLao_" + simpleDateFormat.format(new Date()) + ".txt";

        File file = new File(Constants.getRootTextPath());
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, fname);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(edtCropedText.getText().toString());
            writer.flush();
            writer.close();

            FuncUtils.showToast(CropedImageActivity.this, "Scanned Text saved to \"TxMeLao\" folder.");
        }catch (Exception e){
            FuncUtils.showToast(this, e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_croped_image);
        getSupportActionBar().hide();

        initApp();
    }

    private void initApp() {
        progressDlg = ProgressDialog.show(this, "", "Capturing now...");
        progressDlg.dismiss();

        imgCropedImage = findViewById(R.id.img_croped_image);

        edtCropedText = findViewById(R.id.edt_croped_text);
        edtCropedText.setText("");

        try {
            cropedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Constants.cropedImageUti);
            imgCropedImage.setImageBitmap(cropedImageBitmap);
        } catch (IOException e) {
            FuncUtils.showToast(CropedImageActivity.this, "Failed to Load Image, Please take the picture again.");
            Constants.capturedText = "";
            finish();
        }

        if(FuncUtils.checkNetwork(this)){
            processCropedImage();
        }else {
            FirebaseMLKitOCR();
        }
    }

    private void FirebaseMLKitOCR() {
        progressDlg.show();
        InputImage image;
        try {
            image = InputImage.fromFilePath(this, Constants.cropedImageUti);
            TextRecognizer recognizer = TextRecognition.getClient();
            Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        progressDlg.dismiss();
                        if (visionText.getTextBlocks().size() == 0) {
                            FuncUtils.showToast(CropedImageActivity.this, "No text found");
                        }
                        for (Text.TextBlock block : visionText.getTextBlocks()) {
                            edtCropedText.append(block.getText());
                        }
                    }
                })
                .addOnFailureListener(
                        e -> {
                            progressDlg.dismiss();
                            FuncUtils.showToast(CropedImageActivity.this, "Check your network");
                        });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processCropedImage() {

        FirebaseVisionImage myImage;
        try {
            myImage = FirebaseVisionImage.fromFilePath(CropedImageActivity.this, Constants.cropedImageUti);
            String[] scan_lang_codes = new String[Constants.getLanguages().size()];
            for(int i=0; i<Constants.getLanguages().size(); i++){
                scan_lang_codes[i] = Constants.getLanguages(i).text_scann_lang_code;
            }

            FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
                    .setLanguageHints(Arrays.asList(scan_lang_codes))
                    .setModelType(FirebaseVisionCloudTextRecognizerOptions.DENSE_MODEL)
                    .build();

            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getCloudTextRecognizer(options);

            progressDlg.show();
            detector.processImage(myImage)
                    .addOnSuccessListener(texts -> {
                        progressDlg.dismiss();
                        if (texts.getTextBlocks().size() == 0) {
                            FuncUtils.showToast(CropedImageActivity.this, "No text found");
                            return;
                        }
                        for (FirebaseVisionText.TextBlock block : texts.getTextBlocks()) {
                            edtCropedText.append(block.getText());
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDlg.dismiss();
                        e.printStackTrace();
                        FuncUtils.showToast(CropedImageActivity.this, "Failed");
                    });

        } catch (IOException e) {
            e.printStackTrace();
            FuncUtils.showToast(CropedImageActivity.this, "Failed");
        }
    }

    public void onClickOK(View view) {
        Constants.capturedText = edtCropedText.getText().toString();
        Constants.capturedText.replace("cookies", "");
        Constants.capturedText.replace("cookie", "");
        finish();
    }

    public void onClickShare(View view) {
        Intent sendInt = new Intent(Intent.ACTION_SEND);
        sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sendInt.putExtra(Intent.EXTRA_TEXT, edtCropedText.getText().toString());
        sendInt.setType("text/plain");
        startActivity(Intent.createChooser(sendInt, "Share"));
    }

    public void onCLickSave(View view) {
        writeToFile();
    }

    public void onCLickBack(View view) {
        Constants.capturedText = "";
        finish();
    }

    @Override
    public void onBackPressed() {
        Constants.capturedText = "";
        super.onBackPressed();
    }
}
