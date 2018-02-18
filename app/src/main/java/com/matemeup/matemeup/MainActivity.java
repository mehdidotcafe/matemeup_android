package com.matemeup.matemeup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;

public class MainActivity extends AppCompatActivity {
    private void checkJWT()
    {
        JsonObject obj = new JsonObject();
        String jwt = JWT.get(this);

        if (jwt != null && !jwt.equals(""))
        {
            Request.addQueryString("token", jwt);
        }
        Request req = (new Request()
        {
            @Override
            public void success(JsonObject data)
            {
                System.out.println("Dans le login success");
                System.out.println(data);
                goToHome();
            }

            @Override
            public void fail(String error)
            {
                System.out.println("Dans le login fail");
                System.out.println(error);
            }
        });

        req.send(this, "me", "POST", null, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.checkJWT();
    }

    public void goToRegister(View android)
    {
        IntentManager.goTo(MainActivity.this, RegisterActivity.class);
    }

    public void goToLogin(View android)
    {
        IntentManager.goTo(MainActivity.this, LoginActivity.class);
    }

    public void goToHome()
    {
        IntentManager.replace(MainActivity.this, HomeActivity.class);
    }
}
