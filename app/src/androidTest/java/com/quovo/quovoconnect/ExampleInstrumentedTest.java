package com.quovo.quovoconnect;

import android.app.Activity;

import com.quovo.sdk.QuovoConnectSdk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleInstrumentedTest {
    @Mock
    Activity mMockContext;
    @InjectMocks
    QuovoConnectSdk.Builder quovoConnectSdkBuilder = new QuovoConnectSdk.Builder(mMockContext);


    @Test
    public void titleDefault() {
      when(quovoConnectSdkBuilder.titleDefault("fhdwhfhekfhefhekjfhkjehfkjhfjk")).thenReturn(quovoConnectSdkBuilder);

    }
    @Test
    public void titleDefaultRes() {
        when(quovoConnectSdkBuilder.titleDefaultRes(1)).thenReturn(quovoConnectSdkBuilder);

    }


}
