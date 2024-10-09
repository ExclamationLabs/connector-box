/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;


public class BoxConfiguration extends AbstractConfiguration {

    private String configFilePath;
    private GuardedString configJson;
    private String httpProxyHost;
    private int httpProxyPort;
    private String httpProxyUser;
    private GuardedString httpProxyPassword;
    private Boolean groupAdminDefaultPermissionCanCreateAccounts;
    private Boolean groupAdminDefaultPermissionCanEditAccounts;
    private Boolean groupAdminDefaultPermissionCanCInstantLogin;
    private Boolean groupAdminDefaultPermissionCanRunReports;
    private int maxRetryAttempts = 5;
    private int connectionTimeoutInMilliseconds = 10000;
    private int readTimeoutInMilliseconds = 10000;

    @ConfigurationProperty(
            order = 1,
            displayMessageKey = "JWT Config File Path",
            helpMessageKey = "File path for the JWT Config File",
            required = false,
            confidential = false)
    public String getConfigFilePath() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @ConfigurationProperty(
            order = 2,
            displayMessageKey = "JWT Config JSON",
            helpMessageKey = "JSON String of the JWT Config File",
            required = false,
            confidential = true)
    public GuardedString getConfigJson() {
        return configJson;
    }

    public void setConfigJson(GuardedString configJson) {
        this.configJson = configJson;
    }

    @ConfigurationProperty(
            order = 3,
            displayMessageKey = "HTTP Proxy Host",
            helpMessageKey = "Hostname for the HTTP Proxy",
            required = false,
            confidential = false)
    public String getHttpProxyHost() {
        return httpProxyHost;
    }

    public void setHttpProxyHost(String httpProxyHost) {
        this.httpProxyHost = httpProxyHost;
    }

    @ConfigurationProperty(
            order = 4,
            displayMessageKey = "HTTP Proxy Port",
            helpMessageKey = "Port for the HTTP Proxy",
            required = false,
            confidential = false)
    public int getHttpProxyPort() {
        return httpProxyPort;
    }

    public void setHttpProxyPort(int httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
    }

    @ConfigurationProperty(
            order = 5,
            displayMessageKey = "HTTP Proxy User",
            helpMessageKey = "Username for the HTTP Proxy Authentication",
            required = false,
            confidential = false)
    public String getHttpProxyUser() {
        return httpProxyUser;
    }

    public void setHttpProxyUser(String httpProxyUser) {
        this.httpProxyUser = httpProxyUser;
    }

    @ConfigurationProperty(
            order = 6,
            displayMessageKey = "HTTP Proxy Password",
            helpMessageKey = "Password for the HTTP Proxy Authentication",
            required = false,
            confidential = true)
    public GuardedString getHttpProxyPassword() {
        return httpProxyPassword;
    }

    public void setHttpProxyPassword(GuardedString httpProxyPassword) {
        this.httpProxyPassword = httpProxyPassword;
    }

    @ConfigurationProperty(
            order = 7,
            displayMessageKey = "Group Admin Default Permission - can_create_accounts",
            helpMessageKey = "If true, when assigned to a group as group admin, the user has \"can_create_accounts\" permission by default",
            required = false,
            confidential = false)
    public Boolean getGroupAdminDefaultPermissionCanCreateAccounts() {
        return groupAdminDefaultPermissionCanCreateAccounts;
    }

    public void setGroupAdminDefaultPermissionCanCreateAccounts(Boolean groupAdminDefaultPermissionCanCreateAccounts) {
        this.groupAdminDefaultPermissionCanCreateAccounts = groupAdminDefaultPermissionCanCreateAccounts;
    }

    @ConfigurationProperty(
            order = 8,
            displayMessageKey = "Group Admin Default Permission - can_edit_accounts",
            helpMessageKey = "If true, when assigned to a group as group admin, the user has \"can_edit_accounts\" permission by default",
            required = false,
            confidential = false)
    public Boolean getGroupAdminDefaultPermissionCanEditAccounts() {
        return groupAdminDefaultPermissionCanEditAccounts;
    }

    public void setGroupAdminDefaultPermissionCanEditAccounts(Boolean groupAdminDefaultPermissionCanEditAccounts) {
        this.groupAdminDefaultPermissionCanEditAccounts = groupAdminDefaultPermissionCanEditAccounts;
    }

    @ConfigurationProperty(
            order = 9,
            displayMessageKey = "Group Admin Default Permission - can_instant_login",
            helpMessageKey = "If true, when assigned to a group as group admin, the user has \"can_instant_login\" permission by default",
            required = false,
            confidential = false)
    public Boolean getGroupAdminDefaultPermissionCanCInstantLogin() {
        return groupAdminDefaultPermissionCanCInstantLogin;
    }

    public void setGroupAdminDefaultPermissionCanCInstantLogin(Boolean groupAdminDefaultPermissionCanCInstantLogin) {
        this.groupAdminDefaultPermissionCanCInstantLogin = groupAdminDefaultPermissionCanCInstantLogin;
    }

    @ConfigurationProperty(
            order = 10,
            displayMessageKey = "Group Admin Default Permission - can_run_reports",
            helpMessageKey = "If true, when assigned to a group as group admin, the user has \"can_run_reports\" permission by default",
            required = false,
            confidential = false)
    public Boolean getGroupAdminDefaultPermissionCanRunReports() {
        return groupAdminDefaultPermissionCanRunReports;
    }

    public void setGroupAdminDefaultPermissionCanRunReports(Boolean groupAdminDefaultPermissionCanRunReports) {
        this.groupAdminDefaultPermissionCanRunReports = groupAdminDefaultPermissionCanRunReports;
    }

    @ConfigurationProperty(
            order = 11,
            displayMessageKey = "Maximum retries",
            helpMessageKey = "Configure how many times API will retry calls (Default: 5)",
            required = false,
            confidential = false)
    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    @ConfigurationProperty(
            order = 12,
            displayMessageKey = "Connection Timeout (in milliseconds)",
            helpMessageKey = "Set up how log (in milliseconds) API waits to establish connection (Default: 10000)",
            required = false,
            confidential = false)
    public int getConnectionTimeoutInMilliseconds() {
        return connectionTimeoutInMilliseconds;
    }

    public void setConnectionTimeoutInMilliseconds(int connectionTimeoutInMilliseconds) {
        this.connectionTimeoutInMilliseconds = connectionTimeoutInMilliseconds;
    }

    @ConfigurationProperty(
            order = 13,
            displayMessageKey = "Read Timeout (in milliseconds)",
            helpMessageKey = "Set up how log (in milliseconds) API waits to read data from connection (Default: 10000)",
            required = false,
            confidential = false)
    public int getReadTimeoutInMilliseconds() {
        return readTimeoutInMilliseconds;
    }

    public void setReadTimeoutInMilliseconds(int readTimeoutInMilliseconds) {
        this.readTimeoutInMilliseconds = readTimeoutInMilliseconds;
    }

    @Override
    public void validate() {
        if (StringUtil.isBlank(configFilePath) && configJson == null) {
            throw new ConfigurationException("configFilePath or configJson must not be empty");
        }
    }

    @Override
    public String toString() {
        return "BoxConfiguration{" +
                "configFilePath='" + configFilePath + '\'' +
                '}';
    }
}
