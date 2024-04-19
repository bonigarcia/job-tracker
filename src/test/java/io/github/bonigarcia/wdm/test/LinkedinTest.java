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
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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
    static final int WAIT_SEC = 10;

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

            // Wait a guard time to avoid Linkedin to force login
            Thread.sleep(Duration.ofSeconds(20).toMillis());
        }

        // Write results to CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(DATASET, true))) {
            writer.writeNext(results.toArray(new String[0]), false);
        }
    }

    WebElement getElement(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    int searchJobs(String keyword) throws InterruptedException {
        String linkedinUrl = String.format(
                "%sjobs/search/?currentJobId=3902340547&f_TPR=r86400&geoId=92000000&keywords=%s%%20testing&location=Worldwide&origin=JOB_SEARCH_PAGE_SEARCH_BUTTON&refresh=true",
                LINKEDIN_BASE_URL, keyword);
        driver.get(linkedinUrl);
        log.debug("URL: {}", linkedinUrl);
        testScreenshotBase64();

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
                log.debug("Error locating {}: {}", element, e.getMessage());
            }
        }
        int numbersOnly = 0;
        if (jobsText != null) {
            numbersOnly = Integer.parseInt(jobsText.replaceAll("[^0-9]", ""));
        }
        log.trace("{} jobs: {} -> ", keyword, jobsText, numbersOnly);

        return numbersOnly;
    }

    void testScreenshotBase64() {
        TakesScreenshot ts = (TakesScreenshot) driver;
        String screenshot = ts.getScreenshotAs(OutputType.BASE64);
        log.debug("data:image/png;base64,{}", screenshot);
    }

}
