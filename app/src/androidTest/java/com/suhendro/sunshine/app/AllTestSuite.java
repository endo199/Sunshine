package com.suhendro.sunshine.app;

import com.suhendro.sunshine.app.data.TestDb;
import com.suhendro.sunshine.app.data.TestProvider;
import com.suhendro.sunshine.app.data.TestUriMatcher;
import com.suhendro.sunshine.app.data.TestUtilities;
import com.suhendro.sunshine.app.data.TestWeatherContract;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestDb.class, TestWeatherContract.class, TestUriMatcher.class, TestProvider.class, TestFetchWeatherTask.class})
public class AllTestSuite {
}
