package com.appzum.smartshoppinglist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    /*
     * need
     * picked
     * purchased
     */
    String TAG = "markzum";

    ArrayList<Product> products = new ArrayList<>();
    private DatabaseReference mDatabase;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference("smartShoppingList");
        // mDatabase.child("products").child("asd").removeValue();

        /*String new_id = CreateId.createId();
        mDatabase.child("smartShoppingList").child("products").child(new_id).setValue(new Product(new_id, "Banana", "I like banana", "need"));
        */

        /*mDatabase.child("products").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("markzum", "Error getting data", task.getException());
                } else {
                    for (DataSnapshot messageSnapshot : task.getResult().getChildren()) {
                        Map<String, String> x = (Map<String, String>) messageSnapshot.getValue();
                        String id = "none";
                        String name = "none";
                        String description = "none";
                        String status = "need";
                        for (Map.Entry<String, String> entry : x.entrySet()) {
                            String key = entry.getKey();
                            String val = entry.getValue();
                            switch (key){
                                case "id":
                                    id = val;
                                    break;
                                case "name":
                                    name = val;
                                    break;
                                case "description":
                                    description = val;
                                    break;
                                case "status":
                                    status = val;
                                    break;
                            }
                        }
                        products.add(new Product(id, name, description, status));
                    }

                    RecyclerView recyclerView = findViewById(R.id.recycler1);
                    LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(llm);
                    ShoppingListAdapter adapter = new ShoppingListAdapter(products);
                    recyclerView.setAdapter(adapter);
                }
            }
        });*/

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener((View v) -> {
            Log.i(TAG, "onCreate: Adding");

            Intent intent = new Intent(this, AddNewProduct.class);
            startActivityForResult(intent, 1);

        });


        // listen products change
        mDatabase.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                Map<String, HashMap<String, String>> x = (Map<String, HashMap<String, String>>) dataSnapshot.getValue();
                for (Map.Entry<String, HashMap<String, String>> entry : x.entrySet()) {
                    String key = entry.getKey();
                    HashMap<String, String> val = entry.getValue();

                    String id = "none";
                    String name = "none";
                    String description = "none";
                    String status = "need";
                    for (Map.Entry<String, String> entry2 : val.entrySet()) {
                        String key2 = entry2.getKey();
                        String val2 = entry2.getValue();
                        Log.i(TAG, "onDataChange: " + key2 + " - " + val2);
                        switch (key2) {
                            case "id":
                                id = val2;
                                break;
                            case "name":
                                name = val2;
                                break;
                            case "description":
                                description = val2;
                                break;
                            case "status":
                                status = val2;
                                break;
                        }
                    }
                    if (!status.equals("debug")) {
                        products.add(new Product(id, name, description, status));
                    }
                }

                // Sort products
                Collections.sort(products, new Comparator<Product>() {
                    @Override
                    public int compare(final Product lhs, Product rhs) {
                        switch (lhs.getStatus()) {
                            case "purchased":
                                if (rhs.getStatus().equals("purchased")) {
                                    return lhs.getName().compareTo(rhs.getName());
                                } else {
                                    return 1;
                                }
                            case "need":
                                if (rhs.getStatus().equals("need")) {
                                    return lhs.getName().compareTo(rhs.getName());
                                } else {
                                    return -1;
                                }
                            case "picked":
                                if (rhs.getStatus().equals("need")) {
                                    return 1;
                                } else if (rhs.getStatus().equals("purchased")) {
                                    return -1;
                                } else {
                                    return lhs.getName().compareTo(rhs.getName());
                                }
                            default:
                                return lhs.getName().compareTo(rhs.getName());
                        }
                    }
                });

                // Create RecyclerView
                RecyclerView recyclerView = findViewById(R.id.recycler1);
                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(llm);
                ShoppingListAdapter adapter = new ShoppingListAdapter(products);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String name_of_product = data.getStringExtra("name");
        String description_of_product = data.getStringExtra("description");
        Log.i(TAG, "onActivityResult: " + name_of_product + " " + description_of_product);

        String new_id = CreateId.createId();
        mDatabase.child("products").child(new_id)
                .setValue(new Product(new_id,
                        name_of_product,
                        description_of_product,
                        "need"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_finish_purchase) {
            ArrayList<Product> temp_products = new ArrayList<>();

            mDatabase.child("products").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("markzum", "Error getting data", task.getException());
                    } else {
                        for (DataSnapshot messageSnapshot : task.getResult().getChildren()) {
                            Map<String, String> x = (Map<String, String>) messageSnapshot.getValue();
                            String id = "none";
                            String name = "none";
                            String description = "none";
                            String status = "need";
                            for (Map.Entry<String, String> entry : x.entrySet()) {
                                String key = entry.getKey();
                                String val = entry.getValue();
                                switch (key) {
                                    case "id":
                                        id = val;
                                        break;
                                    case "name":
                                        name = val;
                                        break;
                                    case "description":
                                        description = val;
                                        break;
                                    case "status":
                                        status = val;
                                        break;
                                }
                            }
                            if (status.equals("picked")) {
                                temp_products.add(new Product(id, name, description, "purchased"));
                            }
                        }
                        for (Product product : temp_products) {
                            Log.i(TAG, "onComplete: " + product.getId());
                            mDatabase.child("products").child(product.getId()).setValue(product);
                        }

                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}