package rattclub.eCommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import rattclub.eCommerce.Interface.ItemClickListener;
import rattclub.eCommerce.R;

public class SellerItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductStatus;
    public ImageView imageView;
    public ItemClickListener listener;


    public SellerItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.seller_product_image);
        txtProductName = itemView.findViewById(R.id.seller_product_name);
        txtProductDescription = itemView.findViewById(R.id.seller_product_description);
        txtProductPrice = itemView.findViewById(R.id.seller_product_price);
        txtProductStatus = itemView.findViewById(R.id.seller_product_state);

    }

    public void setItemClickListener (ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getAdapterPosition(), false);
    }
}
