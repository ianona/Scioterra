package ph.edu.dlsu.ian_ona.scioterra;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;

import java.util.Arrays;
import java.util.List;

public class LocationFragment extends Fragment{
    View view;
    private String[] valid = {"plant","soil","flowerpot","garden","field","branch","trunk","twig","forest","jungle"};
    private ConstraintLayout layout;

    public LocationFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.location,container,false);
        layout = view.findViewById(R.id.locationLayout);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            byte[] byteArray = bundle.getByteArray("IMG");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            scanImage(bmp);
        }

        return view;
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
        String tentative = "NYARLIGANS";
        boolean found = false;

        for (FirebaseVisionLabel label: labels) {
            String text = label.getLabel();
            if (Arrays.asList(valid).contains(text.toLowerCase())) {
                found = true;
                tentative = text;
            }

            float confidence = label.getConfidence();
            if(confidence>maxCon){
                maxCon = confidence;
                bestFound = text;
            }
            all += " "+text;
        }

        String plant = null;
        if (found) {
            if (!Arrays.asList(valid).contains(bestFound.toLowerCase())) {
                plant = tentative;
            } else {
                plant = bestFound;
            }
        }

        if (plant!=null){

        } else {
            layout.setAlpha(0.0f);
        }
    }
}
