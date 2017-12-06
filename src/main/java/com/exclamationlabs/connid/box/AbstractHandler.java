package com.exclamationlabs.connid.box;

import com.box.sdk.CreateUserParams;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;

import java.util.List;
import java.util.Set;

public class AbstractHandler {

    public static String getStringAttr(Set<Attribute> attributes, String attrName) {
        return getAttr(attributes, attrName, String.class);
    }

    public static Boolean getBoolAttr(Set<Attribute> attributes, String attrName) {
        return getAttr(attributes, attrName, Boolean.class);
    }

    public static Integer getIntegerAttr(Set<Attribute> attributes, String attrName) {
        return getAttr(attributes, attrName, Integer.class);
    }

    public static Long getLongAttr(Set<Attribute> attributes, String attrName) {
        return getAttr(attributes, attrName, Long.class);
    }

    public static <T> T getAttr(Set<Attribute> attributes, String attrName, Class<T> type) {
        return getAttr(attributes, attrName, type, null);
    }

    public static <T> T getAttr(Set<Attribute> attributes, String attrName, Class<T> type, T defaultVal) {
        if (attributes == null || attributes.isEmpty()) {
            throw new InvalidAttributeValueException("Attributes not provided or empty");
        }
        if (attrName == null || attrName.isEmpty()) {
            throw new InvalidAttributeValueException("AttrName not provided or empty");
        }
        for (Attribute attr : attributes) {
            if (attrName.equals(attr.getName())) {

                List<Object> vals = attr.getValue();
                if (vals == null || vals.isEmpty()) {
                    return defaultVal;
                }
                if (vals.size() == 1) {

                    Object val = vals.get(0);
                    if (val == null) {
                        return defaultVal;
                    }
                    if (type.isAssignableFrom(val.getClass())) {
                        return (T) val;
                    }
                    throw new InvalidAttributeValueException(
                            "Unsupported type " + val.getClass() + " for attribute " + attrName);
                }
                throw new InvalidAttributeValueException("More than one value for attribute " + attrName);
            }
        }
        return defaultVal;
    }

    public static <T> List<T> getMultiAttr(Set<Attribute> attributes, String attrName, Class<T> listContentType) {
        if (attributes == null || attributes.isEmpty()) {
            throw new InvalidAttributeValueException("Attributes not provided or empty");
        }
        if (attrName == null || attrName.isEmpty()) {
            throw new InvalidAttributeValueException("AttrName not provided or empty");
        }
        for (Attribute attr : attributes) {
            if (attrName.equals(attr.getName())) {
                for(Object item : attr.getValue()){
                    if(!listContentType.isAssignableFrom(item.getClass())){
                        throw new InvalidAttributeValueException(
                            "Unsupported type " + item.getClass() + " for attribute " + attrName);
                    }
                }
                return (List<T>) attr.getValue();
            }
        }
        return null;
    }

    public static <T> void addAttr(ConnectorObjectBuilder builder, String attrName, T attrVal) {
        if (attrName == null || attrName.isEmpty()) {
            throw new InvalidAttributeValueException("AttrName not provided or empty");
        }
        if (attrVal == null) {
            throw new InvalidAttributeValueException("AttrName not provided or empty");
        }
        builder.addAttribute(attrName, attrVal);

    }

}
