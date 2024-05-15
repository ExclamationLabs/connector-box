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
import org.identityconnectors.framework.common.exceptions.RetryableException;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.exclamationlabs.connid.box.GroupsHandler.*;
import static com.exclamationlabs.connid.box.testutil.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hiroyuki Wada
 */
class GroupGetTests extends AbstractTests {

    @Test
    void getGroup() {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-get.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_GROUP,
                new Uid(uid, new Name(groupName)),
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/groups/" + uid, request.get().getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS), fields);

        assertEquals(OBJECT_CLASS_GROUP, result.getObjectClass());
        assertEquals(uid, result.getUid().getUidValue());
        assertEquals(groupName, result.getName().getNameValue());

        for (String attr : GroupsHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : GroupsHandler.FULL_ATTRS) {
            assertNull(result.getAttributeByName(attr), attr + " should be null");
        }
    }

    @Test
    void getGroup_addAttribute() {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-get.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_GROUP,
                new Uid(uid, new Name(groupName)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_DESCRIPTION
                        )
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/groups/" + uid, request.get().getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS, new String[]{ATTR_DESCRIPTION}), fields);

        assertEquals(OBJECT_CLASS_GROUP, result.getObjectClass());
        assertEquals(uid, result.getUid().getUidValue());
        assertEquals(groupName, result.getName().getNameValue());

        for (String attr : GroupsHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : GroupsHandler.FULL_ATTRS) {
            if (!attr.equals(ATTR_DESCRIPTION)) {
                assertNull(result.getAttributeByName(attr), attr + " should be null");
            }
        }

        assertEquals("Support Group - as imported from Active Directory",
                result.getAttributeByName(ATTR_DESCRIPTION).getValue().get(0));
    }

    @Test
    void getGroup_fullAttributes() {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("group-get.json");
        });
        mockAPI.push(req -> {
            return ok("group-member-0.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_GROUP,
                new Uid(uid, new Name(groupName)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                GroupsHandler.FULL_ATTRS
                        )
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/groups/" + uid, request.get().getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS, FULL_ATTRS), fields);

        assertEquals(OBJECT_CLASS_GROUP, result.getObjectClass());
        assertEquals(uid, result.getUid().getUidValue());
        assertEquals(groupName, result.getName().getNameValue());

        for (String attr : GroupsHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : GroupsHandler.FULL_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
    }

    @Test
    void getGroup_notFound() {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            throw notFound();
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_GROUP,
                new Uid(uid, new Name(groupName)),
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/groups/" + uid, request.get().getUrl().getPath());
        assertNull(result, "It should not throw any exception");
    }

    @Test
    void getGroup_otherError() {
        // Given
        String uid = "11446498";
        String groupName = "Support";

        // Set retry count of the Box SDK
        mockAPI.setMaxRequestAttempts(1);

        AtomicInteger count = new AtomicInteger();
        mockAPI.push(req -> {
            count.incrementAndGet();

            throw internalServerError();
        });
        mockAPI.push(req -> {
            count.incrementAndGet();

            throw internalServerError();
        });

        // When
        RetryableException e = assertThrows(RetryableException.class, () -> {
            ConnectorObject result = connector.getObject(OBJECT_CLASS_GROUP,
                    new Uid(uid, new Name(groupName)),
                    new OperationOptionsBuilder()
                            .build());
        });

        // Then
        assertNotNull(e);
        assertEquals(2, count.get());
        ;
    }

}
