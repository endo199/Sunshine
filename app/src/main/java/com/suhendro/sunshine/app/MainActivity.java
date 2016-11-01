package com.suhendro.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ForecastFragment.OnFragmentInteractionListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation;
    private boolean mUnit;
    private final String FORECAST_TAG = "forecast_fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocation = Utility.getPreferredLocation(this);
        mUnit = Utility.isMetric(this);

//        if(savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.activity_main, new ForecastFragment(), FORECAST_TAG)
//                    .commit();
//        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);

            return true;
        } else if(id == R.id.action_location_map) {
//            Intent locationIntent = new Intent(Intent.ACTION_VIEW);
//
//            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//            String location = pref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
//
//            Uri geolocation = Uri.parse("geo:0,0?q="+location);
//            locationIntent.setData(geolocation);
//
//            if(locationIntent.resolveActivity(getPackageManager()) != null) {
//                startActivity(locationIntent);
//            }

            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        String location = Utility.getPreferredLocation(this);

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("XXX", "onResume is called");
        String location = Utility.getPreferredLocation(this);

        ForecastFragment fragment = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECAST_TAG);

        if(!location.equalsIgnoreCase(mLocation)) {
            Log.i("XXX", "location is changed");
            fragment.onLocationChanged();

            mLocation = location;
        }

        boolean unit = Utility.isMetric(this);
        if(unit != mUnit) {
            mUnit = unit;
            fragment.onUnitChanged();
        }
    }
}
