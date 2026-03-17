package framework.utils;

import framework.config.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        // Lấy số lần retry tối đa từ file config (Bài 5)
        int maxRetry = ConfigReader.getInstance().getRetryCount();

        if (retryCount < maxRetry) {
            retryCount++;
            System.out.println("[Retry] Đang chạy lại lần " + retryCount + " cho test: " + result.getName());
            return true; // Trả về true để yêu cầu TestNG chạy lại [cite: 535-537]
        }
        return false; // Hết số lượt cho phép, đánh dấu FAIL chính thức [cite: 539-540]
    }
}