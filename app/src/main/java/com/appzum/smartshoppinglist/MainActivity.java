package com.appzum.smartshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {


    /*
     * need
     * picked
     * purchased
     */
    String TAG = "markzum";

    ArrayList<Product> products = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Toolbar toolbar;

    public static String family_name;
    public static String family_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
        family_name = sPref.getString("family_name", "none");
        family_password = sPref.getString("family_password", "none");

        if (family_name.equals("none") || family_password.equals("none")) {
            mAuth.signOut();
            return;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("smartShoppingList")
                .child("families").child(family_name).child(family_password);


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

        // Show new-product window
        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener((View v) -> {
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.edit_product, null);
            final EditText name_et_edit_product = promptsView.findViewById(R.id.name_et_edit_product);
            final EditText description_et_edit_product = promptsView.findViewById(R.id.description_et_edit_product);
            new AlertDialog.Builder(this)
                    .setTitle("Добавление товара")
                    .setView(promptsView)
                    .setPositiveButton(android.R.string.ok, (dialog2, which) -> {
                        /*Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name_et_edit_product.getText().toString());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);*/
                        String new_id = CreateId.createId();
                        String datetime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
                        mDatabase.child("products").child(new_id)
                                .setValue(new Product(new_id,
                                        name_et_edit_product.getText().toString(),
                                        description_et_edit_product.getText().toString(),
                                        "need",
                                        datetime,
                                        datetime,
                                        mAuth.getCurrentUser().getEmail(),
                                        "false"));
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setCancelable(false).show();
        });


        // listen products change
        mDatabase.child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();
                if (!dataSnapshot.exists()) {
                    Toast.makeText(MainActivity.this, getString(R.string.db_error), Toast.LENGTH_SHORT).show();
                    SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
                    SharedPreferences.Editor sPrefEdit = sPref.edit();
                    sPrefEdit.putString("family_name", "none");
                    sPrefEdit.putString("family_password", "none");
                    sPrefEdit.putString("is_first_launch", "true");
                    sPrefEdit.apply();
                    mAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return;
                }
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String id = (String) messageSnapshot.child("id").getValue();
                    String name = (String) messageSnapshot.child("name").getValue();
                    String description = (String) messageSnapshot.child("description").getValue();
                    String status = (String) messageSnapshot.child("status").getValue();
                    String created = (String) messageSnapshot.child("created").getValue();
                    String edited = (String) messageSnapshot.child("edited").getValue();
                    String creator = (String) messageSnapshot.child("creator").getValue();
                    String purchased = (String) messageSnapshot.child("purchased").getValue();

                    if (!status.equals("debug")) {
                        products.add(new Product(id, name, description, status, created, edited,
                                creator, purchased));
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

                TextView listIsEmptyTW = findViewById(R.id.listIsEmptyTW);
                if (!products.isEmpty()) {
                    listIsEmptyTW.setText("");
                } else {
                    listIsEmptyTW.setText(R.string.there_is_nothing_here);
                }
                // Create RecyclerView
                RecyclerView recyclerView = findViewById(R.id.recycler1);
                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                recyclerView.setLayoutManager(llm);
                ShoppingListAdapter adapter = new ShoppingListAdapter(MainActivity.this,
                        products);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Finish purchase
        if (id == R.id.action_finish_purchase) {
            ArrayList<Product> temp_products = new ArrayList<>();

            mDatabase.child("products").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("markzum", "Error getting data", task.getException());
                    } else {
                        for (DataSnapshot messageSnapshot : task.getResult().getChildren()) {
                            String id = (String) messageSnapshot.child("id").getValue();
                            String name = (String) messageSnapshot.child("name").getValue();
                            String description = (String) messageSnapshot.child("description").getValue();
                            String status = (String) messageSnapshot.child("status").getValue();
                            String created = (String) messageSnapshot.child("created").getValue();
                            String edited = (String) messageSnapshot.child("edited").getValue();
                            String creator = (String) messageSnapshot.child("creator").getValue();

                            if (status.equals("picked")) {
                                temp_products.add(new Product(id, name, description,
                                        "purchased", created, edited, creator,
                                        new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date())));
                            }
                        }
                        for (Product product : temp_products) {
                            mDatabase.child("products").child(product.getId()).setValue(product);
                        }

                    }
                }
            });
            Toast.makeText(this, R.string.purchase_completed_successfully, Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_show_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, TutorialActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
            String family_name = sPref.getString("family_name", "none");
            String family_password = sPref.getString("family_password", "none");
            String is_created = sPref.getString("is_created", "false");
            if (is_created.equals("true")) {

                SharedPreferences.Editor sPrefEdit = sPref.edit();
                sPrefEdit.putString("is_created", "false");
                sPrefEdit.apply();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.family_created)
                        .setMessage(getString(R.string.it_is_name_of_family) + " " + family_name + "\n" +
                                getString(R.string.it_is_password_of_family) + " " + family_password)
                        .setPositiveButton(android.R.string.ok, null).show();
                // .setIcon(android.R.drawable.ic_dialog_alert)

            }
        }
    }
}