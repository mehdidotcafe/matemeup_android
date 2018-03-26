package com.matemeup.matemeup;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.Date;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.containers.Quad;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.fragments.DatePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AccountModifierLayout {

    public void goToHome()
    {
        IntentManager.replace(this, HomeActivity.class);
    }


    @SuppressWarnings("unchecked")
    public void submitRegister(View view) {
        List<Quad<Integer, ValueGetter, ValueValidation, String>> fieldsId = new ArrayList();
        JSONObject obj;

        fieldsId.add(new Quad(R.id.name_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "name"));

        fieldsId.add(new Quad(R.id.email_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateEmail((String) value);
            }
        }, "email"));
        fieldsId.add(new Quad(R.id.register_password_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validatePassword((String) value);
            }
        }, "password"));
        fieldsId.add(new Quad(R.id.conf_password_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateConfPassword((String) value, (String) map.get("password"));
            }
        }, "password_confirmation"));
        fieldsId.add(new Quad(R.id.place_autocomplete_fragment, new ValueGetter() {
            public Object get(View _view) {
                return placeAddress;
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "location"));
        fieldsId.add(new Quad(R.id.sponsor_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "parrain"));
        fieldsId.add(new Quad(R.id.firstname_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "firstname"));
        fieldsId.add(new Quad(R.id.lastname_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "lastname"));
        fieldsId.add(new Quad(R.id.identity_visibility_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "showLastName"));
        fieldsId.add(new Quad(R.id.chat_visibility_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "allMP"));
        fieldsId.add(new Quad(R.id.accept_email_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        },"wantMails"));

        fieldsId.add(new Quad(R.id.accept_CGU_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "accept_cgu"));
        fieldsId.add(new Quad(R.id.gender_spinner, new ValueGetter() {
            public Object get(View _view) {
                return getFromGender(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "gender"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                Calendar cal = Calendar.getInstance();

                cal.setTime(birthdate);
                //return 1990;
                return cal.get(Calendar.YEAR);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdyear"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                Calendar cal = Calendar.getInstance();

                cal.setTime(birthdate);
                //return 2;
                return cal.get(Calendar.MONTH) + 1;
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdmonth"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                Calendar cal = Calendar.getInstance();

                cal.setTime(birthdate);
                //return 1;
                return cal.get(Calendar.DAY_OF_MONTH);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdday"));

        if ((obj = validateFields(fieldsId)) == null) {
        }
        else {
            final Request req = Request.getInstance();

            Callback cb = new Callback()
            {
                @Override
                public void success(Object objData)
                {
                    String token;
                    JSONObject data = (JSONObject)objData;

                    try {
                        token = data.getString("token");
                    } catch (JSONException e) {
                        token = "";
                    }

                    JWT.putAPI(RegisterActivity.this, token);
                    req.addQueryString("token", token);
                    goToHome();
                }

                @Override
                public void fail(String error)
                {
                    System.out.println("Dans le fail");
                    System.out.println(error);
                }
            };

            req.send(this, "register", "POST", null, obj, cb);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_register);
        stylizePlaceFragment(true);
    }
}
