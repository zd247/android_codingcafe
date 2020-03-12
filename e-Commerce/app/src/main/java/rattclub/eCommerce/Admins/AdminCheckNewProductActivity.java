package rattclub.eCommerce.Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import rattclub.eCommerce.Model.Product;
import rattclub.eCommerce.R;
import rattclub.eCommerce.ViewHolder.ProductViewHolder;

public class AdminCheckNewProductActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_new_product);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = findViewById(R.id.admin_product_checklist);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(unverifiedProductsRef.orderByChild("productState").equalTo("Not Approved"), Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product product) {
                holder.txtProductName.setText(product.getPname());
                holder.txtProductDescription.setText(product.getDescription());
                holder.txtProductPrice.setText("Price = " + product.getPrice());

                Picasso.get().load(product.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String productID = product.getPid();

                        CharSequence options[] = new CharSequence[] {
                                "Yes",
                                "No"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminCheckNewProductActivity.this);
                        builder.setTitle("Do you want to approve this product ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    changeProductState(productID);
                                }
                                if (which == 1) {

                                }
                            }
                        });

                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;

            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void changeProductState(String productID) {
        unverifiedProductsRef.child(productID)
                .child("productState").setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminCheckNewProductActivity.this
                                    , "Item approved, item is now available for sale"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
