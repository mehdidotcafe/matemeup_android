package com.matemeup.matemeup;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.koushikdutta.async.http.body.JSONObjectBody;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.ConnectedUser;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Loginout;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.containers.Quad;
import com.matemeup.matemeup.entities.rendering.Alert;
import com.matemeup.matemeup.entities.rendering.AlertCallback;
import com.matemeup.matemeup.entities.validation.AccountModifier;
import com.matemeup.matemeup.entities.rendering.AvatarRemoteImageLoader;
import com.matemeup.matemeup.entities.validation.ValueGetter;
import com.matemeup.matemeup.entities.validation.ValueValidation;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;
import com.matemeup.matemeup.fragments.DatePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ProfileActivity extends Layout implements DatePickerFragment.OnDatePicked {

    private AccountModifier accountModifier;
    private ProfileActivity self = this;

    public void showBirthdatePicker(View view) {
        accountModifier.showBirthdatePicker(view);
    }


    @Override
    public void onDatePicked(int year, int month, int day) {
        accountModifier.onDatePicked(year, month, day);
    }

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

                    Alert.ok(self, getResources().getString(R.string.update_sucess), getResources().getString(R.string.profile_updated), new AlertCallback());
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
        Toolbar toolbar = findViewById(R.id.toolbar_back);
        setSupportActionBar(toolbar);
    }

    private List<String> getRewardsFromResponse(JSONObject rewards) {
        List<String> messages = new ArrayList<String>();
        for(Iterator<String> iter = rewards.keys(); iter.hasNext();) {
            String key = iter.next();

            try {
                messages.add(rewards.getInt(key) + " " + key + ".");
            } catch (JSONException e) {}
        }
        return messages;
    }

    private void setListeners() {
        final ProfileActivity self = this;
        this.findViewById(R.id.disconnect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loginout.logout(self);
                IntentManager.goTo(self, MainActivity.class);
            }
        });

        this.findViewById(R.id.reward_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MMUWebSocket.getInstance(self).emit("reward.use", new JSONObject(), new WebSocketCallback() {
                    @Override
                    public void onMessage(String message, Object... args) {
                        JSONObject res = (JSONObject)args[0];

                        try {
                            if (res.getBoolean("state")) {
                                self.findViewById(R.id.reward_button).setVisibility(View.INVISIBLE);
                                Alert.ok(self, "Récompense", self.getRewardsFromResponse(res.getJSONObject("rewards")), new AlertCallback());

                            }
                        } catch (JSONException e) {}
                    }
                });
            }
        });
    }

    private void setRewardButtonVisibility() {
        MMUWebSocket.getInstance(self).emit("reward.isAvailable", new JSONObject(), new WebSocketCallback() {
            @Override
            public void onMessage(String message, Object... args) {
                JSONObject res = (JSONObject)args[0];
                
                try {
                    if (res.getBoolean("state") && res.getBoolean("isAvailable")) {
                        self.findViewById(R.id.reward_button).setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {

                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_profile);
        IntentManager.setCurrentActivity(ProfileActivity.class);
        accountModifier = new AccountModifier(this);
        setListeners();
        initToolbar();
        setRewardButtonVisibility();
        getUser();
    }
}
