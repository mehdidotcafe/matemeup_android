package com.matemeup.matemeup.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.Factory;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.model.Serializable;
import com.matemeup.matemeup.entities.model.UserChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public abstract class RemoteArrayAdapter<T> extends ArrayAdapter<T> {
    protected List<T> list;
    protected Context ctx;
    protected String route;

    public RemoteArrayAdapter(String route, Context c, int textViewResourceId, List<T> l) {
        super(c, textViewResourceId, l);
        ctx = c;
        list = l;
        this.route = route;
    }

    abstract public void setListFromResponse(JSONArray array);

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                JSONObject obj = new JSONObject();
                Request req = Request.getInstance();
                Callback cb = new Callback()
                {
                    public void success(Object data)
                    {
                        JSONObject result = (JSONObject)data;
                        JSONArray jsonUsers = new JSONArray();
                        try {
                            jsonUsers = result.getJSONArray("users");
                        } catch (JSONException e) {}
                        setListFromResponse(jsonUsers);
                        FilterResults filterResults = new FilterResults();

                        filterResults.values = list;
                        filterResults.count = list.size();
                        publishResults(constraint, filterResults);
                    }

                    public void fail(String result)
                    {
                        System.out.println("Error " + result);
                    }
                };

                try {
                    obj.put("needle", constraint);
                } catch (JSONException e){}
                if (constraint != null) {
                    req.send(ctx, route, "GET", null, obj, cb);
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }

            }


        };

     return myFilter;
    }
}