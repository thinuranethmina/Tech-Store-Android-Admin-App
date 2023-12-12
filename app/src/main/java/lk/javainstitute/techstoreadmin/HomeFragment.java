package lk.javainstitute.techstoreadmin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Iterator;

import lk.javainstitute.techstoreadmin.model.Product;
import lk.javainstitute.techstoreadmin.model.User;

public class HomeFragment extends Fragment {

    View view;

    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();

        Picasso.get()
                .load(R.drawable.product_icon)
                .into((ImageView)view.findViewById(R.id.pendingProductView));

        Picasso.get()
                .load(R.drawable.user_icon)
                .into((ImageView)view.findViewById(R.id.activeUserView));

        Picasso.get()
                .load(R.drawable.order_icon)
                .into((ImageView)view.findViewById(R.id.pendingOrderView));

        view.findViewById(R.id.addProductBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddProductActivity.class));
            }
        });

        view.findViewById(R.id.viewProductBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ViewProductActivity.class));
            }
        });

        firestore.collection("orders")
                .whereEqualTo("deliver_status",0)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        TextView order = view.findViewById(R.id.orders);
                        order.setText(String.valueOf(queryDocumentSnapshots.size()));
                    }
                });

        firestore.collection("products")
                .whereEqualTo("status","true")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        TextView products = view.findViewById(R.id.products);
                        products.setText(String.valueOf(queryDocumentSnapshots.size()));
                    }
                });

        firestore.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        TextView users = view.findViewById(R.id.users);
                        users.setText(String.valueOf(queryDocumentSnapshots.size()));
                    }
                });

        return view;


    }
}