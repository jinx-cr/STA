import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ProductSearchTest {

    public static void main(String[] args) {

        WebDriver driver = null;

        try {

            // Setup ChromeDriver
            WebDriverManager.chromedriver().setup();

            // Launch Chrome
            driver = new ChromeDriver();

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            System.out.println("==================================");
            System.out.println("PRODUCT SEARCH TEST");
            System.out.println("==================================");

            // Navigate to Tricentis DemoWebShop (same site used in Exp 2)
            driver.get("https://demowebshop.tricentis.com/");

            // Search for a product
            String searchTerm = "computer";
            System.out.println("Searching for: " + searchTerm);

            WebElement searchBox = driver.findElement(By.id("small-searchterms"));
            searchBox.sendKeys(searchTerm);
            searchBox.sendKeys(Keys.ENTER);

            // Wait for search results
            Thread.sleep(3000);

            System.out.println("---------------------------------------------------------------");

            // Verify search results
            List<WebElement> searchResults = driver.findElements(
                    By.cssSelector("div.product-item")
            );

            if (searchResults.size() > 0) {
                System.out.println("Search results displayed.");
                System.out.println("Products Found: " + searchResults.size());
            } else {
                System.out.println("Defect: Search results not displayed.");
            }
            System.out.println("---------------------------------------------------------------");

            // Screenshot
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(
                    src.toPath(),
                    new File("ProductSearchResults.png").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot Saved Successfully");

            Thread.sleep(2000);

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        } finally {

            if (driver != null) {
                driver.quit();
            }

            System.out.println("Browser Closed.");
            System.out.println("Test Completed.");

            System.exit(0);
        }
    }
}
