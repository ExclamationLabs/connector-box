package com.exclamationlabs.connid;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxConfig;
import com.box.sdk.BoxUser;
import com.box.sdk.CreateUserParams;
import com.google.common.collect.Lists;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.spi.SearchResultsHandler;
import org.identityconnectors.test.common.TestHelpers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoxConnectorTests {

    protected BoxConfiguration newConfiguration() {
        return new BoxConfiguration();
    }

    private static final Log LOG = Log.getLog(BoxConnectorTests.class);

    private static ArrayList<ConnectorObject> results = new ArrayList<>();

    private static BoxAPIConnection boxAPIConnection = null;
    private static BoxConfig boxConfig = null;


    @Before
    public void setup() {

        try(Reader reader = new FileReader("test-config.json")) {
            boxConfig = BoxConfig.readFrom(reader);
        } catch (IOException ex) {
            LOG.error("Error loading test credentials", ex);
        }

        assertNotNull("Error loading test credentials; boxConfig was null", boxConfig);


        boxAPIConnection = new BoxAPIConnection(boxConfig);
    }


    protected ConnectorFacade newFacade() {
        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();
        APIConfiguration impl = TestHelpers.createTestConfiguration(BoxConnector.class, newConfiguration());
        impl.getResultsHandlerConfiguration().setFilteredResultsHandlerInValidationMode(true);


        // Even though we already have a connection from setup(), we are creating one through the connector as another test
        LOG.info("Setting client id {0}", boxConfig.getClientId());
        impl.getConfigurationProperties().setPropertyValue("configFilePath", "test-config.json" );

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


    private Set<Attribute> getFixtureAccountAttributes() {

        Schema schema = newFacade().schema();
        Set<Attribute> accountAttributes = new HashSet<Attribute>();
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

        return accountAttributes;
    }

    private BoxUser.Info getTestUser() {
        assertNotNull(boxAPIConnection);

        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxAPIConnection);

        for (BoxUser.Info user : users) {
            if (user.getLogin().equals("test_user@testmail.com")) {
                return user;
            }

        }

        return null;

    }

    private BoxUser.Info createTestUser() {
        assertNotNull(boxAPIConnection);

        CreateUserParams params = new CreateUserParams();
        params.setExternalAppUserId("test_user@testmail.com");
        BoxUser.Info createdUserInfo = BoxUser.createAppUser(boxAPIConnection, "test_user", params);
        return createdUserInfo;
    }

    private void deleteTestUser() {
        assertNotNull(boxAPIConnection);

        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxAPIConnection);

        for (BoxUser.Info user : users) {
            if (user.getLogin().equals("test_user@testmail.com")) {
                user.getResource().delete(false, false);
            }

        }
    }


    private void waitAFew() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException ex) {
            //ignore
        }
    }

    @Test
    public void schema() {
        Schema schema = newFacade().schema();
        Assert.assertNotNull(schema);
    }

    @Test
    public void create() {
        assertNotNull(newFacade());

        BoxUser.Info userInfo = getTestUser();
        if (userInfo != null) {
            deleteTestUser();
        }

        Uid accountUid = newFacade().create(
                new ObjectClass("__ACCOUNT__"),
                getFixtureAccountAttributes(),
                null
        );

        assertNotNull(accountUid);

        waitAFew();

        deleteTestUser();
    }

    @Test
    public void delete() {
        BoxUser.Info userInfo = getTestUser();
        if (userInfo == null) {
            userInfo = createTestUser();
        }

        waitAFew();

        newFacade().delete(
                new ObjectClass("__ACCOUNT__"),
                new Uid(userInfo.getID()),
                null
        );

        assertNull(getTestUser());
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
