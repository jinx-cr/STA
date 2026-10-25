import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ProductDetailsTest {

    // Delay Method
    public static void pause(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {

        WebDriver driver = new ChromeDriver();

        driver.manage().window().maximize();

        System.out.println("==================================");
        System.out.println("PRODUCT DETAILS TEST");
        System.out.println("==================================");

        // Open Website
        System.out.println("Opening Demoblaze...");
        driver.get("https://www.demoblaze.com/index.html");
        pause(3);

        System.out.println("Homepage loaded");

        // Product selection
        // Demoblaze does NOT have a search box (same limitation as SauceDemo).
        // Products are browsed via the home page catalog / category links,
        // so the command-line argument is matched against the catalog instead.

        String searchKeyword =
                args.length > 0 ? args[0] : "Samsung galaxy s6";

        System.out.println(
                "Searching for product: " + searchKeyword
        );

        // Get all products on the current catalog page
        List<WebElement> results =
                driver.findElements(By.cssSelector("#tbodyid .card"));

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
                            .findElement(By.cssSelector("a.hrefch"))
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

            // If requested product is not found on this page,
            // fall back to the first product, same as original behavior.
            if (selectedResult == null) {

                System.out.println(
                        "Requested product not found on this page."
                );

                System.out.println(
                        "Opening first available product instead..."
                );

                selectedResult = results.get(0);

                selectedProduct = selectedResult
                        .findElement(By.cssSelector("a.hrefch"))
                        .getText()
                        .trim();
            }

            System.out.println(
                    "Product found: " + selectedProduct
            );

            // Open product details
            selectedResult
                    .findElement(By.cssSelector("a.hrefch"))
                    .click();

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

        // If product name was not obtained from the catalog,
        // read it directly from the product details page.
        if (productNameOutput.isEmpty()) {

            try {

                productNameOutput =
                        driver.findElement(By.cssSelector(".name"))
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