package framework.base;

import framework.config.ConfigReader; // Bắt buộc phải import ConfigReader
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
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
    // Dùng ThreadLocal để chạy song song an toàn, không sợ crash khi mở nhiều tab [cite: 288-289]
    private static ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    protected WebDriver getDriver() {
        return tlDriver.get();
    }

    @Parameters({"browser", "env"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browser, @Optional("dev") String env) {
        // 1. Set biến môi trường TRƯỚC khi khởi tạo ConfigReader [cite: 294-296]
        System.setProperty("env", env);

        // 2. Khởi tạo Driver hỗ trợ chạy nhiều trình duyệt
        WebDriver driver;
        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
        }

        driver.manage().window().maximize();

        // 3. Dùng ConfigReader đọc timeout thay vì hardcode 5 giây [cite: 508-510]
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getInstance().getImplicitWait()));

        // 4. Dùng ConfigReader đọc URL thay vì hardcode "https://www.saucedemo.com" [cite: 503-504]
        driver.get(ConfigReader.getInstance().getBaseUrl());

        tlDriver.set(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        // Chụp ảnh màn hình nếu test bị FAIL [cite: 304-306]
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot(result.getName());
        }

        // Dọn dẹp an toàn tránh memory leak [cite: 314-316]
        if (getDriver() != null) {
            getDriver().quit();
            tlDriver.remove();
        }
    }

    private void captureScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = testName + "_" + timestamp + ".png";
        File srcFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        File destFile = new File("target/screenshots/" + fileName);

        try {
            destFile.getParentFile().mkdirs(); // Tự động tạo thư mục screenshots nếu chưa có
            Files.copy(srcFile.toPath(), destFile.toPath());
            System.out.println("Screenshot saved successfully at: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}