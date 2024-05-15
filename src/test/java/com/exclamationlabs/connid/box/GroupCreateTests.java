/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import com.box.sdk.BoxAPIRequest;
import com.exclamationlabs.connid.box.testutil.AbstractTests;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.RetryableException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.exclamationlabs.connid.box.GroupsHandler.OBJECT_CLASS_GROUP;
import static com.exclamationlabs.connid.box.testutil.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hiroyuki Wada
 */
class GroupCreateTests extends AbstractTests {

    @Test
    void createGroup() {
        // Given
        String groupName = "Support";
        String description = "Support Group - as imported from Active Directory";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(groupName));
        attributes.add(AttributeBuilder.build("description", description));

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return created("group-create.json");
        });

        // When
        Uid uid = connector.create(OBJECT_CLASS_GROUP, attributes, new OperationOptionsBuilder().build());

        // Then
        assertNotNull(request.get());
        assertEquals(groupName, getJsonAttr(request.get(), "name"));
        assertEquals(description, getJsonAttr(request.get(), "description"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(groupName, uid.getNameHintValue());
    }

    @Test
    void createGroup_alreadyExists() {
        // Given
        String groupName = "Support";
        String description = "Support Group - as imported from Active Directory";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(groupName));
        attributes.add(AttributeBuilder.build("description", description));

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            throw conflict();
        });

        // When
        AlreadyExistsException e = assertThrows(AlreadyExistsException.class, () -> {
            Uid uid = connector.create(OBJECT_CLASS_GROUP, attributes, new OperationOptionsBuilder().build());
        });

        // Then
        assertNotNull(e);
    }

    @Test
    void createGroup_notFound() {
        // Given
        String groupName = "Support";
        String description = "Support Group - as imported from Active Directory";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(groupName));
        attributes.add(AttributeBuilder.build("description", description));

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            throw notFound();
        });

        // When
        UnknownUidException e = assertThrows(UnknownUidException.class, () -> {
            Uid uid = connector.create(OBJECT_CLASS_GROUP, attributes, new OperationOptionsBuilder().build());
        });

        // Then
        assertNotNull(e);
    }

    @Test
    void createGroup_otherError() {
        // Given
        String groupName = "Support";
        String description = "Support Group - as imported from Active Directory";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(groupName));
        attributes.add(AttributeBuilder.build("description", description));

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
            Uid uid = connector.create(OBJECT_CLASS_GROUP, attributes, new OperationOptionsBuilder().build());
        });

        // Then
        assertNotNull(e);
        assertEquals(2, count.get());
    }
}
