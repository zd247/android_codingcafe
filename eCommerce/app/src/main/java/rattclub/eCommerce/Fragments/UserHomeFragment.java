package rattclub.eCommerce.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import rattclub.eCommerce.Admins.AdminMaintainProductActivity;
import rattclub.eCommerce.Model.Product;
import rattclub.eCommerce.Users.ProductDetailsActivity;
import rattclub.eCommerce.R;
import rattclub.eCommerce.ViewHolder.ProductViewHolder;

public class UserHomeFragment extends Fragment {
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference productsRef;
    private String type = "";

    public UserHomeFragment() { }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_user, container, false);

        InitializeFields(root);

        return root;
    }

    private void InitializeFields(View root) {
        recyclerView = root.findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = intent.getExtras().get("Admin").toString();
        }


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(productsRef.orderByChild("productState").equalTo("Approved"), Product.class)
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int i, @NonNull final Product product) {
                        holder.txtProductName.setText(product.getPname());
                        holder.txtProductDescription.setText(product.getDescription());
                        holder.txtProductPrice.setText("Price = " + product.getPrice());

                        Picasso.get().load(product.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (type.equals("Admin")) {
                                    Intent intent = new Intent(UserHomeFragment.this.getContext(), AdminMaintainProductActivity.class);
                                    intent.putExtra("pid", product.getPid());
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(UserHomeFragment.this.getContext(), ProductDetailsActivity.class);
                                    intent.putExtra("pid", product.getPid());
                                    startActivity(intent);
                                }

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
}
