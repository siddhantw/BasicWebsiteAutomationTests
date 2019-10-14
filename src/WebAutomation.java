//Importing packages required for the project

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.IOException;


public class WebAutomation {

    public static WebDriver driver;
    public static String brand = "yahoo";          //Example: Running our tests to scan Yahoo homepage
    public static String brand_diff_case = "Yahoo";
    public static String subdomain = "";
    public static String tld = ".com";
    public static String baseUrl = "https://www."+ brand + subdomain + tld + "";      //Customizable query for setting the base URL
    public static String copyright;

    @org.testng.annotations.BeforeTest
    public static void config() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();        //Pre-browser test configurations
        LoggingPreferences loggingprefs = new LoggingPreferences();
        loggingprefs.enable(LogType.BROWSER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingprefs);
        capabilities.setCapability (CapabilityType.ACCEPT_SSL_CERTS, true);     //handles SSL certificate issue (if any)
        driver = new ChromeDriver(capabilities);        //Note: Add chrome-driver executable file in /usr/local/bin
        driver.manage().window().maximize();            //maximizing window size to max resolution possible as per machine
    }

    @org.testng.annotations.Test(priority=1)                //Priority defines the importance of the test case and it's level of execution
    public static void verifyPageLoadTime() {
        long start = System.currentTimeMillis();        //Records the browser's start time
        driver.get(baseUrl);
        long finish = System.currentTimeMillis();       //Records the browser's time after loading
        long totalTime = finish - start;                //Returns the page load time for the webpage
        System.out.println("\n****** Calculating the page load time for " + baseUrl + " ******");
        System.out.println("\nTotal page load time: " + totalTime + " seconds");
    }

    @org.testng.annotations.Test(priority=4)
    public static void verifyBrokenLinks() {        //Verify broken/invalid links from webpage
        String url = "";
        HttpURLConnection huc = null;
        int respCode = 0;
        int broken=0;		
        int numoflinks=0;

        List<WebElement> links = driver.findElements(By.tagName("a"));
        Iterator<WebElement> it = links.iterator();
        System.out.println("\n****** Finding all valid and broken links ******\n");

        while (it.hasNext()) {
            numoflinks++;	
            url = it.next().getAttribute("href");
            if (url == null || url.isEmpty()) {
                System.out.println(url + " URL is either not configured for anchor tag or it is empty.");
                continue;
            }
            if (url.startsWith("tel")) {
                System.out.println(url + " This is linked to a telephone number.");
                continue;
            }
            if (!url.startsWith(baseUrl) && (!url.contains(brand)) && (!url.contains(brand_diff_case))) {
                System.out.println(url + " URL belongs to another domain, skipping it.");
                continue;
            }
            try {
                huc = (HttpURLConnection) (new URL(url).openConnection());
                huc.setRequestMethod("HEAD");
                huc.connect();
                respCode = huc.getResponseCode();
                if (respCode >= 400) {
                    System.out.println(url + " is returning an error page with the following response status: " + respCode);
                    broken++;	
                } else {
                    System.out.println(url + " is a valid link");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nNumber of broken links: " + broken);		
        System.out.println("Total number of links: " + numoflinks);
    }

    @org.testng.annotations.Test(priority=6)
    public static void verifyJSLogs() {           //Extracting all JavaScript error logs from the webpage
        System.out.println("\n****** JavaScript Error Logs: ******");
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries) {
            System.out.println("\n"+new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
        }
    }

    @org.testng.annotations.Test(priority=5)
    public static void verifyBrokenImages() {      //Extracting all broken images from the webpage
        List<WebElement> allImages = driver.findElements(By.tagName("img"));
        System.out.println("\n****** Verifying broken images on webpage ******\n");
        for (int iElement = 0; iElement < allImages.size(); iElement++)
        {
            try{
                String url = allImages.get(iElement).getAttribute("src");
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                if (String.valueOf(con.getResponseCode()).equals("200")){
                    System.out.println("Image @ url " + url + " exists at server");
                }
                else
                    System.out.println("Image @ url " + url + " does not exists at server");
            }
            catch(Exception e) {
            }
        }
    }

    @org.testng.annotations.Test(priority=3)
    public static void verifyPageTitle() {      //Verify page title for the respective brand
        System.out.println("\n****** Verifying page title of webpage ******");
        String actualTitle = driver.getTitle();
        String expectedTitle = "";
        if(brand.equals("")||(brand_diff_case=="")) {
            expectedTitle = "";
        }
        else if(brand.equals("")||(brand_diff_case=="")) {
            expectedTitle = "";
        }
        System.out.println("\nActual Title: " + actualTitle);
        System.out.println("Expected Title: " + expectedTitle);
        if(actualTitle.equals(expectedTitle))
        {
            System.out.println("Title is correct for the brand: " + brand);
        }
        else
            System.out.println("Incorrect title for brand: " + brand + ".\nThe correct title should be " + expectedTitle);
    }

    @org.testng.annotations.Test(priority=2)
    public static void verifyCopyrightYear() {      //Verify copyright year with present year from webpage
        System.out.println("\n****** Verifying Copyright year from webpage ******");
        if (brand == "" || brand_diff_case == "") {
            copyright = driver.findElement(By.className("")).getText();
        }
        else if (brand == "" || brand_diff_case == "") {
            copyright = driver.findElement(By.className("")).getText();
        }
        else {
            copyright = "0";
        }

        int year = Calendar.getInstance().get(Calendar.YEAR);

        if(copyright.contains(Integer.toString(year)))
        {
            System.out.println("\nCorrect! Copyright year is updated to latest: " + year);
        }
        else if(copyright.equals("0")){
            System.out.println("\nWe do not validate the copyright year for the URL: " + baseUrl);
        }
        else
            System.out.println("\nError! Copyright year needs to be updated to: " + year);
    }

    @org.testng.annotations.Test(priority=7)		
        public static void takeScreenshot() {      //Take screenshot of page		
            File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);		
            System.out.println("****** Screenshot Process: ******");		
            try {		
                FileUtils.copyFile(src, new File("screenshot"+"_"+brand.toString()));		
                System.out.println("Screenshot saved successfully!");		
            } catch (IOException e) {		
                // TODO Auto-generated catch block		
                e.printStackTrace();		
                System.out.println("Taking Screenshot failed!");		
            }
        }

    @org.testng.annotations.AfterTest
    public static void garbageCollection() {        //Cleanup of memory utilised
        driver.quit();                              //Closes the browser instance
    }
}
