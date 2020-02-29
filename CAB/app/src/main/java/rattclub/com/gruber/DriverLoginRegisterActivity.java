package rattclub.com.gruber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverLoginRegisterActivity extends AppCompatActivity {

    private Button driverLoginButton, driverRegisterButton;
    private EditText driverEmail, driverPassword;
    private TextView driverRegisterLink, driverStatus;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login_register);

        InitializeFields();

        setButtonsOnClick();
    }

    private void InitializeFields() {
        //Android
        driverEmail = findViewById(R.id.driver_logreg_email);
        driverPassword = findViewById(R.id.driver_logreg_password);
        driverLoginButton = findViewById(R.id.driver_logreg_login_button);
        driverRegisterButton = findViewById(R.id.driver_logreg_register_button);
        driverRegisterLink = findViewById(R.id.driver_logreg_register_link);
        driverStatus = findViewById(R.id.driver_logreg_status);
        loadingBar = new ProgressDialog(this);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null) {
            sendUserToDriversMapActivity();

            String currentUserID = mAuth.getCurrentUser().getUid();

            rootRef.child("Users").child(currentUserID).child("login_as").setValue("driver");
        }
    }

    private void setButtonsOnClick(){
        //login button
        driverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = driverEmail.getText().toString();
                String password = driverPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(DriverLoginRegisterActivity.this, "Email is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(DriverLoginRegisterActivity.this, "Password is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                }else {
                    loadingBar.setTitle("Sign in");
                    loadingBar.setMessage("Redirecting...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //store in database
                            if (task.isSuccessful()) {
                                String currentUserID = mAuth.getCurrentUser().getUid();

                                rootRef.child("Users").child(currentUserID).child("login_as").setValue("driver");

                                loadingBar.dismiss();
                                Toast.makeText(DriverLoginRegisterActivity.this, "Login successful !", Toast.LENGTH_SHORT).show();
                                sendUserToDriversMapActivity();
                            }else {
                                String e = task.getException().toString();
                                Toast.makeText(DriverLoginRegisterActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });

        //register link
        driverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverLoginButton.setVisibility(View.INVISIBLE);
                driverLoginButton.setEnabled(false);
                driverRegisterButton.setVisibility(View.VISIBLE);
                driverStatus.setText("register driver");
            }
        });


        //register button
        driverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = driverEmail.getText().toString();
                String password = driverPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(DriverLoginRegisterActivity.this, "Email is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(DriverLoginRegisterActivity.this, "Password is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                }else {
                    loadingBar.setTitle("Create New Account");
                    loadingBar.setMessage("Redirecting...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUserID = mAuth.getCurrentUser().getUid();

                                rootRef.child("Users").child(currentUserID).setValue("");
                                rootRef.child("Users").child(currentUserID).child("login_as").setValue("driver");

                                loadingBar.dismiss();
                                Toast.makeText(DriverLoginRegisterActivity.this, "Registered successful !", Toast.LENGTH_SHORT).show();
                                sendUserToDriversMapActivity();
                            }else {
                                loadingBar.dismiss();
                                String e = task.getException().toString();
                                Toast.makeText(DriverLoginRegisterActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void sendUserToDriversMapActivity() {
        Intent intent = new Intent (this, DriversMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}
