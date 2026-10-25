import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class UserLoginTest {

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
        System.out.println("USER LOGIN TEST");
        System.out.println("==================================");

        // DemoWebShop has no fixed public account, so this test registers
        // a fresh account first, logs out, then logs back in explicitly.
        String email = "jinx" + System.currentTimeMillis() + "@gmail.com";
        String password = "Password123";

        System.out.println("Registering a temporary account...");
        driver.get("https://demowebshop.tricentis.com/register");
        pause(3);

        driver.findElement(By.id("gender-male")).click();
        driver.findElement(By.id("FirstName")).sendKeys("Hemanth");
        driver.findElement(By.id("LastName")).sendKeys("k");
        driver.findElement(By.id("Email")).sendKeys(email);
        driver.findElement(By.id("Password")).sendKeys(password);
        driver.findElement(By.id("ConfirmPassword")).sendKeys(password);
        driver.findElement(By.id("register-button")).click();
        pause(4);

        System.out.println("Logging out to test explicit login...");
        driver.findElement(By.linkText("Log out")).click();
        pause(3);

        System.out.println("Opening Login Page...");
        driver.get("https://demowebshop.tricentis.com/login");
        pause(3);

        System.out.println("Entering Email...");
        driver.findElement(By.id("Email")).sendKeys(email);
        pause(2);

        System.out.println("Entering Password...");
        driver.findElement(By.id("Password")).sendKeys(password);
        pause(2);

        System.out.println("Clicking Login...");
        driver.findElement(By.cssSelector("input.login-button")).click();
        pause(4);

        // Verify Login - header shows the account email once logged in
        List<WebElement> accountLinks = driver.findElements(By.cssSelector("a.account"));

        if (!accountLinks.isEmpty() && accountLinks.get(0).getText().contains(email)) {
            System.out.println("Login Successful");
        } else {
            System.out.println("Login Failed");
        }

        pause(2);

        // Screenshot
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        Files.copy(
                src.toPath(),
                new File("LoginSuccess.png").toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Screenshot Saved Successfully");

        pause(5);

        driver.quit();

        System.out.println("Browser Closed");

        System.exit(0);
    }
}
