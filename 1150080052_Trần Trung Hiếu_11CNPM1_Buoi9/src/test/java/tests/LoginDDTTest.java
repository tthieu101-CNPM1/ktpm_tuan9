package tests;

import framework.base.BaseTest;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import framework.utils.ExcelReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginDDTTest extends BaseTest {

    // Đường dẫn trỏ tới file Excel test data
    private final String EXCEL_PATH = "src/test/resources/testdata/login_data.xlsx";

    // --- Cung cấp dữ liệu từ 3 sheet ---
    @DataProvider(name = "smokeData")
    public Object[][] getSmokeData() {
        return ExcelReader.getData(EXCEL_PATH, "SmokeCases");
    }

    @DataProvider(name = "negativeData")
    public Object[][] getNegativeData() {
        return ExcelReader.getData(EXCEL_PATH, "NegativeCases");
    }

    @DataProvider(name = "boundaryData")
    public Object[][] getBoundaryData() {
        return ExcelReader.getData(EXCEL_PATH, "BoundaryCases");
    }

    // --- Kịch bản 1: Đăng nhập thành công (Happy Path) ---
    // Chạy trong cả Smoke và Regression
    @Test(groups = {"smoke", "regression"}, dataProvider = "smokeData")
    public void testLoginSmoke(String username, String password, String expectedUrl, String description) {
        LoginPage loginPage = new LoginPage(getDriver());
        InventoryPage inventoryPage = loginPage.login(username, password);

        Assert.assertTrue(inventoryPage.isLoaded(), "Lỗi tải trang sau khi login: " + description);
        Assert.assertTrue(getDriver().getCurrentUrl().contains(expectedUrl), "Sai URL: " + description);

        // In log ra console để khi chụp HTML Report sẽ thấy tên description rõ ràng
        System.out.println("[Test Description] " + description);
    }

    // --- Kịch bản 2: Đăng nhập thất bại ---
    @Test(groups = {"regression"}, dataProvider = "negativeData")
    public void testLoginNegative(String username, String password, String expectedError, String description) {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loginExpectingFailure(username, password);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Lỗi không hiển thị: " + description);
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError, "Sai nội dung thông báo: " + description);

        System.out.println("[Test Description] " + description);
    }

    // --- Kịch bản 3: Dữ liệu biên ---
    @Test(groups = {"regression"}, dataProvider = "boundaryData")
    public void testLoginBoundary(String username, String password, String expectedError, String description) {
        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.loginExpectingFailure(username, password);

        Assert.assertTrue(loginPage.isErrorDisplayed(), "Lỗi không hiển thị cho test biên: " + description);
        Assert.assertEquals(loginPage.getErrorMessage(), expectedError, "Sai nội dung lỗi biên: " + description);

        System.out.println("[Test Description] " + description);
    }
}