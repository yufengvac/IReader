package com.yufeng.ireader;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.yufeng.ireader.reader.utils.CharCalculator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.yufeng.demo", appContext.getPackageName());
    }

    @Test
    void testFirstIndexNotBlank(){
        Assert.assertEquals(CharCalculator.getFirstIndexNotBlank("  你好世界"),2);
    }
}
