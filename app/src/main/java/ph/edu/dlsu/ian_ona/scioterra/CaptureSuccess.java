package ph.edu.dlsu.ian_ona.scioterra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;

import java.util.List;

public class CaptureSuccess extends AppCompatActivity {
    private ImageView imgCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_success);
        imgCapture = (ImageView)findViewById(R.id.imgCapture);

        byte[] byteArray = getIntent().getByteArrayExtra("IMG");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imgCapture.setImageBitmap(bmp);

        scanImage(bmp);
    }

    private void scanImage(Bitmap bmp){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionLabelDetector detector = FirebaseVision.getInstance().getVisionLabelDetector();

        Task<List<FirebaseVisionLabel>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionLabel> labels) {
                                        // Task completed successfully
                                        initViews(labels);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                    }
                                });
    }

    private void initViews(List<FirebaseVisionLabel> labels) {
        float maxCon = 0;
        String bestFound = "NYARLIGANS";
        String all = "";
        for (FirebaseVisionLabel label: labels) {
            String text = label.getLabel();
            float confidence = label.getConfidence();
            if(confidence>maxCon){
                maxCon = confidence;
                bestFound = text;
            }
            all += " "+text;
        }
        Toast.makeText(getApplicationContext(),"FOUND: "+all,Toast.LENGTH_LONG).show();
    }
}
