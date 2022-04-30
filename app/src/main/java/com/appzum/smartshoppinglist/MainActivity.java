package com.appzum.smartshoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    /*
     * need
     * picked
     * purchased
     */
    String TAG = "markzum";
    /*
    public FirebaseFirestore db;
    public ArrayList<Product> products = new ArrayList<Product>();*/
    ArrayList<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("smartShoppingList");
        // mDatabase.child("products").child("asd").removeValue();

        /*String new_id = CreateId.createId();
        mDatabase.child("smartShoppingList").child("products").child(new_id).setValue(new Product(new_id, "Banana", "I like banana", "need"));
        */

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
        });
        /*
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        Log.i(TAG, "onCreate: Start");
        db.collection("smartShoppingList")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String loadDataName = "none";
                                String loadDataDescription = "none";
                                String loadDataStatus = "need";
                                for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
                                    String key = entry.getKey();
                                    Object val = entry.getValue();
                                    switch (key) {
                                        case "name":
                                            loadDataName = (String) val;
                                            break;
                                        case "description":
                                            loadDataDescription = (String) val;
                                            break;
                                        case "status":
                                            loadDataStatus = (String) val;
                                            break;
                                    }
                                }
                                products.add(new Product(document.getId(),
                                        loadDataName,
                                        loadDataDescription,
                                        loadDataStatus));

                            }

                            RecyclerView recyclerView = findViewById(R.id.recycler1);
                            LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                            recyclerView.setLayoutManager(llm);
                            ShoppingListAdapter adapter = new ShoppingListAdapter(products, db);
                            recyclerView.setAdapter(adapter);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        Log.i(TAG, "onCreate: " + products.isEmpty());
        Log.i(TAG, "onCreate: Stop");*/

    }
}