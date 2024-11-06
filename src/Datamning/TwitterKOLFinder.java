package Datamning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TwitterKOLFinder {

	private WebDriver driver;

	public TwitterKOLFinder(WebDriver driver) {
		new WebDriverWait(driver, Duration.ofSeconds(10));
		this.driver = driver;
	}

	public void loginToTwitter() {
		// Sử dụng class TwitterLogin để đăng nhập
		TwitterLogin login = new TwitterLogin();
		login.login(driver);
	}

	public void searchHashtag(String hashtag) {
		// Truy cập vào trang Explore
		driver.get("https://x.com/explore");

		try {
			// Đợi cho đến khi trang Explore tải hoàn toàn và ô tìm kiếm có sẵn
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

			// Sử dụng CSS Selector để tìm phần tử ô tìm kiếm
			WebElement searchBox = wait.until(
					ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[aria-label='Search query']")));

			// Nhập hashtag vào ô tìm kiếm
			searchBox.sendKeys(hashtag);
			searchBox.submit();

			System.out.println("Tìm kiếm với hashtag " + hashtag + " thành công.");

			WebElement peopleTab = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
					"//*[@id='react-root']/div/div/div[2]/main/div/div/div/div[1]/div/div[1]/div[1]/div[2]/nav/div/div[2]/div/div[3]/a/div/div/span")));

			// Nhấp vào tab "People"
			peopleTab.click();
			System.out.println("Đã chuyển sang tab People.");

			// Chờ cho đến khi kết quả tìm kiếm trong tab "People" được tải
			wait.until(ExpectedConditions.urlContains("f=user"));

		} catch (Exception e) {
			System.out.println("Không tìm thấy ô tìm kiếm hoặc xảy ra lỗi trong quá trình tìm kiếm.");
			e.printStackTrace();
		}
	}

	public void collectKOLInfo() {
		try {
			// Tạo file để ghi thông tin vào
			File file = new File("user_links.txt");
			if (!file.exists()) {
				file.createNewFile(); // Tạo mới file nếu chưa có
			}

			// Tạo BufferedWriter để ghi vào file
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			// Đợi trang tải kết quả
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

			// Chờ đến khi phần tử người dùng xuất hiện
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-testid='cellInnerDiv']")));

			// Lấy danh sách người dùng từ kết quả tìm kiếm
			List<WebElement> users = driver.findElements(By.cssSelector("div[data-testid='cellInnerDiv']"));

			// Kiểm tra số lượng người dùng tìm được
			System.out.println("Số lượng người dùng tìm thấy: " + users.size());

			// Đếm số người dùng đã xử lý
			int count = 0;

			// Lọc và in liên kết của người dùng, chỉ lấy 10 người đầu tiên
			for (WebElement user : users) {
				if (count >= 10) {
					break; // Dừng lại sau khi đã lấy 10 người đầu tiên
				}
				try {
					// In ra thông tin về người dùng hiện tại
					System.out.println("Đang xử lý một người dùng...");

					// Lấy liên kết của người dùng
					WebElement userLink = user.findElement(By.cssSelector("a[href*='/']"));
					String userProfileUrl = userLink.getAttribute("href");

					// Kiểm tra liên kết của người dùng
					System.out.println("Liên kết của người dùng: " + userProfileUrl);

					// Ghi liên kết vào file
					writer.write(userProfileUrl);
					writer.newLine(); // Thêm một dòng mới

					// Tăng biến đếm
					count++;

				} catch (Exception e) {
					// Nếu có lỗi khi lấy thông tin từng người, tiếp tục với người khác
					System.out.println("Không thể lấy liên kết của người dùng này.");
					e.printStackTrace(); // In ra thông tin lỗi chi tiết
				}
			}

			// Đóng BufferedWriter sau khi ghi xong
			writer.close();

		} catch (IOException e) {
			System.out.println("Lỗi khi ghi vào file.");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeBrowser() {
		driver.quit();
	}
}
