package dev.emmaguy.twitterclient.authentication;

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
import dev.emmaguy.twitterclient.authentication.OAuthObtainRequestTokenAndRedirectToBrowser.OnRequestTokenReceivedListener;

public class SignInFragment extends Fragment implements OnClickListener, OnRequestTokenReceivedListener, OnAccessTokenRetrievedListener {
    public static final String SCROLLLOCK_CALLBACK = "scrolllock://callback";

    private static RequestToken requestToken;
    private OnSignInCompleteListener signInCompleteListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	final View v = inflater.inflate(R.layout.fragment_sign_in, null);

	v.findViewById(R.id.login_button).setOnClickListener(this);

	return v;
    }

    @Override
    public void onClick(View arg0) {
	new OAuthObtainRequestTokenAndRedirectToBrowser(getActivity(), (OnRequestTokenReceivedListener) this).execute();
    }
    
    @Override
    public void onResume() {
	super.onResume();

	Uri uri = getActivity().getIntent().getData();
	if (uri != null && uri.toString().startsWith("scrolllock")) {
	    String verifier = uri.getQueryParameter("oauth_verifier");
	    new OAuthRetrieveAccessTokenFromRequestToken(new SettingsManager(getActivity()), requestToken, verifier,
		    (OnAccessTokenRetrievedListener) this).execute(uri);
	}
    }

    @Override
    public void onAttach(Activity activity) {
	super.onAttach(activity);

	try {
	    signInCompleteListener = (OnSignInCompleteListener) activity;
	} catch (ClassCastException e) {
	    throw new ClassCastException(activity.toString() + " must implement OnSignInCompleteListener");
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

    public interface OnSignInCompleteListener {
	void onSignInComplete();
    }
}