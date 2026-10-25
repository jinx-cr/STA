import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class UserRegistrationTest {

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
        System.out.println("USER REGISTRATION TEST");
        System.out.println("==================================");

        System.out.println("Opening Tricentis DemoWebShop...");
        driver.get("https://demowebshop.tricentis.com/register");
        pause(3);

        String email = "jinx" + System.currentTimeMillis() + "@gmail.com";

        System.out.println("Selecting Gender...");
        driver.findElement(By.id("gender-male")).click();
        pause(1);

        System.out.println("Entering First Name...");
        driver.findElement(By.id("FirstName")).sendKeys("Rohan");
        pause(1);

        System.out.println("Entering Last Name...");
        driver.findElement(By.id("LastName")).sendKeys("CR");
        pause(1);

        System.out.println("Entering Email...");
        driver.findElement(By.id("Email")).sendKeys(email);
        pause(1);

        System.out.println("Entering Password...");
        driver.findElement(By.id("Password")).sendKeys("Password123");
        driver.findElement(By.id("ConfirmPassword")).sendKeys("Password123");
        pause(1);

        System.out.println("Submitting Registration...");
        driver.findElement(By.id("register-button")).click();
        pause(4);

        // Verify registration success message
        String resultText = driver.findElement(By.cssSelector("div.result")).getText();

        // Screenshot
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        Files.copy(
                src.toPath(),
                new File("RegistrationSuccess.png").toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        System.out.println("==================================");
        System.out.println("Result Message : " + resultText);
        System.out.println("Email Used : " + email);
        System.out.println("Screenshot Saved");
        System.out.println("==================================");

        pause(5);

        driver.quit();

        System.exit(0);
    }
}
