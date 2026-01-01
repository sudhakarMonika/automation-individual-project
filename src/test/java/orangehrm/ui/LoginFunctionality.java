package orangehrm.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginFunctionality {

    public WebDriver driver;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(StringFiles.waitTime));
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
    }

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
                {"ADMIN", "admin123", false},
                {"Admin", "Admin123", false}
        };
    }

    @Test(dataProvider = "loginData")
    public void loginTestCases(String username, String password, boolean expectedResult) {
        WebElement userField = driver.findElement(By.xpath(StringFiles.username));
        userField.clear();
        userField.sendKeys(username);

        WebElement passField = driver.findElement(By.xpath(StringFiles.pass));
        passField.clear();
        passField.sendKeys(password);

        WebElement loginButton = driver.findElement(By.xpath(StringFiles.loginbutton));
        loginButton.click();

        try {
            if (expectedResult) {
                // Successful login
                Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), "Expected successful login");
            } else {
                // Failed login
                WebElement errorMessage = driver.findElement(By.xpath("//p[@class='oxd-text oxd-text--p oxd-alert-content-text']"));
                Assert.assertTrue(errorMessage.isDisplayed(), "Expected error message on failed login");
            }
        } catch (Exception e) {
            if (expectedResult) {
                Assert.fail("Login failed unexpectedly for user: " + username);
            }
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

