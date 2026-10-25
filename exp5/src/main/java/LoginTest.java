import java.io.File;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;


public class LoginTest {

    static void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static void screenshot(WebDriver driver, String fileName) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), new File(fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Screenshot saved: " + fileName);
        } catch (Exception e) {
            System.out.println("Could not save screenshot: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.SEVERE);
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless");
        // options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Step 1: Navigate to the login page
            driver.get("https://practicetestautomation.com/practice-test-login/");
            pause(2);

            // Step 2: Log in
            WebElement usernameField = driver.findElement(By.id("username"));
            WebElement passwordField = driver.findElement(By.id("password"));
            WebElement loginButton = driver.findElement(By.id("submit"));

            usernameField.sendKeys("student");
            pause(1);
            passwordField.sendKeys("Password123");
            pause(1);
            loginButton.click();

            // Wait for redirect to the success page.
            // If login had failed, the URL stays on /practice-test-login/ and
            // this throws - reaching the next line already proves login succeeded.
            wait.until(ExpectedConditions.urlContains("logged-in-successfully"));
            pause(2);

            // Step 3: Verify the page confirms a successful login
            WebElement heading = driver.findElement(By.tagName("h1"));
            wait.until(ExpectedConditions.textToBePresentInElement(heading, "Logged In Successfully"));

            boolean loginSucceeded = heading.getText().contains("Logged In Successfully");

            if (loginSucceeded) {
                System.out.println("Test Passed: Login Successfully - " + heading.getText().trim());
            } else {
                System.out.println("Test Failed: unexpected heading - " + heading.getText().trim());
            }

            screenshot(driver, "LoggedInSuccessfully.png");
            pause(2);

            // Step 4: Log back out, to leave the site in its starting state
            WebElement logoutLink = driver.findElement(By.linkText("Log out"));
            logoutLink.click();

            wait.until(ExpectedConditions.urlContains("practice-test-login"));
            System.out.println("Logout step passed: redirected back to the login page");

        } catch (Exception e) {
            System.out.println("An error occurred during the test: " + e.getMessage());
            screenshot(driver, "AppiumLoginTest_failure.png");
            e.printStackTrace();
        } finally {
            // Step 5: Close the browser
            driver.quit();
        }
    }
}