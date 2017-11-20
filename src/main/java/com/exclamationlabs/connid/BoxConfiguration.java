package com.exclamationlabs.connid;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;


public class BoxConfiguration extends AbstractConfiguration {

    private String clientId;
    private String clientSecret;
    private String enterpriseId;
    private String publicKeyID;
    private String privateKey;
    private String privateKeyPassword;

    @ConfigurationProperty(
            order = 1,
            displayMessageKey = "ClientId",
            helpMessageKey = "Client identifier issued to the client during the registration process",
            required = true,
            confidential = false)
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @ConfigurationProperty(order = 2,
            displayMessageKey = "ClientSecret",
            helpMessageKey = "Client secret issued to the client during the registration process",
            required = true,
            confidential = false)
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @ConfigurationProperty(order = 2,
            displayMessageKey = "EnterpriseId",
            helpMessageKey = "Client Enterprise Id to the client during the registration process",
            required = false,
            confidential = false)
    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    @ConfigurationProperty(order = 2,
            displayMessageKey = "PublicKeyID",
            helpMessageKey = "Client PublicKey ID to the client during the registration process",
            required = false,
            confidential = false)
    public String getPublicKeyID() {
        return publicKeyID;
    }

    public void setPublicKeyID(String publicKeyID) {
        this.publicKeyID = publicKeyID;
    }

    @ConfigurationProperty(order = 2,
            displayMessageKey = "PrivateKey",
            helpMessageKey = "Client Private Key to the client during the registration process",
            required = false,
            confidential = false)
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @ConfigurationProperty(order = 2,
            displayMessageKey = "Private Key Password",
            helpMessageKey = "Client Private Key Password to the client during the registration process",
            required = false,
            confidential = false)
    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }


    @Override
    public void validate() {
        if (StringUtil.isBlank(clientId)) {
            throw new ConfigurationException("Client Id must not be empty");
        }

        if (StringUtil.isBlank(clientSecret)) {
            throw new ConfigurationException("Client Secret must not be empty");
        }

        if (StringUtil.isBlank(enterpriseId)) {
            throw new ConfigurationException("Enterprise Id must not be empty");
        }

        if (StringUtil.isBlank(publicKeyID)) {
            throw new ConfigurationException("Public Key ID must not be empty");
        }

        if (StringUtil.isBlank(privateKey.toString())) {
            throw new ConfigurationException("Private Key must not be empty");
        }

        if (StringUtil.isBlank(privateKeyPassword.toString())) {
            throw new ConfigurationException("Private Key Password must not be empty");
        }
    }


}
