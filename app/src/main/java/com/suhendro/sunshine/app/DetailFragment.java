package com.suhendro.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.suhendro.sunshine.app.data.WeatherContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int DETAIL_ID = DetailFragment.class.hashCode();
    public static final String DETAIL_URI = "detail_uri";

    private OnFragmentInteractionListener mListener;
    private ShareActionProvider mShareActionProvider;
    private String mForecastData = null;
    private Uri mUri;

    private ImageView mIconView;
    private TextView mDateView;
    private TextView mFriendlyDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    private static final String[] DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    public static DetailFragment newInstance(Uri uri) {
        DetailFragment detailFragment = new DetailFragment();

        Bundle args = new Bundle();
        args.putParcelable(DETAIL_URI, uri);
        detailFragment.setArguments(args);

        return detailFragment;
    }

    public DetailFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = (getArguments() != null) ? getArguments().<Uri>getParcelable(DETAIL_URI) : null;

        getLoaderManager().initLoader(DETAIL_ID, savedInstanceState, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mIconView = (ImageView) view.findViewById(R.id.detail_icon);
        mDateView = (TextView) view.findViewById(R.id.detail_day_textview);
        mFriendlyDateView = (TextView) view.findViewById(R.id.detail_date_textview);
        mDescriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) view.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) view.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        if(mForecastData != null)
            mShareActionProvider.setShareIntent(createForecastIntent());
    }

    private Intent createForecastIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");

        String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        intent.putExtra(Intent.EXTRA_TEXT, mForecastData + FORECAST_SHARE_HASHTAG);

        return intent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Intent intent = getActivity().getIntent();
//
//        if(mUri == null) {
//            if((intent == null || intent.getData() == null)) {
//                Log.i("XXX", "there is no intent or uri data");
//                return null;
//            } else {
//                // data from activity call
//                mUri = intent.getData();
//            }
//        }
//
//        if(mUri == null)
//            Log.e("XXX", "mUri should not be empty");
        if(getArguments() == null)
            return null;

        mUri = getArguments().getParcelable(DETAIL_URI);

        return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && !data.moveToFirst())
            return;

        int weatherCondId = data.getInt(COL_WEATHER_CONDITION_ID);


        mIconView.setImageResource(Utility.getWeatherResource(weatherCondId, false));

        long date = data.getLong(COL_WEATHER_DATE);
        String day = Utility.getDayName(getActivity(), date);
        mDateView.setText(day);

        String theDate = Utility.getFormattedMonthDay(getActivity(), date);
        mFriendlyDateView.setText(theDate);

        String description = data.getString(COL_WEATHER_DESC);
        mDescriptionView.setText(description);

        boolean isMetric = Utility.isMetric(getActivity());
        String tempHigh = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        mHighTempView.setText(tempHigh);

        String tempLow = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        mLowTempView.setText(tempLow);

        String humidity = getActivity().getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY));
        mHumidityView.setText(humidity);

        String wind = Utility.getFormattedWind(getActivity(), data.getFloat(COL_WEATHER_WIND_SPEED), data.getFloat(COL_WEATHER_DEGREES));
        mWindView.setText(wind);

        String pressure = getActivity().getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESSURE));
        mPressureView.setText(pressure);

        mForecastData = String.format("%s - %s - %s / %s", date, description, tempHigh, tempLow);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onLocationChanged(String newLocation) {
        Uri uri = mUri;

        if(uri != null) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updateUri;

            getLoaderManager().restartLoader(DETAIL_ID, null, this);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onDetailInteraction(Uri uri);
    }
}
