package ph.edu.dlsu.ian_ona.scioterra;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class CaptureSuccess extends AppCompatActivity{


    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_success);

        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        ViewPagerAdapter vpa = new ViewPagerAdapter(getSupportFragmentManager());

        ResultFragment resultFragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putByteArray("IMG",getIntent().getByteArrayExtra("IMG"));
        resultFragment.setArguments(bundle);

        vpa.addFragment(resultFragment,"Result");

        InfoFragment infoFragment = new InfoFragment();
        infoFragment.setArguments(bundle);
        vpa.addFragment(infoFragment,"Info");

        LocationFragment locationFragment = new LocationFragment();
        locationFragment.setArguments(bundle);
        vpa.addFragment(locationFragment,"Location");
        viewPager.setAdapter(vpa);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        CaptureSuccess.this.startActivity(intent);
    }
}
