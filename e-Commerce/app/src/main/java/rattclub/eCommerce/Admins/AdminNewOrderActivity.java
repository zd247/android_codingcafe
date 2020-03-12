package rattclub.eCommerce.Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rattclub.eCommerce.Model.AdminOrders;
import rattclub.eCommerce.R;

public class AdminNewOrderActivity extends AppCompatActivity {
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        ordersList = findViewById(R.id.order_list);
        ordersList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ordersList.setLayoutManager(layoutManager);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(ordersRef, AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int i, @NonNull final AdminOrders adminOrders) {
                        holder.userName.setText("Name: " + adminOrders.getName());
                        holder.userPhoneNumber.setText("Phone: " + adminOrders.getPhone());
                        holder.userTotalPrice.setText("Total price: " + adminOrders.getTotalPrice() + "$");
                        holder.userDateTime.setText("Name: " + adminOrders.getDate());
                        holder.userShippingAddress.setText("Name: " + adminOrders.getAddress());

                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uID = getRef(i).getKey();
                                Intent intent = new Intent (AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{
        public TextView userName, userPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
        public Button showOrderBtn;


        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            showOrderBtn = itemView.findViewById(R.id.order_show_products_btn);

        }
    }
}
