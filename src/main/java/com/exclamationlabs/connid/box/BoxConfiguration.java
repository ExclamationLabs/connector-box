package com.exclamationlabs.connid.box;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;


public class BoxConfiguration extends AbstractConfiguration {

    private String configFilePath;

    @ConfigurationProperty(
            order = 1,
            displayMessageKey = "JWT Config File Path",
            helpMessageKey = "File path for the JWT Config File",
            required = true,
            confidential = false)
    public String getConfigFilePath() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @Override
    public void validate() {
        if (StringUtil.isBlank(configFilePath)) {
            throw new ConfigurationException("Client Id must not be empty");
        }
    }


    @Override
    public String toString() {
        return "BoxConfiguration{" +
                "configFilePath='" + configFilePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoxConfiguration)) return false;

        BoxConfiguration that = (BoxConfiguration) o;

        return getConfigFilePath().equals(that.getConfigFilePath());
    }

    @Override
    public int hashCode() {
        return getConfigFilePath().hashCode();
    }
}
