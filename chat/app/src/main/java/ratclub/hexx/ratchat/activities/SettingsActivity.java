package ratclub.hexx.ratchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ratclub.hexx.ratchat.R;

//TODO: make it return to the main activity when back button is clicked...

public class SettingsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button updateSettingsButton;
    private EditText settingsUserName, settingsUserStatus;
    private CircleImageView userProfileImage;

    private String currentUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private static final int galleryPick = 1;
    private StorageReference userProfileImageRef;
    private ProgressDialog loadingBar;
    private String downloadedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images"); //create storage reference named "Profile Images"

        InitializeFields();

        updateSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);
            }
        });

    }

    private void InitializeFields() {
        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");
        updateSettingsButton = findViewById(R.id.update_settings_button);
        settingsUserName = findViewById(R.id.settings_user_name);
        settingsUserStatus = findViewById(R.id.settings_profile_status);
        userProfileImage = findViewById(R.id.settings_profile_image);
        loadingBar = new ProgressDialog(this);
    }

    /**
     * Directing to select image in the Gallery activity or other activities bases on request code.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==galleryPick && resultCode == RESULT_OK && data != null){
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //save images to the cloud storage under
            if (resultCode == RESULT_OK){
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Uploading image... ");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                final Uri resultUri = result.getUri();

                final StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg"); //create child reference.

                //start storing into the cloud
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadedUrl = uri.toString();
                                rootRef.child("Users").child(currentUserID).child("image").setValue(downloadedUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(SettingsActivity.this, "Profile Image stored in database..", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(SettingsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        }

    }

    /**
     * Update the database with the information entered from this activity
     */
    private void updateSettings() {
        String setUserName = settingsUserName.getText().toString();
        String setUserStatus = settingsUserStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please write your user name...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setUserStatus)){
            Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String,Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            if (!TextUtils.isEmpty(downloadedUrl)) {profileMap.put("image", downloadedUrl);}
            rootRef.child("Users").child(currentUserID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this, "Profile Updated successfully ...", Toast.LENGTH_SHORT).show();
                    }else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * Retrieve data from database (name, image, status)
     */
    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){
                    String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveImage = dataSnapshot.child("image").getValue().toString();

                    settingsUserName.setText(retrieveUserName);
                    settingsUserStatus.setText(retrieveStatus);

                    Picasso.get().load(retrieveImage).into(userProfileImage); //display image using picasso from the retrieved URL from database

                }else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){
                    String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();

                    settingsUserName.setText(retrieveUserName);
                    settingsUserStatus.setText(retrieveStatus);
                }
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String retrieveImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(retrieveImage).into(userProfileImage); //display image using picasso from the retrieved URL from database
                }
                else {
                    settingsUserName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please update your info...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
