package com.suhendro.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.suhendro.sunshine.app.data.WeatherContract;

public class MainActivity extends AppCompatActivity implements ForecastFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private String mLocation;
    private boolean mUnit;
    private final String FORECAST_FRAGMENT_TAG = "forecast_fragment_tag";
    private final String DETAIL_FRAGMENT_TAG = "detail_fragment_tag";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocation = Utility.getPreferredLocation(this);
        mUnit = Utility.isMetric(this);

        if(findViewById(R.id.weather_detail_container) != null) {
            // this is two pane layout
            mTwoPane = true;

            // add detail fragment
            // savedInstanceState != null, it could be screen update because of orientation changed
            if(savedInstanceState == null) {
                String location = Utility.getPreferredLocation(this);

                Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, System.currentTimeMillis());
                DetailFragment detailFragment = DetailFragment.newInstance(uri);

                // newly created view, initialize fragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.weather_detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                        .commit();
            } else {
                mTwoPane = false;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent settingIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingIntent);

            return true;
        } else if(id == R.id.action_location_map) {

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
        String location = Utility.getPreferredLocation(this);

        ForecastFragment fragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.forecast_fragment);

        if(location != null && !location.equalsIgnoreCase(mLocation)) {
            if(fragment != null)
                fragment.onLocationChanged();

            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);
            if(detailFragment != null)
                detailFragment.onLocationChanged(location);

            mLocation = location;
        }

        boolean unit = Utility.isMetric(this);
        if(unit != mUnit) {
            mUnit = unit;
            fragment.onUnitChanged();
        }
    }

    @Override
    public void onForecastInteraction(Uri uri) {
        if(mTwoPane) {
            // change the detail fragment
            String location = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
            DetailFragment detailFragment = DetailFragment.newInstance(uri);

            // if we already have detail fragment, why can't we just change the data not the whole fragment ?
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, detailFragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            // start detail activity, because it's a smartphone type
            Intent intent = new Intent(this, DetailActivity.class);
            intent.setData(uri);

            startActivity(intent);
        }
    }

    @Override
    public void onDetailInteraction(Uri uri) {

    }
}
