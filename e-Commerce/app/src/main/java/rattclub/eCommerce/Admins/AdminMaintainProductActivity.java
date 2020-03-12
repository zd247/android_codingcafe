package rattclub.eCommerce.Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import rattclub.eCommerce.R;

public class AdminMaintainProductActivity extends AppCompatActivity {
    private Button applyChangesBtn;
    private EditText name, price, description;
    private ImageView productImage;
    private String productID = "";
    private DatabaseReference productsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        InitializeFields();

        displaySpecificProductInfo();
    }

    private void InitializeFields() {
        applyChangesBtn = findViewById(R.id.maintain_apply_changes_btn);
        name = findViewById(R.id.maintain_product_name);
        price = findViewById(R.id.maintain_product_price);
        description = findViewById(R.id.maintain_product_description);
        productImage = findViewById(R.id.maintain_product_image);
        productID = getIntent().getStringExtra("pid");

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    pPrice = pPrice.replaceAll("\\D+","");
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(productImage);

                    applyChangesBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyChanges();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();

        if (pName.equals("")) {
            Toast.makeText(this, "Product name cannot be left blank", Toast.LENGTH_SHORT).show();
        }
        if (pPrice.equals("")) {
            Toast.makeText(this, "Product price cannot be left blank", Toast.LENGTH_SHORT).show();
        }
        if (pDescription.equals("")) {
            Toast.makeText(this, "Product description cannot be left blank", Toast.LENGTH_SHORT).show();
        }
        else {
            final HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productID);
            productMap.put("pname", pName);
            productMap.put("price", pPrice + "$");
            productMap.put("description", pDescription);

            productsRef.updateChildren(productMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(AdminMaintainProductActivity.this,
                            "Changes applied successfully",
                            Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent (AdminMaintainProductActivity.this, SellerAddCategoryActivity.class);
//                    startActivity(intent);
//                    finish();
                }
            });
        }

    }
}
