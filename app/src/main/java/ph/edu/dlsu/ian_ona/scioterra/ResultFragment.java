package ph.edu.dlsu.ian_ona.scioterra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

public class ResultFragment extends Fragment{
    View view;
    private ImageView imgCapture;
    private TextView plantName, sciName,conStatus;
    private Button camBtn;
    private ConstraintLayout layout;
    private String[] valid = {"plant","soil","flowerpot","garden","field","branch","trunk","twig","forest","jungle"};

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public ResultFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.result,container,false);

        imgCapture = view.findViewById(R.id.imgCapture);
        plantName = view.findViewById(R.id.plantName);
        sciName = view.findViewById(R.id.sciName);
        conStatus = view.findViewById(R.id.conStatus);
        camBtn = view.findViewById(R.id.camBtn);
        layout = view.findViewById(R.id.consLayout);

        //camBtn.setAlpha(0.0f);

        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            byte[] byteArray = bundle.getByteArray("IMG");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgCapture.setImageBitmap(bmp);
            scanImage(bmp);
        }

        return view;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == this.getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(this.getActivity(), CaptureSuccess.class);
            intent.putExtra("IMG",byteArray);
            ResultFragment.this.startActivity(intent);
        }
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
            plantName.setText(plant);
            sciName.setText("Schefflera arboricola");
            conStatus.setText("Conservation Status: Least concern");
        } else {
            plantName.setText(bestFound);
            sciName.setText("No information available for this object");
            conStatus.setAlpha(0.0f);
            layout.setBackgroundColor(Color.BLACK);
            plantName.setBackgroundColor(Color.BLACK);
        }
    }
}
