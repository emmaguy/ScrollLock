package dev.emmaguy.twitterclient.ui;

import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;
import dev.emmaguy.twitterclient.oauth.OAuthObtainRequestTokenAndRedirectToBrowser;
import dev.emmaguy.twitterclient.oauth.OAuthObtainRequestTokenAndRedirectToBrowser.RequestTokenReceivedListener;
import dev.emmaguy.twitterclient.oauth.OAuthRetrieveAccessTokenFromRequestToken;

public class SignInFragment extends Fragment implements OnAccessTokenRetrievedListener, OnClickListener, RequestTokenReceivedListener {

    public static final String SCROLLLOCK_CALLBACK = "scrolllock://callback";
    
    private static RequestToken requestToken;
    private SignInCompleteListener signInCompleteListener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.fragment_sign_in, null);
	
	v.findViewById(R.id.login_button).setOnClickListener(this);
	
	return v;
    }
    
    @Override
    public void onClick(View arg0) {
	new OAuthObtainRequestTokenAndRedirectToBrowser(getActivity(), (RequestTokenReceivedListener) this).execute();
    }

    @Override
    public void onResume() {
	super.onResume();
	
	Uri uri = getActivity().getIntent().getData();
	if (uri != null && uri.toString().startsWith("scrolllock")) {
	    String verifier = uri.getQueryParameter("oauth_verifier");
	    new OAuthRetrieveAccessTokenFromRequestToken(new SettingsManager(getActivity()), requestToken, verifier, (OnAccessTokenRetrievedListener) this).execute(uri);
	}
    }

    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);

	try {
	    signInCompleteListener = (SignInCompleteListener) activity;
	} catch (ClassCastException e) {
	    throw new ClassCastException(activity.toString() + " must implement SignInCompleteListener");
	}
    }
    
    @Override
    public void onRetrievedAccessToken() {
	signInCompleteListener.onSignInComplete();
    }

    @Override
    public void onRequestTokenReceived(RequestToken r) {
	requestToken = r;
    }
    
    public interface SignInCompleteListener {
	 void onSignInComplete();
    }
}