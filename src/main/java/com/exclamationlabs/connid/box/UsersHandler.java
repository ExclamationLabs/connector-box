/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import com.box.sdk.*;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UsersHandler extends AbstractHandler {

    private static final Log LOGGER = Log.getLog(UsersHandler.class);

    // Use the type of the Box User resource:
    // https://developer.box.com/reference/resources/user/
    public static final ObjectClass OBJECT_CLASS_USER = new ObjectClass("user");

    // Mini
    protected static final String ATTR_LOGIN = "login";
    protected static final String ATTR_NAME = "name";

    // Standard
    protected static final String ATTR_ADDRESS = "address";
    protected static final String ATTR_AVATAR_URL = "avatar_url";
    protected static final String ATTR_CREATED_AT = "created_at";
    protected static final String ATTR_JOB_TITLE = "job_title";
    protected static final String ATTR_LANGUAGE = "language";
    protected static final String ATTR_MAX_UPLOAD_SIZE = "max_upload_size";
    protected static final String ATTR_MODIFIED_AT = "modified_at";
    protected static final String ATTR_NOTIFICATION_EMAIL = "notification_email";
    protected static final String ATTR_NOTIFICATION_EMAIL_EMAIL = "notification_email.email";
    protected static final String ATTR_NOTIFICATION_EMAIL_ISCONFIRMED = "notification_email.is_confirmed";
    protected static final String ATTR_PHONE = "phone";
    protected static final String ATTR_SPACE_AMOUNT = "space_amount";
    protected static final String ATTR_SPACE_USED = "space_used";
    protected static final String ATTR_STATUS = "status";
    protected static final String ATTR_TIMEZONE = "timezone";

    // Full
    protected static final String ATTR_CAN_SEE_MANAGED_USERS = "can_see_managed_users";
    protected static final String ATTR_ENTERPRISE = "enterprise";
    protected static final String ATTR_ENTERPRISE_ID = "enterprise.id";
    protected static final String ATTR_ENTERPRISE_NAME = "enterprise.name";
    protected static final String ATTR_EXTERNAL_APP_USER_ID = "external_app_user_id";
    protected static final String ATTR_HOSTNAME = "hostname";
    protected static final String ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS = "is_exempt_from_device_limits";
    protected static final String ATTR_IS_EXEMPT_FROM_LOGIN_VERIFICATION = "is_exempt_from_login_verification";
    protected static final String ATTR_IS_EXEMPT_COLLAB_RESTRICTED = "is_external_collab_restricted";
    protected static final String ATTR_IS_PLATFORM_ACCESS_ONLY = "is_platform_access_only";
    protected static final String ATTR_IS_SYNC_ENABLED = "is_sync_enabled";
    protected static final String ATTR_MY_TAGS = "my_tags";
    protected static final String ATTR_ROLE = "role";
    protected static final String ATTR_TRACKING_CODES = "tracking_codes";

    // Only for update
    // https://developer.box.com/reference/put-users-id/
    protected static final String ATTR_IS_PASSWORD_RESET_REQUIRED = "is_password_reset_required";
    protected static final String ATTR_NOTIFY = "notify";

    // Association
    protected static final String ATTR_GROUP_MEMBERSHIP = "group_membership";

    protected static final String[] MINI_ATTRS = new String[]{
            ATTR_NAME,
            ATTR_LOGIN
    };
    protected static final String[] STANDARD_ATTRS = new String[]{
            ATTR_CREATED_AT,
            ATTR_MODIFIED_AT,
            ATTR_LANGUAGE,
            ATTR_TIMEZONE,
            ATTR_SPACE_AMOUNT,
            ATTR_SPACE_USED,
            ATTR_MAX_UPLOAD_SIZE,
            ATTR_STATUS,
            ATTR_JOB_TITLE,
            ATTR_PHONE,
            ATTR_ADDRESS,
            ATTR_AVATAR_URL,
            // Box SDK for Java can't handle them currently
//            ATTR_NOTIFICATION_EMAIL_EMAIL,
//            ATTR_NOTIFICATION_EMAIL_ISCONFIRMED
    };
    protected static final String[] FULL_ATTRS = new String[]{
            ATTR_CAN_SEE_MANAGED_USERS,
            ATTR_ENTERPRISE_ID,
            ATTR_ENTERPRISE_NAME,
            ATTR_EXTERNAL_APP_USER_ID,
            ATTR_HOSTNAME,
            ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS,
            ATTR_IS_EXEMPT_FROM_LOGIN_VERIFICATION,
            ATTR_IS_EXEMPT_COLLAB_RESTRICTED,
            ATTR_IS_PLATFORM_ACCESS_ONLY,
            ATTR_IS_SYNC_ENABLED,
            ATTR_MY_TAGS,
            ATTR_ROLE,
            ATTR_TRACKING_CODES
    };
    protected static final Set<String> STANDARD_ATTRS_SET =
            Collections.unmodifiableSet(Stream.of(
                    MINI_ATTRS,
                    STANDARD_ATTRS
            ).flatMap(Arrays::stream).collect(Collectors.toSet()));

    public UsersHandler(String instanceName, BoxAPIConnection boxAPI) {
        super(instanceName, boxAPI);
    }

    public ObjectClassInfo getUserSchema() {
        ObjectClassInfoBuilder builder = new ObjectClassInfoBuilder();
        builder.setType(OBJECT_CLASS_USER.getObjectClassValue());

        // Base

        // id (__UID__)
        // Caution: Don't define a schema for "id" of user because the name conflicts with midPoint side.
//        builder.addAttributeInfo(
//                AttributeInfoBuilder.define(Uid.NAME)
//                        .setRequired(false) // Must be optional. It is not present for create operations
//                        .setCreateable(false)
//                        .setUpdateable(false)
//                        .setNativeName(ATTR_ID)
//                        .build()
//        );

        // type (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_TYPE)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_TYPE))
                .build());

        // Mini

        // login (__NAME__)
        builder.addAttributeInfo(AttributeInfoBuilder.define(Name.NAME)
                .setRequired(true)
                .setNativeName(ATTR_LOGIN)
                .setSubtype(AttributeInfo.Subtypes.STRING_CASE_IGNORE)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_LOGIN))
                .build());

        // name
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_NAME)
                .setRequired(true)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_NAME))
                .build());

        // Standard

        // address
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_ADDRESS)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_ADDRESS))
                .build());

        // avatar (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_AVATAR_URL)
                .setSubtype(AttributeInfo.Subtypes.STRING_URI)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_AVATAR_URL))
                .build());

        // created_at (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_CREATED_AT)
                .setType(ZonedDateTime.class)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_CREATED_AT))
                .build());

        // job_title
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_JOB_TITLE)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_JOB_TITLE))
                .build());

        // language
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_LANGUAGE)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_LANGUAGE))
                .build());

        // max_upload_size (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_MAX_UPLOAD_SIZE)
                .setType(Long.class)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_MAX_UPLOAD_SIZE))
                .build());

        // modified_at (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_MODIFIED_AT)
                .setType(ZonedDateTime.class)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_MODIFIED_AT))
                .build());

        // Box SDK for Java can't handle notification_email currently
        // notification_email.email (read/update-only)
//        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_NOTIFICATION_EMAIL_EMAIL)
//                .setCreateable(false)
//                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_NOTIFICATION_EMAIL_EMAIL))
//                .build());

        // notification_email.is_confirmed (read/update-only)
//        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_NOTIFICATION_EMAIL_ISCONFIRMED)
//                .setType(Boolean.class)
//                .setCreateable(false)
//                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_NOTIFICATION_EMAIL_ISCONFIRMED))
//                .build());

        // phone
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_PHONE)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_PHONE))
                .build());

        // space_amount
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_SPACE_AMOUNT)
                .setType(Long.class)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_SPACE_AMOUNT))
                .build());

        // space_used (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_SPACE_USED)
                .setType(Long.class)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_SPACE_USED))
                .build());

        // status
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_STATUS)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_STATUS))
                .build());

        // timezone
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_TIMEZONE)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_TIMEZONE))
                .build());

        // Full

        // can_see_managed_users
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_CAN_SEE_MANAGED_USERS)
                .setType(Boolean.class)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_CAN_SEE_MANAGED_USERS))
                .build());

        // enterprise.id (read/update-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_ENTERPRISE_ID)
                .setCreateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_ENTERPRISE_ID))
                .build());

        // enterprise.name (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_ENTERPRISE_NAME)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_ENTERPRISE_NAME))
                .build());

        // external_app_user_id (read/create-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_EXTERNAL_APP_USER_ID)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_EXTERNAL_APP_USER_ID))
                .build());

        // hostname (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_HOSTNAME)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_HOSTNAME))
                .build());

        // is_exempt_from_device_limits
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS)
                .setType(Boolean.class)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS))
                .build());

        // is_exempt_from_login_verification
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_IS_EXEMPT_FROM_LOGIN_VERIFICATION)
                .setType(Boolean.class)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_IS_EXEMPT_FROM_LOGIN_VERIFICATION))
                .build());

        // is_external_collab_restricted
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_IS_EXEMPT_COLLAB_RESTRICTED)
                .setType(Boolean.class)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_IS_EXEMPT_COLLAB_RESTRICTED))
                .build());

        // is_platform_access_only (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_IS_PLATFORM_ACCESS_ONLY)
                .setType(Boolean.class)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_IS_PLATFORM_ACCESS_ONLY))
                .build());

        // is_sync_enabled
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_IS_SYNC_ENABLED)
                .setType(Boolean.class)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_IS_SYNC_ENABLED))
                .build());

        // my_tags (read-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_MY_TAGS)
                .setMultiValued(true)
                .setCreateable(false)
                .setUpdateable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_MY_TAGS))
                .build());

        // role
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_ROLE)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_ROLE))
                .build());

        // tracking_codes
        // e.g. "code1: 12345"
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_TRACKING_CODES)
                .setMultiValued(true)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_TRACKING_CODES))
                .build());

        // Only for update

        // is_password_reset_required (update-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_IS_PASSWORD_RESET_REQUIRED)
                .setType(Boolean.class)
                .setCreateable(false)
                .setReadable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_IS_PASSWORD_RESET_REQUIRED))
                .build());

        // notify (update-only)
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_NOTIFY)
                .setType(Boolean.class)
                .setCreateable(false)
                .setReadable(false)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_NOTIFY))
                .build());

        // Association

        // Group membership
        builder.addAttributeInfo(AttributeInfoBuilder.define(ATTR_GROUP_MEMBERSHIP)
                .setMultiValued(true)
                .setReturnedByDefault(STANDARD_ATTRS_SET.contains(ATTR_GROUP_MEMBERSHIP))
                .build());

        // __ENABLE__
        builder.addAttributeInfo(OperationalAttributeInfos.ENABLE);

        ObjectClassInfo userSchemaInfo = builder.build();

        LOGGER.info("[{0}] The constructed User core schema: {1}", instanceName, userSchemaInfo);

        return userSchemaInfo;
    }

    public void query(BoxFilter query, ResultsHandler handler, OperationOptions ops) {
        LOGGER.info("[{0}] UserHandler query VALUE: {1}", instanceName, query);

        Set<String> attributesToGet = createFullAttributesToGetSet(STANDARD_ATTRS_SET, ops);

        if (query == null) {
            getAllUsers(handler, ops, attributesToGet);
        } else {
            if (query.isByUid()) {
                getUser(query.uid, handler, ops, attributesToGet);
            } else {
                getUser(query.name, handler, ops, attributesToGet);
            }
        }
    }

    private void getAllUsers(ResultsHandler handler, OperationOptions ops, Set<String> attributesToGet) {
        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxAPI, null,
                attributesToGet.toArray(new String[attributesToGet.size()]));

        for (BoxUser.Info info : users) {
            handler.handle(userToConnectorObject(info, attributesToGet));
        }
    }

    private void getUser(Uid uid, ResultsHandler handler, OperationOptions ops, Set<String> attributesToGet) {
        BoxUser user = new BoxUser(boxAPI, uid.getUidValue());
        try {
            // Fetch an user
            BoxUser.Info info = user.getInfo(attributesToGet.toArray(new String[attributesToGet.size()]));

            handler.handle(userToConnectorObject(info, attributesToGet));

        } catch (BoxAPIException e) {
            if (isNotFoundError(e)) {
                LOGGER.warn("[{0}] Unknown uid: {1}", instanceName, user.getID());
                // It should not throw any exception
                return;
            }
            throw e;
        }
    }

    private void getUser(Name name, ResultsHandler handler, OperationOptions ops, Set<String> attributesToGet) {
        // "List enterprise users" supports find by "login" which is treated as __NAME__ in this connector.
        // https://developer.box.com/reference/get-users/
        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxAPI, name.getNameValue(),
                attributesToGet.toArray(new String[attributesToGet.size()]));

        for (BoxUser.Info info : users) {
            if (info.getLogin().equalsIgnoreCase(name.getNameValue())) {
                handler.handle(userToConnectorObject(info, attributesToGet));
                // Break the loop to stop fetching remaining users if found
                return;
            }
        }
    }

    public Uid createUser(Set<Attribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new InvalidAttributeValueException("attributes not provided or empty");
        }

        CreateUserParams createUserParams = new CreateUserParams();

        String login = null;
        String name = null;
        List<String> groupsToAdd = new ArrayList<>();

        for (Attribute attr : attributes) {
            if (attr.getName().equals(Name.NAME)) {
                login = getStringValue(attr);

            } else if (attr.getName().equals(ATTR_NAME)) {
                name = getStringValue(attr);

            } else if (attr.getName().equals(ATTR_ADDRESS)) {
                createUserParams.setAddress(getStringValue(attr));

            } else if (attr.getName().equals(ATTR_CAN_SEE_MANAGED_USERS)) {
                Boolean value = getBooleanValue(attr);
                if (value != null) {
                    createUserParams.setCanSeeManagedUsers(value.booleanValue());
                }
            } else if (attr.getName().equals(ATTR_EXTERNAL_APP_USER_ID)) {
                createUserParams.setExternalAppUserId(getStringValue(attr));

            } else if (attr.getName().equals(ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS)) {
                Boolean value = getBooleanValue(attr);
                if (value != null) {
                    createUserParams.setIsExemptFromDeviceLimits(value.booleanValue());
                }
            } else if (attr.getName().equals(ATTR_IS_SYNC_ENABLED)) {
                Boolean value = getBooleanValue(attr);
                if (value != null) {
                    createUserParams.setIsSyncEnabled(value.booleanValue());
                }
            } else if (attr.getName().equals(ATTR_JOB_TITLE)) {
                createUserParams.setJobTitle(getStringValue(attr));

            } else if (attr.getName().equals(ATTR_LANGUAGE)) {
                createUserParams.setLanguage(getStringValue(attr));

            } else if (attr.getName().equals(ATTR_PHONE)) {
                createUserParams.setPhone(getStringValue(attr));

            } else if (attr.getName().equals(ATTR_SPACE_AMOUNT)) {
                Long value = getLongValue(attr);
                if (value != null) {
                    createUserParams.setSpaceAmount(value.longValue());
                }
            } else if (attr.getName().equals(OperationalAttributes.ENABLE_NAME)) {
                Boolean status = getBooleanValue(attr);
                if (Boolean.TRUE.equals(status)) {
                    createUserParams.setStatus(BoxUser.Status.ACTIVE);
                } else {
                    createUserParams.setStatus(BoxUser.Status.INACTIVE);
                }
            } else if (attr.getName().equals(ATTR_ROLE)) {
                // When creating a user, we can use "coadmin" or "user" only.
                // https://developer.box.com/reference/post-users/
                String role = getStringValue(attr);
                if (role != null) {
                    switch (role) {
                        case "coadmin":
                            createUserParams.setRole(BoxUser.Role.COADMIN);
                            break;
                        case "user":
                            createUserParams.setRole(BoxUser.Role.USER);
                            break;
                        default:
                            throw new InvalidAttributeValueException("Invalid role value of Box user: " + role);
                    }
                }
            } else if (attr.getName().equals(ATTR_GROUP_MEMBERSHIP)) {
                for (Object o : attr.getValue()) {
                    groupsToAdd.add(o.toString());
                }
            }
        }

        if (StringUtil.isBlank(login)) {
            throw new InvalidAttributeValueException(String.format("Missing mandatory attribute %s (%s)", Name.NAME, ATTR_LOGIN));
        }
        if (StringUtil.isBlank(name)) {
            throw new InvalidAttributeValueException("Missing mandatory attribute " + ATTR_NAME);
        }

        try {
            BoxUser.Info createdUserInfo = BoxUser.createEnterpriseUser(boxAPI, login, name, createUserParams);

            if (!groupsToAdd.isEmpty()) {
                BoxUser user = createdUserInfo.getResource();
                for (String group : groupsToAdd) {
                    BoxGroup boxGroup = new BoxGroup(boxAPI, group);
                    boxGroup.addMembership(user);
                }
            }

            return new Uid(createdUserInfo.getID(), new Name(createdUserInfo.getLogin()));

        } catch (BoxAPIResponseException e) {
            if (isUserAlreadyExistsError(e)) {
                throw new AlreadyExistsException(e);
            }
            throw e;
        }
    }

    public Set<AttributeDelta> updateUser(Uid uid, Set<AttributeDelta> modifications) {
        BoxUser user = new BoxUser(boxAPI, uid.getUidValue());
        BoxUser.Info info = user.new Info();

        if (StringUtil.isEmpty(info.getID())) {
            throw new ConnectorIOException("Unable to confirm uid on box resource");
        }

        boolean renameLogin = false;
        Set<String> groupsToAdd = null;
        Set<String> groupsToRemove = null;

        for (AttributeDelta delta : modifications) {
            if (delta.getName().equals(ATTR_NAME)) {
                info.setName(getStringValue(delta));

            } else if (delta.getName().equals(ATTR_ADDRESS)) {
                info.setAddress(getStringValue(delta));

            } else if (delta.getName().equals(ATTR_CAN_SEE_MANAGED_USERS)) {
                info.setCanSeeManagedUsers(getBooleangValue(delta));

            } else if (delta.getName().equals(ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS)) {
                info.setIsExemptFromDeviceLimits(getBooleangValue(delta));

            } else if (delta.getName().equals(ATTR_IS_SYNC_ENABLED)) {
                info.setIsSyncEnabled(getBooleangValue(delta));

            } else if (delta.getName().equals(ATTR_JOB_TITLE)) {
                info.setJobTitle(getStringValue(delta));

            } else if (delta.getName().equals(ATTR_LANGUAGE)) {
                info.setLanguage(getStringValue(delta));

            } else if (delta.getName().equals(ATTR_PHONE)) {
                info.setPhone(getStringValue(delta));

            } else if (delta.getName().equals(ATTR_SPACE_AMOUNT)) {
                info.setSpaceAmount(getLongValue(delta));

            } else if (delta.getName().equals(OperationalAttributes.ENABLE_NAME)) {
                if (getBooleangValue(delta)) {
                    info.setStatus(BoxUser.Status.ACTIVE);
                } else {
                    info.setStatus(BoxUser.Status.INACTIVE);
                }

            } else if (delta.getName().equals(ATTR_ROLE)) {
                // When updating a user, we can use "coadmin" or "user" only.

                String role = getStringValue(delta);
                switch (role) {
                    case "coadmin":
                        info.setRole(BoxUser.Role.COADMIN);
                        break;
                    case "user":
                        info.setRole(BoxUser.Role.USER);
                        break;
                    default:
                        throw new InvalidAttributeValueException("Invalid role value of Box user: " + role);
                }
            } else if (delta.getName().equals(Name.NAME)) {
                info.setLogin(getStringValue(delta));
                renameLogin = true;

            } else if (delta.getName().equals(ATTR_GROUP_MEMBERSHIP)) {
                groupsToAdd = getStringValuesToAdd(delta);
                groupsToRemove = getStringValuesToRemove(delta);
            }
        }

        // Handling email changing.
        // When updating email, we need to add email alias with confirmed flag first.
        // Then update the user with new email. If successful, the old email is moved to the alias.
        // Finally, we need to delete the alias.
        //
        // https://community.box.com/t5/Platform-and-Development-Forum/How-to-change-user-s-primary-login-via-API/td-p/26483

        EmailAlias newEmailAlias = null;
        String oldLogin = null;
        if (renameLogin) {
            // We need to get the current login (email) to rename it.
            // If the uid has NameHint, we can use the value as current login.
            // If not, we need to fetch the value from Box.
            if (uid.getNameHint() != null) {
                oldLogin = uid.getNameHint().getNameValue();
            } else {
                oldLogin = new BoxUser(boxAPI, uid.getUidValue()).getInfo("login").getLogin();
            }
            newEmailAlias = addEmailAlias(uid, info.getLogin());
        }

        try {
            if (info.getPendingChangesAsJsonObject() != null) {
                info.getResource().updateInfo(info);
            }
        } catch (BoxAPIException e) {
            LOGGER.error(e, "[{0}] Failed to update an user. response: {1}", instanceName, e.getResponse());

            // If updating email was failed, the new email alias will remain.
            // So we try to delete added new email alias for cleanup.
            if (newEmailAlias != null) {
                try {
                    user.deleteEmailAlias(newEmailAlias.getID());
                } catch (BoxAPIException e2) {
                    LOGGER.error(e2, "[{0}] Failed to clean up added email alias {1} for {2}. response: {3}",
                            instanceName, newEmailAlias.getEmail(), oldLogin, e.getResponse());
                }
            }
            throw e;
        }

        // If updating email was successful, find the old email in the alias and delete it.
        if (renameLogin) {
            deleteEmailAlias(uid, oldLogin);
        }

        if (groupsToAdd != null || groupsToRemove != null) {
            updateMemberships(uid, groupsToAdd, groupsToRemove);
        }

        // Box doesn't support to modify user's id
        return null;
    }

    private void updateMemberships(Uid uid, Set<String> groupsToAdd, Set<String> groupsToRemove) {
        BoxUser user = new BoxUser(boxAPI, uid.getUidValue());

        if (groupsToAdd != null && !groupsToAdd.isEmpty()) {
            for (String group : groupsToAdd) {
                BoxGroup boxGroup = new BoxGroup(boxAPI, group);
                boxGroup.addMembership(user);
            }
        }
        if (groupsToRemove != null && !groupsToRemove.isEmpty()) {
            Iterable<BoxGroupMembership.Info> memberships = user.getAllMemberships();
            for (BoxGroupMembership.Info membershipInfo : memberships) {
                if (groupsToRemove.contains(membershipInfo.getGroup().getID())) {
                    membershipInfo.getResource().delete();
                }
            }
        }
    }

    private EmailAlias addEmailAlias(Uid uid, String email) {
        BoxUser user = new BoxUser(boxAPI, uid.getUidValue());
        EmailAlias newEmailAlias = null;
        try {
            return user.addEmailAlias(email, true);

        } catch (BoxAPIException e) {
            // Find email alias with new email because it might be added before.
            // In that case, we ignore the error.
            for (EmailAlias emailAlias : user.getEmailAliases()) {
                if (emailAlias.getEmail().equalsIgnoreCase(email)) {
                    newEmailAlias = emailAlias;
                    break;
                }
            }
            if (newEmailAlias == null) {
                LOGGER.error(e, "[{0}] Failed to add email alias {1}. response: {2}",
                        instanceName, email, e.getResponse());
                throw e;
            }
        }
        return newEmailAlias;
    }

    private void deleteEmailAlias(Uid uid, String email) {
        BoxUser user = new BoxUser(boxAPI, uid.getUidValue());
        for (EmailAlias emailAlias : user.getEmailAliases()) {
            if (emailAlias.getEmail().equalsIgnoreCase(email)) {
                try {
                    user.deleteEmailAlias(emailAlias.getID());
                    break;
                } catch (BoxAPIException e) {
                    LOGGER.error(e, "[{0}] Failed to delete old email: {1} response: {2}",
                            instanceName, email, e.getResponse());
                    throw e;
                }
            }
        }
    }

    public void deleteUser(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        if (uid == null) {
            throw new InvalidAttributeValueException("uid not provided");
        }

        BoxUser user = new BoxUser(boxAPI, uid.getUidValue());
        user.delete(false, false);
    }

    private ConnectorObject userToConnectorObject(BoxUser.Info info, Set<String> attributesToGet) {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();

        builder.setObjectClass(OBJECT_CLASS_USER);

        builder.setUid(new Uid(info.getID(), new Name(info.getLogin())));
        builder.setName(info.getLogin());

        // Mini
        if (attributesToGet.contains(ATTR_NAME)) {
            builder.addAttribute(ATTR_NAME, info.getName());
        }

        // Standard
        if (attributesToGet.contains(ATTR_CREATED_AT)) {
            builder.addAttribute(ATTR_CREATED_AT, toZonedDateTime(info.getCreatedAt()));
        }
        if (attributesToGet.contains(ATTR_MODIFIED_AT)) {
            builder.addAttribute(ATTR_MODIFIED_AT, toZonedDateTime(info.getModifiedAt()));
        }
        if (attributesToGet.contains(ATTR_LANGUAGE)) {
            builder.addAttribute(ATTR_LANGUAGE, toString(info.getLanguage()));
        }
        if (attributesToGet.contains(ATTR_TIMEZONE)) {
            builder.addAttribute(ATTR_TIMEZONE, toString(info.getTimezone()));
        }
        if (attributesToGet.contains(ATTR_SPACE_AMOUNT)) {
            builder.addAttribute(ATTR_SPACE_AMOUNT, info.getSpaceAmount());
        }
        if (attributesToGet.contains(ATTR_SPACE_USED)) {
            builder.addAttribute(ATTR_SPACE_USED, info.getSpaceUsed());
        }
        if (attributesToGet.contains(ATTR_STATUS)) {
            builder.addAttribute(ATTR_STATUS, toString(info.getStatus()));
        }
        if (attributesToGet.contains(ATTR_MAX_UPLOAD_SIZE)) {
            builder.addAttribute(ATTR_MAX_UPLOAD_SIZE, info.getMaxUploadSize());
        }
        if (attributesToGet.contains(ATTR_JOB_TITLE)) {
            builder.addAttribute(ATTR_JOB_TITLE, toString(info.getJobTitle()));
        }
        if (attributesToGet.contains(ATTR_PHONE)) {
            builder.addAttribute(ATTR_PHONE, toString(info.getPhone()));
        }
        if (attributesToGet.contains(ATTR_ADDRESS)) {
            builder.addAttribute(ATTR_ADDRESS, toString(info.getAddress()));
        }
        if (attributesToGet.contains(ATTR_AVATAR_URL)) {
            builder.addAttribute(ATTR_AVATAR_URL, toString(info.getAvatarURL()));
        }
        // Box SDK for Java can't handle them currently
//        if (attributesToGet.contains(ATTR_NOTIFICATION_EMAIL_EMAIL)) {
//        }
//        if (attributesToGet.contains(ATTR_NOTIFICATION_EMAIL_ISCONFIRMED)) {
//        }

        // Full
        if (attributesToGet.contains(ATTR_CAN_SEE_MANAGED_USERS)) {
            builder.addAttribute(ATTR_CAN_SEE_MANAGED_USERS, info.getCanSeeManagedUsers());
        }
        if (attributesToGet.contains(ATTR_ENTERPRISE)) {
            builder.addAttribute(ATTR_ENTERPRISE_ID, toString(info.getEnterprise().getID()));
        }
        if (attributesToGet.contains(ATTR_ENTERPRISE)) {
            builder.addAttribute(ATTR_ENTERPRISE_NAME, toString(info.getEnterprise().getName()));
        }
        if (attributesToGet.contains(ATTR_EXTERNAL_APP_USER_ID)) {
            builder.addAttribute(ATTR_EXTERNAL_APP_USER_ID, toString(info.getExternalAppUserId()));
        }
        if (attributesToGet.contains(ATTR_HOSTNAME)) {
            builder.addAttribute(ATTR_HOSTNAME, toString(info.getHostname()));
        }
        if (attributesToGet.contains(ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS)) {
            builder.addAttribute(ATTR_IS_EXEMPT_FROM_DEVICE_LIMITS, info.getIsExemptFromDeviceLimits());
        }
        if (attributesToGet.contains(ATTR_IS_EXEMPT_FROM_LOGIN_VERIFICATION)) {
            builder.addAttribute(ATTR_IS_EXEMPT_FROM_LOGIN_VERIFICATION, info.getIsExemptFromLoginVerification());
        }
        if (attributesToGet.contains(ATTR_IS_EXEMPT_COLLAB_RESTRICTED)) {
            builder.addAttribute(ATTR_IS_EXEMPT_COLLAB_RESTRICTED, info.getIsExternalCollabRestricted());
        }
        if (attributesToGet.contains(ATTR_IS_PLATFORM_ACCESS_ONLY)) {
            builder.addAttribute(ATTR_IS_PLATFORM_ACCESS_ONLY, info.getIsPlatformAccessOnly());
        }
        if (attributesToGet.contains(ATTR_IS_SYNC_ENABLED)) {
            builder.addAttribute(ATTR_IS_SYNC_ENABLED, info.getIsSyncEnabled());
        }
        if (attributesToGet.contains(ATTR_MY_TAGS)) {
            builder.addAttribute(ATTR_MY_TAGS, info.getMyTags());
        }
        if (attributesToGet.contains(ATTR_ROLE)) {
            builder.addAttribute(ATTR_ROLE, toString(info.getRole()));
        }
        if (attributesToGet.contains(ATTR_TRACKING_CODES)) {
            builder.addAttribute(ATTR_TRACKING_CODES, toString(info.getTrackingCodes()));
        }

        // __ENABLE__
        if (info.getStatus().equals(BoxUser.Status.ACTIVE)) {
            builder.addAttribute(OperationalAttributes.ENABLE_NAME, Boolean.TRUE);
        } else if (info.getStatus().equals(BoxUser.Status.INACTIVE)) {
            builder.addAttribute(OperationalAttributes.ENABLE_NAME, Boolean.FALSE);
        }

        // Association
        if (attributesToGet.contains(ATTR_GROUP_MEMBERSHIP)) {
            // Fetch groups
            Iterable<BoxGroupMembership.Info> memberships = info.getResource().getAllMemberships();
            List<String> groupMemberships = new ArrayList<>();
            for (BoxGroupMembership.Info membershipInfo : memberships) {
                LOGGER.info("[{0}] Group INFO getID {1}", instanceName, membershipInfo.getGroup().getID());
                groupMemberships.add(membershipInfo.getGroup().getID());
            }
            builder.addAttribute(ATTR_GROUP_MEMBERSHIP, groupMemberships);
        }

        ConnectorObject connectorObject = builder.build();
        return connectorObject;
    }

    private String toString(BoxUser.Status status) {
        switch (status) {
            case ACTIVE:
                return "active";
            case INACTIVE:
                return "inactive";
            case CANNOT_DELETE_EDIT:
                return "cannot_delete_edit";
            case CANNOT_DELETE_EDIT_UPLOAD:
                return "cannot_delete_edit_upload";
        }
        throw new InvalidAttributeValueException("Unknown status: " + status);
    }

    private List<String> toString(Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private String toString(BoxUser.Role role) {
        switch (role) {
            case USER:
                return "user";
            case ADMIN:
                return "admin";
            case COADMIN:
                return "coadmin";
        }
        throw new InvalidAttributeValueException("Unknown role: " + role);
    }
}
