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

public class CustomerLoginRegisterActivity extends AppCompatActivity {

    private Button customerLoginButton, customerRegisterButton;
    private EditText customerEmail, customerPassword;
    private TextView customerRegisterLink, customerStatus;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);

        InitializeFields();

        setButtonsOnClick();
    }

    private void InitializeFields() {
        //Android
        customerEmail = findViewById(R.id.customer_logreg_email);
        customerPassword = findViewById(R.id.customer_logreg_password);
        customerLoginButton = findViewById(R.id.customer_logreg_login_button);
        customerRegisterButton = findViewById(R.id.customer_logreg_register_button);
        customerRegisterLink = findViewById(R.id.customer_logreg_register_link);
        customerStatus = findViewById(R.id.customer_logreg_status);
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
            sendUserToCustomersMapActivity();
            String currentUserID = mAuth.getCurrentUser().getUid();

            rootRef.child("Users").child(currentUserID).child("login_as").setValue("customer");
        }
    }

    private void setButtonsOnClick() {
        //login button
        customerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = customerEmail.getText().toString();
                String password = customerPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(CustomerLoginRegisterActivity.this, "Email is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(CustomerLoginRegisterActivity.this, "Password is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                } else {

                    loadingBar.setTitle("Sign in");
                    loadingBar.setMessage("Redirecting...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String currentUserID = mAuth.getCurrentUser().getUid();

                            rootRef.child("Users").child(currentUserID).child("login_as").setValue("customer");


                            //store in database
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                sendUserToCustomersMapActivity();
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Login successful !", Toast.LENGTH_SHORT).show();
                            } else {
                                String e = task.getException().toString();
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });

        //register link
        customerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerLoginButton.setVisibility(View.INVISIBLE);
                customerLoginButton.setEnabled(false);
                customerRegisterButton.setVisibility(View.VISIBLE);
                customerStatus.setText("register customer");
            }
        });

        //register button
        customerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = customerEmail.getText().toString();
                String password = customerPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(CustomerLoginRegisterActivity.this, "Email is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(CustomerLoginRegisterActivity.this, "Password is empty, please re-enter..", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Create New Account");
                    loadingBar.setMessage("Redirecting...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String currentUserID = mAuth.getCurrentUser().getUid();

                            rootRef.child("Users").child(currentUserID).setValue("");

                            rootRef.child("Users").child(currentUserID).child("login_as").setValue("customer");

                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                sendUserToCustomersMapActivity();
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Registered successful !", Toast.LENGTH_SHORT).show();
                            } else {
                                loadingBar.dismiss();
                                String e = task.getException().toString();
                                Toast.makeText(CustomerLoginRegisterActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void sendUserToCustomersMapActivity() {
        Intent intent = new Intent(this, CustomersMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}
