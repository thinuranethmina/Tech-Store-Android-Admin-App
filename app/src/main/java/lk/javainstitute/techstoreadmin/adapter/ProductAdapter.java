package lk.javainstitute.techstoreadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.javainstitute.techstoreadmin.R;
import lk.javainstitute.techstoreadmin.listener.ProductSelectListener;
import lk.javainstitute.techstoreadmin.model.Category;
import lk.javainstitute.techstoreadmin.model.Product;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private ArrayList<Product> products;
    private Context context;
    private FirebaseStorage storage;
    private ProductSelectListener selectListener;

    public ProductAdapter(ArrayList<Product> products, Context context,ProductSelectListener selectListener) {
        this.products = products;
        this.context = context;
        this.selectListener = selectListener;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_row_layout, parent, false);
        return new ProductAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = products.get(position);
        holder.productTitleTextView.setText(product.getTitle());
        holder.productPriceTextView.setText(product.getPrice());

        if(product.getStatus().equals("true")){
            holder.statusBtn.setText("Active");
            holder.statusBtn.setChecked(true);
        }else{
            holder.statusBtn.setText("Deactive");
            holder.statusBtn.setChecked(false);
        }
        holder.statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.changeStatus(products.get(position));
            }
        });

        storage.getReference("product_img/"+product.getImage1())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(150, 150)
                                .centerCrop()
                                .into(holder.productIconImageView);
                    }
                });

        holder.editProductbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectListener.editProduct(products.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productTitleTextView;
        TextView productPriceTextView;
        ImageView productIconImageView;
        View editProductbtn;
        Switch statusBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitleTextView = itemView.findViewById(R.id.productTitle);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            productIconImageView = itemView.findViewById(R.id.productIcon);
            editProductbtn = itemView.findViewById(R.id.itemCard);
            statusBtn = itemView.findViewById(R.id.statusBtn);
        }
    }
}

