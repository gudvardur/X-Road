/*
 * The MIT License
 * Copyright (c) 2019- Nordic Institute for Interoperability Solutions (NIIS)
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

module.exports = {
  tags: ['ss', 'logout'],
  'Security server logout': (browser) => {
    const frontPage = browser.page.ssFrontPage();
    const mainPage = browser.page.ssMainPage();

    // Navigate to app and check that the browser has loaded the page
    frontPage.navigate();
    browser.waitForElementVisible('//*[@id="app"]');

    // Enter valid credentials
    frontPage
      .clearUsername()
      .clearPassword()
      .enterUsername(browser.globals.login_usr)
      .enterPassword(browser.globals.login_pwd)
      .signin();

    // Verify successful login
    browser.waitForElementVisible('//div[contains(@class, "server-name")]');

    // Logout and verify
    mainPage.logout();
    browser.waitForElementVisible('//*[@id="username"]');

    browser.end();
  },
  'Security server timeout logout': (browser) => {
    const frontPage = browser.page.ssFrontPage();
    const mainPage = browser.page.ssMainPage();

    frontPage.navigate();
    browser.waitForElementVisible('//*[@id="app"]');

    // Enter valid credentials
    frontPage
      .clearUsername()
      .clearPassword()
      .enterUsername(browser.globals.login_usr)
      .enterPassword(browser.globals.login_pwd)
      .signin();

    // Verify successful login
    browser.waitForElementVisible('//div[contains(@class, "server-name")]');

    // Wait for the timeout message to appear
    browser.waitForElementVisible(
      mainPage.elements.sessionExpiredPopupOkButton,
      browser.globals.logout_timeout_ms + 60000,
      1000,
    );
    mainPage.closeSessionExpiredPopup();

    browser.waitForElementVisible('//*[@id="username"]');
    browser.end();
  },
};
