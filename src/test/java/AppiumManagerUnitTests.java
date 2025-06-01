import org.example.AppiumManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class AppiumManagerUnitTests {

    @Test
    public void testStartServer() {
        AppiumManager.startAppiumServer("127.0.0.1",4723);

        boolean isRunning = AppiumManager.getService().isRunning();
        Assert.assertTrue(isRunning, "Appium server should be running after startAppiumServer()");

        AppiumManager.stopServer();

        boolean isStopped = !AppiumManager.getService().isRunning();
        Assert.assertTrue(isStopped, "Appium server should be stopped after stopServer()");
    }

    @Test
    public void testAppiumJSPathExistsAndIsMainJS() {
        String appiumJSPath = AppiumManager.getAppiumJSPath();

        Assert.assertNotNull(appiumJSPath, "AppiumJS path should not be null");
        Assert.assertTrue(appiumJSPath.contains("main.js"), "AppiumJS file should be main.js");
    }
}
