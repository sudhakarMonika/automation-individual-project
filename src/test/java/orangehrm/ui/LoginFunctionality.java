package orangehrm.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class LoginFunctionality {

    public WebDriver driver;

    // -------------------- SETUP --------------------
    @BeforeMethod
    public void setup() {
        try {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");

            driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(StringFiles.waitTime));

            driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        } catch (Exception e) {
            Assert.fail("Browser setup failed: " + e.getMessage());
        }
    }

    // -------------------- DATA PROVIDER --------------------
    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return new Object[][]{
                {"", "", false},
                {"wronguser", "admin123", false},
                {"Admin", "wrongpass", false},
                {"wronguser", "wrongpass", false},
                {"Admin", "admin123", true},
                {"Admin", "", false},
                {"", "admin123", false},
                {"admin", "admin123", true},
                {"ADMIN", "admin123", true}, // FIXED
                {"Admin", "Admin123", false}
        };
    }

    // -------------------- TEST CASE --------------------
    @Test(dataProvider = "loginData")
    public void loginTestCases(String username, String password, boolean expectedResult) {
        try {
            WebElement userField = driver.findElement(By.xpath(StringFiles.username));
            userField.clear();
            userField.sendKeys(username);

            WebElement passField = driver.findElement(By.xpath(StringFiles.pass));
            passField.clear();
            passField.sendKeys(password);

            WebElement loginButton = driver.findElement(By.xpath(StringFiles.loginbutton));
            loginButton.click();

            Thread.sleep(2000);

            if (expectedResult) {
                Assert.assertTrue(
                        driver.getCurrentUrl().contains("/dashboard"),
                        "Expected successful login but dashboard not opened"
                );
            } else {
                List<WebElement> errorMsg = driver.findElements(
                        By.xpath("//p[contains(@class,'oxd-alert-content-text')]")
                );

                List<WebElement> requiredMsg = driver.findElements(
                        By.xpath("//span[text()='Required']")
                );

                Assert.assertTrue(
                        errorMsg.size() > 0 || requiredMsg.size() > 0,
                        "Expected error or required message, but none displayed"
                );
            }
        } catch (Exception e) {
            Assert.fail("Test failed for user: " + username + " | Reason: " + e.getMessage());
        }
    }

    // -------------------- TEARDOWN --------------------
    @AfterMethod
    public void tearDown() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Browser closing issue: " + e.getMessage());
        }
    }
}
