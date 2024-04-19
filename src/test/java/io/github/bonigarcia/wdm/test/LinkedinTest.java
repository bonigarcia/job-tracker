/*
 * (C) Copyright 2023 Boni Garcia (https://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.wdm.test;

import static java.lang.invoke.MethodHandles.lookup;
import static org.assertj.core.api.Assertions.assertThat;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;

import com.opencsv.CSVWriter;

class LinkedinTest {

    static final Logger log = getLogger(lookup().lookupClass());
    static final String DATASET = "docs/dataset.csv";
    static final String DATE_PATTERN = "dd MMM, yyyy";
    static final String LINKEDIN_BASE_URL = "https://www.linkedin.com/";
    static final int WAIT_SEC = 30;

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void testLinkedinJobs() throws Exception {
        login();

        List<String> results = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);
        String now = simpleDateFormat.format(new Date());
        results.add(now);

        List<String> frameworks = Arrays.asList("Puppeteer", "Playwright",
                "Cypress", "Selenium");

        // Read number of jobs for each framework from Linkedin
        for (String framework : frameworks) {
            int numJobs = searchJobs(framework);
            log.info("{} ---> {}", framework, numJobs);
            results.add(String.valueOf(numJobs));
        }

        // Write results to CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATASET, true))) {
            writer.writeNext(results.toArray(new String[0]), false);
        }
    }

    void login() {
        driver.get(LINKEDIN_BASE_URL);

        String login = System.getenv("LI_LOGIN");
        assertThat(login).isNotNull();
        getElement(By.id("session_key")).sendKeys(login);

        String password = System.getenv("LI_PASSWORD");
        assertThat(password).isNotNull();
        getElement(By.id("session_password")).sendKeys(password);

        getElement(By.cssSelector("button[type='submit']")).click();

        // Get search text box to wait until login is completed
        getElement(By.className("search-global-typeahead__input"));
    }

    WebElement getElement(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    int searchJobs(String keyword) {
        String linkedinUrl = String.format(
                "%sjobs/search/?keywords=%s&location=Worldwide&locationId=&geoId=92000000&f_TPR=r86400&position=1&pageNum=0",
                LINKEDIN_BASE_URL, keyword);
        driver.get(linkedinUrl);
        log.trace("URL: {}", linkedinUrl);

        String[] elementNames = { "jobs-search-results-list__subtitle",
                "results-context-header__new-jobs",
                "results-context-header__job-count" };
        String jobsText = null;
        for (String element : elementNames) {
            try {
                WebElement newJobs = getElement(By.className(element));
                jobsText = newJobs.getText();
                break;
            } catch (Exception e) {
                log.trace("Error locating {}", element);
            }
        }
        int numbersOnly = 0;
        if (jobsText != null) {
            numbersOnly = Integer.parseInt(jobsText.replaceAll("[^0-9]", ""));
        }
        log.trace("{} jobs: {} -> ", keyword, jobsText, numbersOnly);

        return numbersOnly;
    }

}
