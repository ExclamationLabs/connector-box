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
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.exclamationlabs.connid.box.UsersHandler.*;
import static com.exclamationlabs.connid.box.testutil.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Hiroyuki Wada
 */
class UserGroupMembershipTests extends AbstractTests {

    @Test
    void createUser_group_1() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(login));
        attributes.add(AttributeBuilder.build("name", name));
        attributes.add(AttributeBuilder.build("group_membership", "12345678"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-create.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-1.json");
        });

        // When
        Uid uid = connector.create(OBJECT_CLASS_USER, attributes, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertEquals(login, getJsonAttr(requests.get(0), "login"));
        assertEquals(name, getJsonAttr(requests.get(0), "name"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(login, uid.getNameHintValue());
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertNull(getJsonAttr(requests.get(1), "role"));
    }

    @Test
    void createUser_group_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(login));
        attributes.add(AttributeBuilder.build("name", name));
        attributes.add(AttributeBuilder.build("group_membership", "12345678", "87654321"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-create.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-1.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-2.json");
        });

        // When
        Uid uid = connector.create(OBJECT_CLASS_USER, attributes, new OperationOptionsBuilder().build());

        // Then
        assertEquals(3, requests.size());
        assertEquals(login, getJsonAttr(requests.get(0), "login"));
        assertEquals(name, getJsonAttr(requests.get(0), "name"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(login, uid.getNameHintValue());
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertNull(getJsonAttr(requests.get(1), "role"));
        assertEquals("11446498", getJsonObject(requests.get(2), "user").get("id").asString());
        assertEquals("87654321", getJsonObject(requests.get(2), "group").get("id").asString());
        assertNull(getJsonAttr(requests.get(1), "role"));
    }

    @Test
    void createUser_group_admin_1() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(login));
        attributes.add(AttributeBuilder.build("name", name));
        attributes.add(AttributeBuilder.build("group_admin_membership", "12345678"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-create.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Uid uid = connector.create(OBJECT_CLASS_USER, attributes, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertEquals(login, getJsonAttr(requests.get(0), "login"));
        assertEquals(name, getJsonAttr(requests.get(0), "name"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(login, uid.getNameHintValue());
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertNull(getJsonObject(requests.get(1), "configurable_permissions"));
    }

    @Test
    void createUser_group_admin_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(login));
        attributes.add(AttributeBuilder.build("name", name));
        attributes.add(AttributeBuilder.build("group_admin_membership", "12345678", "87654321"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-create.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-2.json");
        });

        // When
        Uid uid = connector.create(OBJECT_CLASS_USER, attributes, new OperationOptionsBuilder().build());

        // Then
        assertEquals(3, requests.size());
        assertEquals(login, getJsonAttr(requests.get(0), "login"));
        assertEquals(name, getJsonAttr(requests.get(0), "name"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(login, uid.getNameHintValue());
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertNull(getJsonObject(requests.get(1), "configurable_permissions"));
        assertEquals("11446498", getJsonObject(requests.get(2), "user").get("id").asString());
        assertEquals("87654321", getJsonObject(requests.get(2), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(2), "role"));
        assertNull(getJsonObject(requests.get(2), "configurable_permissions"));
    }

    @Test
    void createUser_group_admin_1_with_default_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(login));
        attributes.add(AttributeBuilder.build("name", name));
        attributes.add(AttributeBuilder.build("group_admin_membership", "12345678"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-create.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // Configure group admin default permissions
        BoxConfiguration configuration = newConfig();
        configuration.setGroupAdminDefaultPermissionCanCreateAccounts(false);
        configuration.setGroupAdminDefaultPermissionCanEditAccounts(false);
        configuration.setGroupAdminDefaultPermissionCanCInstantLogin(false);
        configuration.setGroupAdminDefaultPermissionCanRunReports(false);
        updateConfiguration(configuration);

        // When
        Uid uid = connector.create(OBJECT_CLASS_USER, attributes, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertEquals(login, getJsonAttr(requests.get(0), "login"));
        assertEquals(name, getJsonAttr(requests.get(0), "name"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(login, uid.getNameHintValue());
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_create_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_edit_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_instant_login").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_run_reports").asBoolean());
    }

    @Test
    void createUser_group_admin_0_with_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(login));
        attributes.add(AttributeBuilder.build("name", name));
        attributes.add(AttributeBuilder.build("group_admin_membership_permission", "12345678#can_create_accounts=false,can_edit_accounts=false,can_instant_login=true,can_run_reports=true"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-create.json");
        });
        mockAPI.push(req -> {
            fail("Shouldn't be called if group_admin_membership_permission is set without group_admin_membership when creating user");
            return null;
        });

        // When
        Uid uid = connector.create(OBJECT_CLASS_USER, attributes, new OperationOptionsBuilder().build());

        // Then
        assertEquals(1, requests.size());
        assertEquals(login, getJsonAttr(requests.get(0), "login"));
        assertEquals(name, getJsonAttr(requests.get(0), "name"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(login, uid.getNameHintValue());
    }

    @Test
    void createUser_group_admin_1_with_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<Attribute> attributes = new HashSet<>();
        attributes.add(new Name(login));
        attributes.add(AttributeBuilder.build("name", name));
        attributes.add(AttributeBuilder.build("group_admin_membership", "12345678"));
        attributes.add(AttributeBuilder.build("group_admin_membership_permission", "12345678#can_create_accounts=false,can_edit_accounts=false,can_instant_login=true,can_run_reports=true"));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("user-create.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Uid uid = connector.create(OBJECT_CLASS_USER, attributes, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertEquals(login, getJsonAttr(requests.get(0), "login"));
        assertEquals(name, getJsonAttr(requests.get(0), "name"));
        assertEquals("11446498", uid.getUidValue());
        assertEquals(login, uid.getNameHintValue());
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_create_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_edit_accounts").asBoolean());
        assertTrue(getJsonObject(requests.get(1), "configurable_permissions").get("can_instant_login").asBoolean());
        assertTrue(getJsonObject(requests.get(1), "configurable_permissions").get("can_run_reports").asBoolean());
    }

    @Test
    void updateUser_group_add_1() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                Arrays.asList("12345678"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-1.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(1, requests.size());
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(0), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(0), "group").get("id").asString());
    }

    @Test
    void updateUser_group_add_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                Arrays.asList("12345678", "87654321"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-1.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-2.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(3, requests.size());
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("11446498", getJsonObject(requests.get(2), "user").get("id").asString());
        assertEquals("87654321", getJsonObject(requests.get(2), "group").get("id").asString());
    }

    @Test
    void updateUser_group_admin_add_1() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("12345678"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(1, requests.size());
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(0), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(0), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(0), "role"));
        assertNull(getJsonObject(requests.get(0), "configurable_permissions"));
    }

    @Test
    void updateUser_group_admin_add_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("12345678", "87654321"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-2.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(3, requests.size());
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertNull(getJsonObject(requests.get(1), "configurable_permissions"));
        assertEquals("11446498", getJsonObject(requests.get(2), "user").get("id").asString());
        assertEquals("87654321", getJsonObject(requests.get(2), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(2), "role"));
        assertNull(getJsonObject(requests.get(2), "configurable_permissions"));
    }

    @Test
    void updateUser_group_admin_add_1_with_default_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("12345678"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // Configure group admin default permissions
        BoxConfiguration configuration = newConfig();
        configuration.setGroupAdminDefaultPermissionCanCreateAccounts(false);
        configuration.setGroupAdminDefaultPermissionCanEditAccounts(false);
        configuration.setGroupAdminDefaultPermissionCanCInstantLogin(false);
        configuration.setGroupAdminDefaultPermissionCanRunReports(false);
        updateConfiguration(configuration);

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(1, requests.size());
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(0), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(0), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(0), "role"));
        assertFalse(getJsonObject(requests.get(0), "configurable_permissions").get("can_create_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(0), "configurable_permissions").get("can_edit_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(0), "configurable_permissions").get("can_instant_login").asBoolean());
        assertFalse(getJsonObject(requests.get(0), "configurable_permissions").get("can_run_reports").asBoolean());
    }

    @Test
    void updateUser_group_admin_add_0_with_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership_permission",
                Arrays.asList("12345678#can_create_accounts=false,can_edit_accounts=false,can_instant_login=true,can_run_reports=true"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-2.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertNull(sideEffects);
        assertEquals("/2.0/group_memberships/11111111", requests.get(1).getUrl().getPath());
        assertNull(getJsonObject(requests.get(1), "user"));
        assertNull(getJsonObject(requests.get(1), "group"));
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_create_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_edit_accounts").asBoolean());
        assertTrue(getJsonObject(requests.get(1), "configurable_permissions").get("can_instant_login").asBoolean());
        assertTrue(getJsonObject(requests.get(1), "configurable_permissions").get("can_run_reports").asBoolean());
    }

    @Test
    void updateUser_group_admin_add_0_with_permission_but_unmatched_membership() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership_permission",
                Arrays.asList("12345678#can_create_accounts=false,can_edit_accounts=false,can_instant_login=true,can_run_reports=true"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-0.json");
        });
        mockAPI.push(req -> {
            fail("Shouldn't be called if fetched membership is unmatched for updating permission");
            return null;
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(1, requests.size());
        assertNull(sideEffects);
    }

    @Test
    void updateUser_group_admin_add_1_with_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("12345678"),
                null));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership_permission",
                Arrays.asList("12345678#can_create_accounts=false,can_edit_accounts=false,can_instant_login=true,can_run_reports=true"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(1, requests.size());
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(0), "user").get("id").asString());
        assertEquals("12345678", getJsonObject(requests.get(0), "group").get("id").asString());
        assertEquals("admin", getJsonAttr(requests.get(0), "role"));
        assertFalse(getJsonObject(requests.get(0), "configurable_permissions").get("can_create_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(0), "configurable_permissions").get("can_edit_accounts").asBoolean());
        assertTrue(getJsonObject(requests.get(0), "configurable_permissions").get("can_instant_login").asBoolean());
        assertTrue(getJsonObject(requests.get(0), "configurable_permissions").get("can_run_reports").asBoolean());
    }

    @Test
    void updateUser_switch_from_member_to_group_admin() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                null,
                Arrays.asList("12345678")));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("12345678"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-2.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(0).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(1).getUrl().getPath());
        assertNull(getJsonObject(requests.get(1), "user"));
        assertNull(getJsonObject(requests.get(1), "group"));
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertNull(getJsonObject(requests.get(1), "configurable_permissions"));
    }

    @Test
    void updateUser_switch_from_member_to_group_admin_with_default_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                null,
                Arrays.asList("12345678")));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("12345678"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-2.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // Configure group admin default permissions
        BoxConfiguration configuration = newConfig();
        configuration.setGroupAdminDefaultPermissionCanCreateAccounts(false);
        configuration.setGroupAdminDefaultPermissionCanEditAccounts(false);
        configuration.setGroupAdminDefaultPermissionCanCInstantLogin(false);
        configuration.setGroupAdminDefaultPermissionCanRunReports(false);
        updateConfiguration(configuration);

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(0).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(1).getUrl().getPath());
        assertNull(getJsonObject(requests.get(1), "user"));
        assertNull(getJsonObject(requests.get(1), "group"));
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_create_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_edit_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_instant_login").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_run_reports").asBoolean());
    }

    @Test
    void updateUser_switch_from_member_to_group_admin_with_permission() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                null,
                Arrays.asList("12345678")));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("12345678"),
                null));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership_permission",
                Arrays.asList("12345678#can_create_accounts=false,can_edit_accounts=false,can_instant_login=true,can_run_reports=true"),
                null));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-2.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(0).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(1).getUrl().getPath());
        assertNull(getJsonObject(requests.get(1), "user"));
        assertNull(getJsonObject(requests.get(1), "group"));
        assertEquals("admin", getJsonAttr(requests.get(1), "role"));
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_create_accounts").asBoolean());
        assertFalse(getJsonObject(requests.get(1), "configurable_permissions").get("can_edit_accounts").asBoolean());
        assertTrue(getJsonObject(requests.get(1), "configurable_permissions").get("can_instant_login").asBoolean());
        assertTrue(getJsonObject(requests.get(1), "configurable_permissions").get("can_run_reports").asBoolean());
    }

    @Test
    void updateUser_switch_from_admin_to_member_group() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                Arrays.asList("23456789"),
                null));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                null,
                Arrays.asList("23456789")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-3.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(0).getUrl().getPath());
        assertEquals("/2.0/group_memberships/33333333", requests.get(1).getUrl().getPath());
        assertNull(getJsonObject(requests.get(1), "user"));
        assertNull(getJsonObject(requests.get(1), "group"));
        assertEquals("member", getJsonAttr(requests.get(1), "role"));
    }

    @Test
    void updateUser_group_remove_1() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                null,
                Arrays.asList("12345678")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-1.json");
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
        assertEquals(3, requests.size());
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(1).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(2).getUrl().getPath());
    }

    @Test
    void updateUser_group_remove_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                null,
                Arrays.asList("12345678", "87654321")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-2.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return noContent();
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
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(1).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(2).getUrl().getPath());
        assertEquals("/2.0/group_memberships/22222222", requests.get(3).getUrl().getPath());
    }

    @Test
    void updateUser_different_group_remove_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                null,
                Arrays.asList("12345678", "87654321")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            // The user belongs to the groups as admin
            return ok("user-group-admin-membership-2.json");
        });
        mockAPI.push(req -> {
            fail("Shouldn't be called");
            return null;
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(1).getUrl().getPath());
    }

    @Test
    void updateUser_group_admin_remove_1() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                null,
                Arrays.asList("12345678")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-admin-membership-1.json");
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
        assertEquals(3, requests.size());
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(1).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(2).getUrl().getPath());
    }

    @Test
    void updateUser_group_admin_remove_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                null,
                Arrays.asList("12345678", "87654321")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-admin-membership-2.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return noContent();
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
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(1).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(2).getUrl().getPath());
        assertEquals("/2.0/group_memberships/22222222", requests.get(3).getUrl().getPath());
    }

    @Test
    void updateUser_different_group_admin_remove_2() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                null,
                Arrays.asList("12345678", "87654321")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            // The user belongs to the groups as member
            return ok("user-group-membership-2.json");
        });
        mockAPI.push(req -> {
            fail("Shouldn't be called");
            return null;
        });

        // When
        Set<AttributeDelta> sideEffects = connector.updateDelta(OBJECT_CLASS_USER,
                new Uid("11446498", new Name(login)),
                modifications, new OperationOptionsBuilder().build());

        // Then
        assertEquals(2, requests.size());
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("/2.0/users/11446498/memberships", requests.get(1).getUrl().getPath());
    }

    @Test
    void updateUser_group_add_remove() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_membership",
                Arrays.asList("87654321"),
                Arrays.asList("12345678")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-1.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-1.json");
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
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("87654321", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("/2.0/users/11446498/memberships", requests.get(2).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(3).getUrl().getPath());
    }

    @Test
    void updateUser_group_admin_add_remove() {
        // Given
        String login = "ceo@example.com";
        String name = "Aaron Levie";

        Set<AttributeDelta> modifications = new HashSet<>();
        modifications.add(AttributeDeltaBuilder.build("job_title", "CTO"));
        modifications.add(AttributeDeltaBuilder.build("group_admin_membership",
                Arrays.asList("87654321"),
                Arrays.asList("12345678")));

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-update.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return created("group-membership-add-user-to-group-admin-1.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-admin-membership-1.json");
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
        assertEquals("CTO", getJsonAttr(requests.get(0), "job_title"));
        assertNull(sideEffects);
        assertEquals("11446498", getJsonObject(requests.get(1), "user").get("id").asString());
        assertEquals("87654321", getJsonObject(requests.get(1), "group").get("id").asString());
        assertEquals("/2.0/users/11446498/memberships", requests.get(2).getUrl().getPath());
        assertEquals("/2.0/group_memberships/11111111", requests.get(3).getUrl().getPath());
    }

    @Test
    void getUser_group_0() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-get.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-0.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_GROUP_MEMBERSHIP
                        )
                        .build());

        // Then
        assertEquals(2, requests.size());
        assertEquals("/2.0/users/" + uid, requests.get(0).getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(requests.get(0));
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

        assertEquals(new ArrayList(), result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue());

        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP));
        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION));
    }

    @Test
    void getUser_group_1() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-get.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-1.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_GROUP_MEMBERSHIP
                        )
                        .build());

        // Then
        assertEquals(2, requests.size());
        assertEquals("/2.0/users/" + uid, requests.get(0).getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(requests.get(0));
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

        assertEquals(1, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().size());
        assertEquals("12345678", result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().get(0));

        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP));
        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION));
    }

    @Test
    void getUser_group_2() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-get.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-2.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_GROUP_MEMBERSHIP
                        )
                        .build());

        // Then
        assertEquals(2, requests.size());
        assertEquals("/2.0/users/" + uid, requests.get(0).getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(requests.get(0));
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

        assertEquals(2, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().size());
        assertEquals("12345678", result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().get(0));
        assertEquals("87654321", result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().get(1));

        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP));
        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION));
    }

    @Test
    void getUser_group_3_with_group_admin() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-get.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-3.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_GROUP_MEMBERSHIP,
                                ATTR_GROUP_ADMIN_MEMBERSHIP
                        )
                        .build());

        // Then
        assertEquals(2, requests.size());
        assertEquals("/2.0/users/" + uid, requests.get(0).getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(requests.get(0));
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

        assertEquals(2, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().size());
        assertEquals("12345678", result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().get(0));
        assertEquals("87654321", result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().get(1));

        assertEquals(1, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getValue().size());
        assertEquals("23456789", result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getValue().get(0));

        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION));
    }

    @Test
    void getUser_group_3_with_group_admin_and_permission() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-get.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-group-membership-3.json");
        });
        mockAPI.push(req -> {
            requests.add(req);

            return ok("group-admin-membership.json");
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_GROUP_MEMBERSHIP,
                                ATTR_GROUP_ADMIN_MEMBERSHIP,
                                ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION
                        )
                        .build());

        // Then
        assertEquals(3, requests.size());
        assertEquals("/2.0/users/" + uid, requests.get(0).getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(requests.get(0));
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

        assertEquals(2, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().size());
        assertEquals("12345678", result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().get(0));
        assertEquals("87654321", result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().get(1));

        assertEquals(1, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getValue().size());
        assertEquals("23456789", result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getValue().get(0));

        assertEquals(1, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION).getValue().size());
        assertEquals("23456789#can_create_accounts=false,can_edit_accounts=false,can_instant_login=false,can_run_reports=false",
                result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION).getValue().get(0));
    }

    @Test
    void getUser_group_3_with_admin_group_incomplete() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-get.json");
        });
        mockAPI.push(req -> {
            fail("Shouldn't be called");
            return null;
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_GROUP_MEMBERSHIP,
                                ATTR_GROUP_ADMIN_MEMBERSHIP
                        )
                        .setAllowPartialAttributeValues(true) // set ALLOW_PARTIAL_ATTRIBUTE_VALUES operation option
                        .build());

        // Then
        assertEquals(1, requests.size());
        assertEquals("/2.0/users/" + uid, requests.get(0).getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(requests.get(0));
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

        assertEquals(0, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().size());
        assertEquals(AttributeValueCompleteness.INCOMPLETE, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getAttributeValueCompleteness());

        assertEquals(0, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getValue().size());
        assertEquals(AttributeValueCompleteness.INCOMPLETE, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getAttributeValueCompleteness());

        assertNull(result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION));
    }

    @Test
    void getUser_group_3_with_admin_group_and_permission_incomplete() {
        // Given
        String uid = "11446498";
        String login = "ceo@example.com";

        List<BoxAPIRequest> requests = new ArrayList<>();
        mockAPI.push(req -> {
            requests.add(req);

            return ok("user-get.json");
        });
        mockAPI.push(req -> {
            fail("Shouldn't be called");
            return null;
        });

        // When
        ConnectorObject result = connector.getObject(OBJECT_CLASS_USER,
                new Uid(uid, new Name(login)),
                new OperationOptionsBuilder()
                        .setReturnDefaultAttributes(true)
                        .setAttributesToGet(
                                ATTR_GROUP_MEMBERSHIP,
                                ATTR_GROUP_ADMIN_MEMBERSHIP,
                                ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION
                        )
                        .setAllowPartialAttributeValues(true) // set ALLOW_PARTIAL_ATTRIBUTE_VALUES operation option
                        .build());

        // Then
        assertEquals(1, requests.size());
        assertEquals("/2.0/users/" + uid, requests.get(0).getUrl().getPath());

        Map<String, String> query = TestUtils.parseQuery(requests.get(0));
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

        assertEquals(0, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getValue().size());
        assertEquals(AttributeValueCompleteness.INCOMPLETE, result.getAttributeByName(ATTR_GROUP_MEMBERSHIP).getAttributeValueCompleteness());

        assertEquals(0, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getValue().size());
        assertEquals(AttributeValueCompleteness.INCOMPLETE, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP).getAttributeValueCompleteness());

        assertEquals(0, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION).getValue().size());
        assertEquals(AttributeValueCompleteness.INCOMPLETE, result.getAttributeByName(ATTR_GROUP_ADMIN_MEMBERSHIP_PERMISSION).getAttributeValueCompleteness());
    }
}
