package rattclub.eCommerce.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import rattclub.eCommerce.Admins.AdminCheckNewProductActivity;
import rattclub.eCommerce.Model.Product;
import rattclub.eCommerce.R;
import rattclub.eCommerce.ViewHolder.ProductViewHolder;
import rattclub.eCommerce.ViewHolder.SellerItemViewHolder;

public class SellerHomeFragment extends Fragment {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productsRef;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home_seller, container, false);

        recyclerView = root.findViewById(R.id.seller_products_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(productsRef.orderByChild("sid")
                        .equalTo(FirebaseAuth.getInstance()
                                .getCurrentUser().getUid()), Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, SellerItemViewHolder> adapter = new FirebaseRecyclerAdapter<Product, SellerItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellerItemViewHolder holder, int position, @NonNull final Product product) {
                holder.txtProductName.setText(product.getPname());
                holder.txtProductDescription.setText(product.getDescription());
                holder.txtProductPrice.setText("Price = " + product.getPrice());
                holder.txtProductStatus.setText(product.getProductState());

                Picasso.get().load(product.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String productID = product.getPid();

                        CharSequence options[] = new CharSequence[] {
                                "Yes",
                                "No"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                        builder.setTitle("Do you want to delete this product ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    deleteProduct(productID);
                                }
                                if (which == 1) { }
                            }
                        });

                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public SellerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.seller_item_view, parent, false);
                SellerItemViewHolder holder = new SellerItemViewHolder(view);
                return holder;

            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProduct(String productID) {
        productsRef.child(productID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(root.getContext()
                                    , "Item approved, item is now available for sale"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
