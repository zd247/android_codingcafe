package rattclub.com.gruber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WelcomeActivity extends AppCompatActivity {
    private Button welcomeDriverButton, welcomeCustomerButton;

    private FirebaseUser currentUser;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        InitializeFields();

        welcomeDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToDriverLoginRegisterActivity();
            }
        });

        welcomeCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToCustomerLoginRegisterActivity();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void InitializeFields() {
        welcomeDriverButton = findViewById(R.id.welcome_driver_button);
        welcomeCustomerButton = findViewById(R.id.welcome_customer_button);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void sendUserToDriverLoginRegisterActivity() {
        Intent intent = new Intent(WelcomeActivity.this, DriverLoginRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void sendUserToCustomerLoginRegisterActivity() {
        Intent intent = new Intent(WelcomeActivity.this, CustomerLoginRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}
