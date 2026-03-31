package framework.base;

import framework.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public abstract class BaseTest {
    // Sử dụng ThreadLocal để đảm bảo an toàn khi chạy song song (Parallel Testing) [cite: 288-289]
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    protected WebDriver getDriver() {
        return tlDriver.get();
    }

    @Parameters({"browser", "env"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser, @Optional("dev") String env) {
        // Thiết lập biến môi trường trước khi khởi tạo cấu hình [cite: 294-296]
        System.setProperty("env", env);

        // Kiểm tra xem có đang chạy trên môi trường CI (GitHub Actions) hay không [cite: 118, 702]
        boolean isCI = System.getenv("CI") != null;
        WebDriver driver;

        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (isCI) {
                    ffOptions.addArguments("-headless"); // Kích hoạt headless cho Firefox trên CI [cite: 156, 723]
                }
                driver = new FirefoxDriver(ffOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (isCI) {
                    // Cấu hình Chrome chạy Headless bắt buộc trên Linux CI [cite: 129, 132, 135, 710, 711, 714]
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage"); // Tránh lỗi tràn bộ nhớ (OOM)
                    chromeOptions.addArguments("--window-size=1920,1080"); // Đặt độ phân giải cố định [cite: 136, 715]
                } else {
                    chromeOptions.addArguments("--start-maximized");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        // Cấu hình timeouts và URL từ ConfigReader [cite: 503, 508]
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getInstance().getImplicitWait()));
        driver.get(ConfigReader.getInstance().getBaseUrl());

        tlDriver.set(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Tự động chụp ảnh màn hình nếu kịch bản kiểm thử thất bại [cite: 202, 304, 1006]
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot(result.getName());
        }

        // Giải phóng driver để tránh rò rỉ bộ nhớ [cite: 314, 1009]
        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove();
        }
    }

    private void captureScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        File srcFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        File destFile = new File(ConfigReader.getInstance().getScreenshotPath() + fileName);

        try {
            destFile.getParentFile().mkdirs(); 
            Files.copy(srcFile.toPath(), destFile.toPath());
            System.out.println("[Log] Screenshot saved at: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[Error] Could not save screenshot: " + e.getMessage());
        }
    }
}
