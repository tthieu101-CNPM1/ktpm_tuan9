package tests;

import framework.base.BaseTest;
import framework.pages.InventoryPage;
import framework.pages.LoginPage;
import framework.utils.JsonReader;
import framework.utils.UserData;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class UserLoginTest extends BaseTest {

    @DataProvider(name = "jsonUsers")
    public Object[][] getUsersFromJson() throws IOException {
        List<UserData> users = JsonReader.readUsers("src/test/resources/testdata/users.json");
        return users.stream()
                .map(u -> new Object[]{u.username, u.password, u.expectSuccess, u.description})
                .toArray(Object[][]::new);
    }

    @Test(dataProvider = "jsonUsers")
    public void testLoginFromJson(String username, String password, boolean expectSuccess, String description) {
        System.out.println("[Test JSON] " + description);
        LoginPage loginPage = new LoginPage(getDriver());

        if (expectSuccess) {
            InventoryPage inventoryPage = loginPage.login(username, password);
            Assert.assertTrue(inventoryPage.isLoaded(), "Đăng nhập đáng lẽ phải thành công: " + description);
        } else {
            loginPage.loginExpectingFailure(username, password);
            Assert.assertTrue(loginPage.isErrorDisplayed(), "Đăng nhập đáng lẽ phải thất bại: " + description);
        }
    }
}