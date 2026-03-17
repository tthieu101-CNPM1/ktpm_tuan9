package framework.pages;

import framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class InventoryPage extends BasePage {

    @FindBy(css = ".inventory_list")
    private WebElement inventoryList;

    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(css = ".inventory_item_button")
    private List<WebElement> addToCartButtons;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> itemNames;

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isElementVisible(By.cssSelector(".inventory_list"));
    }

    public InventoryPage addFirstItemToCart() {
        if (!addToCartButtons.isEmpty()) {
            waitAndClick(addToCartButtons.get(0));
        }
        return this;
    }

    public InventoryPage addItemByName(String name) {
        for (int i = 0; i < itemNames.size(); i++) {
            if (getText(itemNames.get(i)).equals(name)) {
                waitAndClick(addToCartButtons.get(i));
                break;
            }
        }
        return this;
    }

    public int getCartItemCount() {
        try {
            return Integer.parseInt(getText(cartBadge));
        } catch (Exception e) {
            return 0; // Trả về 0 nếu giỏ hàng rỗng (badge không hiện)
        }
    }

    public CartPage goToCart() {
        driver.findElement(By.className("shopping_cart_link")).click();
        return new CartPage(driver);
    }
}