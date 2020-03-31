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

import static com.exclamationlabs.connid.box.UsersHandler.*;
import static com.exclamationlabs.connid.box.testutil.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hiroyuki Wada
 */
class UserGetTests extends AbstractTests {

    @Test
    void getUser() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-get.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/users/" + uid, request.get().getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS), fields);

        assertEquals(OBJECT_CLASS_USER, result.getObjectClass());
        assertEquals(uid, result.getUid().getUidValue());
        assertEquals(login, result.getName().getNameValue());

        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            assertNull(result.getAttributeByName(attr), attr + " should be null");
        }

        assertEquals("6509241374", result.getAttributeByName("phone").getValue().get(0));
    }

    @Test
    void getUser_minimalAttributes() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-get-minimal.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/users/" + uid, request.get().getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS), fields);

        assertEquals(OBJECT_CLASS_USER, result.getObjectClass());
        assertEquals(uid, result.getUid().getUidValue());
        assertEquals(login, result.getName().getNameValue());

        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            assertNull(result.getAttributeByName(attr), attr + " should be null");
        }

        assertEquals("en", result.getAttributeByName("language").getValue().get(0));
        assertEquals("Africa/Bujumbura", result.getAttributeByName("timezone").getValue().get(0));
        assertEquals("https://www.box.com/api/avatar/large/181216415",
                result.getAttributeByName("avatar_url").getValue().get(0));
        assertNull(result.getAttributeByName("phone").getValue().get(0));
        assertNull(result.getAttributeByName("address").getValue().get(0));
        assertNull(result.getAttributeByName("job_title").getValue().get(0));
    }

    @Test
    void getUser_addAttribute() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-get.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        // Standard + role
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_ROLE
                        )
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/users/" + uid, request.get().getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS, new String[]{ATTR_ROLE}), fields);

        assertEquals(OBJECT_CLASS_USER, result.getObjectClass());
        assertEquals(uid, result.getUid().getUidValue());
        assertEquals(login, result.getName().getNameValue());

        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            if (!attr.equals(ATTR_ROLE)) {
                assertNull(result.getAttributeByName(attr), attr + " should be null");
            }
        }

        assertEquals("admin", result.getAttributeByName(ATTR_ROLE).getValue().get(0));
    }

    @Test
    void getUser_fullAttributes() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-get.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                UsersHandler.FULL_ATTRS
                        )
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/users/" + uid, request.get().getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(request.get());
        assertNotNull(query.get("fields"));
        Set<String> fields = TestUtils.parseFields(query.get("fields"));
        assertEquals(mergeFields(MINI_ATTRS, STANDARD_ATTRS, FULL_ATTRS), fields);

        assertEquals(OBJECT_CLASS_USER, result.getObjectClass());
        assertEquals(uid, result.getUid().getUidValue());
        assertEquals(login, result.getName().getNameValue());

        for (String attr : UsersHandler.STANDARD_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
        for (String attr : UsersHandler.FULL_ATTRS) {
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }
    }

    @Test
    void getUser_notFound() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            throw notFound();
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .build());

        // Then
        assertNotNull(request.get());
        assertEquals("/2.0/users/" + uid, request.get().getUrl().getPath());
        assertNull(result, "It should not throw any exception");
    }

    @Test
    void getUser_otherError() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        // Set retry count of the Box SDK
        mockAPI.setMaxRequestAttempts(2);

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
            ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                    new Uid(uid, new Name(login)),
                    new OperationOptionsBuilder()
                            .build());
        });

        // Then
        assertNotNull(e);
        assertEquals(2, count.get());
    }
}
