package lk.javainstitute.techstoreadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lk.javainstitute.techstoreadmin.adapter.ProductAdapter;
import lk.javainstitute.techstoreadmin.listener.ProductSelectListener;
import lk.javainstitute.techstoreadmin.model.Category;
import lk.javainstitute.techstoreadmin.model.Product;

public class ViewProductActivity extends AppCompatActivity implements ProductSelectListener {

    private FirebaseStorage storage;

    private FirebaseFirestore firestore;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//============================ LOAD PRODUCTS ==================================================

        products = new ArrayList<>();

        RecyclerView itemView = findViewById(R.id.cardRecyclerView);

        ProductAdapter productAdapter = new ProductAdapter(products, ViewProductActivity.this,this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        itemView.setLayoutManager(linearLayoutManager);

        itemView.setAdapter(productAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("products").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentChange change : value.getDocumentChanges()) {
                            Product product = change.getDocument().toObject(Product.class);
                            switch (change.getType()) {
                                case ADDED:
                                    products.add(product);
                                case MODIFIED:
                                    for (Product existingProduct : products) {
                                        if (existingProduct.getId().equals(product.getId())) {
                                            existingProduct.setTitle(product.getTitle());
                                            existingProduct.setPrice(product.getPrice());
                                            existingProduct.setImage1(product.getImage1());
                                            break;
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    Iterator<Product> iterator = products.iterator();
                                    while (iterator.hasNext()) {
                                        Product existingProduct = iterator.next();
                                        if (existingProduct.getId().equals(product.getId())) {
                                            iterator.remove(); // Remove the specific object reference
                                            break;
                                        }
                                    }
                            }
                        }

                        productAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();


    }


    @Override
    public void editProduct(Product product) {
        startActivity(new Intent(ViewProductActivity.this, UpdateProductActivity.class).putExtra("productID", product.getId()));

    }

    @Override
    public void changeStatus(Product product) {
        String status = product.getStatus().toString();
        Map<String, Object> updates = new HashMap<>();
        if(status.equals("true")){
            updates.put("status","false");
        }else{
            updates.put("status","true");
        }

        firestore.collection("products")
                .whereEqualTo("id", product.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> documentIds = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentId = document.getId();
                                firestore.collection("products").document(documentId).update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if(status.equals("true")){
                                                    Toast.makeText(getApplicationContext(), "Product deactive", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Product active", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                });
    }

}