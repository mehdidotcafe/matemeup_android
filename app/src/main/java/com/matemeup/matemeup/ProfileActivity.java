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
import com.matemeup.matemeup.entities.ConnectedUser;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.containers.Quad;
import com.matemeup.matemeup.entities.validation.AccountModifier;
import com.matemeup.matemeup.entities.rendering.AvatarRemoteImageLoader;
import com.matemeup.matemeup.entities.validation.ValueGetter;
import com.matemeup.matemeup.entities.validation.ValueValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends Layout {

    private AccountModifier accountModifier;

    @SuppressWarnings("unchecked")
    public void submitModif(View view) {
        List<Quad<Integer, ValueGetter, ValueValidation, String>> fieldsId = new ArrayList();
        JSONObject obj;

        fieldsId.add(new Quad(R.id.firstname_input, new ValueGetter() {
            public Object get(View _view) {
                //return "toto";
                return AccountModifier.getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "firstname"));
        fieldsId.add(new Quad(R.id.lastname_input, new ValueGetter() {
            public Object get(View _view) {
                //return "toto";
                return AccountModifier.getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "lastname"));
        fieldsId.add(new Quad(R.id.password_input, new ValueGetter() {
            public Object get(View _view) {
                return AccountModifier.getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return value.toString().length() == 0 || Validator.validateString((String) value);
            }
        }, "password"));
        fieldsId.add(new Quad(R.id.conf_password_input, new ValueGetter() {
            public Object get(View _view) {
                return AccountModifier.getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return value.toString().length() == 0 || Validator.validateString((String) value);
            }
        }, "password_confirmation"));
        fieldsId.add(new Quad(R.id.place_autocomplete_fragment, new ValueGetter() {
            public Object get(View _view) {
                //return "Paris, France";
                return accountModifier.getPlaceAddress();
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "location"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                Calendar cal = Calendar.getInstance();

                cal.setTime(accountModifier.getBirthdate());
                //return 1990;
                return cal.get(Calendar.YEAR);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdyear"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                Calendar cal = Calendar.getInstance();

                cal.setTime(accountModifier.getBirthdate());
                //return 2;
                return cal.get(Calendar.MONTH) + 1;
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdmonth"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                Calendar cal = Calendar.getInstance();

                cal.setTime(accountModifier.getBirthdate());
                //return 1;
                return cal.get(Calendar.DAY_OF_MONTH);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdday"));
        fieldsId.add(new Quad(R.id.gender_spinner, new ValueGetter() {
            public Object get(View _view) {
                //return 1;
                return AccountModifier.getFromGender(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "gender"));
        fieldsId.add(new Quad(R.id.chat_visibility_input, new ValueGetter() {
            public Object get(View _view) {
                //return 1;
                return AccountModifier.getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "open_chat_disabled"));

        if ((obj = accountModifier.validateFields(fieldsId)) == null) {
            System.out.println("Error validating form");
        }
        else {
            Request req = Request.getInstance();
            Callback cb = new Callback() {
                @Override
                public void success(Object obj) {
                    JSONObject data = (JSONObject)obj;

                    try {
                        ConnectedUser.set(data.getJSONObject("user"));
                    } catch (JSONException e) {}
                }
            };

            req.send(this, "update", "POST", null, obj, cb);
        }

    }

    private void updateLayoutFromUser(ConnectedUser user)
    {
        ((EditText) findViewById(R.id.firstname_input)).setText(user.firstname);
        ((EditText) findViewById(R.id.lastname_input)).setText(user.lastname);
        AvatarRemoteImageLoader.load((ImageView)findViewById(R.id.avatar_container), user.avatar);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            accountModifier.setBirthdate(format.parse(user.birthdate));
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            ((TextView) findViewById(R.id.birthdate_input)).setText(df.format(accountModifier.getBirthdate())); }
        catch (ParseException e) {
            e.printStackTrace();
        }
        ((Switch) findViewById(R.id.chat_visibility_input)).setChecked(user.isOpenChatDisabled);
        accountModifier.setLocalisation(user.location);
        accountModifier.stylizePlaceFragment();
        ((TextView) findViewById(R.id.username_container)).setText(user.name);
        ((Spinner) findViewById(R.id.gender_spinner)).setSelection(user.gender);
        findViewById(R.id.avatar_container).setClipToOutline(true);
    }

    private void getUser() {
        updateLayoutFromUser(ConnectedUser.getInstance());
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_profile);
        IntentManager.setCurrentActivity(ProfileActivity.class);
        accountModifier = new AccountModifier(this);
        initToolbar();
        getUser();
    }
}
