package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    
    private static final Object lock = new Object(); // Lock object for synchronization

    // Method to get a property value from the config file
    public static String getPropertyFromConfig(String keyName) 
    {
        synchronized (lock) {  // Locking access to the config file
            String value = null;
            Properties pro = new Properties();

            try {
                pro.load(new FileInputStream(new File(System.getProperty("user.dir") + "/Config/config.properties")));
                value = pro.getProperty(keyName);
            } catch (IOException e) {
                System.out.println("Could not load/operate this file: " + e.getMessage());
            }

            return value;
        }
    }

    // Helper method to update the token in the configuration file
    public static void updateTokenInConfig(String token) {
        synchronized (lock) {  // Locking access to the config file for update
            Properties props = new Properties();

            try {
                // Load existing properties
                String configPath = System.getProperty("user.dir") + "/Config/config.properties";
                props.load(new FileInputStream(configPath));

                // Update the token property
                props.setProperty("token", token);

                // Save the updated properties back to the file
                try (FileOutputStream output = new FileOutputStream(configPath)) {
                    props.store(output, null);
                }

                System.out.println("Token updated successfully in config file.");
            } catch (IOException e) {
                throw new RuntimeException("Failed to update token in config file.", e);
            }
        }
    }
}
