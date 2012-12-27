package com.eguy;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUser
{
    private JSONObject jsonObject;

    public JsonUser(JSONObject status)
    {
        this.jsonObject = status;
    }

    public long getUserId() throws JSONException
    {
        return jsonObject.getLong("id");
    }

    public String getUsername() throws JSONException
    {
        return jsonObject.getString("screen_name");
    }

    public String getProfilePicUrl() throws JSONException
    {
        return jsonObject.getString("profile_image_url");
    }
}