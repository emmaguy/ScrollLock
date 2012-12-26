package com.eguy;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

public class LoadTweetsTask extends AsyncTask<Void, Void, JSONObject>
{
    private final String USER_TIMELINE_URL = "https://api.twitter.com/1.1/statuses/user_timeline.json";
    HttpClient client;
    private OAuthProviderAndConsumer producerAndConsumer;
    private AuthCredentialManager credentialManager;

    public LoadTweetsTask(OAuthProviderAndConsumer producerAndConsumer, AuthCredentialManager credentialManager)
    {
        this.producerAndConsumer = producerAndConsumer;
        this.credentialManager = credentialManager;

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();
        SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

        client = new DefaultHttpClient(mgr, params);
    }

    @Override
    protected JSONObject doInBackground(Void... arg0)
    {
        JSONObject jso = null;

        try
        {
            Long tsLong = (System.currentTimeMillis() / 1000);
            String ts = tsLong.toString();

            Uri sUri = Uri.parse(USER_TIMELINE_URL);
            Uri.Builder builder = sUri.buildUpon();
            builder.appendQueryParameter("screen_name", credentialManager.getUsername());
            builder.appendQueryParameter("count", "1");

            String uri = builder.build().toString();
            HttpGet get = new HttpGet(uri);
            producerAndConsumer.getConsumer().sign(get);

            String response = client.execute(get, new BasicResponseHandler());

            jso = new JSONObject(response);
        } catch (Exception e)
        {
            Log.e("blah", e.getClass().toString(), e);
        }

        return jso;
    }

    protected void onPostExecute(JSONObject jso)
    {
        Log.d("TimelineActivity", "rar");
    }
}