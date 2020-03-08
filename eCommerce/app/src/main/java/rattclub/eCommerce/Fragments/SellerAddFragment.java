package rattclub.eCommerce.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import rattclub.eCommerce.R;
import rattclub.eCommerce.Sellers.SellerAddNewProductActivity;

public class SellerAddFragment extends Fragment {
    View root;
    private ImageView tShirts, sportsTShirts, femaleDresses, sweaters,
            glasses, hatsCaps, walletsBagsPurses, shoes,
            headPhonesHandFree, laptops, watches, mobilePhones;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_seller, container, false);

        InitializeFields();

        InitializeOnClicks();

        return root;
    }

    private void InitializeFields() {
        tShirts = root.findViewById(R.id.t_shirts);
        sportsTShirts =  root.findViewById(R.id.sports_t_shirts);
        femaleDresses = root.findViewById(R.id.female_dresses);
        sweaters =  root.findViewById(R.id.sweaters);

        glasses = root.findViewById(R.id.glasses);
        hatsCaps = root.findViewById(R.id.hats_caps);
        walletsBagsPurses = root.findViewById(R.id.purses_bags_wallets);
        shoes = root.findViewById(R.id.shoes);

        headPhonesHandFree = root.findViewById(R.id.headphones_handfree);
        laptops = root.findViewById(R.id.laptop_pc);
        watches = root.findViewById(R.id.watches);
        mobilePhones = root.findViewById(R.id.mobilephones);
    }

    private void InitializeOnClicks() {
        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "tShirts");
                startActivity(intent);
            }
        });

        sportsTShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Sports tShirts");
                startActivity(intent);
            }
        });

        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Female Dresses");
                startActivity(intent);
            }
        });


        sweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Sweaters");
                startActivity(intent);
            }
        });


        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Glasses");
                startActivity(intent);
            }
        });


        hatsCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Hats Caps");
                startActivity(intent);
            }
        });



        walletsBagsPurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Wallets Bags Purses");
                startActivity(intent);
            }
        });


        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Shoes");
                startActivity(intent);
            }
        });



        headPhonesHandFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "HeadPhones HandFree");
                startActivity(intent);
            }
        });


        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Laptops");
                startActivity(intent);
            }
        });


        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Watches");
                startActivity(intent);
            }
        });


        mobilePhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(root.getContext(), SellerAddNewProductActivity.class);
                intent.putExtra("category", "Mobile Phones");
                startActivity(intent);
            }
        });

    }


}
