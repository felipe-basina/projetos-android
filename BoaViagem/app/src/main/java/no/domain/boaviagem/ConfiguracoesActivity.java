package no.domain.boaviagem;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Felipe on 07/06/2016.
 */
public class ConfiguracoesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferencias);
    }
}
