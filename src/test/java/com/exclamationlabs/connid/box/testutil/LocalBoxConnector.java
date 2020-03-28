package com.exclamationlabs.connid.box.testutil;

import com.exclamationlabs.connid.box.BoxConnector;

/**
 * BoxConnector implementation for local testing environment which uses mock Box API instead of the real Box API.
 *
 * @author Hiroyuki Wada
 */
public class LocalBoxConnector extends BoxConnector {

    @Override
    protected void authenticateResource() {
        boxAPI = MockBoxAPIHelper.instance().getAPIConnection();
    }

    @Override
    public void checkAlive() {
        // Do nothing for unit tests.
    }
}
