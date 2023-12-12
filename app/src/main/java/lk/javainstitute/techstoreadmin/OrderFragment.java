package lk.javainstitute.techstoreadmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lk.javainstitute.techstoreadmin.adapter.OrderAdapter;
import lk.javainstitute.techstoreadmin.listener.OrderSelectListner;
import lk.javainstitute.techstoreadmin.model.Order;


public class OrderFragment extends Fragment  implements OrderSelectListner {

    private View view;
    private ArrayList<Order> orders;
    private FirebaseFirestore firestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_order, container, false);

        firestore = FirebaseFirestore.getInstance();

        orders = new ArrayList<>();
        RecyclerView categoryView = view.findViewById(R.id.orderRecyclerView);
        OrderAdapter ordersAdapter = new OrderAdapter(orders, getActivity().getApplicationContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        categoryView.setLayoutManager(linearLayoutManager);
        categoryView.setAdapter(ordersAdapter);


        firestore.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
                                Order order = snapshot1.toObject(Order.class);
                                orders.add(order);
                            }

                            ordersAdapter.notifyDataSetChanged();


                        } else {

                        }
                    }
                });

        return view;
    }

    @Override
    public void selectOrder(Order order) {
        startActivity(new Intent(getActivity().getApplicationContext(), OrderItemsActivity.class)
                .putExtra("orderID",order.getId())
        );
    }

    @Override
    public void markAsDelivered(Order order, Button button, TextView view) {
        firestore.collection("orders")
                .whereEqualTo("id", order.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> updates = new HashMap<>();
                                updates.put("deliver_status", 1);

                            for(QueryDocumentSnapshot snapshot:task.getResult()){
                                String orderId=snapshot.getId();

                                firestore.collection("orders").document(orderId).update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getActivity().getApplicationContext(), "Order Status Updated", Toast.LENGTH_SHORT).show();
                                                button.setVisibility(View.GONE);
                                                view.setText("Delivered");
                                            }
                                        });

                            }

                        }
                    }
                });
    }
}