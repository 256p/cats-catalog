package com.bidstack.cat_gallery.espresso;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.bidstack.cat_gallery.App;
import com.bidstack.cat_gallery.R;
import com.bidstack.cat_gallery.di.DaggerTestAppComponent;
import com.bidstack.cat_gallery.di.TestAppComponent;
import com.bidstack.cat_gallery.di.TestAppModule;
import com.bidstack.cat_gallery.ui.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.bidstack.cat_gallery.TestUtils.withCustomConstraints;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SuccessfulDataRefreshTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TestAppComponent testAppComponent = DaggerTestAppComponent.builder()
                .appModule(new TestAppModule(new App()))
                .build();
        App.setComponent(testAppComponent);
        testAppComponent.inject(this);

    }

    @Test
    public void testRefresh() throws InterruptedException {
        SwipeRefreshLayout swipeRefreshLayout = activityTestRule.getActivity().findViewById(R.id.swipe_refresh);
        RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.cats_rv);
        Thread.sleep(5000);
        onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.error_container)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.swipe_refresh)).perform(withCustomConstraints(swipeDown(), isDisplayingAtLeast(80)));
        Thread.sleep(5000);

        activityTestRule.getActivity().runOnUiThread(() -> {
            assertFalse(swipeRefreshLayout.isRefreshing());
            assertTrue(recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() == 40);
        });

        onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.error_container)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

}
