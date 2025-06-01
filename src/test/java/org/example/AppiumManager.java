package org.example;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;

public class AppiumManager {

    private static AppiumDriverLocalService service;

    /**
     * Starts the Appium server programmatically using the AppiumServiceBuilder.
     * <p>
     * This method initializes the Appium server on a free port with the specified
     * IP address and path to the Appium main.js script. It must be called before
     * initializing Appium drivers in your test.
     * </p>
     * <p>
     * If the server starts successfully, its URL will be printed to the console.
     * </p>
     *
     * @throws RuntimeException if the Appium server fails to start.
     */
    public static void startAppiumServer(String serverIp,int port) {

        String pathToMainJsFile = getAppiumJSPath();
        service = new AppiumServiceBuilder()
                .usingPort(port)
                .withAppiumJS(new File(pathToMainJsFile))
                .withIPAddress(serverIp)
                .build();

        service.start();

        if (service.isRunning()) {
            System.out.println("Appium server started at: " + service.getUrl());
        } else {
            System.out.println("Failed to start Appium server.");
        }
    }

    /**
     * Stops the Appium server if it is currently running.
     * <p>
     * This method safely shuts down the server started with {@link #startServer()}.
     * It's recommended to call this method in test cleanup or teardown logic to
     * release system resources.
     * </p>
     */
    public static void stopServer() {
        if (service != null) {
            service.stop();
            System.out.println("Appium server stopped.");
        }
    }

    /**
     * Resolves the absolute path to Appium's main.js file based on the operating system.
     * <p>
     * This method assumes Appium is installed globally using npm and located in the default
     * global node_modules directory:
     * <ul>
     *     <li>On Windows: %APPDATA%\npm\node_modules\appium\build\lib\main.js</li>
     *     <li>On macOS/Linux: /usr/local/lib/node_modules/appium/build/lib/main.js</li>
     * </ul>
     * </p>
     *
     * @return A {@link File} object representing the absolute path to Appium's main.js file.
     * @throws UnsupportedOperationException if the OS is not Windows, macOS, or Linux.
     * @throws RuntimeException              if the resolved file path does not exist.
     */

    public static String getAppiumJSPath() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String appiumPath;

        if (os.contains("win")) {
            // Windows path
            String userHome = System.getenv("APPDATA");
            appiumPath = Paths.get(userHome, "npm", "node_modules", "appium", "build", "lib", "main.js").toString();

        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
            // macOS or Linux
            appiumPath = "/usr/local/lib/node_modules/appium/build/lib/main.js";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }

        File file = new File(appiumPath);
        if (!file.exists()) {
            throw new RuntimeException("Appium main.js not found at: " + appiumPath);
        }

        return file.getAbsolutePath();
    }

    /**
     * Exposes the AppiumDriverLocalService instance for testing.
     *
     * @return the AppiumDriverLocalService instance
     */
    public static AppiumDriverLocalService getService() {
        return service;
    }
}
