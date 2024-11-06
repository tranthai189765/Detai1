package Datamning;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Main {
	public static void main(String[] args) {
		// Khởi tạo WebDriver
		WebDriver driver = new ChromeDriver();
		TwitterKOLFinder finder = new TwitterKOLFinder(driver);

		// Đăng nhập vào Twitter
		finder.loginToTwitter();

		// Tìm kiếm hashtag Blockchain
		finder.searchHashtag("#Blockchain");

		// Thu thập thông tin KOL
		finder.collectKOLInfo();

		// Đóng trình duyệt
		finder.closeBrowser();
	}
}
