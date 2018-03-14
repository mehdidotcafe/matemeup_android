package com.matemeup.matemeup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.IntentManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BackToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_login);
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
        JSONObject loginObj = new JSONObject();

        try {
            loginObj.put("email", username);
            loginObj.put("password", password);
        } catch (JSONException e) {return ;}

        Request req = (new Request()
        {
            @Override
            public void success(JSONObject data)
            {
                String token;

                try {
                    token = data.getString("token");
                } catch (JSONException e) {
                    token = "";
                }

                JWT.putAPI(LoginActivity.this, token);
                Request.addQueryString("token", token);
                goToHome();
            }

            @Override
            public void fail(String error)
            {
                System.out.println("Dans le fail login");
                System.out.println(error);
            }
        });

        req.send(this, "login", "POST", null, loginObj);
    }
}
