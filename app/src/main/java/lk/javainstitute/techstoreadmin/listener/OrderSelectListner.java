package lk.javainstitute.techstoreadmin.listener;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import lk.javainstitute.techstoreadmin.model.Order;

public interface OrderSelectListner {
    void selectOrder(Order order);
    void markAsDelivered(Order order, Button button, TextView status);
}
