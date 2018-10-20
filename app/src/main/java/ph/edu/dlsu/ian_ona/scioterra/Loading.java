package ph.edu.dlsu.ian_ona.scioterra;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.os.Handler;

public class Loading extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    protected TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        textView = findViewById(R.id.textView2);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Anson-Regular.otf");
        textView.setTypeface(typeface);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);


        //Need to use this so that it will create a new intent on its own
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Loading.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); //Enables our fading transition
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
