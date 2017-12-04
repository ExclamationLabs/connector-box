package com.exclamationlabs.connid;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.operations.ResolveUsernameApiOp;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.AuthenticateOp;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;

import org.identityconnectors.framework.spi.operations.UpdateAttributeValuesOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;


@ConnectorClass(configurationClass = BoxConfiguration.class, displayNameKey = "Exclamation Labs Box Connector")
public class BoxConnector implements Connector,
        CreateOp, UpdateOp, UpdateAttributeValuesOp, DeleteOp, SchemaOp, TestOp, SearchOp<String> {

    private static final Log LOG = Log.getLog(BoxConnector.class);

    private BoxConfiguration configuration;

    private BoxDeveloperEditionAPIConnection boxDeveloperEditionAPIConnection;

    private Schema schema;

    private BoxConfig boxConfig;

    @Override
    public BoxConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(final Configuration configuration) {
        this.configuration = (BoxConfiguration) configuration;
        this.boxConfig = null;
        authenticateResource();

        LOG.ok("Connector {0} successfully inited", getClass().getName());
    }

    private void authenticateResource() {
        String configFilePath = getConfiguration().getConfigFilePath();

        try(Reader reader = new FileReader(configFilePath)) {
            boxConfig = BoxConfig.readFrom(reader);
        } catch (IOException ex) {
            LOG.error("Error loading Box JWT Auth Config File", ex);
        }

        try {
            boxDeveloperEditionAPIConnection = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
        } catch (Exception e) {
            throw new ConnectorIOException("Failed to connect", e);
        }

    }

    @Override
    public void dispose() {
        this.boxDeveloperEditionAPIConnection = null;
    }

    @Override
    public Uid create(
            final ObjectClass objectClass,
            final Set<Attribute> createAttributes,
            final OperationOptions options) {

        if (objectClass == null) {
            throw new InvalidAttributeValueException("ObjectClass value not provided");
        }
        LOG.info("CREATE METHOD OBJECTCLASS VALUE: {0}", objectClass);

        if (createAttributes == null) {
            throw new InvalidAttributeValueException("Attributes not provided or empty");
        }

        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            UsersHandler usersHandler = new UsersHandler(boxDeveloperEditionAPIConnection);
            return usersHandler.createUser(createAttributes);

        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            GroupsHandler groupsHandler = new GroupsHandler(boxDeveloperEditionAPIConnection);
            return groupsHandler.createGroup(createAttributes);

        } else {
            throw new UnsupportedOperationException("Unsupported object class " + objectClass);
        }

    }

    @Override
    public Uid update(
            final ObjectClass objectClass,
            final Uid uid,
            final Set<Attribute> replaceAttributes,
            final OperationOptions options) {

        if (objectClass == null) {
            throw new InvalidAttributeValueException("ObjectClass value not provided");
        }
        LOG.info("UPDATE METHOD OBJECTCLASS VALUE: {0}", objectClass);

        if (replaceAttributes == null) {
            throw new InvalidAttributeValueException("Attributes not provided or empty");
        }

        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            UsersHandler usersHandler = new UsersHandler(boxDeveloperEditionAPIConnection);
            return usersHandler.updateUser(uid, replaceAttributes);

        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            GroupsHandler groupsHandler = new GroupsHandler(boxDeveloperEditionAPIConnection);
            return groupsHandler.updateGroup(replaceAttributes);
        }

        throw new UnsupportedOperationException("Unsupported object class " + objectClass);

    }

    @Override
    public Uid addAttributeValues(
            final ObjectClass objclass,
            final Uid uid,
            final Set<Attribute> valuesToAdd,
            final OperationOptions options) {
        return update(objclass, uid, valuesToAdd, options);
    }

    @Override
    public Uid removeAttributeValues(
            final ObjectClass objclass,
            final Uid uid,
            final Set<Attribute> valuesToRemove,
            final OperationOptions options) {
        return update(objclass, uid, valuesToRemove, options);
    }

    @Override
    public void delete(
            final ObjectClass objectClass,
            final Uid uid,
            final OperationOptions options) {


        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {
            UsersHandler usersHandler = new UsersHandler(boxDeveloperEditionAPIConnection);
            usersHandler.deleteUser(objectClass, uid, options);

        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            GroupsHandler groupsHandler = new GroupsHandler(boxDeveloperEditionAPIConnection);
            groupsHandler.deleteGroup(uid);

        } else {
            throw new UnsupportedOperationException("Unsupported object class " + objectClass);
        }

    }

    @Override
    public Schema schema() {
        if (null == schema) {
            SchemaBuilder schemaBuilder = new SchemaBuilder(BoxConnector.class);

            UsersHandler usersHandler = new UsersHandler(boxDeveloperEditionAPIConnection);
            ObjectClassInfo userSchemaInfo = usersHandler.getUserSchema();
            schemaBuilder.defineObjectClass(userSchemaInfo);

            GroupsHandler group = new GroupsHandler(boxDeveloperEditionAPIConnection);
            ObjectClassInfo groupSchemaInfo = group.getGroupSchema();
            schemaBuilder.defineObjectClass(groupSchemaInfo);

            return schemaBuilder.build();
        }
        return this.schema;
    }

    @Override
    public void test() {

        dispose();

        authenticateResource();

        if (!boxDeveloperEditionAPIConnection.canRefresh()) {
            throw new ConnectorIOException("Cannot refresh auth token");
        }

        boxDeveloperEditionAPIConnection.refresh();

    }

    @Override
    public FilterTranslator<String> createFilterTranslator(
            final ObjectClass objectClass,
            final OperationOptions options) {

        return new BoxFilterTranslator();
    }

    @Override
    public void executeQuery(
            final ObjectClass objectClass,
            final String query,
            final ResultsHandler handler,
            final OperationOptions options) {

        if (objectClass == null) {
            throw new InvalidAttributeValueException("ObjectClass value not provided");
        }

        LOG.info("EXECUTE_QUERY METHOD OBJECTCLASS VALUE: {0}", objectClass);


        if (objectClass.is(ObjectClass.ACCOUNT_NAME)) {

            UsersHandler usersHandler = new UsersHandler(boxDeveloperEditionAPIConnection);
            usersHandler.query(query, handler, options);


        } else if (objectClass.is(ObjectClass.GROUP_NAME)) {
            GroupsHandler groupsHandler = new GroupsHandler(boxDeveloperEditionAPIConnection);
            ArrayList<ConnectorObject> groups = groupsHandler.getAllGroups();

            for (ConnectorObject groupConnectorObject : groups) {
                handler.handle(groupConnectorObject);
            }

        } else {
            throw new UnsupportedOperationException("Unsupported object class " + objectClass);
        }




    }
}
