package com.appzum.smartshoppinglist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ProductViewHolder> {

    private final String TAG = "markzum";
    int row_index = -1;

    private List<Product> products;
    private DatabaseReference mDatabase;


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productDescription;
        CheckBox checkBox;
        ConstraintLayout rootLayout;
        ImageView deleteBtn;

        ProductViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.productNameTextView);
            productDescription = (TextView) itemView.findViewById(R.id.productDescriptionTextView);
            checkBox = (CheckBox) itemView.findViewById(R.id.productCheckBox);
            rootLayout = (ConstraintLayout) itemView.findViewById(R.id.rootLayout);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    public ShoppingListAdapter(List<Product> products) {
        this.products = products;
        mDatabase = FirebaseDatabase.getInstance().getReference("smartShoppingList");
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product, viewGroup, false);
        ProductViewHolder pvh = new ProductViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder productViewHolder, int i) {
        final boolean[] x = {false};
        productViewHolder.productName.setText(products.get(i).getName());
        productViewHolder.productDescription.setText(products.get(i).getDescription());
        CheckBox checkBox = productViewHolder.checkBox;
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.i(TAG, "onBindViewHolder: pressed");
            if (x[0]) {
                x[0] = false;
                Log.i(TAG, "onBindViewHolder: return");
                return;
            }

            if (products.get(i).getStatus().equals("purchased")){
                Log.i(TAG, "onBindViewHolder: U'll not pass!!!");
                checkBox.setChecked(true);
                return;
            }

            String old_product_id = products.get(i).getId();

            // Creating new product
            String new_product_id = CreateId.createId();
            String local_status;
            if (products.get(i).getStatus().equals("need")) {
                local_status = "picked";
            } else {
                local_status = "need";
            }
            Product new_product = new Product(old_product_id,
                    products.get(i).getName(),
                    products.get(i).getDescription(),
                    local_status);

            // Adding product to DB
            mDatabase.child("products").child(old_product_id).setValue(new_product);

            // Adding product
            /*products.add(new_product);

            // Deleting old product
            Iterator<Product> productIterator = products.iterator();
            while (productIterator.hasNext()) {
                Product nextProduct = productIterator.next();
                if (nextProduct.getId().equals(old_product_id)) {
                    productIterator.remove();
                }
            }

            // Deleting old product from DB
            mDatabase.child("products").child(old_product_id).removeValue();*/

            if (local_status.equals("picked")) {
                productViewHolder.rootLayout.setBackgroundColor(Color.parseColor("#bbbbbb"));
                productViewHolder.productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                productViewHolder.productDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                productViewHolder.rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                productViewHolder.productName.setPaintFlags(0);
                productViewHolder.productDescription.setPaintFlags(0);
            }
        });


        // Set listener on delete button
        productViewHolder.deleteBtn.setOnLongClickListener((View v) -> {
            mDatabase.child("products").child(products.get(i).getId()).removeValue();
            return false;
        });


        if (products.get(i).getStatus().equals("picked")) {
            productViewHolder.rootLayout.setBackgroundColor(Color.parseColor("#bbbbbb"));
            productViewHolder.productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            productViewHolder.productDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            x[0] = true;
            checkBox.setChecked(true);
        } else if (products.get(i).getStatus().equals("purchased")){
            productViewHolder.rootLayout.setBackgroundColor(Color.parseColor("#20DF07"));
            productViewHolder.productName.setTypeface(null, Typeface.ITALIC);
            productViewHolder.productName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            productViewHolder.productDescription.setTypeface(null, Typeface.ITALIC);
            productViewHolder.productDescription.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            if (!checkBox.isChecked()) {
                x[0] = true;
                checkBox.setChecked(true);
            }
        } else {
            productViewHolder.rootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            productViewHolder.productName.setPaintFlags(0);
            productViewHolder.productDescription.setPaintFlags(0);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}