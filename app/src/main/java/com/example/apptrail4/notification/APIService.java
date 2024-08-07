package com.example.apptrail4.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key = AAAAtTzHHW8:APA91bGk0cSqqhRu3BkBM_tECq8peQ2arcrLvdEiigMmD1SbjRaGrM2Untf3JTDo29D8TKNS4irHtQ17zNO10q-bjlm746MDLm0JHEvLMSnj6U86-09L0_Vi7VkbP3HjG-EJd2NVKLkY"

    })

    @POST("fcm/send")
    Call<Responce> sendNotification(@Body Sender body);

}
