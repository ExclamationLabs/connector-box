/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import com.box.sdk.BoxAPIRequest;
import com.exclamationlabs.connid.box.testutil.AbstractTests;
import com.exclamationlabs.connid.box.testutil.TestUtils;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.exclamationlabs.connid.box.UsersHandler.*;
import static com.exclamationlabs.connid.box.testutil.TestUtils.mergeFields;
import static com.exclamationlabs.connid.box.testutil.TestUtils.ok;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hiroyuki Wada
 */
class UserSearchTests extends AbstractTests {

    @Test
    void searchAllUser_1() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-list-1.json");
        });

        List<ConnectorObject> users = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            users.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_USER,
                null,
                handler,
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(1, users.size());
        assertEquals(OBJECT_CLASS_USER, users.get(0).getObjectClass());
        assertEquals("11446498", users.get(0).getUid().getUidValue());
        assertEquals("ceo@example.com", users.get(0).getName().getNameValue());
        assertEquals("Aaron Levie", users.get(0).getAttributeByName("name").getValue().get(0));

        ConnectorObject result = users.get(0);
        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            assertNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void searchAllUser_1_fullAttributes() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-list-1.json");
        });

        List<ConnectorObject> users = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            users.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_USER,
                null,
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                UsersHandler.FULL_ATTRS
                        )
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(1, users.size());
        assertEquals(OBJECT_CLASS_USER, users.get(0).getObjectClass());
        assertEquals("11446498", users.get(0).getUid().getUidValue());
        assertEquals("ceo@example.com", users.get(0).getName().getNameValue());
        assertEquals("Aaron Levie", users.get(0).getAttributeByName("name").getValue().get(0));

        ConnectorObject result = users.get(0);
        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void searchAllUser_2() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-list-2.json");
        });

        List<ConnectorObject> users = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            users.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_USER,
                null,
                handler,
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(2, users.size());
        assertEquals(OBJECT_CLASS_USER, users.get(0).getObjectClass());
        assertEquals("11446498", users.get(0).getUid().getUidValue());
        assertEquals("ceo@example.com", users.get(0).getName().getNameValue());
        assertEquals("Aaron Levie", users.get(0).getAttributeByName("name").getValue().get(0));
        assertEquals(OBJECT_CLASS_USER, users.get(1).getObjectClass());
        assertEquals("12345678", users.get(1).getUid().getUidValue());
        assertEquals("foo@example.com", users.get(1).getName().getNameValue());
        assertEquals("Foo Bar", users.get(1).getAttributeByName("name").getValue().get(0));
    }

    @Test
    void searchAllUser_empty() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-list-0.json");
        });

        List<ConnectorObject> users = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            users.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_USER,
                null,
                handler,
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(0, users.size());
    }

    @Test
    void searchUserByName() throws UnsupportedEncodingException {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-list-1.json");
        });

        List<ConnectorObject> users = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            users.add(connectorObject);
            return true;
        };

        // When
        OperationOptions options = new OperationOptionsBuilder()
                .build();
        connector.search(OBJECT_CLASS_USER,
                new EqualsFilter(new Name(login)),
                handler,
                options);

        // Then
        assertNotNull(request.get());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS), fields);
        assertEquals(login, query.get("filter_term"));

        assertEquals(1, users.size());
        assertEquals(OBJECT_CLASS_USER, users.get(0).getObjectClass());
        assertEquals("11446498", users.get(0).getUid().getUidValue());
        assertEquals(login, users.get(0).getName().getNameValue());
        assertEquals("Aaron Levie", users.get(0).getAttributeByName("name").getValue().get(0));

        ConnectorObject result = users.get(0);
        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            assertNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void searchUserByName_fullAttributes() throws UnsupportedEncodingException {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-list-1.json");
        });

        List<ConnectorObject> users = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            users.add(connectorObject);
            return true;
        };

        // When
        OperationOptions options = new OperationOptionsBuilder()
                .setReturnDefaultAttributes(true)
                .setAttributesToGet(
                        UsersHandler.FULL_ATTRS
                )
                .build();
        connector.search(OBJECT_CLASS_USER,
                new EqualsFilter(new Name(login)),
                handler,
                options);

        // Then
        assertNotNull(request.get());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS, FULL_ATTRS), fields);
        assertEquals(login, query.get("filter_term"));

        assertEquals(1, users.size());
        assertEquals(OBJECT_CLASS_USER, users.get(0).getObjectClass());
        assertEquals("11446498", users.get(0).getUid().getUidValue());
        assertEquals(login, users.get(0).getName().getNameValue());
        assertEquals("Aaron Levie", users.get(0).getAttributeByName("name").getValue().get(0));

        ConnectorObject result = users.get(0);
        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void searchUserByName_empty() throws UnsupportedEncodingException {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-list-0.json");
        });

        List<ConnectorObject> users = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            users.add(connectorObject);
            return true;
        };

        // When
        OperationOptions options = new OperationOptionsBuilder()
                .build();
        connector.search(OBJECT_CLASS_USER,
                new EqualsFilter(new Name(login)),
                handler,
                options);

        // Then
        assertNotNull(request.get());
        assertEquals(0, users.size());
    }
}
