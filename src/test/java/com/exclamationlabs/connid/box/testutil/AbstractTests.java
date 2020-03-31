package com.exclamationlabs.connid.box.testutil;

import com.exclamationlabs.connid.box.BoxConfiguration;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.test.common.TestHelpers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public abstract class AbstractTests {
    protected ConnectorFacade connector;
    protected MockBoxAPIHelper mockAPI;

    @BeforeEach
    protected void setup(TestInfo testInfo) {
        connector = newFacade(testInfo.getDisplayName());
        mockAPI = MockBoxAPIHelper.instance();
        mockAPI.init();
    }

    @AfterEach
    protected void close() {
        mockAPI.close();
        connector.dispose();
    }

    protected ConnectorFacade newFacade(String instanceName) {
        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();
        APIConfiguration impl = TestHelpers.createTestConfiguration(LocalBoxConnector.class, newConfig());
        impl.getResultsHandlerConfiguration().setEnableAttributesToGetSearchResultsHandler(false);
        impl.getResultsHandlerConfiguration().setEnableNormalizingResultsHandler(false);
        impl.getResultsHandlerConfiguration().setEnableFilteredResultsHandler(false);
        impl.setInstanceName(instanceName);
        return factory.newInstance(impl);
    }

    protected BoxConfiguration newConfig() {
        BoxConfiguration boxConfiguration = new BoxConfiguration();
        return boxConfiguration;
    }
}
