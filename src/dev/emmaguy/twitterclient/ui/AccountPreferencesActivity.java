package dev.emmaguy.twitterclient.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import dev.emmaguy.twitterclient.IContainSettings;
import dev.emmaguy.twitterclient.R;
import dev.emmaguy.twitterclient.SettingsManager;

public class AccountPreferencesActivity extends PreferenceActivity implements OnPreferenceClickListener {

    protected Method loadHeaders = null;
    protected Method hasHeaders = null;

    private static List<Header> headers;
    private static IContainSettings settingsManager;

    /**
     * Checks to see if using new v11+ way of handling PrefsFragments.
     * 
     * @return Returns false pre-v11, else checks to see if using headers.
     */
    public boolean isNewV11Prefs() {
	if (hasHeaders != null && loadHeaders != null) {
	    try {
		return (Boolean) hasHeaders.invoke(this);
	    } catch (IllegalArgumentException e) {
	    } catch (IllegalAccessException e) {
	    } catch (InvocationTargetException e) {
	    }
	}
	return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle aSavedState) {
	try {
	    loadHeaders = getClass().getMethod("loadHeadersFromResource", int.class, List.class);
	    hasHeaders = getClass().getMethod("hasHeaders");
	} catch (NoSuchMethodException e) {
	}

	super.onCreate(aSavedState);
	settingsManager = new SettingsManager(this.getApplicationContext());

	if (!isNewV11Prefs()) {
	    addPreferencesFromResource(R.xml.preference_login);
	    
	    ListPreference themePreference = (ListPreference) findPreference("theme_preference");
	    setOnThemeChangeListener(themePreference);
	    
	    PreferenceScreen screen = (PreferenceScreen) findPreference("authentication_preferencescreen");
	    if (screen != null) {
		initAuthenticationPreferences(screen);
	    }
	}

	updateAccountHeader();
    }

    @Override
    public void onBuildHeaders(List<Header> headersList) {
	try {
	    headers = headersList;
	    loadHeaders.invoke(this, new Object[] { R.xml.preference_headers, headersList });
	} catch (IllegalArgumentException e) {
	} catch (IllegalAccessException e) {
	} catch (InvocationTargetException e) {
	}
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static public class PrefsFragment extends PreferenceFragment implements OnPreferenceClickListener {
	@Override
	public void onCreate(Bundle aSavedState) {
	    super.onCreate(aSavedState);

	    Context context = getActivity().getApplicationContext();
	    Resources resources = context.getResources();
	    String resourcesValue = getArguments().getString("pref-resource");
	    int thePrefRes = resources.getIdentifier(resourcesValue, "xml", context.getPackageName());
	    addPreferencesFromResource(thePrefRes);
	    
	    settingsManager = new SettingsManager(getActivity());
	    updateAccountHeader();
	    
	    ListPreference themePreference = (ListPreference) findPreference("theme_preference");
	    if(themePreference != null) {
		setOnThemeChangeListener(themePreference);
	    }
	    
	    PreferenceScreen screen = (PreferenceScreen) findPreference("authentication_preferencescreen");
	    if (screen != null) {
		initAuthenticationPreferences(screen);
	    }
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
	    updateAccountHeader();
	    return true;
	}
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
	updateAccountHeader();
	return true;
    }

    private static void updateAccountHeader() {
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    buildHeader();
	}
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void buildHeader() {
	if (headers != null && headers.size() > 0) {
	    Header account = headers.get(0);
	    final String username = settingsManager.getUsername();

	    if (username != null && username.length() > 0) {
		account.title = "Twitter Account (" + username + ")";
	    } else {
		account.title = "Twitter Account";
	    }
	}
    }

    private static void setOnThemeChangeListener(ListPreference themePreference) {
	themePreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
	@Override
	public boolean onPreferenceChange(Preference preference, Object value) {
	    settingsManager.setTheme((String)value);
	    return true;
	}});
    }
    
    private static void initAuthenticationPreferences(PreferenceScreen screen) {
	screen.setOnPreferenceClickListener(new OnPreferenceClickListener() {
	    
	    @Override
	    public boolean onPreferenceClick(Preference preference) {
		settingsManager.clearUserData();
		return false;
	    }
	});
    }
}