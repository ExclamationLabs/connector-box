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
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.exclamationlabs.connid.box.GroupsHandler.*;
import static com.exclamationlabs.connid.box.testutil.TestUtils.mergeFields;
import static com.exclamationlabs.connid.box.testutil.TestUtils.ok;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hiroyuki Wada
 */
class GroupSearchTests extends AbstractTests {

    @Test
    void searchAllGroup_1() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-list-1.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        List<ConnectorObject> groups = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            groups.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_GROUP,
                null,
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(1, groups.size());
        assertEquals(OBJECT_CLASS_GROUP, groups.get(0).getObjectClass());
        assertEquals("11446498", groups.get(0).getUid().getUidValue());
        assertEquals("Support", groups.get(0).getName().getNameValue());

        ConnectorObject result = groups.get(0);
        for (String attr : GroupsHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : GroupsHandler.FULL_ATTRS) {
            assertNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void searchAllGroup_1_fullAttributes() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-list-1.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        List<ConnectorObject> groups = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            groups.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_GROUP,
                null,
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                FULL_ATTRS
                        )
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(1, groups.size());
        assertEquals(OBJECT_CLASS_GROUP, groups.get(0).getObjectClass());
        assertEquals("11446498", groups.get(0).getUid().getUidValue());
        assertEquals("Support", groups.get(0).getName().getNameValue());

        ConnectorObject result = groups.get(0);
        for (String attr : GroupsHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : GroupsHandler.FULL_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void searchAllGroup_2() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-list-2.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        List<ConnectorObject> groups = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            groups.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_GROUP,
                null,
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(2, groups.size());
        assertEquals(OBJECT_CLASS_GROUP, groups.get(0).getObjectClass());
        assertEquals("11446498", groups.get(0).getUid().getUidValue());
        assertEquals("Support", groups.get(0).getName().getNameValue());
        assertNull(groups.get(0).getAttributeByName("description"));
//        assertEquals("Support Group - as imported from Active Directory", groups.get(0).getAttributeByName("description").getValue().get(0));
        assertEquals(OBJECT_CLASS_GROUP, groups.get(1).getObjectClass());
        assertEquals("12345678", groups.get(1).getUid().getUidValue());
        assertEquals("Foo", groups.get(1).getName().getNameValue());
        assertNull(groups.get(1).getAttributeByName("description"));
//        assertEquals("Foo Group", groups.get(1).getAttributeByName("description").getValue().get(0));
    }

    @Test
    void searchAllGroup_empty() {
        // Given
        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-list-0.json");
        });

        List<ConnectorObject> groups = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            groups.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_GROUP,
                null,
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(0, groups.size());
    }

    @Test
    void searchGroupByName() throws UnsupportedEncodingException {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-list-1.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        List<ConnectorObject> groups = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            groups.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_GROUP,
                new EqualsFilter(new Name(groupName)),
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .build());

        // Then
        assertNotNull(request.get());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS), fields);
        assertEquals(groupName, query.get("filter_term"));

        assertEquals(1, groups.size());
        assertEquals(OBJECT_CLASS_GROUP, groups.get(0).getObjectClass());
        assertEquals("11446498", groups.get(0).getUid().getUidValue());
        assertEquals(groupName, groups.get(0).getName().getNameValue());
    }

    @Test
    void searchGroupByName_fullAttributes() throws UnsupportedEncodingException {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-list-1.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        List<ConnectorObject> groups = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            groups.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_GROUP,
                new EqualsFilter(new Name(groupName)),
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                FULL_ATTRS
                        )
                        .build());

        // Then
        assertNotNull(request.get());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS, FULL_ATTRS), fields);
        assertEquals(groupName, query.get("filter_term"));

        assertEquals(1, groups.size());
        assertEquals(OBJECT_CLASS_GROUP, groups.get(0).getObjectClass());
        assertEquals("11446498", groups.get(0).getUid().getUidValue());
        assertEquals(groupName, groups.get(0).getName().getNameValue());

        ConnectorObject result = groups.get(0);
        for (String attr : GroupsHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : GroupsHandler.FULL_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void searchGroupByName_empty() throws UnsupportedEncodingException {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-list-0.json");
        });

        List<ConnectorObject> groups = new ArrayList<>();
        ResultsHandler handler = connectorObject -> {
            groups.add(connectorObject);
            return true;
        };

        // When
        connector.search(OBJECT_CLASS_GROUP,
                new EqualsFilter(new Name(groupName)),
                handler,
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals(0, groups.size());
    }
}
