package com.exclamationlabs.connid;

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
        return super.createEqualsExpression(filter, not);
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
}
