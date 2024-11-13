package datacollection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverSetup {
	private static final String CHROME_DRIVER_PATH = "D:\\clusterTestFile\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";

	public static void setupChromeDriver() {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
	}

	public static WebDriver getChromeDriver() {
		setupChromeDriver();
		return new ChromeDriver();
	}

}
