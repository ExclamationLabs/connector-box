package com.exclamationlabs.connid;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxGroupMembership;
import com.box.sdk.BoxUser;
import com.box.sdk.CreateUserParams;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.ArrayList;
import java.util.Set;

public class UsersHandler extends AbstractHandler {

    private static final Log LOGGER = Log.getLog(UsersHandler.class);

    private static final String ATTR_LOGIN = "login";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_ROLE = "role";
    private static final String ATTR_ID = "userID";
    private static final String ATTR_LANGUAGE = "language";
    private static final String ATTR_SYNC = "is_sync_enabled";
    private static final String ATTR_TITLE = "job_title";
    private static final String ATTR_PHONE = "phone";
    private static final String ATTR_ADDRESS = "address";
    private static final String ATTR_SPACE = "space_amount";
    private static final String ATTR_MANAGED = "can_see_managed_users";
    private static final String ATTR_TIMEZONE = "timezone";
    private static final String ATTR_DEVICELIMITS = "is_exempt_from_device_limits";
    private static final String ATTR_LOGINVERIFICATION = "is_exempt_from_login_verification";
    private static final String ATTR_COLLAB = "is_external_collab_restricted";
    private static final String ATTR_STATUS = "status";
    private static final String ATTR_AVATAR = "avatar_url";
    private static final String ATTR_ENTERPRISE = "enterprise";
    private static final String ATTR_NOTIFY = "notify";
    private static final String ATTR_CREATED = "created_at";
    private static final String ATTR_MODIFIED = "modified_at";
    private static final String ATTR_USED = "space_used";
    private static final String ATTR_PSSWD = "is_password_reset_required";
    private static final String ATTR_CODE = "tracking_codes";
    private static final String ATTR_MEMBERSHIPS = "group_membership";

    private BoxDeveloperEditionAPIConnection boxDeveloperEditionAPIConnection;

    public UsersHandler(BoxDeveloperEditionAPIConnection boxDeveloperEditionAPIConnection) {
        this.boxDeveloperEditionAPIConnection = boxDeveloperEditionAPIConnection;
    }

    public ObjectClassInfo getUserSchema() {

        ObjectClassInfoBuilder ocBuilder = new ObjectClassInfoBuilder();
        // mail
        AttributeInfoBuilder attrLoginBuilder = new AttributeInfoBuilder(ATTR_LOGIN);
        attrLoginBuilder.setRequired(true);
        attrLoginBuilder.setMultiValued(false);
        attrLoginBuilder.setCreateable(true);
        attrLoginBuilder.setReadable(true);
        attrLoginBuilder.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrLoginBuilder.build());
        // role
        AttributeInfoBuilder attrRoleBuilder = new AttributeInfoBuilder(ATTR_ROLE);
        attrRoleBuilder.setRequired(false);
        attrRoleBuilder.setMultiValued(false);
        attrRoleBuilder.setCreateable(true);
        attrRoleBuilder.setReadable(false);
        attrRoleBuilder.setUpdateable(true);
        attrRoleBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrRoleBuilder.build());
        // explicit_ID
        AttributeInfoBuilder attrUserId = new AttributeInfoBuilder(ATTR_ID);
        attrUserId.setRequired(false);
        attrUserId.setMultiValued(false);
        attrUserId.setCreateable(false);
        attrUserId.setReadable(true);
        attrUserId.setUpdateable(false);
        attrUserId.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrUserId.build());
        // language
        AttributeInfoBuilder attrLanguageBuilder = new AttributeInfoBuilder(ATTR_LANGUAGE);
        attrLanguageBuilder.setRequired(false);
        attrLanguageBuilder.setMultiValued(false);
        attrLanguageBuilder.setCreateable(true);
        attrLanguageBuilder.setReadable(true);
        attrLanguageBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrLanguageBuilder.build());
        // is_sync_enabled
        AttributeInfoBuilder attrIsSyncEnabledBuilder = new AttributeInfoBuilder(ATTR_SYNC, Boolean.class);
        attrIsSyncEnabledBuilder.setRequired(false);
        attrIsSyncEnabledBuilder.setMultiValued(false);
        attrIsSyncEnabledBuilder.setCreateable(true);
        attrIsSyncEnabledBuilder.setReadable(false);
        attrIsSyncEnabledBuilder.setUpdateable(true);
        attrIsSyncEnabledBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrIsSyncEnabledBuilder.build());
        // job_titile
        AttributeInfoBuilder attrJobTitleBuilder = new AttributeInfoBuilder(ATTR_TITLE);
        attrJobTitleBuilder.setRequired(false);
        attrJobTitleBuilder.setMultiValued(false);
        attrJobTitleBuilder.setCreateable(true);
        attrJobTitleBuilder.setReadable(true);
        attrJobTitleBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrJobTitleBuilder.build());
        // phone
        AttributeInfoBuilder attrPhoneBuilder = new AttributeInfoBuilder(ATTR_PHONE);
        attrPhoneBuilder.setRequired(false);
        attrPhoneBuilder.setMultiValued(false);
        attrPhoneBuilder.setCreateable(true);
        attrPhoneBuilder.setReadable(true);
        attrPhoneBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrPhoneBuilder.build());
        // address
        AttributeInfoBuilder attrAddressBuilder = new AttributeInfoBuilder(ATTR_ADDRESS);
        attrAddressBuilder.setRequired(false);
        attrAddressBuilder.setMultiValued(false);
        attrAddressBuilder.setCreateable(true);
        attrAddressBuilder.setReadable(true);
        attrAddressBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrAddressBuilder.build());
        // space_amount
        AttributeInfoBuilder attrSpaceAmountBuilder = new AttributeInfoBuilder(ATTR_SPACE, Integer.class);
        attrSpaceAmountBuilder.setRequired(false);
        attrSpaceAmountBuilder.setMultiValued(false);
        attrSpaceAmountBuilder.setCreateable(true);
        attrSpaceAmountBuilder.setReadable(true);
        attrSpaceAmountBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrSpaceAmountBuilder.build());
        // tracking_codes
        AttributeInfoBuilder attrTrackingCodeBuilder = new AttributeInfoBuilder(ATTR_CODE);
        attrTrackingCodeBuilder.setRequired(false);
        attrTrackingCodeBuilder.setMultiValued(true);
        attrTrackingCodeBuilder.setCreateable(true);
        attrTrackingCodeBuilder.setReadable(false);
        attrTrackingCodeBuilder.setUpdateable(true);
        attrTrackingCodeBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrTrackingCodeBuilder.build());
        // can_see_managed_users
        AttributeInfoBuilder attrCanSeeManagedUsersBuilder = new AttributeInfoBuilder(ATTR_MANAGED, Boolean.class);
        attrCanSeeManagedUsersBuilder.setRequired(false);
        attrCanSeeManagedUsersBuilder.setMultiValued(false);
        attrCanSeeManagedUsersBuilder.setCreateable(true);
        attrCanSeeManagedUsersBuilder.setReadable(false);
        attrCanSeeManagedUsersBuilder.setUpdateable(true);
        attrCanSeeManagedUsersBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrCanSeeManagedUsersBuilder.build());
        // timezone
        AttributeInfoBuilder attrTimezoneBuilder = new AttributeInfoBuilder(ATTR_TIMEZONE);
        attrTimezoneBuilder.setRequired(false);
        attrTimezoneBuilder.setMultiValued(false);
        attrTimezoneBuilder.setCreateable(true);
        attrTimezoneBuilder.setReadable(true);
        attrTimezoneBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrTimezoneBuilder.build());
        // is_exempt_from_device_limits
        AttributeInfoBuilder attrIsExemptFromDeviceLimits = new AttributeInfoBuilder(ATTR_DEVICELIMITS, Boolean.class);
        attrIsExemptFromDeviceLimits.setRequired(false);
        attrIsExemptFromDeviceLimits.setMultiValued(false);
        attrIsExemptFromDeviceLimits.setCreateable(true);
        attrIsExemptFromDeviceLimits.setReadable(false);
        attrIsExemptFromDeviceLimits.setUpdateable(true);
        attrIsExemptFromDeviceLimits.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrIsExemptFromDeviceLimits.build());
        // is_exempt_from_login_verification
        AttributeInfoBuilder attrIsExemptFromLoginVerification = new AttributeInfoBuilder(ATTR_LOGINVERIFICATION,
                Boolean.class);
        attrIsExemptFromLoginVerification.setRequired(false);
        attrIsExemptFromLoginVerification.setMultiValued(false);
        attrIsExemptFromLoginVerification.setCreateable(true);
        attrIsExemptFromLoginVerification.setReadable(false);
        attrIsExemptFromLoginVerification.setUpdateable(true);
        attrIsExemptFromLoginVerification.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrIsExemptFromLoginVerification.build());
        // avatar
        AttributeInfoBuilder attrAvatar = new AttributeInfoBuilder(ATTR_AVATAR, String.class);
        attrAvatar.setRequired(false);
        attrAvatar.setMultiValued(false);
        attrAvatar.setCreateable(false);
        attrAvatar.setReadable(true);
        attrAvatar.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrAvatar.build());
        // is_external_collab_restricted
        AttributeInfoBuilder attrCollab = new AttributeInfoBuilder(ATTR_COLLAB, Boolean.class);
        attrCollab.setRequired(false);
        attrCollab.setMultiValued(false);
        attrCollab.setCreateable(true);
        attrCollab.setReadable(false);
        attrCollab.setUpdateable(true);
        attrCollab.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrCollab.build());
        // enterprise
        AttributeInfoBuilder attrEnterpise = new AttributeInfoBuilder(ATTR_ENTERPRISE);
        attrEnterpise.setRequired(false);
        attrEnterpise.setMultiValued(false);
        attrEnterpise.setCreateable(false);
        attrEnterpise.setReadable(false);
        attrEnterpise.setUpdateable(true);
        attrEnterpise.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrEnterpise.build());
        // notify
        AttributeInfoBuilder attrNotify = new AttributeInfoBuilder(ATTR_NOTIFY, Boolean.class);
        attrNotify.setRequired(false);
        attrNotify.setMultiValued(false);
        attrNotify.setCreateable(false);
        attrNotify.setReadable(false);
        attrNotify.setUpdateable(true);
        attrNotify.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrNotify.build());

        AttributeInfoBuilder attrCreated = new AttributeInfoBuilder(ATTR_CREATED);
        attrCreated.setRequired(false);
        attrCreated.setMultiValued(false);
        attrCreated.setCreateable(false);
        attrCreated.setReadable(true);
        attrCreated.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrCreated.build());

        AttributeInfoBuilder attrModified = new AttributeInfoBuilder(ATTR_MODIFIED);
        attrModified.setRequired(false);
        attrModified.setMultiValued(false);
        attrModified.setCreateable(false);
        attrModified.setReadable(true);
        attrModified.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrModified.build());

        AttributeInfoBuilder attrUsed = new AttributeInfoBuilder(ATTR_USED, Integer.class);
        attrUsed.setRequired(false);
        attrUsed.setMultiValued(false);
        attrUsed.setCreateable(false);
        attrUsed.setReadable(true);
        attrUsed.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrUsed.build());

        AttributeInfoBuilder attrPsswd = new AttributeInfoBuilder(ATTR_PSSWD, Boolean.class);
        attrPsswd.setRequired(false);
        attrPsswd.setMultiValued(false);
        attrPsswd.setCreateable(false);
        attrPsswd.setReadable(false);
        attrPsswd.setUpdateable(true);
        attrPsswd.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrPsswd.build());

        AttributeInfoBuilder attrMembership = new AttributeInfoBuilder(ATTR_MEMBERSHIPS);
        attrMembership.setMultiValued(true);
        attrMembership.setRequired(false);
        attrMembership.setCreateable(true);
        attrMembership.setReadable(true);
        attrMembership.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrMembership.build());

        ObjectClassInfo userSchemaInfo = ocBuilder.build();
        LOGGER.info("The constructed User core schema: {0}", userSchemaInfo);
        return userSchemaInfo;
    }

    public ArrayList<ConnectorObject> getAllUsers() {
        ArrayList<ConnectorObject> connectorObjects = new ArrayList<>();
        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxDeveloperEditionAPIConnection);

        for (BoxUser.Info user : users) {
            connectorObjects.add(userToConnectorObject(user.getResource()));
        }

        return connectorObjects;
    }

    public void query(String query, ResultsHandler handler, OperationOptions ops) {

        LOGGER.info("UserHandler query VALUE: {0}", query);

        if (query == null) {

            ArrayList<ConnectorObject> users = getAllUsers();

            for (ConnectorObject userConnectorObject : users) {
                handler.handle(userConnectorObject);
            }
        } else {
            Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseOrExternalUsers(boxDeveloperEditionAPIConnection, query);
            for (BoxUser.Info user : users) {

                handler.handle(userToConnectorObject(user.getResource()));
            }
        }


    }

    public Uid createUser(Set<Attribute> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            throw new InvalidAttributeValueException("attributes not provided or empty");
        }

        CreateUserParams createUserParams = new CreateUserParams();

        String login = getStringAttr(attributes, ATTR_LOGIN);
        if (StringUtil.isBlank(login)) {
            throw new InvalidAttributeValueException("Missing mandatory attribute " + ATTR_LOGIN);
        }

        String name = getStringAttr(attributes, "__NAME__");
        if (StringUtil.isBlank(name)) {
            throw new InvalidAttributeValueException("Missing mandatory attribute " + ATTR_NAME);
        }

        String address = getStringAttr(attributes, ATTR_ADDRESS);
        if (address != null) {
            createUserParams.setAddress(address);
        }

        Boolean canSeeManagedUsers = getBoolAttr(attributes, ATTR_MANAGED);
        if (canSeeManagedUsers != null) {
            createUserParams.setCanSeeManagedUsers(canSeeManagedUsers);
        }

//      Let the username be generated
//        String externalAppUserId = getStringAttr(attributes, ATTR_ID);
//        if (externalAppUserId != null) {
//            createUserParams.setExternalAppUserId(externalAppUserId);
//        }

        Boolean isExemptFromDeviceLimits = getBoolAttr(attributes, ATTR_DEVICELIMITS);
        if (isExemptFromDeviceLimits != null) {
            createUserParams.setIsExemptFromDeviceLimits(isExemptFromDeviceLimits);
        }

        Boolean isSyncEnabled = getBoolAttr(attributes, ATTR_SYNC);
        if (isSyncEnabled != null) {
            createUserParams.setIsSyncEnabled(isSyncEnabled);
        }

        String jobTitle = getStringAttr(attributes, ATTR_TITLE);
        if (jobTitle != null) {
            createUserParams.setJobTitle(jobTitle);
        }

        String language = getStringAttr(attributes, ATTR_LANGUAGE);
        if (language != null) {
            createUserParams.setLanguage(language);
        }

        String phone = getStringAttr(attributes, ATTR_PHONE);
        if (phone != null) {
            createUserParams.setPhone(phone);
        }

        Integer spaceAmount = getIntegerAttr(attributes, ATTR_SPACE);
        if (spaceAmount != null) {
            createUserParams.setSpaceAmount(spaceAmount);
        }

        //Administrative status
        if ((getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class)) != null) {
            Boolean status = getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class);
            if (status != null) {
                createUserParams.setStatus(BoxUser.Status.ACTIVE);
            } else {
                createUserParams.setStatus(BoxUser.Status.INACTIVE);
            }
        }

        String role = getStringAttr(attributes, ATTR_ROLE);
        if (role != null) {
            switch (role) {
                case "admin":
                    createUserParams.setRole(BoxUser.Role.ADMIN);
                    break;
                case "coadmin":
                    createUserParams.setRole(BoxUser.Role.COADMIN);
                    break;
                case "user":
                    createUserParams.setRole(BoxUser.Role.USER);
                    break;
                default:
                    //If it's wrong, just default to regular user account
                    createUserParams.setRole(BoxUser.Role.USER);
            }
        }

        BoxUser.Info createdUserInfo = BoxUser.createEnterpriseUser(boxDeveloperEditionAPIConnection, login, name, createUserParams);

        return new Uid(createdUserInfo.getLogin());
    }



    public Uid updateUser(Uid uid, Set<Attribute> attributes) {
        BoxUser user = new BoxUser(boxDeveloperEditionAPIConnection, uid.toString());
        BoxUser.Info info = user.new Info();

        String login = getStringAttr(attributes, ATTR_LOGIN);
        if (StringUtil.isBlank(login)) {
            throw new InvalidAttributeValueException("Missing mandatory attribute " + ATTR_LOGIN);
        }

        String name = getStringAttr(attributes, "__NAME__");
        if (StringUtil.isBlank(name)) {
            throw new InvalidAttributeValueException("Missing mandatory attribute " + ATTR_NAME);
        }

        info.setAddress(getStringAttr(attributes, ATTR_ADDRESS));
        info.setCanSeeManagedUsers(getBoolAttr(attributes, ATTR_MANAGED));
        info.setExternalAppUserId(getStringAttr(attributes, ATTR_ID));
        info.setIsExemptFromDeviceLimits(getBoolAttr(attributes, ATTR_DEVICELIMITS));
        info.setIsSyncEnabled(getBoolAttr(attributes, ATTR_SYNC));
        info.setJobTitle(getStringAttr(attributes, ATTR_TITLE));
        info.setLanguage(getStringAttr(attributes, ATTR_LANGUAGE));
        info.setPhone(getStringAttr(attributes, ATTR_PHONE));
        info.setSpaceAmount(getIntegerAttr(attributes, ATTR_SPACE));

        //Administrative status
        if ((getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class)) != null) {
            Boolean status = getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class);
            if (status) {
                info.setStatus(BoxUser.Status.ACTIVE);
            } else {
                info.setStatus(BoxUser.Status.INACTIVE);
            }
        }

        String role = getStringAttr(attributes, ATTR_ROLE);
        switch (role) {
            case "admin":
                info.setRole(BoxUser.Role.ADMIN);
                break;
            case "coadmin":
                info.setRole(BoxUser.Role.COADMIN);
                break;
            case "user":
                info.setRole(BoxUser.Role.USER);
                break;
            default:
                //If it's wrong, just default to regular user account
                info.setRole(BoxUser.Role.USER);
        }

        user.updateInfo(info);

        return uid;
    }


    public void deleteUser(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        if (uid == null) {
            throw new InvalidAttributeValueException("uid not provided");
        }

        BoxUser user = new BoxUser(boxDeveloperEditionAPIConnection, uid.toString());
        user.delete(false, false);
    }

    public static ConnectorObject userToConnectorObject(BoxUser user) {
        if (user == null) {
            throw new InvalidAttributeValueException("BoxUser Object not provided");
        }

        BoxUser.Info info = user.getInfo();

        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setUid(new Uid(user.getID()));

        builder.setName(info.getName());
        builder.addAttribute(ATTR_ID, info.getID());
        builder.addAttribute(ATTR_LOGIN, info.getLogin());
        builder.addAttribute(ATTR_ADDRESS, info.getAddress());
        builder.addAttribute(ATTR_DEVICELIMITS, info.getIsExemptFromDeviceLimits());
        builder.addAttribute(ATTR_LANGUAGE, info.getLanguage());
        builder.addAttribute(ATTR_PHONE, info.getPhone());
        builder.addAttribute(ATTR_ROLE, info.getRole());
        builder.addAttribute(ATTR_SPACE, info.getSpaceAmount());
        builder.addAttribute(ATTR_TIMEZONE, info.getTimezone());
        builder.addAttribute(ATTR_TITLE, info.getJobTitle());
        builder.addAttribute(ATTR_AVATAR, info.getAvatarURL());
        builder.addAttribute(ATTR_CREATED, info.getCreatedAt().getTime());
        builder.addAttribute(ATTR_MODIFIED, info.getModifiedAt().getTime());
        builder.addAttribute(ATTR_USED, info.getSpaceUsed());


        if (info.getStatus().equals(BoxUser.Status.ACTIVE)) {
            addAttr(builder, OperationalAttributes.ENABLE_NAME, true);
        } else if (info.getStatus().equals(BoxUser.Status.INACTIVE)) {
            addAttr(builder, OperationalAttributes.ENABLE_NAME, false);
        }

        Iterable<BoxGroupMembership.Info> memberships = user.getAllMemberships();
        for (BoxGroupMembership.Info membershipInfo : memberships) {
            builder.addAttribute(ATTR_MEMBERSHIPS, membershipInfo.getID());
        }


        ConnectorObject connectorObject = builder.build();
        return connectorObject;
    }

}
