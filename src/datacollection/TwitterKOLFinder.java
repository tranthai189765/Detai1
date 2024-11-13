package datacollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

	public void collectKOLInfo(int maxUsers) {

		File file = new File("user_links.txt");

		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					System.out.println("Không thể tạo file mới.");
					return;
				}
			}

			// Kiểm tra đường dẫn tuyệt đối của file
			System.out.println("Đường dẫn tuyệt đối của file: " + file.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Lỗi khi tạo file.");
			e.printStackTrace();
			return;
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			int count = 0;
			Set<String> recordedLinks = new HashSet<>(); // Dùng Set để tránh trùng lặp liên kết

			while (count < maxUsers) {
				// Đợi cho các phần tử người dùng xuất hiện trên trang
				List<WebElement> users = wait.until(ExpectedConditions
						.presenceOfAllElementsLocatedBy(By.cssSelector("button[data-testid='UserCell']")));

				// Nếu không có người dùng, kiểm tra lại
				if (users.isEmpty()) {
					System.out.println("Không có người dùng nào trên trang này.");
					break;
				}

				// Ghi dữ liệu của từng người dùng
				for (WebElement user : users) {
					if (count >= maxUsers) {
						break;
					}

					try {
						// Tìm liên kết đến trang cá nhân của người dùng
						List<WebElement> links = user.findElements(By.cssSelector("a[href*='/']"));

						if (!links.isEmpty()) {
							String userProfileUrl = links.get(0).getAttribute("href");

							// Kiểm tra xem liên kết đã được ghi chưa, tránh trùng lặp
							if (!recordedLinks.contains(userProfileUrl)) {
								writer.write(userProfileUrl);
								writer.newLine();
								recordedLinks.add(userProfileUrl);

								System.out.println("Đã ghi liên kết của người dùng: " + userProfileUrl);
								count++;
							}
						} else {
							// Nếu không có liên kết, in thông tin về người dùng
							WebElement nameElement = user.findElement(By.cssSelector("span[dir='ltr']"));
							if (nameElement != null) {
								String userName = nameElement.getText();
								System.out.println("Không tìm thấy liên kết cho người dùng: " + userName);
							}
						}
					} catch (Exception e) {
						System.out.println("Không thể lấy thông tin của người dùng này.");
						e.printStackTrace();
					}
				}

				// Cuộn trang xuống sử dụng JavaScript để tải thêm người dùng
				((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 200)");

				// Đợi một khoảng thời gian để trang kịp tải thêm dữ liệu
				Thread.sleep(2000); // Tăng thời gian đợi để trang kịp tải thêm
			}

			// Đảm bảo ghi và flush dữ liệu vào file
			writer.flush();
			System.out.println("Đã thu thập và ghi thông tin người dùng thành công.");
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
