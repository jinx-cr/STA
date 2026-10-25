import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class productSearch {

    // Delay Method
    public static void pause(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        // ---- Chrome options: disable password breach popup ----
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);

        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection,AutofillServerCommunication");

        WebDriver driver = new ChromeDriver(options);

        driver.manage().window().maximize();

        System.out.println("==================================");
        System.out.println("PRODUCT DETAILS TEST");
        System.out.println("==================================");

        // Open Website
        System.out.println("Opening SauceDemo...");
        driver.get("https://www.saucedemo.com/");
        pause(2);

        // Login
        System.out.println("Logging in...");

        WebElement username = driver.findElement(By.id("user-name"));
        WebElement password = driver.findElement(By.id("password"));

        username.sendKeys("standard_user");
        password.sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        pause(4);

        System.out.println("Login successful");

        // Product selection
        // SauceDemo does not have a search box.
        // Therefore, the command-line argument is used
        // to select a product from the available products.

        String searchKeyword =
                args.length > 0 ? args[0] : "Sauce Labs Backpack";

        System.out.println(
                "Searching for product: " + searchKeyword
        );

        // Get all products
        List<WebElement> results =
                driver.findElements(By.cssSelector(".inventory_item"));

        String selectedProduct = "";

        if (!results.isEmpty()) {

            System.out.println(
                    "Found " + results.size() + " products"
            );

            WebElement selectedResult = null;

            // Find product matching the command-line argument
            for (WebElement product : results) {

                try {

                    String productName = product
                            .findElement(
                                    By.cssSelector(".inventory_item_name")
                            )
                            .getText()
                            .trim();

                    if (productName.equalsIgnoreCase(searchKeyword)) {

                        selectedResult = product;
                        selectedProduct = productName;
                        break;
                    }

                } catch (Exception e) {
                    // Continue checking other products
                }
            }

            // If requested product is not found,
            // use the first product just like the original code.
            if (selectedResult == null) {

                System.out.println(
                        "Requested product not found."
                );

                System.out.println(
                        "Opening first available product instead..."
                );

                selectedResult = results.get(0);

                selectedProduct = selectedResult
                        .findElement(
                                By.cssSelector(".inventory_item_name")
                        )
                        .getText()
                        .trim();
            }

            System.out.println(
                    "Product found: " + selectedProduct
            );

            // Open product details
            try {

                WebElement productLink =
                        selectedResult.findElement(
                                By.cssSelector(
                                        ".inventory_item_name a"
                                )
                        );

                productLink.click();

            } catch (Exception e) {

                // Fallback
                selectedResult
                        .findElement(
                                By.cssSelector(
                                        ".inventory_item_img a"
                                )
                        )
                        .click();
            }

            System.out.println(
                    "Opened product details"
            );

        } else {

            System.out.println(
                    "No products found."
            );
        }

        pause(3);

        // Read product page details
        String pageTitle = driver.getTitle();
        String currentUrl = driver.getCurrentUrl();

        String productNameOutput = selectedProduct;

        // If product name was not obtained from product list,
        // read it directly from product details page.
        if (productNameOutput.isEmpty()) {

            try {

                productNameOutput =
                        driver.findElement(
                                        By.cssSelector(
                                                ".inventory_details_name"
                                        )
                                )
                                .getText()
                                .trim();

            } catch (Exception e) {

                productNameOutput = pageTitle;
            }
        }

        System.out.println("----------------------------------");
        System.out.println("Page Title : " + pageTitle);
        System.out.println("Page URL : " + currentUrl);
        System.out.println(
                "Product Name Output : " + productNameOutput
        );
        System.out.println("----------------------------------");

        pause(2);

        // Take Screenshot
        File src =
                ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.FILE);

        Files.copy(
                src.toPath(),
                new File("ProductDetails.png").toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println(
                "Screenshot Saved Successfully"
        );

        pause(5);

        // Close browser
        driver.quit();

        System.out.println("Browser Closed");
        System.out.println(
                "Test Completed Successfully"
        );

        System.exit(0);
    }
}