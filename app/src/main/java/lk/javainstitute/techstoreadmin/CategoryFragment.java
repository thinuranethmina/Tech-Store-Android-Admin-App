package lk.javainstitute.techstoreadmin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import lk.javainstitute.techstoreadmin.adapter.CategoryAdapter;
import lk.javainstitute.techstoreadmin.listener.CategorySelectListener;
import lk.javainstitute.techstoreadmin.model.Category;

public class CategoryFragment extends Fragment  {

    View view;

    private FirebaseStorage storage;

    private FirebaseFirestore firestore;

    private ImageButton imageButton;
    private Uri imagePath;

    private ArrayList<Category> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_category, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        imageButton = view.findViewById(R.id.addCategoryImageBtn);

        categories = new ArrayList<>();

        Picasso.get()
                .load(R.drawable.add_image)
                .resize(150, 150)
                .into((ImageView) view.findViewById(R.id.addCategoryImageBtn));


//============================ LOAD CATEGORY ==================================================

        RecyclerView itemView = view.findViewById(R.id.categoryRecyclerView);

        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        itemView.setLayoutManager(linearLayoutManager);

        itemView.setAdapter(categoryAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection("categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentChange change : value.getDocumentChanges()) {
                            Category category = change.getDocument().toObject(Category.class);
                            switch (change.getType()) {
                                case ADDED:
                                    categories.add(category);
                                case MODIFIED:
                                    for (Category existingCategory : categories) {
                                        if (existingCategory.getId().equals(category.getId())) {
                                            existingCategory.setName(category.getName());
                                            existingCategory.setImageId(category.getImageId());
                                            break;
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    Iterator<Category> iterator = categories.iterator();
                                    while (iterator.hasNext()) {
                                        Category existingCategory = iterator.next();
                                        if (existingCategory.getId().equals(category.getId())) {
                                            iterator.remove(); // Remove the specific object reference
                                            break;
                                        }
                                    }
                            }
                        }

                        categoryAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();




//============================ ADD CATEGORY ==================================================

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(Intent.createChooser(intent, "Select Image"));
            }
        });


        view.findViewById(R.id.categoryAddBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText name = view.findViewById(R.id.categoryNameInsert);
                String categoryName = name.getText().toString();

                String imageId = UUID.randomUUID().toString();

                if (categoryName.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter Category Name.", Toast.LENGTH_LONG).show();
                } else if (imagePath == null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please select icon for category.", Toast.LENGTH_LONG).show();
                } else {

                    ProgressDialog dialog = new ProgressDialog(getActivity());
                    dialog.setMessage("Added new item...");
                    dialog.setCancelable(false);
                    dialog.show();

                    String ref = String.valueOf(System.currentTimeMillis());

                    firestore.collection("categories").add(new Category(ref,categoryName, imageId))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    dialog.setMessage("Uploading image...");

                                    StorageReference reference = storage.getReference("category-images")
                                            .child(imageId);

                                    reference.putFile(imagePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            name.setText("");
                                            imagePath = null;
                                            Picasso.get()
                                                    .load(R.drawable.add_image)
                                                    .resize(150, 150)
                                                    .into((ImageView) view.findViewById(R.id.addCategoryImageBtn));
                                            dialog.dismiss();
                                            Toast.makeText(getActivity().getApplicationContext(), "New category has been added to database.", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            imagePath = null;
                                            Picasso.get()
                                                    .load(R.drawable.add_image)
                                                    .resize(150, 150)
                                                    .into((ImageView) view.findViewById(R.id.addCategoryImageBtn));
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                            dialog.setMessage("Image Uploading " + (int) progress + "%");
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    name.setText("");
                                    imagePath = null;
                                    Picasso.get()
                                            .load(R.drawable.add_image)
                                            .resize(150, 150)
                                            .into((ImageView) view.findViewById(R.id.addCategoryImageBtn));
                                    dialog.dismiss();
                                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                                }
                            });
                }

            }
        });


        return view;


    }


    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        imagePath = result.getData().getData();

                        Picasso.get()
                                .load(imagePath)
                                .resize(150, 150)
                                .into(imageButton);


                    } else {
                        imagePath = null;
                    }
                }
            }
    );

}