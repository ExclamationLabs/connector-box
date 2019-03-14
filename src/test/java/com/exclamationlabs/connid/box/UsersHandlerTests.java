/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import com.box.sdk.*;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UsersHandlerTests {

    private static final Log LOG = Log.getLog(UsersHandlerTests.class);

    private static BoxDeveloperEditionAPIConnection boxAPIConnection = null;

    private static BoxConfig boxConfig = null;



    @Before
    public void setup() {

        try(Reader reader = new FileReader("test-config.json")) {
            boxConfig = BoxConfig.readFrom(reader);
        } catch (IOException ex) {
            LOG.error("Error loading test credentials", ex);
        }

        assertNotNull("Error loading test credentials; boxConfig was null", boxConfig);

        boxAPIConnection = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);

        assertNotNull(boxAPIConnection);
    }


    private BoxUser.Info getTestUser() {
        assertNotNull(boxAPIConnection);

        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxAPIConnection);

        for (BoxUser.Info user : users) {
            if (user.getLogin().equals("test_user@testmail.com")) {
                return user;
            }

        }

        return null;

    }

    private BoxUser.Info createTestUser() {
        assertNotNull(boxAPIConnection);

        CreateUserParams params = new CreateUserParams();
        params.setExternalAppUserId("test_user@testmail.com");

        BoxUser.Info createdUserInfo = BoxUser.createAppUser(boxAPIConnection, "test_user", params);
        return createdUserInfo;
    }

    private void deleteTestUser() {
        assertNotNull(boxAPIConnection);

        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxAPIConnection);

        for (BoxUser.Info user : users) {
            if (user.getLogin().equals("test_user@testmail.com")) {
                user.getResource().delete(false, false);
            }

        }
    }

    private Set<Attribute> getFixtureAccountAttributes() {
        BoxConnector boxConnector = new BoxConnector();

        Schema schema = boxConnector.schema();
        Set<Attribute> accountAttributes = new HashSet<Attribute>();
        Set<AttributeInfo> accountAttributesInfo = schema.findObjectClassInfo(ObjectClass.ACCOUNT_NAME)
                .getAttributeInfo();

        for (AttributeInfo attributeInfo : accountAttributesInfo) {
            if (!attributeInfo.isMultiValued() && attributeInfo.isCreateable() && attributeInfo.isReadable()) {
                if (attributeInfo.getName().equals("login")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "test_user@testmail.com"));
                } else if (attributeInfo.getName().equals("timezone")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "Europe/Bratislava"));
                } else if (attributeInfo.getName().equals("language")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "en"));
                } else if (attributeInfo.getName().equals("role")) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "coadmin"));
                } else if (attributeInfo.getType().equals(String.class)) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), "test_user"));
                } else if (attributeInfo.getType().equals(Boolean.class)) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), true));
                } else if (attributeInfo.getType().equals(Integer.class)) {
                    accountAttributes.add(AttributeBuilder.build(attributeInfo.getName(), 0));
                }
                accountAttributes.add(AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, false));

            }

        }

        return accountAttributes;
    }

    @Test
    public void create(){
        UsersHandler usersHandler = new UsersHandler(boxAPIConnection);
        usersHandler.createUser(getFixtureAccountAttributes());

        assertNotNull(getTestUser());

        deleteTestUser();
    }

    @Test
    public void delete() {

        BoxUser.Info userInfo = createTestUser();

        UsersHandler usersHandler = new UsersHandler(boxAPIConnection);
        usersHandler.deleteUser(
                new ObjectClass("__ACCOUNT__"),
                new Uid(userInfo.getID()),
                null
        );

        assertNull(getTestUser());

    }


}
