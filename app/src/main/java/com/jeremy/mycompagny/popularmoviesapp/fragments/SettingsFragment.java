package com.jeremy.mycompagny.popularmoviesapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.jeremy.mycompagny.popularmoviesapp.R;


public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final int SORT_BY_POPULAR = 0;
    private final int SORT_BY_RATED = 1;
    private final int SORT_BY_MY_FAVORITES = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        loadPreferences();

    }

    private  void loadPreferences(){

        ListPreference listPreferenceSortBy = (ListPreference) findPreference(getActivity().getString(R.string.pref_sort_by_key));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortByValue = sharedPref.getString(getActivity().getString(R.string.pref_sort_by_key), getActivity().getString(R.string.pref_sort_by_default));
        CharSequence[] entries = listPreferenceSortBy.getEntries();

        if(sortByValue.equals(getActivity().getString(R.string.pref_sort_by_value_popular))){
            listPreferenceSortBy.setSummary(entries[SORT_BY_POPULAR]);
        }

        if(sortByValue.equals(getActivity().getString(R.string.pref_sort_by_value_rated))){
            listPreferenceSortBy.setSummary(entries[SORT_BY_RATED]);
        }

        if(sortByValue.equals(getActivity().getString(R.string.pref_sort_by_value_favorites))){
            listPreferenceSortBy.setSummary(entries[SORT_BY_MY_FAVORITES]);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getActivity().getString(R.string.pref_sort_by_key)))
        {
            ListPreference listPreferenceSortBy = (ListPreference)findPreference(key);
            listPreferenceSortBy.setSummary(listPreferenceSortBy.getEntry());

        }
    }

}
