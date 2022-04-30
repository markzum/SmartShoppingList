package com.appzum.smartshoppinglist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

        ProductViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.productNameTextView);
            productDescription = (TextView) itemView.findViewById(R.id.productDescriptionTextView);
            checkBox = (CheckBox) itemView.findViewById(R.id.productCheckBox);
            rootLayout = (ConstraintLayout) itemView.findViewById(R.id.rootLayout);
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
        Log.i(TAG, "onBindViewHolder: start2");
        // Log.i(TAG, products.toString());
        final boolean[] x = {false};
        productViewHolder.productName.setText(products.get(i).getName());
        productViewHolder.productDescription.setText(products.get(i).getDescription());
        CheckBox checkBox = productViewHolder.checkBox;
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (x[0]) {
                x[0] = false;
                Log.i(TAG, "onBindViewHolder: return");
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
            Product new_product = new Product(new_product_id,
                    products.get(i).getName(),
                    products.get(i).getDescription(),
                    local_status);

            // Adding product to DB
            mDatabase.child("products").child(new_product_id).setValue(new_product);

            // Adding product
            products.add(new_product);

            // Deleting old product
            Iterator<Product> productIterator = products.iterator();
            while(productIterator.hasNext()) {
                Product nextProduct = productIterator.next();
                if (nextProduct.getId().equals(old_product_id)) {
                    productIterator.remove();
                }
            }

            // Deleting old product from DB
            mDatabase.child("products").child(old_product_id).removeValue();

            /*db.collection("smartShoppingList")
                    .add(new_product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                            Log.i(TAG, "onBindViewHolder: " + products.get(i).getName());
                            Product new_product_for_list = new Product(documentReference.getId(),
                                    products.get(i).getName(),
                                    products.get(i).getDescription(),
                                    local_status);
                            products.add(new_product_for_list);
                            Log.i(TAG, "onSuccess: 1");
                            Log.i(TAG, "onSuccess: 1");

                            Iterator<Product> catIterator = products.iterator();
                            while(catIterator.hasNext()) {
                                Product nextCat = catIterator.next();
                                if (nextCat.getId().equals(old_product_id)) {
                                    catIterator.remove();
                                }
                            }
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query applesQuery = ref.child("firebase-test").orderByChild("title").equalTo("Apple");
                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                        appleSnapshot.getRef().removeValue();
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(TAG, "onCancelled", databaseError.toException());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });*/


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
        if (products.get(i).getStatus().equals("picked")) {
            productViewHolder.rootLayout.setBackgroundColor(Color.parseColor("#bbbbbb"));
            productViewHolder.productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            productViewHolder.productDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            x[0] = true;
            checkBox.setChecked(true);
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