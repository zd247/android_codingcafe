package rattclub.eCommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import io.paperdb.Paper;
import rattclub.eCommerce.Model.User;
import rattclub.eCommerce.Prevalent.Prevalent;

public class WelcomeActivity extends AppCompatActivity {
    private Button registerButton, loginButton;
    private DatabaseReference rootRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate  (savedInstanceState);
        setContentView(R.layout.activity_welcome);

        InitializeFields();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLoginActivity();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });

        String userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if (userPhoneKey != "" && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){
                AllowAccess(userPhoneKey, userPasswordKey);

                loadingBar.setTitle("Logging in");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }


    }

    private void InitializeFields() {
        registerButton = findViewById(R.id.main_register_btn);
        loginButton = findViewById(R.id.main_login_btn);
        rootRef = FirebaseDatabase.getInstance().getReference();
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
    }

    private void AllowAccess(final String userPhoneKey, final String userPasswordKey) {
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(userPhoneKey).exists()){
                    User userData = dataSnapshot.child("Users").child(userPhoneKey).getValue(User.class);

                    if (userData.getPhone().equals(userPhoneKey)){
                        if (userData.getPassword().equals(userPasswordKey)){
                            loadingBar.dismiss();
                            Prevalent.currentOnlineUser = userData;
                            sendUserToHomeActivity();
                        }
                    }

                }else {
                    Toast.makeText(WelcomeActivity.this, "Unexpected sign-out, please Login again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);
    }

    private void sendUserToRegisterActivity() {
        Intent intent = new Intent (this, RegisterActivity.class);
        startActivity(intent);
    }

    private void sendUserToHomeActivity() {
        Intent intent = new Intent (this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
