package lk.javainstitute.techstoreadmin.service;

import lk.javainstitute.techstoreadmin.model.AuthResponce;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TechStoreService {
    @FormUrlEncoded
    @POST("loginController.php")
    Call<AuthResponce> sendVcode(@Field("email") String email);

    @FormUrlEncoded
    @POST("verifyController.php")
    Call<AuthResponce> login(@Field("vcode") String vcode);
}
