package rattclub.eCommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    //TODO: load data from the user, lazy load the main app, do this after please !!
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
