package lk.javainstitute.techstoreadmin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.javainstitute.techstoreadmin.R;
import lk.javainstitute.techstoreadmin.listener.UserSelectLister;
import lk.javainstitute.techstoreadmin.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private ArrayList<User> users;
    private Context context;
    private FirebaseStorage storage;
    private UserSelectLister userSelectLister;

    public UserAdapter(ArrayList<User> users, Context context, UserSelectLister userSelectLister) {
        this.users = users;
        this.context = context;
        this.userSelectLister = userSelectLister;
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_row_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = users.get(position);
        holder.userNameTxt.setText(user.getName());
        holder.userEmailTxt.setText(user.getEmail());

        holder.userCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSelectLister.selectUser(users.get(position));
            }
        });

        if(user.getImage().isEmpty()|| user.getImage()==null){
            Picasso.get()
                    .load(R.drawable.user)
                    .centerCrop()
                    .resize(150, 150)
                    .into(holder.usertIcon);

        }else{

            storage.getReference("user-images/"+user.getImage())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .centerCrop()
                                    .resize(150, 150)
                                    .into(holder.usertIcon);
                        }
                    });



        }




    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt, userEmailTxt;
        CardView userCardView;
        ImageView usertIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTxt = itemView.findViewById(R.id.name);
            userEmailTxt = itemView.findViewById(R.id.email);
            userCardView = itemView.findViewById(R.id.userCard);
            usertIcon = itemView.findViewById(R.id.usertIcon);
        }
    }

}