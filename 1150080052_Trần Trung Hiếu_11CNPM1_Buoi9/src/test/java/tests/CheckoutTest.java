package tests;

import framework.base.BaseTest;
import framework.utils.TestDataFactory;
import org.testng.annotations.Test;
import java.util.Map;

public class CheckoutTest extends BaseTest {

    @Test(description = "Test sinh dữ liệu ngẫu nhiên với Java Faker")
    public void testCheckoutWithFakerData() {
        // Gọi factory để lấy data ảo
        Map<String, String> checkoutInfo = TestDataFactory.randomCheckoutData();

        String firstName = checkoutInfo.get("firstName");
        String lastName = checkoutInfo.get("lastName");
        String postalCode = checkoutInfo.get("postalCode");

        // In ra log để chụp màn hình chứng minh mỗi lần chạy data sẽ khác nhau (Yêu cầu bài 4B)
        System.out.println("--- Dữ liệu Checkout Ngẫu Nhiên ---");
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);
        System.out.println("Postal Code: " + postalCode);
        System.out.println("-----------------------------------");

        // (Lưu ý: Ở bài thực tế, bạn sẽ dùng loginPage -> cartPage -> checkoutPage và truyền các biến này vào hàm điền form.
        // Hiện tại in ra console là đã đủ chứng minh Faker hoạt động theo yêu cầu bài 4)
    }
}