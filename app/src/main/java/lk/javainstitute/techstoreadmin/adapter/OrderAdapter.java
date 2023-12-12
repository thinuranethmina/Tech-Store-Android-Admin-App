package lk.javainstitute.techstoreadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import lk.javainstitute.techstoreadmin.R;
import lk.javainstitute.techstoreadmin.listener.OrderSelectListner;
import lk.javainstitute.techstoreadmin.model.Order;
import lk.javainstitute.techstoreadmin.utill.Format;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<Order> orders;
    private Context context;
    private OrderSelectListner orderSelectListner;

    public OrderAdapter(ArrayList<Order> orders, Context context, OrderSelectListner orderSelectListner) {
        this.orders = orders;
        this.context = context;
        this.orderSelectListner = orderSelectListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Order order = orders.get(position);

        holder.orderIdTxt.setText(order.getId());
        holder.orderDateTxt.setText(order.getDate_time());
        holder.orderPriceTxt.setText("Rs. " + new Format(order.getTotal()).formatPrice() + "/=");
        holder.orderStatusTxt.setText(String.valueOf((order.getDeliver_status())).equals("1") ? "Delivered" : "Pending");

        if (order.getDeliver_status() == 1) {
            holder.deliverBtn.setVisibility(View.GONE);
        } else {
            holder.deliverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderSelectListner.markAsDelivered(orders.get(position),holder.deliverBtn,holder.orderStatusTxt);
                }
            });
        }

        holder.orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderSelectListner.selectOrder(orders.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt, orderPriceTxt, orderStatusTxt;
        CardView orderCard;
        Button deliverBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderId);
            orderDateTxt = itemView.findViewById(R.id.datetime);
            orderPriceTxt = itemView.findViewById(R.id.price);
            orderStatusTxt = itemView.findViewById(R.id.status);
            orderCard = itemView.findViewById(R.id.orderCard);
            deliverBtn = itemView.findViewById(R.id.deliverBtn);
        }
    }
}
