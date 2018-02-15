package com.matemeup.matemeup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.IntentManager;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goToHome()
    {
        IntentManager.replace(this, HomeActivity.class);
    }

    public void goToForgotPassword(View view)
    {
        IntentManager.goTo(this, ForgotPasswordActivity.class);
    }


    public void onSubmit(View view)
    {
        String username = ((TextView)findViewById(R.id.username_input)).getText().toString();
        String password = ((TextView)findViewById(R.id.login_password_input)).getText().toString();
        JsonObject loginObj = new JsonObject();

        loginObj.addProperty("email", username);
        loginObj.addProperty("password", password);
        Request req = (new Request()
        {
            @Override
            public void success(JsonObject data)
            {
                String token = data.get("token").getAsString();

                JWT.put(LoginActivity.this, token);
                Request.addQueryString("token", token);
                goToHome();
            }

            @Override
            public void fail(String error)
            {
                System.out.println("Dans le fail");
                System.out.println(error);
            }
        });

        req.send(this, "login", "POST", null, loginObj);
    }
}
