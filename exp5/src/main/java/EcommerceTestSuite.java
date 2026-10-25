import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * E-commerce test suite against https://www.demoblaze.com/
 *
 * Real elements confirmed by fetching the live page / verified public
 * automation examples for this site:
 *   - product link on homepage : <a class="hrefch">Samsung galaxy s6</a>
 *   - add-to-cart control          : an <a> with NO id, matched by its
 *                                    visible text "Add to cart" (not #addToCart -
 *                                    that id does not exist on the real page)
 *   - clicking add-to-cart fires a native JS alert("Product added.")
 *   - cart nav link                : id="cartur"
 *   - cart rows                      : table id="tbodyid"
 *   - cart total                      : id="totalp"
 *   - place order button          : <button> with visible text "Place Order"
 *   - checkout modal fields   : id="name", id="country", id="city",
 *                                    id="card", id="month", id="year"
 *   - confirm purchase button  : id="orderModalBtn" (text "Purchase")
 *   - success confirmation      : h2 containing "Thank you for your purchase!"
 *   - close confirmation             : button class "confirm" (text "OK")
 *
 * Each test case is wrapped independently so one failure prints one clean
 * line and does not stop the remaining test cases from attempting to run.
 */
public class EcommerceTestSuite {

    static WebDriver driver;
    static WebDriverWait wait;

    /** Runs one labeled test case; catches its own failure so nothing else stops. */
    interface Check {
        void run() throws Exception;
    }

    static void testCase(int number, String description, Check check) {
        try {
            check.run();
            System.out.println("Test Case " + number + " Passed: " + description);
        } catch (Exception e) {
            String reason = e.getClass().getSimpleName()
                    + (e.getMessage() != null ? " - " + e.getMessage().split("\n")[0] : "");
            System.out.println("Test Case " + number + " Failed: " + description + " (" + reason + ")");
        }
    }

    public static void main(String[] args) {

        // WebDriverManager and ChromeDriver both print startup warnings
        // (missing SLF4J binding, CDP version mismatch) straight to
        // System.err. Neither affects the test itself, so stderr is muted
        // only for these two setup calls, then restored immediately.
        PrintStream realErr = System.err;
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) { /* discard */ }
        }));

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        System.setErr(realErr);

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            testCase(1, "Homepage loads with product catalog visible", () -> {
                driver.get("https://www.demoblaze.com/");
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("card-title")));
                if (driver.findElements(By.className("card-title")).isEmpty())
                    throw new AssertionError("no product cards found");
            });

            testCase(2, "Open product detail page for Samsung galaxy s6", () -> {
                driver.findElement(By.linkText("Samsung galaxy s6")).click();
                wait.until(ExpectedConditions.urlContains("prod.html"));
            });

            testCase(3, "Product title is displayed on the detail page", () -> {
                WebElement title = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.className("name")));
                if (!title.getText().toLowerCase().contains("galaxy s6"))
                    throw new AssertionError("unexpected title: " + title.getText());
            });

            testCase(4, "Add-to-cart control is visible", () -> {
                WebElement addToCart = wait.until(
                        ExpectedConditions.elementToBeClickable(By.linkText("Add to cart")));
                if (!addToCart.isDisplayed())
                    throw new AssertionError("add to cart link not visible");
            });

            testCase(5, "Add product to cart and confirm alert", () -> {
                driver.findElement(By.linkText("Add to cart")).click();
                Alert alert = wait.until(ExpectedConditions.alertIsPresent());
                alert.accept();
            });

            testCase(6, "Navigate to the cart page", () -> {
                driver.findElement(By.id("cartur")).click();
                wait.until(ExpectedConditions.urlContains("cart.html"));
            });

            testCase(7, "Product appears in the cart", () -> {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#tbodyid tr")));
                if (driver.findElements(By.cssSelector("#tbodyid tr")).isEmpty())
                    throw new AssertionError("cart is empty");
            });

            testCase(8, "Cart total price is displayed", () -> {
                wait.until(driver1 -> !driver1.findElement(By.id("totalp")).getText().trim().isEmpty());
                String total = driver.findElement(By.id("totalp")).getText().trim();
                if (total.isEmpty())
                    throw new AssertionError("total price missing");
            });

            testCase(9, "Open the checkout (Place Order) form", () -> {
                driver.findElement(By.xpath("//button[text()='Place Order']")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("orderModal")));
            });

            testCase(10, "Fill in checkout details", () -> {
                driver.findElement(By.id("name")).sendKeys("John Doe");
                driver.findElement(By.id("country")).sendKeys("USA");
                driver.findElement(By.id("city")).sendKeys("New York");
                driver.findElement(By.id("card")).sendKeys("4111111111111111");
                driver.findElement(By.id("month")).sendKeys("12");
                driver.findElement(By.id("year")).sendKeys("2027");
            });

            testCase(11, "Submit purchase and verify confirmation message", () -> {
                driver.findElement(By.id("orderModalBtn")).click();
                WebElement heading = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sweet-alert h2")));
                if (!heading.getText().contains("Thank you for your purchase"))
                    throw new AssertionError("unexpected confirmation: " + heading.getText());
            });

            testCase(12, "Close the purchase confirmation", () -> {
                driver.findElement(By.className("confirm")).click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".sweet-alert")));
            });

        } finally {
            driver.quit();
        }
    }
}