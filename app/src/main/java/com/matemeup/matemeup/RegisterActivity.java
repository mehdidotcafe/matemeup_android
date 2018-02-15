package com.matemeup.matemeup;

import android.app.DialogFragment;
import android.content.Intent;
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
import com.matemeup.matemeup.entities.Date;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.Quad;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.Validator;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.fragments.DatePickerFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements DatePickerFragment.OnDatePicked {

    private Date birthdate = null;
    private String placeAddress = "";

    interface ValueGetter {
        Object get(View view);
    }

    interface ValueValidation {
        Boolean validate(Object value, HashMap<String, Object> map);
    }

    public void goToHome()
    {
        IntentManager.replace(this, HomeActivity.class);
    }

    @Override
    public void onDatePicked(int year, int month, int day) {
        birthdate = new Date(year, month, day);
        ((TextView)findViewById(R.id.birthdate_input)).setText(day + "/" + month + "/" + year);
    }

    public void showBirthdatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static Object getFromString(View view) {
        return ((EditText) view).getText().toString();
    }

    public static Object getFromBoolean(View view) {
        return ((Switch) view).isChecked();
    }

    @SuppressWarnings("unchecked")
    public void submitRegister(View view) {
        List<Quad<Integer, ValueGetter, ValueValidation, String>> fieldsId = new ArrayList();

        fieldsId.add(new Quad(R.id.name_input, new ValueGetter() {
            public Object get(View _view) {
                return "toto";
                //return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "name"));

        fieldsId.add(new Quad(R.id.email_input, new ValueGetter() {
            public Object get(View _view) {
                return "foobarrrrrrrrrr@momo.fr";
                //return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateEmail((String) value);
            }
        }, "email"));
        fieldsId.add(new Quad(R.id.register_password_input, new ValueGetter() {
            public Object get(View _view) {
                return "aqwzsxedc";
                //return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validatePassword((String) value);
            }
        }, "password"));
        fieldsId.add(new Quad(R.id.conf_password_input, new ValueGetter() {
            public Object get(View _view) {
                return "aqwzsxedc";
                //return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateConfPassword((String) value, (String) map.get("password"));
            }
        }, "password_confirmation"));
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
        fieldsId.add(new Quad(R.id.sponsor_input, new ValueGetter() {
            public Object get(View _view) {
                return "bonjour";
                //return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "parrain"));
        fieldsId.add(new Quad(R.id.firstname_input, new ValueGetter() {
            public Object get(View _view) {
                return "mehdi";
                //return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "firstname"));
        fieldsId.add(new Quad(R.id.lastname_input, new ValueGetter() {
            public Object get(View _view) {
                return "meddour";
                //return getFromString(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return Validator.validateString((String) value);
            }
        }, "lastname"));
        fieldsId.add(new Quad(R.id.identity_visibility_input, new ValueGetter() {
            public Object get(View _view) {
                return 1;
                //return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "showLastName"));
        fieldsId.add(new Quad(R.id.chat_visibility_input, new ValueGetter() {
            public Object get(View _view) {
                return 1;
                //return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "allMP"));
        fieldsId.add(new Quad(R.id.accept_email_input, new ValueGetter() {
            public Object get(View _view) {
                return 1;
                //return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        },"wantMails"));

        fieldsId.add(new Quad(R.id.accept_CGU_input, new ValueGetter() {
            public Object get(View _view) {
                return 1;
                //return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
                //return (Boolean) value;
            }
        }, "accept_cgu"));
        fieldsId.add(new Quad(R.id.gender_input, new ValueGetter() {
            public Object get(View _view) {
                return 1;
                //return getFromBoolean(_view);
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                Integer ival = (Integer)value;
                return ival == 0 || ival == 1;
            }
        }, "gender"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                return 1990;
                //return birthdate.year;
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdyear"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                return 11;
                //return birthdate.month;
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdmonth"));
        fieldsId.add(new Quad(null, new ValueGetter() {
            public Object get(View _view) {
                return 01;
                //return birthdate.day;
            }
        }, new ValueValidation() {
            public Boolean validate(Object value, HashMap<String, Object> map) {
                return true;
            }
        }, "bdday"));

        HashMap<String, Object> fieldsValue = new HashMap();
        Object value = "";
        Boolean hasError = false;

        for (int i = 0; i < fieldsId.size(); i++) {
            value = fieldsId.get(i).second.get(fieldsId.get(i).first != null ? findViewById(fieldsId.get(i).first) : null);
            if (!fieldsId.get(i).third.validate(value, fieldsValue)) {
                hasError = true;
                break;

            }
            if (!fieldsId.get(i).fourth.equals(""))
                fieldsValue.put(fieldsId.get(i).fourth, value);
        }

        if (hasError) {
        }
        else {
            JsonObject obj = Serializer.fromMap(fieldsValue);
            Request req = (new Request()
            {
                @Override
                public void success(JsonObject data)
                {
                    String token = data.get("token").getAsString();

                    JWT.put(RegisterActivity.this, token);
                    Request.addQueryString("token", token);
                    goToHome();
                }

                @Override
                public void fail(String error)
                {
                    System.out.println("Dans le fail");
                    System.out.println(error);
                }
            });

            req.send(this, "register", "POST", null, obj);
        }
    }

    private void configurePlaceAutocomplete()
    {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);;
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                placeAddress = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                placeAddress = "";
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        configurePlaceAutocomplete();

    }
}
