package com.matemeup.matemeup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.IntentManager;


public class ForgotPasswordActivity extends AppCompatActivity {

    public void goToLogin()
    {
        IntentManager.goTo(this, LoginActivity.class);
    }

    public void sendRecoverMail(View view)
    {
        String mail = ((TextView)findViewById(R.id.forgot_password_email_input)).getText().toString();

        if (Validator.validateEmail(mail))
        {
            JsonObject toSend = new JsonObject();

            toSend.addProperty("email", mail);
            Request req = (new Request()
            {
                @Override
                public void success(JsonObject data)
                {
                    goToLogin();
                }

                @Override
                public void fail(String error)
                {
                    System.out.println("Dans le fail");
                    System.out.println(error);
                }
            });

            req.send(this, "recover", "POST", null, toSend);
        }
        else
            System.out.println("Invalid Mail");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }
}