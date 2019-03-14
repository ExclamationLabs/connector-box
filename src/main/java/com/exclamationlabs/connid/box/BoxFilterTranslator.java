/*
 * Copyright (C) Exclamation Labs 2019. - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 *
 */

package com.exclamationlabs.connid.box;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.*;

/**
 * This is an implementation of AbstractFilterTranslator that gives a concrete representation
 * of which filters can be applied at the connector level (natively). If the
 * connector doesn't support a certain expression type, that factory
 * method should return null. This level of filtering is present only to allow any
 * native constructs that may be available to help reduce the result set for the framework,
 * which will (strictly) reapply all filters specified after the connector does the initial
 * filtering.<p><p>Note: The generic query type is most commonly a String, but does not have to be.
 *
 * @author Andrew Cope
 * @version $Revision$ $Date$
 */
public class BoxFilterTranslator extends AbstractFilterTranslator<String> {

    private static final Log LOG = Log.getLog(BoxFilterTranslator.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createAndExpression(String leftExpression, String rightExpression) {
        return super.createAndExpression(leftExpression, rightExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createOrExpression(String leftExpression, String rightExpression) {
        return super.createOrExpression(leftExpression, rightExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createContainsExpression(ContainsFilter filter, boolean not) {
        return super.createContainsExpression(filter, not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createEndsWithExpression(EndsWithFilter filter, boolean not) {
        return super.createEndsWithExpression(filter, not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createEqualsExpression(EqualsFilter filter, boolean not) {

        if (not) { // no way (natively) to search for "NotEquals"
            return null;
        }
        Attribute attr = filter.getAttribute();
        if (!attr.is(Name.NAME) && !attr.is(Uid.NAME)) {
            return null;
        }
        String name = attr.getName();
        String value = AttributeUtil.getAsStringValue(attr);
        if (checkSearchValue(value) == null) {
            return null;
        } else {
            return value;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createGreaterThanExpression(GreaterThanFilter filter, boolean not) {
        return super.createGreaterThanExpression(filter, not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createGreaterThanOrEqualExpression(GreaterThanOrEqualFilter filter, boolean not) {
        return super.createGreaterThanOrEqualExpression(filter, not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createLessThanExpression(LessThanFilter filter, boolean not) {
        return super.createLessThanExpression(filter, not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createLessThanOrEqualExpression(LessThanOrEqualFilter filter, boolean not) {
        return super.createLessThanOrEqualExpression(filter, not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createStartsWithExpression(StartsWithFilter filter, boolean not) {
        return super.createStartsWithExpression(filter, not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createContainsAllValuesExpression(ContainsAllValuesFilter filter, boolean not) {
        return super.createContainsAllValuesExpression(filter, not);
    }

    private String checkSearchValue(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        if (value.contains("*") || value.contains("&") || value.contains("|")) {
            throw new IllegalArgumentException(
                    "Value of search attribute contains illegal character(s).");
        }
        return value;
    }
}
