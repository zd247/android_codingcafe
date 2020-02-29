package ratclub.hexx.ratchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import ratclub.hexx.ratchat.R;
import ratclub.hexx.ratchat.TabsAccessorAdapter;


public class MainActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabAccessorAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserID;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        myToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("RATTchat");

        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            currentUserID=currentUser.getUid();
        }

        myTabAccessorAdapter = new TabsAccessorAdapter(this, getSupportFragmentManager());
        myViewPager = findViewById(R.id.main_tabs_pager);
        myViewPager.setAdapter(myTabAccessorAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null){
            sendUserToLoginActivity(); //direct user to Login
        }else {
            updateUserStatus("online");

            VerifyUserExistence(); //perform check on data change in the settings activity to update the database
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null){
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currentUser != null){
            updateUserStatus("offline");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option){
            mAuth.signOut();
            sendUserToLoginActivity();
        }
        if (item.getItemId() == R.id.main_create_group_option){
            requestNewGroup();
        }

        if (item.getItemId() == R.id.main_settings_option){
            sendUserToSettingsActivity();
        }

        if (item.getItemId() == R.id.main_find_friends_option){
            sendUserToFindFriendsActivity();
        }

        return true;
    }




    private void VerifyUserExistence() {
        currentUserID = mAuth.getCurrentUser().getUid();

        //perform check if user exists, if exists then perform callback check to see if data of that user has changed
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())){
                    System.out.println("User existsed, no need to move to settings to set for details");
                }else {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    /**
     * Create an AlertDialog box to listen to onCreate event of a new group
     */
    private void requestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name : ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Group name...");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please set the group name...", Toast.LENGTH_SHORT).show();
                }else {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    /**
     * Add new group to the database. (this data is listened in ValueEventListener)
     * @param groupName
     */
    private void createNewGroup(final String groupName) {
        rootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName + " is created successfully !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void sendUserToFindFriendsActivity() {
        Intent findFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }

    private void updateUserStatus(String state){
            String saveCurrentTime, saveCurrentDate;

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            HashMap<String, Object> onlineStateMap = new HashMap<>();

            onlineStateMap.put ("time", saveCurrentTime);
            onlineStateMap.put ("date", saveCurrentDate);
            onlineStateMap.put ("state", state);

            rootRef.child("Users").child(currentUserID).child("userState")
                    .updateChildren(onlineStateMap);

    }






}
