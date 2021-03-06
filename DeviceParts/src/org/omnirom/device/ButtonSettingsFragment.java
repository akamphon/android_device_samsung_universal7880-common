/*
 * Copyright (C) 2018 Omnirom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnirom.device;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.provider.Settings;
import android.content.Context;

import java.io.File;

import org.omnirom.device.utils.FileUtils;

public class ButtonSettingsFragment extends PreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String KEY_HOMEBUTTON_SWITCH = "homebutton_switch";
    private static final String KEY_HARDWARE_KEYS_DISABLE = "hardware_keys_disable";
    private SwitchPreference mHomeButtonSwitch;
    private SwitchPreference mKeysDisablesSwitch;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.button_panel);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        SwitchPreference mHomeButtonSwitch = (SwitchPreference) findPreference(KEY_HOMEBUTTON_SWITCH);
        mHomeButtonSwitch.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.OMNI_BUTTON_EXTRA_KEY_MAPPING, 0) != 0);

       mHomeButtonSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
             @Override
             public boolean onPreferenceClick(Preference preference) {
                 if (preference == mHomeButtonSwitch) {
                     Settings.System.putInt(getActivity().getContentResolver(),
                             Settings.System.OMNI_BUTTON_EXTRA_KEY_MAPPING, mHomeButtonSwitch.isChecked() ? 1 : 0);
                     return true;
                 }
                 return false;
             }
        });

        SwitchPreference mKeysDisablesSwitch = (SwitchPreference) findPreference(KEY_HARDWARE_KEYS_DISABLE);
        mKeysDisablesSwitch.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.OMNI_HARDWARE_KEYS_DISABLE, 0) != 0);

        mKeysDisablesSwitch.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
             @Override
             public boolean onPreferenceClick(Preference preference) {
                 if (preference == mKeysDisablesSwitch) {
                     Settings.System.putInt(getActivity().getContentResolver(),
                             Settings.System.OMNI_HARDWARE_KEYS_DISABLE, mKeysDisablesSwitch.isChecked() ? 1 : 0);
                     return true;
                 }
                 return false;
             }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferencesBasedOnDependencies();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String node = Constants.sBooleanNodePreferenceMap.get(preference.getKey());
        if (!TextUtils.isEmpty(node)) {
            Boolean value = (Boolean) newValue;
            FileUtils.writeLine(node, value ? "1" : "0");
            if (Constants.FP_WAKEUP_KEY.equals(preference.getKey())) {
                Utils.broadcastCustIntent(getContext(), value);
            }
            return true;
        }
        node = Constants.sStringNodePreferenceMap.get(preference.getKey());
        if (!TextUtils.isEmpty(node)) {
            FileUtils.writeLine(node, (String) newValue);
            return true;
        }

        return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        // Initialize node preferences
        for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            b.setOnPreferenceChangeListener(this);
            String node = Constants.sBooleanNodePreferenceMap.get(pref);
            if (new File(node).exists()) {
                String curNodeValue = FileUtils.readOneLine(node);
                b.setChecked(curNodeValue.equals("1"));
            } else {
                b.setEnabled(false);
            }
        }
        for (String pref : Constants.sStringNodePreferenceMap.keySet()) {
            ListPreference l = (ListPreference) findPreference(pref);
            if (l == null) continue;
            l.setOnPreferenceChangeListener(this);
            String node = Constants.sStringNodePreferenceMap.get(pref);
            if (new File(node).exists()) {
                l.setValue(FileUtils.readOneLine(node));
            } else {
                l.setEnabled(false);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    private void updatePreferencesBasedOnDependencies() {
        for (String pref : Constants.sNodeDependencyMap.keySet()) {
            SwitchPreference b = (SwitchPreference) findPreference(pref);
            if (b == null) continue;
            String dependencyNode = Constants.sNodeDependencyMap.get(pref)[0];
            if (new File(dependencyNode).exists()) {
                String dependencyNodeValue = FileUtils.readOneLine(dependencyNode);
                boolean shouldSetEnabled = dependencyNodeValue.equals(
                        Constants.sNodeDependencyMap.get(pref)[1]);
                Utils.updateDependentPreference(getContext(), b, pref, shouldSetEnabled);
            }
        }
    }
}
