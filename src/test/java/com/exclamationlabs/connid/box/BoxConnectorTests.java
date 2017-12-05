package com.exclamationlabs.connid.box;

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
        assertNotNull(boxAPIConnection);
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





    @Test
    public void schema() {
        Schema schema = newFacade().schema();
        Assert.assertNotNull(schema);
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
