package tests;

import framework.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlakySimulationTest extends BaseTest {
    // Biến static đếm số lần test được gọi
    private static int callCount = 0;

    @Test(description = "Test mô phỏng flaky fail 2 lần đầu, pass lần thứ 3")
    public void testFlakyScenario() {
        callCount++;
        System.out.println("[FlakyTest] Đang chạy lần thứ: " + callCount);

        // Giả lập mạng bị rớt ở 2 lần chạy đầu tiên
        if (callCount <= 2) {
            Assert.fail("Mô phỏng lỗi mạng tạm thời lần " + callCount);
        }

        // Đến lần thứ 3 thì mạng ổn định, pass thành công
        Assert.assertTrue(true, "Test pass ở lần thứ " + callCount);
    }
}