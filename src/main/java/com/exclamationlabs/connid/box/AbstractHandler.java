/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AbstractHandler {

    // Base
    protected static final String ATTR_ID = "id";
    protected static final String ATTR_TYPE = "type";

    protected static final String[] BASE_ATTRS = new String[]{
            ATTR_ID,
            ATTR_TYPE
    };

    protected final String instanceName;
    protected final BoxAPIConnection boxAPI;

    public AbstractHandler(String instanceName, BoxAPIConnection boxAPI) {
        this.instanceName = instanceName;
        this.boxAPI = boxAPI;
    }

    protected String getStringValue(Attribute attr) {
        return AttributeUtil.getStringValue(attr);
    }

    protected String getStringValue(AttributeDelta delta) {
        if (delta.getValuesToReplace().isEmpty()) {
            // To delete the attribute in Box side, we need to set "".
            return null;
        }
        return AttributeDeltaUtil.getStringValue(delta);
    }

    protected Boolean getBooleanValue(Attribute attr) {
        return AttributeUtil.getBooleanValue(attr);
    }

    protected Boolean getBooleanValue(AttributeDelta delta) {
        if (delta.getValuesToReplace().isEmpty()) {
            // To delete the attribute in Box side, we need to set false.
            return false;
        }
        return AttributeDeltaUtil.getBooleanValue(delta);
    }

    protected Long getLongValue(Attribute attr) {
        return AttributeUtil.getLongValue(attr);
    }

    protected Long getLongValue(AttributeDelta delta) {
        if (delta.getValuesToReplace().isEmpty()) {
            // To delete the attribute in Box side, we need to set 0.
            return Long.valueOf(0);
        }
        return AttributeDeltaUtil.getLongValue(delta);
    }

    protected List<String> getStringValuesToAdd(Attribute attr) {
        return attr.getValue().stream().map(v -> v.toString()).collect(Collectors.toList());
    }

    protected Set<String> getStringValuesToAdd(AttributeDelta delta) {
        List<Object> valuesToAdd = delta.getValuesToAdd();
        if (valuesToAdd == null) {
            return null;
        }
        return valuesToAdd.stream().map(v -> v.toString()).collect(Collectors.toSet());
    }

    protected Set<String> getStringValuesToRemove(AttributeDelta delta) {
        List<Object> valuesToRemove = delta.getValuesToRemove();
        if (valuesToRemove == null) {
            return null;
        }
        return valuesToRemove.stream().map(v -> v.toString()).collect(Collectors.toSet());
    }

    protected ZonedDateTime toZonedDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return ZonedDateTime.ofInstant(instant, zone);
    }

    protected String toString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return s;
    }

    protected static Set<String> createFullAttributesToGetSet(Set<String> standardAttributesSet, OperationOptions options) {
        Set<String> attributesToGet = new HashSet<>();
        if (shouldReturnDefaultAttributes(options)) {
            attributesToGet.addAll(standardAttributesSet);
        }
        if (options.getAttributesToGet() != null) {
            for (String a : options.getAttributesToGet()) {
                attributesToGet.add(a);
            }
        }
        return Collections.unmodifiableSet(attributesToGet.stream()
                .map(a -> a.split("\\.")[0])
                .collect(Collectors.toSet()));
    }

    protected static boolean shouldReturnDefaultAttributes(OperationOptions options) {
        return options.getReturnDefaultAttributes() == null || Boolean.TRUE.equals(options.getReturnDefaultAttributes());
    }

    protected boolean isUserAlreadyExistsError(BoxAPIException e) {
        if (e.getResponseCode() != 409) {
            return false;
        }
        String code = getErrorCode(e);
        return code.equals("user_login_already_used");
    }

    protected boolean isGroupAlreadyExistsError(BoxAPIException e) {
        if (e.getResponseCode() != 409) {
            return false;
        }
        String code = getErrorCode(e);
        return code.equals("conflict");
    }

    protected boolean isNotFoundError(BoxAPIException e) {
        if (e.getResponseCode() != 404) {
            return false;
        }
        String code = getErrorCode(e);
        return code.equals("not_found");
    }

    protected String getErrorCode(BoxAPIException e) {
        JsonObject response = JsonObject.readFrom(e.getResponse());
        JsonValue code = response.get("code");
        return code.asString();
    }

    protected UnknownUidException newUnknownUidException(Uid uid, ObjectClass objectClass, Exception e) {
        return new UnknownUidException(
                String.format("Object with Uid '%s' and ObjectClass '%s' does not exist!", uid, objectClass),
                e);
    }

    protected String[] toFetchFields(Set<String> attributesToGet, Set<String> excludes) {
        String[] fetchFields = attributesToGet.stream().filter(a -> !excludes.contains(a)).toArray(String[]::new);
        return fetchFields;
    }
}
