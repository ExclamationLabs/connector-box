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
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.jupiter.api.Test;

import java.util.List;
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
            fail("Shouldn't be called more than once");
            return null;
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
        assertEquals("managed_group", AttributeUtil.getSingleValue(result.getAttributeByName(GroupsHandler.ATTR_GROUP_TYPE)));

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
            fail("Shouldn't be called more than once");
            return null;
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
            return ok("group-member-2.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_GROUP,
                new Uid(uid, new Name(groupName)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                GroupsHandler.FULL_ATTRS_WITH_ASSOCIATION_SET
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

        // The number of fetched attributes is +1 because of the addition of __UID__
        assertEquals(1 + MINI_ATTRS.length + STANDARD_ATTRS.length + FULL_ATTRS.length + ASSOCIATION_ATTRS.length,
                result.getAttributes().size());

        for (String attr : GroupsHandler.FULL_ATTRS_WITH_ASSOCIATION_SET) {
            // name is fetched as __NAME__ and checked already, skip it
            if (attr.equals(GroupsHandler.ATTR_NAME)) {
                continue;
            }
            assertNotNull(result.getAttributeByName(attr), attr + " should not be null");
        }

        List<Object> member = result.getAttributeByName(GroupsHandler.ATTR_MEMBER).getValue();
        assertEquals(1, member.size());
        assertEquals("11446498", member.get(0).toString());

        List<Object> adminMember = result.getAttributeByName(GroupsHandler.ATTR_ADMIN_MEMBER).getValue();
        assertEquals(1, adminMember.size());
        assertEquals("12345678", adminMember.get(0).toString());
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
    }

}
