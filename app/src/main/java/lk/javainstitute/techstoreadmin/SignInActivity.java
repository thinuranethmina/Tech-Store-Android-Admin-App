package lk.javainstitute.techstoreadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import lk.javainstitute.techstoreadmin.model.AuthResponce;
import lk.javainstitute.techstoreadmin.service.TechStoreService;
import lk.javainstitute.techstoreadmin.service.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Retrofit retrofit = RetrofitClient.getClient();

        TechStoreService loginService = retrofit.create(TechStoreService.class);


        CardView vcodeContent = findViewById(R.id.vcodeContent);
        CardView emailContent = findViewById(R.id.emailContent);

        emailContent.setVisibility(View.VISIBLE);
        vcodeContent.setVisibility(View.GONE);

        findViewById(R.id.sendVCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailView = findViewById(R.id.email);
                String emailAddress = emailView.getText().toString();


                Call<AuthResponce> call = loginService.sendVcode(emailAddress);
                call.enqueue(new Callback<AuthResponce>() {
                    @Override
                    public void onResponse(Call<AuthResponce> call, Response<AuthResponce> response) {
                        if (response.isSuccessful()) {

                            AuthResponce responce = response.body();
                            if (responce != null) {
                                String status = responce.getStatus();

                                if (status.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Check your email inbox and enter verification code", Toast.LENGTH_SHORT).show();

                                    vcodeContent.setVisibility(View.VISIBLE);
                                    emailContent.setVisibility(View.GONE);

                                } else {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                                }


                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponce> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();

                    }

                });

            }
        });



        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailView = findViewById(R.id.email);

                EditText vcodeView = findViewById(R.id.vcode);
                String vcode = vcodeView.getText().toString();

                Call<AuthResponce> call = loginService.login(vcode);
                call.enqueue(new Callback<AuthResponce>() {
                    @Override
                    public void onResponse(Call<AuthResponce> call, Response<AuthResponce> response) {
                        if (response.isSuccessful()) {
                            AuthResponce responce = response.body();
                            if (responce != null) {
                                String status = responce.getStatus();
                                if (status.equals("success")) {
                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainLayoutActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                                    vcodeView.setText("");
                                    emailView.setText("");

                                    emailContent.setVisibility(View.VISIBLE);
                                    vcodeContent.setVisibility(View.GONE);
                                }

                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                            vcodeView.setText("");
                            emailView.setText("");

                            emailContent.setVisibility(View.VISIBLE);
                            vcodeContent.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponce> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Try again later.", Toast.LENGTH_SHORT).show();
                        vcodeView.setText("");
                        emailView.setText("");

                        emailContent.setVisibility(View.VISIBLE);
                        vcodeContent.setVisibility(View.GONE);

                    }

                });

            }
        });




    }
}