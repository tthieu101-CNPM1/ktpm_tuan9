package framework.base;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Dùng ConfigReader đọc explicit.wait thay vì hardcode 15 giây
        int waitTime = framework.config.ConfigReader.getInstance().getExplicitWait();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(waitTime));
        PageFactory.initElements(driver, this);
    }

    /**
     * Chờ element có thể click được và thực hiện click.
     * Dùng cho các nút bấm, link để tránh lỗi ElementNotInteractableException.
     */
    protected void waitAndClick(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    /**
     * Chờ hiển thị, xóa nội dung cũ (nếu có) và gõ text mới vào ô input.
     * Dùng cho textbox, textarea để tránh dữ liệu bị nối thêm.
     */
    protected void waitAndType(WebElement element, String text) {
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Chờ hiển thị và lấy text của element, tự động cắt khoảng trắng 2 đầu (trim).
     * Dùng để lấy thông báo lỗi, tiêu đề, nhãn.
     */
    protected String getText(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element)).getText().trim();
    }

    /**
     * Kiểm tra element có hiển thị hay không bằng locator.
     * KHÔNG throw exception nếu không tìm thấy, xử lý an toàn StaleElementReferenceException.
     */
    protected boolean isElementVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * Cuộn trang đến vị trí của element bằng JavascriptExecutor.
     * Dùng khi element bị che khuất hoặc nằm ngoài màn hình (viewport).
     */
    protected void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Chờ cho toàn bộ trang load xong trạng thái ready.
     * Dùng sau khi điều hướng (navigation) sang URL mới.
     */
    protected void waitForPageLoad() {
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Chờ hiển thị và lấy giá trị của một thuộc tính (attribute) từ element.
     * Dùng để lấy href, src, value...
     */
    protected String getAttribute(WebElement element, String attr) {
        return wait.until(ExpectedConditions.visibilityOf(element)).getAttribute(attr);
    }
}