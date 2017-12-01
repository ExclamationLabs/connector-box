package com.exclamationlabs.connid;

import com.box.sdk.*;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.ArrayList;
import java.util.Set;

public class GroupsHandler extends AbstractHandler {
    private static final Log LOGGER = Log.getLog(GroupsHandler.class);

    private static final String ATTR_NAME = "name";
    private static final String ATTR_ID = "groupID";
    private static final String ATTR_PROVENANCE = "provenance";
    private static final String ATTR_IDENTIFIER = "external_sync_identifier";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_INVITABILITY = "invitability_level";
    private static final String ATTR_VIEWABILITY = "member_viewability_level";
    private static final String ATTR_CREATED = "created_at";
    private static final String ATTR_MODIFIED = "modified_at";
    private static final String ATTR_SYNC = "is_sync_enabled";
    private static final String ATTR_MEMBERS = "member";
    private static final String ATTR_ADMINS = "admin";
    private static final String ATTR_CO_OWNER = "co_owner";
    private static final String ATTR_EDITOR = "editor";
    private static final String ATTR_PREVIEWER = "previewer";
    private static final String ATTR_PREVIEWER_UPLOADER = "previewer_uploader";
    private static final String ATTR_UPLOADER = "uploader";
    private static final String ATTR_VIEWER = "viewer";
    private static final String ATTR_VIEWER_UPLOADER = "viewer_uploader";

    private BoxDeveloperEditionAPIConnection boxDeveloperEditionAPIConnection;

    public GroupsHandler(BoxDeveloperEditionAPIConnection boxDeveloperEditionAPIConnection) {
        this.boxDeveloperEditionAPIConnection = boxDeveloperEditionAPIConnection;
    }


    public ObjectClassInfo getGroupSchema() {

        ObjectClassInfoBuilder builder = new ObjectClassInfoBuilder();

        builder.setType(ObjectClass.GROUP_NAME);

        AttributeInfoBuilder attrOwner = new AttributeInfoBuilder(ATTR_CO_OWNER);
        attrOwner.setMultiValued(true);
        attrOwner.setUpdateable(true);
        builder.addAttributeInfo(attrOwner.build());

        AttributeInfoBuilder attrEditor = new AttributeInfoBuilder(ATTR_EDITOR);
        attrEditor.setMultiValued(true);
        attrEditor.setUpdateable(true);
        builder.addAttributeInfo(attrEditor.build());

        AttributeInfoBuilder attrPreviewer = new AttributeInfoBuilder(ATTR_PREVIEWER);
        attrPreviewer.setMultiValued(true);
        attrPreviewer.setUpdateable(true);
        attrPreviewer.setReturnedByDefault(false);
        builder.addAttributeInfo(attrPreviewer.build());

        AttributeInfoBuilder attrPrevUpl = new AttributeInfoBuilder(ATTR_PREVIEWER_UPLOADER);
        attrPrevUpl.setMultiValued(true);
        attrPrevUpl.setUpdateable(true);
        attrPrevUpl.setReturnedByDefault(false);
        builder.addAttributeInfo(attrPrevUpl.build());

        AttributeInfoBuilder attrUploader = new AttributeInfoBuilder(ATTR_UPLOADER);
        attrUploader.setMultiValued(true);
        attrUploader.setUpdateable(true);
        attrUploader.setReturnedByDefault(false);
        builder.addAttributeInfo(attrUploader.build());

        AttributeInfoBuilder attrViewer = new AttributeInfoBuilder(ATTR_VIEWER);
        attrViewer.setMultiValued(true);
        attrViewer.setUpdateable(true);
        attrViewer.setReturnedByDefault(false);
        builder.addAttributeInfo(attrViewer.build());

        AttributeInfoBuilder attrViewUpl = new AttributeInfoBuilder(ATTR_VIEWER_UPLOADER);
        attrViewUpl.setMultiValued(true);
        attrViewUpl.setUpdateable(true);
        attrViewUpl.setReturnedByDefault(false);
        builder.addAttributeInfo(attrViewUpl.build());

        AttributeInfoBuilder attrProvenanceBuilder = new AttributeInfoBuilder(ATTR_PROVENANCE);
        attrProvenanceBuilder.setUpdateable(true);
        attrProvenanceBuilder.setReturnedByDefault(false);
        builder.addAttributeInfo(attrProvenanceBuilder.build());

        AttributeInfoBuilder attrGroupId = new AttributeInfoBuilder(ATTR_ID);
        attrGroupId.setUpdateable(false);
        builder.addAttributeInfo(attrGroupId.build());

        AttributeInfoBuilder attrMembers = new AttributeInfoBuilder(ATTR_MEMBERS);
        attrMembers.setMultiValued(true);
        attrMembers.setUpdateable(true);
        builder.addAttributeInfo(attrMembers.build());

        AttributeInfoBuilder attrAdmins = new AttributeInfoBuilder(ATTR_ADMINS);
        attrAdmins.setMultiValued(true);
        attrAdmins.setUpdateable(true);
        builder.addAttributeInfo(attrAdmins.build());

        AttributeInfoBuilder attrIdentifierBuilder = new AttributeInfoBuilder(ATTR_IDENTIFIER);
        attrIdentifierBuilder.setUpdateable(true);
        attrIdentifierBuilder.setReturnedByDefault(false);
        builder.addAttributeInfo(attrIdentifierBuilder.build());

        AttributeInfoBuilder attrDescriptionBuilder = new AttributeInfoBuilder(ATTR_DESCRIPTION);
        attrDescriptionBuilder.setUpdateable(true);
        attrDescriptionBuilder.setReturnedByDefault(false);
        builder.addAttributeInfo(attrDescriptionBuilder.build());

        AttributeInfoBuilder attrInvitabilityBuilder = new AttributeInfoBuilder(ATTR_INVITABILITY);
        attrInvitabilityBuilder.setUpdateable(true);
        attrInvitabilityBuilder.setReturnedByDefault(false);
        builder.addAttributeInfo(attrInvitabilityBuilder.build());

        AttributeInfoBuilder attrViewabilityBuilder = new AttributeInfoBuilder(ATTR_VIEWABILITY);
        attrViewabilityBuilder.setUpdateable(true);
        attrViewabilityBuilder.setReturnedByDefault(false);
        builder.addAttributeInfo(attrViewabilityBuilder.build());
        
        AttributeInfoBuilder attrIsSyncEnabledBuilder = new AttributeInfoBuilder(ATTR_SYNC, Boolean.class);
        attrIsSyncEnabledBuilder.setUpdateable(true);
        attrIsSyncEnabledBuilder.setReturnedByDefault(false);
        builder.addAttributeInfo(attrIsSyncEnabledBuilder.build());

        AttributeInfoBuilder attrCreated = new AttributeInfoBuilder(ATTR_CREATED);
        attrCreated.setUpdateable(false);
        builder.addAttributeInfo(attrCreated.build());

        AttributeInfoBuilder attrModified = new AttributeInfoBuilder(ATTR_MODIFIED);
        attrModified.setUpdateable(false);
        builder.addAttributeInfo(attrModified.build());

        ObjectClassInfo groupSchemaInfo = builder.build();
        LOGGER.info("The constructed group schema representation: {0}", groupSchemaInfo);
        return groupSchemaInfo;

    }


    public Uid updateGroup(Set<Attribute> attributes) {

        if (attributes == null || attributes.isEmpty()) {
            throw new InvalidAttributeValueException("attributes not provided or empty");
        }

        String name = getStringAttr(attributes, "__NAME__");
        if (StringUtil.isBlank(name)) {
            throw new InvalidAttributeValueException("Missing mandatory attribute " + ATTR_NAME);
        }


        BoxGroup.Info groupInfo = BoxGroup.createGroup(
                boxDeveloperEditionAPIConnection,
                name,
                getStringAttr(attributes, ATTR_PROVENANCE),
                getStringAttr(attributes, ATTR_IDENTIFIER),
                getStringAttr(attributes, ATTR_DESCRIPTION),
                getStringAttr(attributes, ATTR_INVITABILITY),
                getStringAttr(attributes, ATTR_VIEWABILITY)
        );

        return new Uid(groupInfo.getID());
    }

    public ArrayList<ConnectorObject> getAllGroups() {
        ArrayList<ConnectorObject> connectorObjects = new ArrayList<>();
        Iterable<BoxGroup.Info> groups = BoxGroup.getAllGroups(boxDeveloperEditionAPIConnection);
        for (BoxGroup.Info groupInfo : groups) {
            connectorObjects.add(groupToConnectorObject(groupInfo.getResource()));
        }

        return connectorObjects;
    }

    public Uid createGroup(Set<Attribute> attributes) {
        return updateGroup(attributes);
    }

    public void deleteGroup(Uid uid)  {
        BoxGroup group = new BoxGroup(boxDeveloperEditionAPIConnection, uid.toString());
        group.delete();
    }

    public ConnectorObject groupToConnectorObject(BoxGroup group) {
        if (group == null) {
            throw new InvalidAttributeValueException("BoxGroup Object not provided");
        }

        BoxGroup.Info info = group.getInfo();

        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        builder.setUid(new Uid(group.getID()));

        builder.setName(info.getName());
        builder.addAttribute(ATTR_ID, info.getID());

        builder.addAttribute(ATTR_PROVENANCE, info.getProvenance());
        builder.addAttribute(ATTR_DESCRIPTION, info.getDescription());
        builder.addAttribute(ATTR_SYNC, info.getExternalSyncIdentifier());
        builder.addAttribute(ATTR_INVITABILITY, info.getInvitabilityLevel());
        builder.addAttribute(ATTR_VIEWABILITY, info.getMemberViewabilityLevel());
        builder.addAttribute(ATTR_CREATED, info.getCreatedAt().getTime());
        builder.addAttribute(ATTR_MODIFIED, info.getModifiedAt().getTime());

        Iterable<BoxGroupMembership.Info> memberships = group.getAllMemberships();
        for (BoxGroupMembership.Info membershipInfo : memberships) {
            if (membershipInfo.getRole().equals(BoxUser.Role.USER) ) {
                builder.addAttribute(ATTR_MEMBERS, membershipInfo.getID());
            } else if (membershipInfo.getRole().equals(BoxUser.Role.ADMIN) ) {
                builder.addAttribute(ATTR_ADMINS, membershipInfo.getID());

                //I don't know if this is right
            } else if (membershipInfo.getRole().equals(BoxUser.Role.COADMIN) ) {
                builder.addAttribute(ATTR_CO_OWNER, membershipInfo.getID());
            }

        }


        ConnectorObject connectorObject = builder.build();
        return connectorObject;
    }
}
