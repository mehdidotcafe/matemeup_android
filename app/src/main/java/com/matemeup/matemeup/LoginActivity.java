package com.matemeup.matemeup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.ConnectedUser;
import com.matemeup.matemeup.entities.Loginout;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.rendering.Alert;
import com.matemeup.matemeup.entities.rendering.AlertCallback;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private boolean isRequesting = false;

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
        JSONObject loginObj = new JSONObject();
        final LoginActivity self = this;

        if (isRequesting == true) {
            return ;
        }
        try {
            loginObj.put("email", username);
            loginObj.put("password", password);
        } catch (JSONException e) {return ;}

        final Request req = Request.getInstance();

        isRequesting = true;
        Callback cb = new Callback() {
            @Override
            public void success(Object objData) {
                isRequesting = false;
                Loginout.login(self, (JSONObject)objData);
                goToHome();
            }

            @Override
            public void fail(String error) {
                isRequesting = false;
                System.out.println(error);
                Alert.ok(self, getResources().getString(R.string.connect_error), getResources().getString(R.string.email_password_invalid), new AlertCallback());
            }
        };

        req.send(this, "login", "POST", null, loginObj, cb);
    }
}
