/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import com.box.sdk.BoxAPIRequest;
import com.exclamationlabs.connid.box.testutil.AbstractTests;
import org.identityconnectors.framework.common.exceptions.RetryableException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.exclamationlabs.connid.box.UsersHandler.OBJECT_CLASS_USER;
import static com.exclamationlabs.connid.box.testutil.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hiroyuki Wada
 */
class UserUpdateTests extends AbstractTests {

    @Test
    void updateUser() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-update.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertNotNull(request.get());
        assertEquals("CTO", getJsonAttr(request.get(), "job_title"));
        assertNull(sideEffects);
    }

    @Test
    void updateUser_notFound() {
        // Given
        String login = "ceo@example.com";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));

        mockAPI.push(req -> {
            throw notFound();
        });

        // When
        UnknownUidException e = assertThrows(UnknownUidException.class, () -> {
            Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                    new Uid("11446498", new Name(login)),
                    modifications, new OperationOptionsBuilder().build());
        });

        // Then
        assertNotNull(e);
    }

    @Test
    void updateUser_otherError() {
        // Given
        String login = "ceo@example.com";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));

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
            Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                    new Uid("11446498", new Name(login)),
                    modifications, new OperationOptionsBuilder().build());
        });

        // Then
        assertNotNull(e);
    }

    @Test
    void active() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.buildEnabled(true));

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-update.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertNotNull(request.get());
        assertEquals("active", getJsonAttr(request.get(), "status"));
        assertNull(sideEffects);
        ;
    }

    @Test
    void inactive() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.buildEnabled(false));

        AtomicReference<BoxAPIRequest> request = new AtomicReference<>();
        mockAPI.push(req -> {
            request.set(req);

            return ok("user-inactive.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertNotNull(request.get());
        assertEquals("inactive", getJsonAttr(request.get(), "status"));
        assertNull(sideEffects);
        ;
    }

    @Test
    void renameLogin() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build(Name.NAME, "alias@example.com"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-email-alias-create.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-email-alias-list-1.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return noContent();
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(4, requests.size());
        assertEquals("alias@example.com", getJsonAttr(requests.get(1), "login"));
        assertNull(sideEffects);
    }
}
