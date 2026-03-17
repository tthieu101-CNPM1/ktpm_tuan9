package framework.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties props = new Properties();
    private static ConfigReader instance;

    // Private constructor cho Singleton Pattern
    private ConfigReader() {
        // Mặc định là "dev" nếu không truyền tham số env
        String env = System.getProperty("env", "dev");
        String file = "src/test/resources/config-" + env + ".properties";

        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
            System.out.println("[ConfigReader] Đang dùng môi trường: " + env);
        } catch (IOException e) {
            throw new RuntimeException("Không tìm thấy file config: " + file);
        }
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    public String getBaseUrl() {
        return props.getProperty("base.url");
    }

    public String getBrowser() {
        return props.getProperty("browser", "chrome");
    }

    public int getExplicitWait() {
        return Integer.parseInt(props.getProperty("explicit.wait", "15"));
    }

    public int getImplicitWait() {
        return Integer.parseInt(props.getProperty("implicit.wait", "5"));
    }

    public int getRetryCount() {
        return Integer.parseInt(props.getProperty("retry.count", "1"));
    }

    public String getScreenshotPath() {
        return props.getProperty("screenshot.path", "target/screenshots/");
    }
}