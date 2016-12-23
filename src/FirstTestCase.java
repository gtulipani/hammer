import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by gaston.tulipani on 22/12/2016.
 */
public class FirstTestCase {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        //Abro TestLink
        String testLink = "http://10.23.144.134/testlink/index.php";
        driver.get(testLink);

        //Detecto campos de Login y los vacío
        WebElement userField = driver.findElement(By.id("login"));
        WebElement passwordField = driver.findElement(By.name("tl_password"));
        userField.clear();
        passwordField.clear();

        //Pido al usuario datos de Usuario y Contraseña
        System.out.println("Por favor ingrese los siguientes valores");
        System.out.print("User: ");
        String user = (new Scanner(System.in)).nextLine();
        System.out.print("Password: ");
        String password = (new Scanner(System.in)).nextLine();

        //Ingreso datos de usuario y contraseña y logueo
        userField.sendKeys(user);
        passwordField.sendKeys(password);
        driver.findElement(By.name("login_submit")).click();

        //Detecto y elijo Test Project
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("titlebar")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("testproject")));
        Select testProjectSelector = new Select(driver.findElement(By.name("testproject")));
        testProjectSelector.selectByVisibleText("DTVLA Mainline");

        //Detecto y elijo Test Plan
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("mainframe")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("testplan")));
        Select testPlanSelector = new Select(driver.findElement(By.name("testplan")));
        testPlanSelector.selectByVisibleText("Lilo LHR22 QT v1b1826_0x7722");

        //Elijo la opción Execute Tests
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='test_execution_topics']/p/a")));
        driver.findElement(By.xpath("//*[@id='test_execution_topics']/p/a")).click();

        //Selecciono el filtro correspondiente a glo_ealgarbe y Not Run y apreto Apply
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("treeframe")));
        wait.until(ExpectedConditions.presenceOfElementLocated((By.xpath("//*[@id='filters']/div[1]/div"))));
        driver.findElement(By.xpath("//*[@id='filters']/div[1]/div")).click();
        Select assignedToSelector = new Select(driver.findElement(By.name("filter_assigned_to")));
        assignedToSelector.selectByVisibleText("[Any]");
        Select resultSelector = new Select(driver.findElement(By.name("filter_status")));
        resultSelector.selectByVisibleText("[Any]");
        driver.findElement(By.name("submitOptions")).click();

        //Selecciono un caso de prueba y cambio el estado a In Progress

        //Identifico todos los '+' correspondientes a las Test Suites y los clickeo
        List<WebElement> testSuiteLinks = driver.findElements(By.xpath("//*[@class=\"treemenudiv\"]/a[1]"));
        Iterator<WebElement> it = testSuiteLinks.iterator();
        while (it.hasNext()) {
            WebElement link = it.next();
            if (!link.getAttribute("title").equals("testcase")) {
                link.click();
            }
        }


        /*driver.findElement(By.xpath("//*[@id=\"jt1\"]/a[1]")).click();
        driver.findElement(By.xpath("//*[@id=\"jt2\"]/a[1]")).click();
        driver.findElement(By.xpath("//*[@id=\"jt3\"]/a[1]")).click();
        driver.findElement(By.xpath("//*[@id=\"jt4\"]/a[1]")).click();
        driver.findElement(By.xpath("//*[@id=\"jt5\"]/a[1]")).click();
        driver.findElement(By.xpath("//*[@id=\"jt6\"]/a[1]")).click();*/
        wait.until(ExpectedConditions.presenceOfElementLocated((By.xpath("//*[@id=\"jt7\"]/a[2]"))));
        driver.findElement(By.xpath("//*[@id=\"jt7\"]/a[2]")).click();
        driver.switchTo().defaultContent();
        driver.switchTo().frame("mainframe");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("workframe")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='PastResultsRow']/td/a")));
        driver.findElement(By.xpath("//*[@id='PastResultsRow']/td/a")).click();
    }
}