import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class FirstTestCase {
    public static void main(String[] args) {
        WebDriver driver = initializer();

        WebDriverWait wait = new WebDriverWait(driver, 30);

        login(driver);

        String testProject = "DTVLA Mainline";
        selectTestProject(driver, wait, testProject);

        String testPlan = "Lilo LHR22 QT v1b1826_0x7722";
        selectTestPlan(driver, wait, testPlan);

        executeTests(driver, wait);
    }

    private static WebDriver initializer() {
        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        String testLink = "http://10.23.144.134/testlink/index.php";
        driver.get(testLink);
        return driver;
    }

    private static void login(WebDriver driver) {
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
    }

    private static void selectTestProject(WebDriver driver, WebDriverWait wait, String testProject) {
        //Detecto y elijo Test Project
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("titlebar")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("testproject")));
        Select testProjectSelector = new Select(driver.findElement(By.name("testproject")));
        testProjectSelector.selectByVisibleText(testProject);
    }

    private static void selectTestPlan(WebDriver driver, WebDriverWait wait, String testPlan) {
        //Detecto y elijo Test Plan
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("mainframe")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("testplan")));
        Select testPlanSelector = new Select(driver.findElement(By.name("testplan")));
        testPlanSelector.selectByVisibleText(testPlan);
    }

    private static void executeTests(WebDriver driver, WebDriverWait wait) {

        boolean isGloberHammerTime = true;
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
        resultSelector.selectByVisibleText("In Progress");
        driver.findElement(By.name("submitOptions")).click();

        //Identifico todos los '+' correspondientes a las Test Suites y los clickeo
        List<WebElement> testSuiteLinks = driver.findElements(By.xpath("//*[@class=\"treemenudiv\"]/a[1]"));
        Iterator<WebElement> it = testSuiteLinks.iterator();
        while (it.hasNext()) {
            WebElement link = it.next();
            if (!link.getAttribute("title").equals("testcase")) {
                link.click();
            }
        }

        //Identifico todos los TCs que cumplen con los filtros
        List<WebElement> testCasesList = driver.findElements(By.xpath("//*[@title=\"testcase\"][@class=\"phplm\"]"));
        Iterator<WebElement> it2 = testCasesList.iterator();

        //Analizo corridas anteriores para un TC en particular
        WebElement tcPrueba = it2.next();

        tcPrueba.click();
        driver.switchTo().defaultContent();
        driver.switchTo().frame("mainframe");
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("workframe")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='PastResultsRow']/td/a")));
        driver.findElement(By.xpath("//*[@id='PastResultsRow']/td/a")).click();

        //Parseo los ejecutores para ver si cumplen con el requisito "glo"
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"PastResultsDiv\"]/div[1]/table/tbody/tr[2]/td[3]")));
        for(int i=2 ; i<=7 ; i++)
        {
            String textUser = driver.findElement(By.xpath("//*[@id=\"PastResultsDiv\"]/div[1]/table/tbody/tr["+ i +"]/td[3]")).getText();
            String testPastResult = driver.findElement(By.xpath("//*[@id=\"PastResultsDiv\"]/div[1]/table/tbody/tr["+ i +"]/td[4]")).getText();
            if(textUser.substring(0,3).equals("glo") && testPastResult.equals("Passed"))
                isGloberHammerTime = true;
            else{
                isGloberHammerTime = false;
                break;
            }
        }

        driver.findElement(By.xpath("//*[@name=\"AutoReresh\"]")).click();

        if(isGloberHammerTime == true){
            driver.findElement(By.xpath("//*[@value=\"p\"]")).click();
            driver.findElement(By.xpath("//*[@value=\"Save Execution\"]")).click();
        }

        System.out.println(isGloberHammerTime);
    }
}
