package com.matemeup.matemeup;

import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.containers.Quad;
import com.matemeup.matemeup.fragments.DatePickerFragment;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class AccountModifierLayout extends BackToolbarActivity implements DatePickerFragment.OnDatePicked
{
    interface ValueGetter {
        Object get(View view);
    }

    interface ValueValidation {
        Boolean validate(Object value, HashMap<String, Object> map);
    }

    protected java.util.Date birthdate = null;
    protected String placeAddress = "";
    protected PlaceAutocompleteFragment autocompleteFragment;

    protected JSONObject validateFields(List<Quad<Integer, ValueGetter, ValueValidation, String>> fieldsId) {
        HashMap<String, Object> fieldsValue = new HashMap();
        Object value;
        Boolean hasError = false;

        for (int i = 0; i < fieldsId.size(); i++) {
            value = fieldsId.get(i).second.get(fieldsId.get(i).first != null ? findViewById(fieldsId.get(i).first) : null);
            if (!fieldsId.get(i).third.validate(value, fieldsValue)) {
                System.out.println("error validation " + fieldsId.get(i).fourth);
                hasError = true;
                break;

            }
            if (!fieldsId.get(i).fourth.equals(""))
                fieldsValue.put(fieldsId.get(i).fourth, value);
        }
        if (hasError)
            return null;
        return Serializer.fromMap(fieldsValue);
    }

    public static Object getFromString(View view) {
        return ((EditText) view).getText().toString();
    }

    public static Object getFromBoolean(View view) {
        return !((Switch) view).isChecked() ? 0 : 1;
    }

    public static Object getFromGender(View view) {
        return ((Spinner)view).getSelectedItemPosition();
    }


    @Override
    public void onDatePicked(int year, int month, int day) {

        birthdate = new GregorianCalendar(year, month, day).getTime();

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        ((TextView) findViewById(R.id.birthdate_input)).setText(df.format(birthdate));
    }

    public void showBirthdatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    protected void configurePlaceAutocomplete()
    {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment = (PlaceAutocompleteFragment)
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

    protected void configureGenderSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.gender_spinner);

        if (spinner != null) {
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.genders_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        }


    }

    public void stylizePlaceFragment() {
        stylizePlaceFragment(false);
    }

    public void stylizePlaceFragment(Boolean needColor) {
        TextView text = autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input);

        autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        text.setTextSize(16.0f);
        if (needColor == true) {
            text.setTextColor(Color.WHITE);
        }
    }

    public void onCreate(Bundle savedInstanceState, int resourceId) {
        super.onCreate(savedInstanceState, resourceId);
        configurePlaceAutocomplete();
        configureGenderSpinner();
    }
}
