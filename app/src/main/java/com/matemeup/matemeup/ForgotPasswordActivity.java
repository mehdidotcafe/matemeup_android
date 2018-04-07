package com.matemeup.matemeup;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.navigation.BackButton;

import org.json.JSONException;
import org.json.JSONObject;


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
            JSONObject toSend = new JSONObject();

            try {
                toSend.put("email", mail);
            } catch (JSONException e) { return ;}
            Request req = Request.getInstance();

            Callback cb = new Callback()
            {
                @Override
                public void success(Object data)
                {
                    goToLogin();
                }

                @Override
                public void fail(String error)
                {
                    System.out.println("Dans le fail");
                    System.out.println(error);
                }
            };

            req.send(this, "recover", "POST", null, toSend, cb);
        }
        else
            System.out.println("Invalid Mail");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        BackButton.handle(this);
    }
}
