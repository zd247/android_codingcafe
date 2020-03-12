package rattclub.eCommerce.Sellers;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import rattclub.eCommerce.R;
import rattclub.eCommerce.WelcomeActivity;

public class SellerHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        mAuth = FirebaseAuth.getInstance();

        Toolbar myToolbar = findViewById(R.id.seller_app_bar);
        setSupportActionBar(myToolbar);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.seller_navigation_home, R.id.seller_navigation_add)
                .build();

        BottomNavigationView navView = findViewById(R.id.seller_nav_view);
        final NavController navController = Navigation.findNavController(this, R.id.seller_nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.seller_navigation_logout) {
                    Toast.makeText(SellerHomeActivity.this,
                            "Snizzla signed out", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    Intent intent = new Intent (SellerHomeActivity.this, WelcomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                NavigationUI.onNavDestinationSelected(item,navController);

                return true;
            }
        });
    }

}
