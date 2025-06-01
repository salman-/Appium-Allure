package org.example;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AndroidCustomDriver extends AppiumManager{

    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 4723;
    private static AndroidDriver driver;

    @BeforeClass
    public static void setup() throws MalformedURLException, URISyntaxException {
        startAppiumServer(SERVER_IP, PORT);
        UiAutomator2Options options = setUpUiAutomator2Options();
        String appiumServerUri = String.format("http://%s:%d",SERVER_IP,PORT);
        driver = new AndroidDriver(new URI(appiumServerUri).toURL(), options);
    }

    private static UiAutomator2Options setUpUiAutomator2Options() throws URISyntaxException {
        List<String> devices = getConnectedDeviceIds();
        String pathToApkFile = getApkAbsolutePath("ApiDemos-debug.apk");
        return new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName(devices.get(0))
                .setApp(pathToApkFile)
                .setAutomationName("UIAutomator2");

    }

    private static String getApkAbsolutePath(String resourceFileName) throws URISyntaxException {
        ClassLoader classLoader = AndroidCustomDriver.class.getClassLoader();
        URL resource = classLoader.getResource(resourceFileName);

        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + resourceFileName);
        }

        // Convert URL to absolute file path
        return Paths.get(resource.toURI()).toFile().getAbsolutePath();
    }


    /**
     * Executes 'adb devices' and returns a list of connected device IDs.
     *
     * @return List of device IDs connected via ADB
     */
    public static List<String> getConnectedDeviceIds() {
        List<String> deviceList = new ArrayList<>();

        try {
            Process process = new ProcessBuilder("adb", "devices").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            boolean listStarted = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("List of devices attached")) {
                    listStarted = true;
                    continue;
                }

                if (listStarted && !line.isEmpty() && line.contains("\tdevice")) {
                    String deviceId = line.split("\\s+")[0];
                    deviceList.add(deviceId);
                }
            }

            reader.close();
        } catch (Exception e) {
            System.err.println("Error while getting ADB devices: " + e.getMessage());
        }

        return deviceList;
    }



    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
