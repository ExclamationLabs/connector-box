package com.exclamationlabs.connid;

import static org.junit.Assert.assertNotNull;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.box.sdk.BoxConfig;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.SearchResultsHandler;
import org.identityconnectors.test.common.TestHelpers;
import org.junit.Assert;
import org.junit.Test;

public class BoxConnectorTests {

    protected BoxConfiguration newConfiguration() {
        return new BoxConfiguration();
    }

    private static final Log LOG = Log.getLog(BoxConnectorTests.class);

    private static ArrayList<ConnectorObject> results = new ArrayList<>();

    protected ConnectorFacade newFacade() {
        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();
        APIConfiguration impl = TestHelpers.createTestConfiguration(BoxConnector.class, newConfiguration());
        impl.getResultsHandlerConfiguration().setFilteredResultsHandlerInValidationMode(true);
        BoxConfig boxConfig = null;

        try(Reader reader = new FileReader("test-config.json")) {
            boxConfig = BoxConfig.readFrom(reader);
        } catch (IOException ex) {
            LOG.error("Error loading test credentials", ex);
        }

        if (boxConfig == null) {
            LOG.error("Error loading test credentials; boxConfig was null");
            return null;
        }

        LOG.info("Setting client id {0}", boxConfig.getClientId());
        impl.getConfigurationProperties().setPropertyValue("clientId", boxConfig.getClientId() );

        impl.getConfigurationProperties().setPropertyValue("clientSecret",boxConfig.getClientSecret() );
        impl.getConfigurationProperties().setPropertyValue("enterpriseId",boxConfig.getEnterpriseId() );
        impl.getConfigurationProperties().setPropertyValue("publicKeyID", boxConfig.getJWTEncryptionPreferences().getPublicKeyID() );
        impl.getConfigurationProperties().setPropertyValue("privateKey", boxConfig.getJWTEncryptionPreferences().getPrivateKey() );
        impl.getConfigurationProperties().setPropertyValue("privateKeyPassword",boxConfig.getJWTEncryptionPreferences().getPrivateKeyPassword());
        return factory.newInstance(impl);
    }

    public static SearchResultsHandler handler = new SearchResultsHandler() {

        @Override
        public boolean handle(ConnectorObject connectorObject) {
            results.add(connectorObject);
            return true;
        }

        @Override
        public void handleResult(SearchResult result) {
            LOG.info("Im handling {0}", result.getRemainingPagedResults());
        }
    };

    @Test
    public void schema() {
        Schema schema = newFacade().schema();
        Assert.assertNotNull(schema);
    }

    @Test
    public void create() {
        assertNotNull(newFacade());

        Set<Attribute> accountAttributes = new HashSet<Attribute>();

        Schema schema = newFacade().schema();

        Set<AttributeInfo> accountAttributesInfo = schema.findObjectClassInfo(ObjectClass.ACCOUNT_NAME)
                .getAttributeInfo();

        for (AttributeInfo attributeInfo : accountAttributesInfo) {
            if (!attributeInfo.isMultiValued() && attributeInfo.isCreateable() && attributeInfo.isReadable()) {
                if (attributeInfo.getName().equals("login")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "test_user@testmail.com"));
                } else if (attributeInfo.getName().equals("timezone")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "Europe/Bratislava"));
                } else if (attributeInfo.getName().equals("language")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "en"));
                } else if (attributeInfo.getName().equals("role")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "coadmin"));
                } else if (attributeInfo.getType().equals(String.class)) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "test_user"));
                } else if (attributeInfo.getType().equals(Boolean.class)) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "true"));
                } else if (attributeInfo.getType().equals(Integer.class)) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), 0));
                }
                accountAttributes.add(AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, false));

            }

        }

        ObjectClass accountObject = new ObjectClass("__ACCOUNT__");
        Uid accountUid = newFacade().create(accountObject, accountAttributes, null);

        assertNotNull(accountUid);
    }


    @Test
    public void test() {
        newFacade().test();
        Assert.assertTrue(true);
    }

    @Test
    public void validate() {
        newFacade().validate();
        Assert.assertTrue(true);
    }





}
