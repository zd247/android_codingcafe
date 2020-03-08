package rattclub.eCommerce.Admins;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import rattclub.eCommerce.R;
import rattclub.eCommerce.Users.HomeActivity;
import rattclub.eCommerce.WelcomeActivity;

public class AdminHomeActivity extends AppCompatActivity {
    private Button logoutBtn, checkOrderBtn, maintainProductBtn, checkApproveProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        logoutBtn = findViewById(R.id.admin_logout_btn);
        checkOrderBtn = findViewById(R.id.check_order_btn);
        maintainProductBtn = findViewById(R.id.maintain_btn);
        checkApproveProductsBtn = findViewById(R.id.check_approve_product_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (AdminHomeActivity.this, WelcomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (AdminHomeActivity.this, AdminNewOrderActivity.class);
                startActivity(intent);
            }
        });

        maintainProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });

        checkApproveProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (AdminHomeActivity.this, AdminCheckNewProductActivity.class);
                startActivity(intent);
            }
        });
    }
}
