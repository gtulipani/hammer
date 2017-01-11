import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
    final static int CORRIDAS_ANTERIORES_INICIO = 2;
    final static int CORRIDAS_ANTERIORES_CANTIDAD_A_VERIFICAR = 4;
    final static int CORRIDAS_ANTERIORES_FIN = CORRIDAS_ANTERIORES_INICIO + CORRIDAS_ANTERIORES_CANTIDAD_A_VERIFICAR;

    final static String GLO_EALGARBE = "glo_ealgarbe";
    final static String ANY = "[Any]";
    final static String DEFAULT_TESTPLAN = "Lilo LHR22 QT v1b1827_0x7723";

    final static String NOT_RUN = "Not Run";
    final static String IN_PROGRESS = "In Progress";

    public static void main(String[] args) {
        WebDriver driver = initializer();

        WebDriverWait wait = new WebDriverWait(driver, 30);

        login(driver);

        String testProject = "DTVLA Mainline";
        selectTestProject(driver, wait, testProject);

        selectTestPlan(driver, wait);

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

    private static void selectTestPlan(WebDriver driver, WebDriverWait wait) {
        //Detecto y elijo Test Plan
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("mainframe")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("testplan")));
        Select testPlanSelector = new Select(driver.findElement(By.name("testplan")));

        //Pregunto testPlan a correr
        System.out.print("Ingrese el TestPlan a correr. Si no ingresar ninguno o ingresa uno inválido, se cargará uno por defecto: ");
        String testPlan = new Scanner((System.in)).nextLine();

        if (testPlan.equals(""))
            testPlan = DEFAULT_TESTPLAN;

        try {
            testPlanSelector.selectByVisibleText(testPlan);
        } catch (NoSuchElementException e) {
            testPlanSelector.selectByVisibleText(DEFAULT_TESTPLAN);
        }
    }

    private static void executeTests(WebDriver driver, WebDriverWait wait) {
        //Elijo la opción Execute Tests
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='test_execution_topics']/p/a")));
        driver.findElement(By.xpath("//*[@id='test_execution_topics']/p/a")).click();

        //Selecciono el filtro correspondiente a glo_ealgarbe y Not Run y apreto Apply
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("treeframe")));
        wait.until(ExpectedConditions.presenceOfElementLocated((By.xpath("//*[@id='filters']/div[1]/div"))));
        driver.findElement(By.xpath("//*[@id='filters']/div[1]/div")).click();
        Select assignedToSelector = new Select(driver.findElement(By.name("filter_assigned_to")));

        //Pregunto si correr por glo_ealgarbe o asignados a nadie
        System.out.print("¿Desea buscar casos asignados a glo_ealgarbe (1) o [Any] (2)?: ");
        String selector;
        switch (Character.getNumericValue(((new Scanner(System.in)).next().charAt(0))))
        {
            case 1:
                selector = GLO_EALGARBE;
                break;
            default:
                selector = ANY;
                break;
        }
        assignedToSelector.selectByVisibleText(selector);

        Select resultSelector = new Select(driver.findElement(By.name("filter_status")));

        //Pregunto si buscar In Progress o Not Run
        System.out.print("¿Desea buscar casos Not Run (1) o In Progress (2)?: ");
        String statusSelector;
        switch (Character.getNumericValue(((new Scanner(System.in)).next().charAt(0))))
        {
            case 1:
                statusSelector = NOT_RUN;
                break;
            default:
                statusSelector = IN_PROGRESS;
                break;
        }
        resultSelector.selectByVisibleText(statusSelector);
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

        //Ejecuto los casos de prueba
        executeTCs(driver, wait, testCasesList);

    }

    //Método que se encarga de recorrer la lista de TCs y ejecutar cada uno
    private static void executeTCs(WebDriver driver, WebDriverWait wait, List<WebElement> testCasesList) {
        Iterator<WebElement> it2 = testCasesList.iterator();
        while (it2.hasNext()) {
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("mainframe")));
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("treeframe")));
            WebElement tc = it2.next();
            executeTC(driver, wait, tc);
        }
    }

    //Método que se encarga de ejecutar un TC
    private static void executeTC(WebDriver driver, WebDriverWait wait, WebElement tc) {
        tc.click();
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("mainframe")));
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.name("workframe")));
        if (driver.findElement(By.xpath("//*[@name=\"AutoReresh\"]")).isSelected()) {
            driver.findElement(By.xpath("//*[@name=\"AutoReresh\"]")).click();
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='PastResultsRow']/td/a")));
        driver.findElement(By.xpath("//*[@id='PastResultsRow']/td/a")).click();
        boolean isGloberHammerTime = analizePreviousExecutions(driver, wait);
        if(isGloberHammerTime){
            consultarEjecucion(driver);
        }
    }

    private static void consultarEjecucion(WebDriver driver) {
        char answer = '\0';
        while (!respuestaValida(answer)) {
            System.out.print("Se ha detectado un caso válido para hammerear. ¿Desea martillarlo? [Y/N]?: ");
            answer = (new Scanner(System.in)).next().charAt(0);
            if (!respuestaValida(answer)) {
                System.out.println("La respuesta ha sido incorrecta. Recuerde ingresar 'Y' o 'N'.");
            }
            else {
                if (Character.toUpperCase(answer) == 'Y') {
                    driver.findElement(By.xpath("//*[@value=\"p\"]")).click();
                    driver.findElement(By.xpath("//*[@value=\"Save Execution\"]")).click();
                }
            }
        }
    }

    private static boolean respuestaValida(char answer) {
        return (((Character.toUpperCase(answer) == 'Y') || (Character.toUpperCase(answer) == 'N')));
    }

    //Método que se encarga de validar las corridas anteriores
    private static boolean analizePreviousExecutions(WebDriver driver, WebDriverWait wait) {
        //Parseo los ejecutores para ver si cumplen con el requisito "glo" y "Passed"
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"PastResultsDiv\"]/div[1]/table/tbody/tr[2]/td[3]")));
        int contador = CORRIDAS_ANTERIORES_INICIO;
        boolean result = true;
        while ((contador <= CORRIDAS_ANTERIORES_FIN) && (result))
        {
            String textUser = driver.findElement(By.xpath("//*[@id=\"PastResultsDiv\"]/div[1]/table/tbody/tr["+ contador +"]/td[3]")).getText();
            String testPastResult = driver.findElement(By.xpath("//*[@id=\"PastResultsDiv\"]/div[1]/table/tbody/tr["+ contador +"]/td[4]")).getText();
            if (!(textUser.substring(0,3).equals("glo") && testPastResult.equals("Passed")))
                result = false;
            contador++;
        }
        return result;
    }
}
