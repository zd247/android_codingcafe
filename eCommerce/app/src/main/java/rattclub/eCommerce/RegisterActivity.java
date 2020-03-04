package rattclub.eCommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.Switch;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rattclub.eCommerce.Model.User;
import rattclub.eCommerce.Prevalent.Prevalent;

public class RegisterActivity extends AppCompatActivity {
    private RelativeLayout registerLayout;
    private EditText inputName, inputPhone, inputPassword, inputVerify;
    private Button registerButton, sendCodeButton, verifyCodeButton;
    private ProgressDialog loadingBar;
    private Switch modeSwitcher;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    private String name, phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeFields();

        setUpMode(modeSwitcher.isChecked());
        modeSwitcher.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                setUpMode(modeSwitcher.isChecked());
            }
        });


    }


    private void InitializeFields() {
        registerLayout = findViewById(R.id.register_layout);
        modeSwitcher = findViewById(R.id.register_mode_switcher);
        inputName = findViewById(R.id.register_name_input);
        inputPhone = findViewById(R.id.register_phone_number_input);
        inputPassword = findViewById(R.id.register_password_input);
        inputVerify = findViewById(R.id.register_verify_code_input);
        registerButton = findViewById(R.id.register_btn);
        sendCodeButton = findViewById(R.id.register_send_code_btn);
        verifyCodeButton = findViewById(R.id.register_verify_code_btn);
        loadingBar = new ProgressDialog(this);

        //Firebase instances
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        // for deployment callbacks with real phone authentication
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //verified successful, no need to verify device, signing in
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(RegisterActivity.this, "Invalid phone number...", Toast.LENGTH_SHORT).show();

                inputName.setVisibility(View.VISIBLE);
                inputPhone.setVisibility(View.VISIBLE);
                inputPassword.setVisibility(View.VISIBLE);
                sendCodeButton.setVisibility(View.VISIBLE);

                verifyCodeButton.setVisibility(View.INVISIBLE);
                inputVerify.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                loadingBar.dismiss();

                // Save verification ID and resending token so we can use them later
                mVerificationId = s;
                mResendToken = forceResendingToken;

                Toast.makeText(RegisterActivity.this, "Code sent", Toast.LENGTH_SHORT).show();

                inputName.setVisibility(View.INVISIBLE);
                inputPhone.setVisibility(View.INVISIBLE);
                inputPassword.setVisibility(View.INVISIBLE);
                sendCodeButton.setVisibility(View.INVISIBLE);

                inputVerify.setVisibility(View.VISIBLE);
                verifyCodeButton.setVisibility(View.VISIBLE);


            }
        };
    }

    private void setUpMode(boolean deployment) {
        if (deployment){
            registerLayout.setBackgroundResource(R.drawable.background6);
            registerButton.setVisibility(View.INVISIBLE);
            verifyCodeButton.setVisibility(View.INVISIBLE);

            sendCodeButton.setVisibility(View.VISIBLE);
            sendCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUserInput();
                }
            });

            verifyCodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyVerificationCode();
                }
            });
        }else {
            registerLayout.setBackgroundResource(R.drawable.background10);
            verifyCodeButton.setVisibility(View.INVISIBLE);
            sendCodeButton.setVisibility(View.INVISIBLE);

            registerButton.setVisibility(View.VISIBLE);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingBar.setTitle("Verifying");
                    loadingBar.setMessage("Please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    verifyUserInput();
                    storeUserInformation();
                }
            });
        }


    }

    private void verifyVerificationCode() {
        String verificationCode = inputVerify.getText().toString();

        if (TextUtils.isEmpty(verificationCode)){
            Toast.makeText(RegisterActivity.this, "Please enter a valid code... ", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Verifying");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void verifyUserInput() {
        name = inputName.getText().toString();
        phone = inputPhone.getText().toString();
        password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Name field cannot be left blank", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Phone field cannot be left blank", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Phone field cannot be left blank", Toast.LENGTH_SHORT).show();
        }else {
            if (modeSwitcher.isChecked()) {
                loadingBar.setTitle("Verifying");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        mCallbacks
                );
            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            loadingBar.dismiss();
                            storeUserInformation();
                        }else {
                            String message = task.getException().toString();
                            Toast.makeText(RegisterActivity.this, "Error" + message, Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void storeUserInformation() {
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(phone).exists()) {
                    final HashMap<String, Object> userInfoMap = new HashMap<>();
                    userInfoMap.put("name", name);
                    userInfoMap.put("phone", phone);
                    userInfoMap.put("password", password);

                    rootRef.child("Users").child(phone)
                            .updateChildren(userInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this,
                                        "Congrats, you can now buy some snizzles with yo new account",
                                        Toast.LENGTH_SHORT).show();
                                User newUser = new User(userInfoMap.get("name").toString(),
                                        userInfoMap.get("phone").toString(),
                                        userInfoMap.get("password").toString(),
                                        "", "");
                                Prevalent.currentOnlineUser = newUser;
                                sendUserToHomeActivity();
                            }else {
                                Toast.makeText(RegisterActivity.this, "Unable to register.. Try again?", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }

    private void sendUserToHomeActivity() {
        Intent intent = new Intent (this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
