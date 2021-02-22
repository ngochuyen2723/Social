package com.example.social.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorizationn: key=AAAAOiW5p64:APA91bE3hGAut_lM1tl6fNlfex3vM7jpcUS57cza60UjHVbcu8e0jKGeXRyqC0wh0vm2Runpn08TfARJolAWEaJBKb5sENagOguUWt8L4cK6AlPQmcZFxk354AXhP171rC3PgIUQ_7I0"
    })
    @POST("fcm/send")
    Call<Response> sendNotification (@Body Sender body);
}
