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
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.InstanceNameAware;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Set;

import static com.exclamationlabs.connid.box.GroupsHandler.OBJECT_CLASS_GROUP;
import static com.exclamationlabs.connid.box.UsersHandler.OBJECT_CLASS_USER;


@ConnectorClass(configurationClass = BoxConfiguration.class, displayNameKey = "Exclamation Labs Box Connector")
public class BoxConnector implements PoolableConnector,
        CreateOp, UpdateDeltaOp, DeleteOp, SchemaOp, TestOp, SearchOp<BoxFilter>, InstanceNameAware {

    private static final Log LOGGER = Log.getLog(BoxConnector.class);

    private BoxConfiguration configuration;
    protected BoxAPIConnection boxAPI;
    private BoxConfig boxConfig;
    private String instanceName;

    @Override
    public BoxConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void init(final Configuration configuration) {
        this.configuration = (BoxConfiguration) configuration;
        this.boxConfig = null;

        try {
            authenticateResource();
        } catch (RuntimeException e) {
            throw processRuntimeException(e);
        }

        LOGGER.ok("Connector {0} successfully initialized", getClass().getName());
    }

    @Override
    public void setInstanceName(String instanceName) {
        // Called after initialized
        this.instanceName = instanceName;
    }

    protected void authenticateResource() {
        BoxConfiguration config = getConfiguration();

        try (Reader reader = resolveConfigReader(config)) {
            boxConfig = BoxConfig.readFrom(reader);
        } catch (IOException e) {
            LOGGER.error(e, "[{0}] Error loading Box JWT Auth Config File", instanceName);
        }

        final BoxDeveloperEditionAPIConnection boxDeveloperEditionAPIConnection;
        try {
            if (StringUtil.isEmpty(getConfiguration().getHttpProxyHost())) {
                boxDeveloperEditionAPIConnection = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
            } else {
                // Use HTTP Proxy for Box connection
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getHttpProxyHost(),
                        config.getHttpProxyPort()));

                boxDeveloperEditionAPIConnection = new BoxDeveloperEditionAPIConnection(boxConfig.getEnterpriseId(), DeveloperEditionEntityType.ENTERPRISE,
                        boxConfig.getClientId(), boxConfig.getClientSecret(), boxConfig.getJWTEncryptionPreferences());

                if (StringUtil.isNotEmpty(config.getHttpProxyUser())) {
                    boxDeveloperEditionAPIConnection.setProxyUsername(config.getHttpProxyUser());

                    if (config.getHttpProxyPassword() != null) {
                        config.getHttpProxyPassword().access(new GuardedString.Accessor() {
                            @Override
                            public void access(char[] chars) {
                                boxDeveloperEditionAPIConnection.setProxyPassword(String.valueOf(chars));
                            }
                        });
                    }
                } else {
                    boxDeveloperEditionAPIConnection.setProxy(proxy);
                }
            }
        } catch (Exception e) {
            throw new ConnectorIOException("Failed to connect", e);
        }

        boxDeveloperEditionAPIConnection.authenticate();

        this.boxAPI = boxDeveloperEditionAPIConnection;
    }

    private Reader resolveConfigReader(BoxConfiguration config) throws FileNotFoundException {
        if (StringUtil.isNotBlank(config.getConfigFilePath())) {
            return new FileReader(config.getConfigFilePath());

        } else if (config.getConfigJson() != null) {
            final StringReader[] configReader = new StringReader[1];
            config.getConfigJson().access(c -> {
                configReader[0] = new StringReader(String.valueOf(c));
            });
            return configReader[0];
        }
        throw new ConfigurationException("configFilePath or configJson must not be empty");
    }

    @Override
    public void dispose() {
        this.boxAPI = null;
    }

    @Override
    public Uid create(
            final ObjectClass objectClass,
            final Set<Attribute> createAttributes,
            final OperationOptions options) {

        if (objectClass == null) {
            throw new InvalidAttributeValueException("ObjectClass value not provided");
        }
        LOGGER.info("[{0}] CREATE METHOD OBJECTCLASS VALUE: {1}", instanceName, objectClass);

        if (createAttributes == null) {
            throw new InvalidAttributeValueException("Attributes not provided or empty");
        }

        try {
            if (objectClass.equals(OBJECT_CLASS_USER)) {
                UsersHandler usersHandler = new UsersHandler(instanceName, boxAPI, configuration);
                return usersHandler.createUser(createAttributes);

            } else if (objectClass.equals(OBJECT_CLASS_GROUP)) {
                GroupsHandler groupsHandler = new GroupsHandler(instanceName, boxAPI);
                return groupsHandler.createGroup(createAttributes);
            }
        } catch (RuntimeException e) {
            throw processRuntimeException(e);
        }

        throw new InvalidAttributeValueException("Unsupported object class " + objectClass);
    }

    @Override
    public Set<AttributeDelta> updateDelta(
            final ObjectClass objectClass,
            final Uid uid, Set<AttributeDelta> modifications,
            final OperationOptions options) {

        if (objectClass == null) {
            throw new InvalidAttributeValueException("ObjectClass value not provided");
        }
        LOGGER.info("[{0}] UPDATEDELTA METHOD OBJECTCLASS VALUE: {1}", instanceName, objectClass);

        if (modifications == null) {
            throw new InvalidAttributeValueException("modifications not provided or empty");
        }

        try {
            if (objectClass.equals(OBJECT_CLASS_USER)) {
                UsersHandler usersHandler = new UsersHandler(instanceName, boxAPI, configuration);
                return usersHandler.updateUser(uid, modifications);

            } else if (objectClass.equals(OBJECT_CLASS_GROUP)) {
                GroupsHandler groupsHandler = new GroupsHandler(instanceName, boxAPI);
                return groupsHandler.updateGroup(uid, modifications);
            }
        } catch (RuntimeException e) {
            throw processRuntimeException(e);
        }

        throw new InvalidAttributeValueException("Unsupported object class " + objectClass);
    }

    @Override
    public void delete(
            final ObjectClass objectClass,
            final Uid uid,
            final OperationOptions options) {

        try {
            if (objectClass.equals(OBJECT_CLASS_USER)) {
                UsersHandler usersHandler = new UsersHandler(instanceName, boxAPI, configuration);
                usersHandler.deleteUser(objectClass, uid, options);
                return;

            } else if (objectClass.equals(OBJECT_CLASS_GROUP)) {
                GroupsHandler groupsHandler = new GroupsHandler(instanceName, boxAPI);
                groupsHandler.deleteGroup(uid);
                return;
            }
        } catch (RuntimeException e) {
            throw processRuntimeException(e);
        }

        throw new UnsupportedOperationException("Unsupported object class " + objectClass);
    }

    @Override
    public Schema schema() {
        SchemaBuilder schemaBuilder = new SchemaBuilder(BoxConnector.class);

        UsersHandler usersHandler = new UsersHandler(instanceName, boxAPI, configuration);
        ObjectClassInfo userSchemaInfo = usersHandler.getUserSchema();
        schemaBuilder.defineObjectClass(userSchemaInfo);

        GroupsHandler group = new GroupsHandler(instanceName, boxAPI);
        ObjectClassInfo groupSchemaInfo = group.getGroupSchema();
        schemaBuilder.defineObjectClass(groupSchemaInfo);

        schemaBuilder.defineOperationOption(OperationOptionInfoBuilder.buildAttributesToGet(), SearchOp.class);
        schemaBuilder.defineOperationOption(OperationOptionInfoBuilder.buildReturnDefaultAttributes(), SearchOp.class);

        return schemaBuilder.build();
    }

    @Override
    public void test() {
        dispose();

        try {
            authenticateResource();

            if (!boxAPI.canRefresh()) {
                throw new ConnectorIOException("Cannot refresh auth token");
            }

            boxAPI.refresh();
        } catch (RuntimeException e) {
            throw processRuntimeException(e);
        }
    }

    @Override
    public void checkAlive() {
        try {
            if (this.boxAPI.needsRefresh()) {
                this.boxAPI.refresh();
            }
        } catch (RuntimeException e) {
            throw processRuntimeException(e);
        }
    }

    @Override
    public FilterTranslator<BoxFilter> createFilterTranslator(
            final ObjectClass objectClass,
            final OperationOptions options) {

        return new BoxFilterTranslator();
    }

    @Override
    public void executeQuery(
            final ObjectClass objectClass,
            final BoxFilter filter,
            final ResultsHandler handler,
            final OperationOptions options) {

        if (objectClass == null) {
            throw new InvalidAttributeValueException("ObjectClass value not provided");
        }

        LOGGER.info("[{0}] EXECUTE_QUERY METHOD OBJECTCLASS VALUE: {1}", instanceName, objectClass);

        try {
            if (objectClass.equals(OBJECT_CLASS_USER)) {
                UsersHandler usersHandler = new UsersHandler(instanceName, boxAPI, configuration);
                usersHandler.query(filter, handler, options);
                return;

            } else if (objectClass.equals(OBJECT_CLASS_GROUP)) {
                GroupsHandler groupsHandler = new GroupsHandler(instanceName, boxAPI);
                groupsHandler.query(filter, handler, options);
                return;
            }
        } catch (RuntimeException e) {
            throw processRuntimeException(e);
        }

        throw new InvalidAttributeValueException("Unsupported object class " + objectClass);
    }

    protected ConnectorException processRuntimeException(RuntimeException e) {
        if (e instanceof ConnectorException) {
            return (ConnectorException) e;
        }
        if (e instanceof BoxAPIResponseException) {
            return processBoxAPIResponseException((BoxAPIResponseException) e);

        } else if (e instanceof BoxAPIException) {
            return new ConnectorIOException(e);
        }
        return new ConnectorException(e);
    }

    private ConnectorException processBoxAPIResponseException(BoxAPIResponseException e) {
        // https://developer.box.com/guides/api-calls/permissions-and-errors/common-errors/

        switch (e.getResponseCode()) {
            case 400:
                return new InvalidAttributeValueException(e);
            case 401:
                return new ConnectorSecurityException(e);
            case 403:
                return new PermissionDeniedException(e);
            case 404:
                return new UnknownUidException(e);
            case 405:
                return new InvalidAttributeValueException(e);
            case 409:
                return new AlreadyExistsException(e);
            case 410:
                return new ConnectorSecurityException(e);
            case 411:
                return new InvalidAttributeValueException(e);
            case 412:
                return RetryableException.wrap(e.getMessage(), e);
            case 413:
                return new InvalidAttributeValueException(e);
            case 415:
                return new InvalidAttributeValueException(e);
            case 429:
                return RetryableException.wrap(e.getMessage(), e);
            case 500:
                return RetryableException.wrap(e.getMessage(), e);
            case 502:
                return RetryableException.wrap(e.getMessage(), e);
            case 503:
                return RetryableException.wrap(e.getMessage(), e);
            default:
                return new ConnectorIOException(e);
        }
    }
}
