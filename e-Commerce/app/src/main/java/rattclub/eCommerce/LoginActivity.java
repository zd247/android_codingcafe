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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;
import rattclub.eCommerce.Admins.AdminHomeActivity;
import rattclub.eCommerce.Model.User;
import rattclub.eCommerce.Prevalent.Prevalent;
import rattclub.eCommerce.Users.HomeActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText inputPhone, inputPassword;
    private Button loginButton;
    private CheckBox rememberMeCheckBox;
    private ProgressDialog loadingBar;
    private TextView adminLink, notAdminLink, forgetPasswordLink;
    private String parentDBName = "Users";

    FirebaseAuth mAuth;
    DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeFields();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";

            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";

            }
        });

        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });

    }

    private void InitializeFields() {
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
        rememberMeCheckBox = findViewById(R.id.login_remember_me_chkb);
        Paper.init(this);

        inputPhone = findViewById(R.id.login_phone_number_input);
        inputPassword = findViewById(R.id.login_password_input);
        loginButton = findViewById(R.id.login_btn);

        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);
        forgetPasswordLink = findViewById(R.id.forget_password_link);
    }


    private void loginUser() {
        String phone = inputPhone.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Phone number cannot be left blank", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password cannot be left blank", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Logging in");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            verifyLoginInfo(phone, password);
        }
    }

    private void verifyLoginInfo(final String phone, final String password) {
        if (rememberMeCheckBox.isChecked()){
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }


        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDBName).child(phone).exists()){
                    User userData = dataSnapshot.child(parentDBName).child(phone).getValue(User.class);

                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            if (parentDBName.equals("Admins")) {
                                loadingBar.dismiss();
                                sendUserToAdminCategoryActivity();
                            }else if (parentDBName == "Users"){
                                loadingBar.dismiss();
                                Prevalent.currentOnlineUser = userData;
                                sendUserToHomeActivity();
                            }

                        }else {
                            Toast.makeText(LoginActivity.this,
                                    "Invalid password",
                                    Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }

                }else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this,
                            "Invalid credentials provided, please re-enter correctly",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToAdminCategoryActivity() {
        Intent intent = new Intent (this, AdminHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToHomeActivity() {
        Intent intent = new Intent (this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
