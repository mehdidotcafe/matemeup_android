package com.matemeup.matemeup;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.containers.Quad;
import com.matemeup.matemeup.entities.rendering.AvatarRemoteImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AccountModifierLayout {

    @SuppressWarnings("unchecked")
    public void submitModif(View view) {
        List<Quad<Integer, ValueGetter, RegisterActivity.ValueValidation, String>> fieldsId = new ArrayList();
        JSONObject obj;

        fieldsId.add(new Quad(R.id.firstname_input, new ValueGetter() {
            public Object get(View _view) {
                //return "toto";
                return getFromString(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "firstname"));
        fieldsId.add(new Quad(R.id.lastname_input, new ValueGetter() {
            public Object get(View _view) {
                //return "toto";
                return getFromString(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "lastname"));
        fieldsId.add(new Quad(R.id.password_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return value.toString().length() == 0 || Validator.validateString((String) value);
            }
        }, "password"));
        fieldsId.add(new Quad(R.id.conf_password_input, new ValueGetter() {
            public Object get(View _view) {
                return getFromString(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return value.toString().length() == 0 || Validator.validateString((String) value);
            }
        }, "password_confirmation"));
        fieldsId.add(new Quad(R.id.place_autocomplete_fragment, new ValueGetter() {
            public Object get(View _view) {
                //return "Paris, France";
                return placeAddress;
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "location"));
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
        fieldsId.add(new Quad(R.id.gender_spinner, new ValueGetter() {
            public Object get(View _view) {
                //return 1;
                return getFromGender(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "gender"));
        fieldsId.add(new Quad(R.id.chat_visibility_input, new ValueGetter() {
            public Object get(View _view) {
                //return 1;
                return getFromBoolean(_view);
            }
        }, new RegisterActivity.ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "open_chat_disabled"));

        if ((obj = validateFields(fieldsId)) == null) {
            System.out.println("Error validating form");
        }
        else {
            Request req = Request.getInstance();
            Callback cb = new Callback();

            req.send(this, "update", "POST", null, obj, cb);
        }

    }

    private void updateLayoutFromUser(JSONObject user)
    {
        try {
            ((EditText) findViewById(R.id.firstname_input)).setText(user.getString("firstname"));
            ((EditText) findViewById(R.id.lastname_input)).setText(user.getString("lastname"));
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                birthdate = format.parse(user.getString("birthdate"));
                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
                ((TextView) findViewById(R.id.birthdate_input)).setText(df.format(birthdate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ((Switch) findViewById(R.id.chat_visibility_input)).setChecked(user.getInt("open_chat_disabled") == 1);

            ((TextView)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input)).setText(user.getString("location"));
            placeAddress = user.getString("location");
            stylizePlaceFragment();
            ((TextView) findViewById(R.id.username_container)).setText(user.getString("name"));
            ((Spinner) findViewById(R.id.gender_spinner)).setSelection(user.getInt("gender"));
            findViewById(R.id.avatar_container).setClipToOutline(true);
            AvatarRemoteImageLoader.load((ImageView)findViewById(R.id.avatar_container), user.getString("avatar"));
        } catch (JSONException e) {}
        findViewById(R.id.profile_container).setVisibility(View.VISIBLE);
    }

    private void getUser() {
        Request req = Request.getInstance();
        Callback cb = new Callback()
        {
            public void success(Object data) {
                JSONObject result = (JSONObject)data;
                try {
                    updateLayoutFromUser(result.getJSONObject("user"));
                } catch (JSONException e) {}
            }

            public void fail(String res) {
                System.out.println("fail" + res);
            }
        };

        req.send(this, "me", "GET", null, null, cb);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_profile);
        IntentManager.setCurrentActivity(ProfileActivity.class);
        initToolbar();
        getUser();
    }
}
