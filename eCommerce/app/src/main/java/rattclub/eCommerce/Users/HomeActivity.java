package rattclub.eCommerce.Users;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import rattclub.eCommerce.Fragments.HomeFragment;
import rattclub.eCommerce.Prevalent.Prevalent;
import rattclub.eCommerce.R;
import rattclub.eCommerce.WelcomeActivity;

public class HomeActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private DatabaseReference usersRef;
    private String type = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        InitializeFields();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        // Navigation controller
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.nav_settings){
                    Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();

                }
                if (item.getItemId() == R.id.nav_logout) {
                    Paper.book().destroy();
                    Toast.makeText(HomeActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(item,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });


        //set nav header

        if (!type.equals("Admin")) {
            View headerView = navigationView.getHeaderView(0);
            TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
            final CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

            userNameTextView.setText(Prevalent.currentOnlineUser.getName());

            usersRef.child(Prevalent.currentOnlineUser.getPhone()).child("image")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Picasso.get().load(Prevalent.currentOnlineUser.getImage())
                                        .placeholder(R.drawable.profile).into(profileImageView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
        }
    }


    private void InitializeFields() {
        Paper.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = intent.getExtras().get("Admin").toString();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
