package com.appzum.smartshoppinglist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ProductViewHolder> {

    private final String TAG = "markzum";
    int row_index = -1;

    private List<Product> products;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context main_context;


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productDescription;
        CheckBox checkBox;
        ConstraintLayout rootLayout;
        ImageView showProductMenuBtn;
        View view;

        ProductViewHolder(View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.productNameTextView);
            productDescription = (TextView) itemView.findViewById(R.id.productDescriptionTextView);
            checkBox = (CheckBox) itemView.findViewById(R.id.productCheckBox);
            rootLayout = (ConstraintLayout) itemView.findViewById(R.id.rootLayout);
            showProductMenuBtn = itemView.findViewById(R.id.showProductMenuBtn);
            view = itemView;
        }
    }

    public ShoppingListAdapter(Context context, List<Product> products) {
        this.products = products;
        this.main_context = context;

        mDatabase = FirebaseDatabase.getInstance().getReference("smartShoppingList")
                .child("families").child(MainActivity.family_name).child(MainActivity.family_password);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product, viewGroup, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder productViewHolder, int i) {
        // Set listener on long click
        productViewHolder.view.setOnLongClickListener(view -> {
            showProductMenu(i);
            return true;
        });

        // Set listener on menu button
        productViewHolder.showProductMenuBtn.setOnClickListener((View v) -> {
            showProductMenu(i);
        });

        final boolean[] x = {false};
        productViewHolder.productName.setText(products.get(i).getName());
        productViewHolder.productDescription.setText(products.get(i).getDescription());
        CheckBox checkBox = productViewHolder.checkBox;
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (x[0]) {
                x[0] = false;
                return;
            }

            if (products.get(i).getStatus().equals("purchased")){
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
                    local_status,
                    products.get(i).getCreated(),
                    products.get(i).getEdited(),
                    products.get(i).getCreator(),
                    products.get(i).getPurchased());

            // Adding product to DB
            mDatabase.child("products").child(old_product_id).setValue(new_product);

            if (local_status.equals("picked")) {
                productViewHolder.rootLayout.setBackgroundResource(R.color.picked_product_bg);
                productViewHolder.productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                productViewHolder.productDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                productViewHolder.rootLayout.setBackgroundResource(R.color.need_product_bg);
                productViewHolder.productName.setPaintFlags(0);
                productViewHolder.productDescription.setPaintFlags(0);
            }
        });


        if (products.get(i).getStatus().equals("picked")) {
            productViewHolder.rootLayout.setBackgroundResource(R.color.picked_product_bg);
            productViewHolder.productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            productViewHolder.productDescription.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            x[0] = true;
            checkBox.setChecked(true);
        } else if (products.get(i).getStatus().equals("purchased")){
            productViewHolder.rootLayout.setBackgroundResource(R.color.purchased_product_bg);
            productViewHolder.productName.setTypeface(null, Typeface.ITALIC);
            productViewHolder.productName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            productViewHolder.productDescription.setTypeface(null, Typeface.ITALIC);
            productViewHolder.productDescription.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            if (!checkBox.isChecked()) {
                x[0] = true;
                checkBox.setChecked(true);
            }
        } else {
            productViewHolder.rootLayout.setBackgroundResource(R.color.need_product_bg);
            productViewHolder.productName.setPaintFlags(0);
            productViewHolder.productDescription.setPaintFlags(0);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void showProductMenu(int i) {
        CharSequence[] items;
        if (!products.get(i).getStatus().equals("purchased")) {
            items = new CharSequence[] {
                    main_context.getString(R.string.information),
                    main_context.getString(R.string.edit),
                    main_context.getString(R.string.delete)};
        } else {
            items = new CharSequence[] {
                    main_context.getString(R.string.information),
                    main_context.getString(R.string.duplicate_and_restore),
                    main_context.getString(R.string.delete)};
        }

        new AlertDialog.Builder(main_context)
                .setItems(items,
                        (dialog, item) -> {
                            switch (item) {
                                case 0:
                                    String textOfInfo = "Наименование: " + products.get(i).getName();

                                    if (!products.get(i).getDescription().equals("")) {
                                        textOfInfo += "\n\n" +
                                                "Примечание: " + products.get(i).getDescription();
                                    }

                                    textOfInfo += "\n\n" +
                                            "Создан: " + products.get(i).getCreated() + "\n\n" +
                                            "Изменен: " + products.get(i).getEdited()  + "\n\n" +
                                            "Создатель: " + products.get(i).getCreator();

                                    if (products.get(i).getStatus().equals("purchased")) {
                                        textOfInfo += "\n\n" +
                                                "Куплено: " + products.get(i).getPurchased();
                                    }
                                    new AlertDialog.Builder(main_context)
                                            .setTitle(main_context.getString(R.string.information))
                                            .setMessage(textOfInfo)
                                            .setPositiveButton(android.R.string.ok, null).show();
                                    break;
                                case 1:
                                    if (!products.get(i).getStatus().equals("purchased")) {
                                        LayoutInflater li = LayoutInflater.from(main_context);
                                        View promptsView = li.inflate(R.layout.edit_product, null);
                                        final EditText name_et_edit_product = promptsView.findViewById(R.id.name_et_edit_product);
                                        name_et_edit_product.setText(products.get(i).getName());
                                        final EditText description_et_edit_product = promptsView.findViewById(R.id.description_et_edit_product);
                                        description_et_edit_product.setText(products.get(i).getDescription());
                                        new AlertDialog.Builder(main_context)
                                                .setView(promptsView)
                                                .setPositiveButton(android.R.string.ok, (dialog2, which) -> {
                                                    mDatabase.child("products").child(products.get(i).getId())
                                                            .child("name").setValue(name_et_edit_product.getText().toString());
                                                    mDatabase.child("products").child(products.get(i).getId())
                                                            .child("description").setValue(description_et_edit_product.getText().toString());
                                                    mDatabase.child("products").child(products.get(i).getId())
                                                            .child("edited").setValue(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date()));
                                                })
                                                .setNegativeButton(android.R.string.cancel, null)
                                                .setCancelable(false).show();
                                    } else {
                                        String new_id = CreateId.createId();
                                        String datetime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
                                        mDatabase.child("products").child(new_id)
                                                .setValue(new Product(new_id,
                                                        products.get(i).getName(),
                                                        products.get(i).getDescription(),
                                                        "need",
                                                        datetime,
                                                        datetime,
                                                        mAuth.getCurrentUser().getEmail(),
                                                        "false"));
                                    }
                                    break;
                                case 2:
                                    new AlertDialog.Builder(main_context)
                                            .setTitle("Подтверждение")
                                            .setMessage("Вы действительно хотить удалить товар из списка покупок?")
                                            .setPositiveButton(android.R.string.yes, (dialog2, which) -> {
                                                mDatabase.child("products").child(products.get(i).getId()).removeValue();
                                            })
                                            .setNegativeButton(android.R.string.no, null).show();
                                    break;
                            }
                        }).show();
    }
}