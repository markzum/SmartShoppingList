package com.appzum.smartshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private FirebaseAuth mAuth;
    private Toolbar toolbar;

    public static String family_name;
    public static String family_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

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

        // Start new product activity
        Button addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener((View v) -> {
            Intent intent = new Intent(this, AddNewProductActivity.class);
            startActivityForResult(intent, 1);

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
                    return;
                }
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

                TextView listIsEmptyTW = findViewById(R.id.listIsEmptyTW);
                if (!products.isEmpty()) {
                    listIsEmptyTW.setText("");
                } else {
                    listIsEmptyTW.setText("Здесь ничего нет");
                }
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

        String new_id = CreateId.createId();
        mDatabase.child("products").child(new_id)
                .setValue(new Product(new_id,
                        name_of_product,
                        description_of_product,
                        "need"));

        Toast.makeText(this, "Товар успешно добавлен!", Toast.LENGTH_SHORT).show();
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
                            mDatabase.child("products").child(product.getId()).setValue(product);
                        }

                    }
                }
            });
            Toast.makeText(this, "Покупка успешно завершена!", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_show_profile){
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
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            SharedPreferences sPref = getSharedPreferences("family", MODE_PRIVATE);
            String family_name = sPref.getString("family_name", "none");
            String family_password = sPref.getString("family_password", "none");
            String is_first_launch = sPref.getString("is_first_launch", "false");
            String is_created = sPref.getString("is_created", "false");
            if (is_created.equals("true")){

                SharedPreferences.Editor sPrefEdit = sPref.edit();
                sPrefEdit.putString("is_created", "false");
                sPrefEdit.apply();
                new AlertDialog.Builder(this)
                        .setTitle("Семья создана!")
                        .setMessage("Семья \"" + family_name + "\" создана!\n" +
                                "Пароль семьи: " + family_password)
                        .setPositiveButton(android.R.string.ok, null).show();
                        // .setIcon(android.R.drawable.ic_dialog_alert)

            }
            if (is_first_launch.equals("true")){
                SharedPreferences.Editor sPrefEdit = sPref.edit();
                sPrefEdit.putString("is_first_launch", "false");
                sPrefEdit.apply();
                new AlertDialog.Builder(this)
                        .setTitle("Добро пожаловать!")
                        .setMessage("Это приложение \"Что купить" +
                                "\"!\nВы можете добавлять товары в список покупок с помощью " +
                                "кнопки в правом нижнем углу экрана и удалять товары удержанием " +
                                "кнопки удаления напротив названия товара. \nОтмечайте товары, " +
                                "которые кладете в корзину. \nПосле оплаты покупок нажмите на " +
                                "кнопку \"Завершить покупку\" (галочка) в верху экрана, чтобы " +
                                "Товары \"в корзине\" отметились, как купленные.\nВсе изменения " +
                                "в списке покупок синхронизируются на всех устройствах!")
                        .setPositiveButton(android.R.string.ok, null).show();
                        // .setIcon(android.R.drawable.ic_dialog_alert)

            }
        }
    }
}