import java.io.File;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class InventoryControlTest {

    static int failures = 0;

    static void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Fails loud instead of silently continuing on a bad assertion. */
    static void verify(boolean condition, String testCaseLabel, String description) {
        if (condition) {
            System.out.println("  PASS - " + description);
        } else {
            failures++;
            System.out.println("  FAIL - " + testCaseLabel + ": " + description);
        }
    }

    static String cellText(WebDriver driver, int rowFromBottom, int colIndex) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#inventory-body tr"));
        WebElement row = rows.get(rows.size() - rowFromBottom);
        List<WebElement> cells = row.findElements(By.tagName("td"));
        return cells.get(colIndex).getText();
    }

    static int rowCount(WebDriver driver) {
        return driver.findElements(By.cssSelector("#inventory-body tr")).size();
    }

    public static void main(String[] args) {

        WebDriver driver = null;

        try {
            driver = new ChromeDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            System.out.println("Working directory: " + System.getProperty("user.dir"));

            File htmlFile = new File(
                    "exp2/exp2/src/main/java/inventory.html"
            ).getAbsoluteFile();

            // The doubled "exp2/exp2" segment below is almost certainly a
            // leftover from your project layout, not intentional. Fix the
            // path to match wherever inventory.html actually lives on disk.
            System.out.println("Opening HTML file: " + htmlFile.getAbsolutePath());

            if (!htmlFile.exists()) {
                System.out.println("ERROR: inventory.html NOT FOUND at " + htmlFile.getAbsolutePath());
                System.out.println("Fix: run from the directory containing 'exp2', or hardcode an absolute path.");
                return;
            }

            driver.get(htmlFile.toURI().toString());
            driver.manage().window().maximize();
            pause(1);

            System.out.println("\n==========================================");
            System.out.println("   INVENTORY CONTROL SYSTEM - TEST RUN");
            System.out.println("==========================================");

            int initialRowCount = rowCount(driver);

            // =====================================================
            // TC1 - ADD NEW ITEM (valid input)
            // =====================================================
            System.out.println("\nTC1: ADD NEW ITEM");

            driver.findElement(By.id("item-name")).clear();
            driver.findElement(By.id("item-name")).sendKeys("Wireless Mouse");
            driver.findElement(By.id("item-quantity")).clear();
            driver.findElement(By.id("item-quantity")).sendKeys("10");
            driver.findElement(By.id("item-price")).clear();
            driver.findElement(By.id("item-price")).sendKeys("750");
            driver.findElement(By.id("add-item-button")).click();

            wait.until(ExpectedConditions.textToBe(By.id("message"), "New item added successfully."));

            verify(rowCount(driver) == initialRowCount + 1, "TC1", "row count increased by 1");
            verify(cellText(driver, 1, 1).equals("Wireless Mouse"), "TC1", "new row has correct name");
            verify(cellText(driver, 1, 2).equals("10"), "TC1", "new row has correct quantity");

            // =====================================================
            // TC2 - UPDATE ITEM (valid input)
            // =====================================================
            System.out.println("\nTC2: UPDATE ITEM");

            WebElement lastRow = driver.findElement(By.cssSelector("#inventory-body tr:last-child"));
            lastRow.click();

            driver.findElement(By.id("item-name")).clear();
            driver.findElement(By.id("item-name")).sendKeys("Mechanical Keyboard (Updated)");
            driver.findElement(By.id("item-quantity")).clear();
            driver.findElement(By.id("item-quantity")).sendKeys("25");
            driver.findElement(By.id("item-price")).clear();
            driver.findElement(By.id("item-price")).sendKeys("2000");
            driver.findElement(By.id("update-item-button")).click();

            wait.until(ExpectedConditions.textToBe(By.id("message"), "Item updated successfully."));

            verify(cellText(driver, 1, 1).equals("Mechanical Keyboard (Updated)"), "TC2", "name updated correctly");
            verify(cellText(driver, 1, 2).equals("25"), "TC2", "quantity updated correctly");
            verify(cellText(driver, 1, 3).equals("2000"), "TC2", "price updated correctly");

            // =====================================================
            // TC3 - UPDATE WITH INVALID DATA (regression for Defect #2:
            // updateItem() previously had no validation at all)
            // =====================================================
            System.out.println("\nTC3: UPDATE WITH INVALID DATA (should be rejected)");

            String nameBefore = cellText(driver, 1, 1);

            lastRow = driver.findElement(By.cssSelector("#inventory-body tr:last-child"));
            lastRow.click();

            driver.findElement(By.id("item-name")).clear();
            driver.findElement(By.id("item-name")).sendKeys("");
            driver.findElement(By.id("item-quantity")).clear();
            driver.findElement(By.id("item-quantity")).sendKeys("-5");
            driver.findElement(By.id("update-item-button")).click();

            wait.until(ExpectedConditions.textToBe(By.id("message"), "Please enter valid item details."));

            verify(cellText(driver, 1, 1).equals(nameBefore), "TC3", "row unchanged after invalid update attempt");

            // =====================================================
            // TC4 - DELETE ITEM
            // =====================================================
            System.out.println("\nTC4: DELETE ITEM");

            int rowsBeforeDelete = rowCount(driver);

            lastRow = driver.findElement(By.cssSelector("#inventory-body tr:last-child"));
            lastRow.click();
            driver.findElement(By.id("delete-item-button")).click();

            wait.until(ExpectedConditions.textToBe(By.id("message"), "Item deleted successfully."));

            verify(rowCount(driver) == rowsBeforeDelete - 1, "TC4", "row count decreased by 1");

            // =====================================================
            // TC5 - INVALID QUANTITY (non-numeric — regression for
            // Defect #3: Number("abc") is NaN and "NaN <= 0" is false,
            // which previously let garbage input slip through)
            // =====================================================
            System.out.println("\nTC5: INVALID / NON-NUMERIC QUANTITY");

            int rowsBeforeTC5 = rowCount(driver);

            driver.findElement(By.id("item-name")).clear();
            driver.findElement(By.id("item-name")).sendKeys("Invalid Item");
            driver.findElement(By.id("item-quantity")).clear();
            driver.findElement(By.id("item-quantity")).sendKeys("-5");
            driver.findElement(By.id("item-price")).clear();
            driver.findElement(By.id("item-price")).sendKeys("100");
            driver.findElement(By.id("add-item-button")).click();

            wait.until(ExpectedConditions.textToBe(By.id("message"), "Please enter valid item details."));

            verify(rowCount(driver) == rowsBeforeTC5, "TC5", "no row added for invalid quantity");

            // =====================================================
            // TC6 - VALID ITEM
            // =====================================================
            System.out.println("\nTC6: ADD VALID ITEM");

            int rowsBeforeTC6 = rowCount(driver);

            driver.findElement(By.id("item-name")).clear();
            driver.findElement(By.id("item-name")).sendKeys("Mouse");
            driver.findElement(By.id("item-quantity")).clear();
            driver.findElement(By.id("item-quantity")).sendKeys("30");
            driver.findElement(By.id("item-price")).clear();
            driver.findElement(By.id("item-price")).sendKeys("500");
            driver.findElement(By.id("add-item-button")).click();

            wait.until(ExpectedConditions.textToBe(By.id("message"), "New item added successfully."));

            verify(rowCount(driver) == rowsBeforeTC6 + 1, "TC6", "row count increased by 1");
            String newId = cellText(driver, 1, 0);

            // =====================================================
            // TC7 - ID COLLISION REGRESSION (Defect #1)
            // Delete a MIDDLE item, then add a new one. Before the fix,
            // id = items.length + 1 would reuse an existing ID whenever
            // a non-last row was deleted.
            // =====================================================
            System.out.println("\nTC7: ID COLLISION CHECK AFTER DELETING A MIDDLE ROW");

            List<WebElement> rows = driver.findElements(By.cssSelector("#inventory-body tr"));
            WebElement middleRow = rows.get(rows.size() / 2);
            String deletedId = middleRow.findElements(By.tagName("td")).get(0).getText();
            middleRow.click();
            driver.findElement(By.id("delete-item-button")).click();
            wait.until(ExpectedConditions.textToBe(By.id("message"), "Item deleted successfully."));

            driver.findElement(By.id("item-name")).clear();
            driver.findElement(By.id("item-name")).sendKeys("Post-Delete Item");
            driver.findElement(By.id("item-quantity")).clear();
            driver.findElement(By.id("item-quantity")).sendKeys("5");
            driver.findElement(By.id("item-price")).clear();
            driver.findElement(By.id("item-price")).sendKeys("100");
            driver.findElement(By.id("add-item-button")).click();
            wait.until(ExpectedConditions.textToBe(By.id("message"), "New item added successfully."));

            List<WebElement> allRows = driver.findElements(By.cssSelector("#inventory-body tr"));
            long distinctIds = allRows.stream()
                    .map(r -> r.findElements(By.tagName("td")).get(0).getText())
                    .distinct()
                    .count();

            verify(distinctIds == allRows.size(), "TC7",
                    "no duplicate IDs after deleting row " + deletedId + " and adding a new item");

            // =====================================================
            // SUMMARY
            // =====================================================
            System.out.println("\n==========================================");
            if (failures == 0) {
                System.out.println("       ALL TEST CASES PASSED");
            } else {
                System.out.println("       " + failures + " ASSERTION(S) FAILED");
            }
            System.out.println("==========================================");

            pause(2);

        } catch (Exception e) {
            System.out.println("\n==========================================");
            System.out.println("             TEST RUN THREW AN EXCEPTION");
            System.out.println("==========================================");
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}