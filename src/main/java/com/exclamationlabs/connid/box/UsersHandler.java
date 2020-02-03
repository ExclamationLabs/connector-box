/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxGroupMembership;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxUser;
import com.box.sdk.CreateUserParams;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.ArrayList;
import java.util.List;
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
        attrLoginBuilder.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrLoginBuilder.build());
        // role
        AttributeInfoBuilder attrRoleBuilder = new AttributeInfoBuilder(ATTR_ROLE);
        attrRoleBuilder.setUpdateable(true);
        attrRoleBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrRoleBuilder.build());
        // explicit_ID
        AttributeInfoBuilder attrUserId = new AttributeInfoBuilder(ATTR_ID);
        attrUserId.setUpdateable(false);
        attrUserId.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrUserId.build());
        // language
        AttributeInfoBuilder attrLanguageBuilder = new AttributeInfoBuilder(ATTR_LANGUAGE);
        attrLanguageBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrLanguageBuilder.build());
        // is_sync_enabled
        AttributeInfoBuilder attrIsSyncEnabledBuilder = new AttributeInfoBuilder(ATTR_SYNC, Boolean.class);
        attrIsSyncEnabledBuilder.setUpdateable(true);
        attrIsSyncEnabledBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrIsSyncEnabledBuilder.build());
        // job_titile
        AttributeInfoBuilder attrJobTitleBuilder = new AttributeInfoBuilder(ATTR_TITLE);
        attrJobTitleBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrJobTitleBuilder.build());
        // phone
        AttributeInfoBuilder attrPhoneBuilder = new AttributeInfoBuilder(ATTR_PHONE);
        attrPhoneBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrPhoneBuilder.build());
        // address
        AttributeInfoBuilder attrAddressBuilder = new AttributeInfoBuilder(ATTR_ADDRESS);
        attrAddressBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrAddressBuilder.build());
        // space_amount
        AttributeInfoBuilder attrSpaceAmountBuilder = new AttributeInfoBuilder(ATTR_SPACE, Long.class);
        attrSpaceAmountBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrSpaceAmountBuilder.build());
        // tracking_codes
        AttributeInfoBuilder attrTrackingCodeBuilder = new AttributeInfoBuilder(ATTR_CODE);
        attrTrackingCodeBuilder.setMultiValued(true);
        attrTrackingCodeBuilder.setUpdateable(true);
        attrTrackingCodeBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrTrackingCodeBuilder.build());
        // can_see_managed_users
        AttributeInfoBuilder attrCanSeeManagedUsersBuilder = new AttributeInfoBuilder(ATTR_MANAGED, Boolean.class);
        attrCanSeeManagedUsersBuilder.setUpdateable(true);
        attrCanSeeManagedUsersBuilder.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrCanSeeManagedUsersBuilder.build());
        // timezone
        AttributeInfoBuilder attrTimezoneBuilder = new AttributeInfoBuilder(ATTR_TIMEZONE);
        attrTimezoneBuilder.setUpdateable(true);
        ocBuilder.addAttributeInfo(attrTimezoneBuilder.build());
        // is_exempt_from_device_limits
        AttributeInfoBuilder attrIsExemptFromDeviceLimits = new AttributeInfoBuilder(ATTR_DEVICELIMITS, Boolean.class);
        attrIsExemptFromDeviceLimits.setUpdateable(true);
        attrIsExemptFromDeviceLimits.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrIsExemptFromDeviceLimits.build());
        // is_exempt_from_login_verification
        AttributeInfoBuilder attrIsExemptFromLoginVerification = new AttributeInfoBuilder(ATTR_LOGINVERIFICATION,
                Boolean.class);
        attrIsExemptFromLoginVerification.setUpdateable(true);
        attrIsExemptFromLoginVerification.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrIsExemptFromLoginVerification.build());
        // avatar
        AttributeInfoBuilder attrAvatar = new AttributeInfoBuilder(ATTR_AVATAR, String.class);
        attrAvatar.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrAvatar.build());
        // is_external_collab_restricted
        AttributeInfoBuilder attrCollab = new AttributeInfoBuilder(ATTR_COLLAB, Boolean.class);
        attrCollab.setUpdateable(true);
        attrCollab.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrCollab.build());
        // enterprise
        AttributeInfoBuilder attrEnterpise = new AttributeInfoBuilder(ATTR_ENTERPRISE);
        attrEnterpise.setUpdateable(true);
        attrEnterpise.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrEnterpise.build());
        // notify
        AttributeInfoBuilder attrNotify = new AttributeInfoBuilder(ATTR_NOTIFY, Boolean.class);
        attrNotify.setUpdateable(true);
        attrNotify.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrNotify.build());

        AttributeInfoBuilder attrCreated = new AttributeInfoBuilder(ATTR_CREATED);
        attrCreated.setUpdateable(false);
        attrCreated.setCreateable(false);
        ocBuilder.addAttributeInfo(attrCreated.build());

        AttributeInfoBuilder attrModified = new AttributeInfoBuilder(ATTR_MODIFIED);
        attrModified.setUpdateable(false);
        attrModified.setCreateable(false);
        ocBuilder.addAttributeInfo(attrModified.build());

        AttributeInfoBuilder attrUsed = new AttributeInfoBuilder(ATTR_USED, Long.class);
        attrUsed.setUpdateable(false);
        ocBuilder.addAttributeInfo(attrUsed.build());

        AttributeInfoBuilder attrPsswd = new AttributeInfoBuilder(ATTR_PSSWD, Boolean.class);
        attrPsswd.setCreateable(false);
        attrPsswd.setReturnedByDefault(false);
        ocBuilder.addAttributeInfo(attrPsswd.build());

        AttributeInfoBuilder attrMembership = new AttributeInfoBuilder(ATTR_MEMBERSHIPS);
        attrMembership.setMultiValued(true);
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
            BoxUser user = new BoxUser(boxDeveloperEditionAPIConnection, query);
            ConnectorObject userObject = userToConnectorObject(user);
            if(userObject != null){
                handler.handle(userObject);
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

        Long spaceAmount = getLongAttr(attributes, ATTR_SPACE);
        if (spaceAmount != null) {
            createUserParams.setSpaceAmount(spaceAmount);
        }

        //Administrative status
        if ((getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class)) != null) {
            Boolean status = getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class);
            if (status) {
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

        List<String> attrGroups = getMultiAttr(attributes, ATTR_MEMBERSHIPS, String.class);
        if(!attrGroups.isEmpty()){
            BoxUser user = new BoxUser(boxDeveloperEditionAPIConnection, createdUserInfo.getID());
            for (String group : attrGroups){
                BoxGroup boxGroup = new BoxGroup(boxDeveloperEditionAPIConnection, group);
                boxGroup.addMembership(user);
            }
        }

        return new Uid(createdUserInfo.getID());
    }

	public Uid updateUser(Uid uid, Set<Attribute> attributes) {
        BoxUser.Info info = null;
        BoxUser user = new BoxUser(boxDeveloperEditionAPIConnection, uid.getUidValue());
        info = user.getInfo();

        if (StringUtil.isEmpty(info.getID())) {
            throw new ConnectorIOException("Unable to confirm uid on box resource");
        }

        String name = getStringAttr(attributes, "__NAME__");
        if (name != null) {
            info.setName(name);
        }
        String attrAddress = getStringAttr(attributes, ATTR_ADDRESS);
        if (attrAddress != null) {
            info.setAddress(attrAddress);
        }
        Boolean attrManaged = getBoolAttr(attributes, ATTR_MANAGED);
        if (attrManaged != null) {
            info.setCanSeeManagedUsers(attrManaged);
        }
        String attrID = getStringAttr(attributes, ATTR_ID);
        if (attrID != null) {
            info.setExternalAppUserId(attrID);
        }
        Boolean attrDeviceLimits = getBoolAttr(attributes, ATTR_DEVICELIMITS);
        if (attrDeviceLimits != null) {
            info.setIsExemptFromDeviceLimits(attrDeviceLimits);
        }
        Boolean attrSync = getBoolAttr(attributes, ATTR_SYNC);
        if (attrSync != null) {
            info.setIsSyncEnabled(attrSync);
        }
        String attrTitle = getStringAttr(attributes, ATTR_TITLE);
        if (attrTitle != null) {
            info.setJobTitle(attrTitle);
        }
        String attrLanguage = getStringAttr(attributes, ATTR_LANGUAGE);
        if (attrLanguage != null) {
            info.setLanguage(attrLanguage);
        }
        String attrPhone = getStringAttr(attributes, ATTR_PHONE);
        if (attrPhone != null) {
            info.setPhone(attrPhone);
        }
        Long attrSpace = getLongAttr(attributes, ATTR_SPACE);
        if (attrSpace != null) {
            info.setSpaceAmount(attrSpace);
        }

        //Administrative status
        if ((getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class)) != null) {
            Boolean status = getAttr(attributes, OperationalAttributes.ENABLE_NAME, Boolean.class);
            if (status) {
                info.setStatus(BoxUser.Status.ACTIVE);
            } else {
                info.setStatus(BoxUser.Status.INACTIVE);
            }
        }

        String role = getAttr(attributes, ATTR_ROLE, String.class, "");
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

        info.getResource().updateInfo(info);

        List<String> attrGroups = getMultiAttr(attributes, ATTR_MEMBERSHIPS, String.class);
        if(!attrGroups.isEmpty()){
            attrGroups = new ArrayList<String>(attrGroups);
            Iterable<BoxGroupMembership.Info> memberships = user.getAllMemberships();
            for (BoxGroupMembership.Info membershipInfo : memberships){
                if(attrGroups.contains(membershipInfo.getGroup().getID())){
                    attrGroups.remove(membershipInfo.getGroup().getID());
                }else{
                    membershipInfo.getResource().delete();
                }
            }
            for (String group : attrGroups){
                BoxGroup boxGroup = new BoxGroup(boxDeveloperEditionAPIConnection, group);
                boxGroup.addMembership(user);
            }
        }
        
        return uid;
    }


    public void deleteUser(ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
        if (uid == null) {
            throw new InvalidAttributeValueException("uid not provided");
        }

        BoxUser user = new BoxUser(boxDeveloperEditionAPIConnection,uid.getUidValue());
        user.delete(false, false);
    }

    public static ConnectorObject userToConnectorObject(BoxUser user) {
        if (user == null) {
            throw new InvalidAttributeValueException("BoxUser Object not provided");
        }

        BoxUser.Info info;

        try {
            info = user.getInfo();
        } catch (BoxAPIException e) {
            LOGGER.error("Unknown uid: {0}", user.getID());
            return null;
        }

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
        List<String> groupMemberships = new ArrayList<String>();
        for (BoxGroupMembership.Info membershipInfo : memberships) {
            LOGGER.info("Group INFO getID {0}", membershipInfo.getGroup().getID());
            groupMemberships.add(membershipInfo.getGroup().getID());
        }
        builder.addAttribute(ATTR_MEMBERSHIPS, groupMemberships);


        ConnectorObject connectorObject = builder.build();
        return connectorObject;
    }

}
