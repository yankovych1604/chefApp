package com.maksy.chefapp.ui;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.time.Duration;

public abstract class AbstractUiTest {

    protected static WebDriver driver;
    protected static WebDriverWait wait;
    protected static Process springAppProcess;

    @BeforeAll
    static void startSpringApp() throws IOException, InterruptedException {
        // Копіювання бази
        Path source = Paths.get("data/chefdb.mv.db");
        Path target = Paths.get("data/chefdb-test.mv.db");
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Не вдалося скопіювати базу даних: " + e.getMessage(), e);
        }

        // Запуск Spring Boot
        ProcessBuilder builder = new ProcessBuilder(
                "java",
                "-Dspring.profiles.active=test",
                "-Dserver.port=8081",
                "-jar",
                "target/chefApp-0.0.1-SNAPSHOT.jar"
        );
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        springAppProcess = builder.start();

        // Очікування готовності сервера
        int attempts = 0;
        int maxAttempts = 30;
        boolean started = false;
        while (attempts < maxAttempts) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8081").openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(1000);
                connection.connect();
                int code = connection.getResponseCode();
                if (code < 500) {
                    started = true;
                    break;
                }
            } catch (IOException ignored) {
            }

            Thread.sleep(1000);
            attempts++;
        }

        if (!started) {
            throw new IllegalStateException("Сервер не запустився за " + maxAttempts + " секунд");
        }

        // Запуск браузера
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        if (driver != null) {
            driver.quit();
        }
        if (springAppProcess != null && springAppProcess.isAlive()) {
            springAppProcess.destroy();
            springAppProcess.waitFor();
        }

        // Видалення тимчасової бази
        try {
            Files.deleteIfExists(Paths.get("data/chefdb-test.mv.db"));
            Files.deleteIfExists(Paths.get("data/chefdb-test.trace.db"));
            Files.deleteIfExists(Paths.get("data/chefdb-test.lock.db"));
        } catch (IOException e) {
            System.err.println("Не вдалося видалити тестову базу: " + e.getMessage());
        }
    }
}