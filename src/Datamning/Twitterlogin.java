package Datamning;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Twitterlogin {
	public static void main(String[] args) {

		System.setProperty("webdriver.chrome.driver",
				"D:\\clusterTestFile\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");

		WebDriver driver = new ChromeDriver();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		try {
			driver.get("https://twitter.com/login");
			WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("text")));
			emailField.sendKeys("Tranthaiabcabc"); // Nhập tên đăng nhập
			WebElement nextButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Next']")));
			nextButton.click();
			WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
			passwordField.sendKeys("det@i1OOP2024"); // Nhập mật khẩu
			WebElement loginButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Log in']")));
			loginButton.click();
			wait.until(ExpectedConditions.titleContains("Home"));
			String pageTitle = driver.getTitle();
			if (pageTitle.contains("Home") || pageTitle.contains("Twitter")) {
				System.out.println("Đăng nhập thành công!");
			} else {
				System.out.println("Đăng nhập không thành công.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			driver.quit();
		}
	}
}
