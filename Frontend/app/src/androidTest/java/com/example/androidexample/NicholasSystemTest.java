package com.example.androidexample;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertEquals;

import com.example.androidexample.FileView.filesActivity;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NicholasSystemTest {
    @Rule
    public ActivityScenarioRule<filesActivity> activityScenarioRule = new ActivityScenarioRule<>(filesActivity.class);


    @Test
    public void TestingMoveFolder() {

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.setFileSystem("{\"root\": [] }");
            activity.setPath("{\"path\": [\"root\"] }");
        });


        // Context of the app under test.
        onView(withId(1000003)).perform(typeText("elementMove"), closeSoftKeyboard());
        onView(withId(1000001)).perform(click());
        onView(withText("elementMove")).check(matches(withText("elementMove")));

        onView(withId(1000003)).perform(typeText("element2"), closeSoftKeyboard());
        onView(withId(1000002)).perform(click());
        onView(withText("element2")).check(matches(withText("element2")));

        onView(withText("elementMove")).perform(click());

        onView(withId(1000003)).perform(typeText("elementNew"), closeSoftKeyboard());
        onView(withId(1000002)).perform(click());
        onView(withText("elementNew")).check(matches(withText("elementNew")));

        onView(withId(R.id.goback)).perform(click());
        onView(withText("element2")).check(matches(withText("element2")));

        onView(withText("elementMove")).perform(click());
        onView(withText("elementNew")).check(matches(withText("elementNew")));

    }

    @Test
    public void TestingOpenFile() {

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.setFileSystem("{\"root\": [] }");
            activity.setPath("{\"path\": [\"root\"] }");
        });
        // Context of the app under test.
        onView(withId(1000003)).perform(typeText("element3"), closeSoftKeyboard());
        onView(withId(1000002)).perform(click());
        onView(withText("element3")).check(matches(withText("element3")));

        onView(withText("element3")).perform(click());
        onView(withId(R.id.fileName)).check(matches(withText("element3")));

        onView(withId(R.id.back2main)).perform(click());
        onView(withText("element3")).check(matches(withText("element3")));
    }

    @Test
    public void TestingVoiceRecorder() {

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.setFileSystem("{\"root\": [] }");
            activity.setPath("{\"path\": [\"root\"] }");
        });

        // Context of the app under test.
        onView(withId(1000003)).perform(typeText("element5"), closeSoftKeyboard());
        onView(withId(1000002)).perform(click());
        onView(withText("element5")).check(matches(withText("element5")));

        onView(withText("element5")).perform(click());

        onView(withId(R.id.EditMarkdown)).perform(typeText("Hello"), closeSoftKeyboard());
        onView(withId(R.id.voiceButt)).perform(click());
        onView(withId(R.id.reject)).perform(click());
        onView(withId(R.id.EditMarkdown)).check(matches(withText("Hello   \n" + "   \n" + "Nothing is recorded")));
    }

    @Test
    public void TestingFileDeletion() {

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.setFileSystem("{\"root\": [] }");
            activity.setPath("{\"path\": [\"root\"] }");
        });
        // Context of the app under test.

        // Step 1: Create a new file
        onView(withId(1000003)).perform(typeText("fileToDelete"), closeSoftKeyboard());
        onView(withId(1000002)).perform(click());
        onView(withText("fileToDelete")).check(matches(withText("fileToDelete")));

        // Step 3: Click the delete button for the file
        onView(withText("Delete")).perform(click()); // Assumes there is a Delete button in the file layout

        // Step 4: Verify the file is no longer visible in the UI
        onView(withText("fileToDelete")).check(doesNotExist());
    }








}