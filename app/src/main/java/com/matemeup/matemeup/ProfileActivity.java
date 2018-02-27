package com.matemeup.matemeup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.containers.Quad;
import com.matemeup.matemeup.entities.model.UserChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AccountModifierLayout {

    @SuppressWarnings("unchecked")
    public void submitModif(View view) {
        List<Quad<Integer, ValueGetter, RegisterActivity.ValueValidation, String>> fieldsId = new ArrayList();
        JSONObject obj;

        fieldsId.add(new Quad(R.id.firstname_input, new ValueGetter() {
            public Object get(View _view) {
                return "toto";
                //return getFromString(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "firstname"));
        fieldsId.add(new Quad(R.id.lastname_input, new ValueGetter() {
            public Object get(View _view) {
                return "toto";
                //return getFromString(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "lastname"));
        fieldsId.add(new Quad(R.id.location_input, new ValueGetter() {
            public Object get(View _view) {
                return "Paris, France";
                //return placeAddress;
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "location"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                return 1990;
                //return birthdate.year;
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdyear"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                return 11;
                //return birthdate.month;
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdmonth"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                return 01;
                //return birthdate.day;
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdday"));
        fieldsId.add(new Quad(R.id.gender_spinner, new ValueGetter() {
            public Object get(View _view) {
                return 1;
                //return getFromBoolean(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "gender"));
        fieldsId.add(new Quad(R.id.chat_visibility_input, new ValueGetter() {
            public Object get(View _view) {
                return 1;
                //return getFromBoolean(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "open_chat_disabled"));

        if ((obj = validateFields(fieldsId)) == null) {
        }
        else {
            Request req = (new Request()
            {
                @Override
                public void success(JSONObject data)
                {
                    System.out.println("Hello");
                }

                @Override
                public void fail(String error)
                {
                    System.out.println("Dans le fail");
                    System.out.println(error);
                }
            });

            req.send(this, "update", "POST", null, obj);
        }

    }

    private void updateLayoutFromUser(JSONObject user)
    {
        try {
            ((EditText) findViewById(R.id.firstname_input)).setText(user.getString("firstname"));
            ((EditText) findViewById(R.id.lastname_input)).setText(user.getString("lastname"));
            ((TextView) findViewById(R.id.birthdate_input)).setText(user.getString("birthdate"));
            ((Switch) findViewById(R.id.chat_visibility_input)).setChecked(user.getInt("open_chat_disabled") == 1);
            //((PlaceAutocompleteFragment) findViewById(R.id.place_autocomplete_fragment)).setText(user.getString("location"));
        } catch (JSONException e) {}
    }

    private void getUser() {
        Request req = new Request() {
            public void success(JSONObject result) {
                System.out.println("Here res " + result);
                try {
                    updateLayoutFromUser(result.getJSONObject("user"));
                } catch (JSONException e) {}
            }

            public void fail(String res) {
                System.out.println("Ca foire " + res);
            }
        };

        req.send(this, "me", "GET", null, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_profile);
        getUser();
    }
}
