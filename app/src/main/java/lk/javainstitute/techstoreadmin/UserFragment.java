package lk.javainstitute.techstoreadmin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import lk.javainstitute.techstoreadmin.adapter.UserAdapter;
import lk.javainstitute.techstoreadmin.listener.UserSelectLister;
import lk.javainstitute.techstoreadmin.model.User;

public class UserFragment extends Fragment implements UserSelectLister {

    private View view;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private ArrayList<User> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        users = new ArrayList<>();

        RecyclerView userView = view.findViewById(R.id.userRecycleView);
        UserAdapter userAdapter = new UserAdapter(users, getActivity().getApplicationContext(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        userView.setLayoutManager(linearLayoutManager);
        userView.setAdapter(userAdapter);
        firestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    User user = change.getDocument().toObject(User.class);
                    switch (change.getType()) {
                        case ADDED:
                            users.add(user);
                        case MODIFIED:
                            User old = users.stream().filter(i -> i.getEmail().equals(user.getEmail())).findFirst().orElse(null);
                            if (old != null) {
                                old.setName(user.getName());
                            }
                            break;
                        case REMOVED:
                            users.remove(user);
                    }
                }
                userAdapter.notifyDataSetChanged();

            }
        });


        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void selectUser(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View customLayout = getLayoutInflater().inflate(R.layout.user_detail_dialog, null);

        TextView userName = customLayout.findViewById(R.id.userNameText);
        TextView email = customLayout.findViewById(R.id.emailText);
        TextView mobile = customLayout.findViewById(R.id.mobileText);
        TextView address = customLayout.findViewById(R.id.addressText);

        userName.setText(user.getName());
        email.setText(user.getEmail());

        if (user.getMobile() == null) {
            mobile.setText("Not Updated");
        } else {
            mobile.setText(user.getMobile());
        }
        if (user.getAddress() == null) {
            address.setText(user.getAddress());
        }

        builder.setView(customLayout);

        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


}