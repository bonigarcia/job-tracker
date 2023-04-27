[![Build Status](https://github.com/bonigarcia/job-tracker/workflows/scheduled-test/badge.svg)](https://github.com/bonigarcia/job-tracker/actions)
[![badge-jdk](https://img.shields.io/badge/jdk-11-green.svg)](https://www.oracle.com/java/technologies/downloads/)
[![License badge](https://img.shields.io/badge/license-Apache2-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Twitter Follow](https://img.shields.io/twitter/follow/boni_gg.svg?style=social)](https://twitter.com/boni_gg)

# Linkedin Job Tracker ![](https://bonigarcia.dev/linkedin-job-tracker/img/linkedin-job-tracker.png)

This project contains a scheduled automated test for daily tracking jobs about Selenium, Cypress, Puppeteer, and Playwright on Linkedin.

## Motivation

This project aims to measure the popularity of tools typically required for automated testing jobs: [Selenium](https://www.selenium.dev/), [Cypress](https://www.cypress.io/), [Puppeteer](https://pptr.dev/), and [Playwright](https://playwright.dev/). 

To that aim, it measures the number of times new job positions contain these terms on [Linkedin](https://linkedin.com/). Since the Linkedin API does not support a programmatic way to discover jobs, this project implements a scheduled automated test (based on Selenium) to request this information.

## Components

The main elements contained in this project are the following:

- [Automated test](https://github.com/bonigarcia/linkedin-job-tracker/blob/main/src/test/java/io/github/bonigarcia/wdm/test/LinkedinTest.java). This test uses **Selenium** to drive a browser that requests the number of jobs mentioning [Selenium](https://www.linkedin.com/jobs/search/?keywords=Selenium&location=Worldwide&locationId=&geoId=92000000&f_TPR=r86400&position=1&pageNum=0), [Cypress](https://www.linkedin.com/jobs/search/?keywords=Cypress&location=Worldwide&locationId=&geoId=92000000&f_TPR=r86400&position=1&pageNum=0), [Puppeteer](https://www.linkedin.com/jobs/search/?keywords=Puppeteer&location=Worldwide&locationId=&geoId=92000000&f_TPR=r86400&position=1&pageNum=0
), and [Playwright](https://www.linkedin.com/jobs/search/?keywords=Playwright&location=Worldwide&locationId=&geoId=92000000&f_TPR=r86400&position=1&pageNum=0) during the past **24 hours worldwide**.
- [Dataset](https://github.com/bonigarcia/linkedin-job-tracker/blob/main/docs/dataset.csv). The number of jobs gathered by this automated test is written into a CSV (comma-separated value) file.
- [Scheduled job](https://github.com/bonigarcia/linkedin-job-tracker/blob/main/.github/workflows/scheduled-test.yml). The test before is executed daily on **GitHub Actions** using a cron task. This job automatically commits the changes in the CSV dataset.
- [Web page for results](https://github.com/bonigarcia/linkedin-job-tracker/blob/main/docs/index.html). The results are displayed using [Chart.js](https://www.chartjs.org/), reading the data from the CSV dataset. The resulting web page is published automatically with **GitHub Pages**.

## Results

Check out the resulting [web page](https://bonigarcia.dev/linkedin-job-tracker/). The automated job started to work on April 27, 2023. Since then,   each day, the automated test gathers new data daily, and the resulting chart is automatically updated.

## About

linkedin-job-tracker (Copyright &copy; 2023) is a project created and maintained by [Boni García](https://bonigarcia.dev/) licensed under [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0).