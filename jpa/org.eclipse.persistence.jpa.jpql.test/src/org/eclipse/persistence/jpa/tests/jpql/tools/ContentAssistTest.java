/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.AccessType;
import jpql.query.Address;
import jpql.query.Employee;
import jpql.query.EnumType;
import jpql.query.Project;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPAVersion;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.tools.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistExtension;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals;
import org.eclipse.persistence.jpa.jpql.tools.ContentAssistProposals.EnumProposals;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IManagedType;
import org.eclipse.persistence.jpa.jpql.tools.spi.IMapping;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.eclipse.persistence.jpa.tests.jpql.JPQLCoreTest;
import org.eclipse.persistence.jpa.tests.jpql.JPQLQueryHelperTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryBNFAccessor;
import org.eclipse.persistence.jpa.tests.jpql.tools.spi.java.JavaQuery;
import static org.eclipse.persistence.jpa.jpql.parser.Expression.*;
import static org.junit.Assert.*;

/**
 * The abstract unit-test providing helper methods required for testing content assist.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ContentAssistTest extends JPQLCoreTest {

    protected JPQLQueryBNFAccessor bnfAccessor;
    @JPQLQueryHelperTestHelper
    protected AbstractJPQLQueryHelper queryHelper;
    protected JavaQuery virtualQuery;

    /**
     * Returns the return type associated with the given JPQL identifier. Only JPQL identifiers mark
     * with {@link org.eclipse.persistence.jpa.jpql.parser.IdentifierRole#FUNCTION} have a return
     * type.
     *
     * @param identifier The JPQL identifier for which its expected return type should be returned
     * @return Either the return type for the given JPQL identifier or <code>null</code> if the
     * expression does not return a value
     */
    protected Class<?> acceptableType(String identifier) {
        return null;
    }

    /**
     * Creates a new {@link ContentAssistExtension} that can be used to provide additional
     * information that is outside the scope of simply providing JPA metadata information,
     * such as table names, column names, class names.
     *
     * @return By default, {@link ContentAssistExtension.NULL_HELPER} is returned
     */
    protected ContentAssistExtension buildContentAssistExtension() {
        return ContentAssistExtension.NULL_HELPER;
    }

    /**
     * Retrieves the possibles choices that can complete the query from the given position of the
     * cursor within the JPQL query.
     *
     * @param jpqlQuery The JPQL query to parse and for which content assist proposals will be
     * calculated based on the position of the cursor within that query
     * @param position The position of the cursor within the given JPQL query
     * @return The list of valid proposals regrouped by categories
     */
    protected final ContentAssistProposals buildContentAssistProposals(String jpqlQuery, int position) {
        virtualQuery.setExpression(jpqlQuery);
        queryHelper.setQuery(virtualQuery);
        return queryHelper.buildContentAssistProposals(position, buildContentAssistExtension());
    }

    /**
     * Creates an array of two elements containing the result of calculating the valid proposals.
     *
     * @param jpqlQuery The JPQL query to parse and for which content assist proposals will be
     * calculated based on the position of the cursor within that query
     * @param position The position of the cursor within the given JPQL query
     * @param proposals The collection of expected proposals
     * @return The result based on what was found and what is expected:
     * <ul>
     * <li>Index 0: Proposals that were not found by content assist but that are expected;</li>
     * <li>Index 1: Proposals that were found by content assist but that are not expected.</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    protected List<String>[] buildResults(String jpqlQuery,
                                          int position,
                                          Iterable<String> proposals) {

        // In case the Iterable is read-only
        List<String> expectedProposals = new ArrayList<String>();
        CollectionTools.addAll(expectedProposals, proposals);

        ContentAssistProposals contentAssistProposals = buildContentAssistProposals(jpqlQuery, position);
        List<String> unexpectedProposals = new ArrayList<String>();

        // Entities
        for (IEntity entity : contentAssistProposals.abstractSchemaTypes()) {
            handleProposal(expectedProposals, unexpectedProposals, entity.getName());
        }

        // Class names
        for (String className : contentAssistProposals.classNames()) {
            handleProposal(expectedProposals, unexpectedProposals, className);
        }

        // Column names
        for (String columnName : contentAssistProposals.columnNames()) {
            handleProposal(expectedProposals, unexpectedProposals, columnName);
        }

        // Enum constant names
        for (EnumProposals enumProposals : contentAssistProposals.enumConstant()) {
            // Iterate through the list of enum constants
            for (String enumConstantName : enumProposals.enumConstants()) {
                handleProposal(expectedProposals, unexpectedProposals, enumConstantName);
            }
        }

        // Identification variables
        for (String identificationVariable : contentAssistProposals.identificationVariables()) {
            handleProposal(expectedProposals, unexpectedProposals, identificationVariable);
        }

        // JPQL identifiers
        for (String identifier : contentAssistProposals.identifiers()) {
            handleProposal(expectedProposals, unexpectedProposals, identifier);
        }

        // Mappings
        for (IMapping mapping : contentAssistProposals.mappings()) {
            handleProposal(expectedProposals, unexpectedProposals, mapping.getName());
        }

        // Table names
        for (String tableName : contentAssistProposals.tableNames()) {
            handleProposal(expectedProposals, unexpectedProposals, tableName);
        }

        // The remaining proposals were not part of any proposal list
        List<String> proposalsNotRemoved = new ArrayList<String>();
        CollectionTools.addAll(proposalsNotRemoved, expectedProposals);

        return new List[] { proposalsNotRemoved, unexpectedProposals };
    }

    protected List<String> classNames() {
        List<String> classNames = new ArrayList<String>();
        classNames.add(Address      .class.getName());
        classNames.add(ArrayList    .class.getName());
        classNames.add(Employee     .class.getName());
        classNames.add(Project      .class.getName());
        classNames.add(String       .class.getName());
        classNames.add(StringBuilder.class.getName());
        return classNames;
    }

    protected final Iterable<String> collectionValuedFieldNames(Class<?> persistentType) throws Exception {
        return retrieveMappingNames(persistentType, MappingType.COLLECTION_VALUED_FIELD);
    }

    protected List<String> columnNames(String tableName) {

        List<String> columnNames = new ArrayList<String>();

        if ("EMPLOYEE".equals(tableName)) {
            columnNames.add("ADDRESS");
            columnNames.add("EMPLOYEE_ID");
            columnNames.add("FIRST_NAME");
            columnNames.add("LAST_NAME");
            columnNames.add("MANAGER");
        }
        else if ("ADDRESS".equals(tableName)) {
            columnNames.add("ADDRESS_ID");
            columnNames.add("APT_NUMBER");
            columnNames.add("COUNTRY");
            columnNames.add("STREET");
            columnNames.add("ZIP_CODE");
        }

        return columnNames;
    }

    protected Class<?> defaultAcceptableType(String identifier) {

        if (identifier == ABS   ||
            identifier == PLUS  ||
            identifier == MINUS ||
            identifier == AVG   ||
            identifier == MOD   ||
            identifier == SQRT  ||
            identifier == SUM) {

            return Number.class;
        }

        if (identifier == CONCAT    ||
            identifier == LENGTH    ||
            identifier == LOCATE    ||
            identifier == LOWER     ||
            identifier == SUBSTRING ||
            identifier == TRIM      ||
            identifier == UPPER) {

            return String.class;
        }

        return null;
    }

    protected final Iterable<IEntity> entities() throws Exception {
        return getPersistenceUnit().entities();
    }

    protected final Iterable<String> entityNames() throws Exception {
        List<String> names = new ArrayList<String>();
        for (IEntity entity : entities()) {
            names.add(entity.getName());
        }
        return names;
    }

    protected List<String> enumConstants() {

        List<String> names = new ArrayList<String>();

        for (Enum<EnumType> enumType : EnumType.values()) {
            names.add(enumType.name());
        }

        return names;
    }

    protected List<String> enumTypes() {
        List<String> classNames = new ArrayList<String>();
        classNames.add(EnumType  .class.getName());
        classNames.add(AccessType.class.getName());
        return classNames;
    }

    protected final Iterable<String> filter(Iterable<String> proposals, String startsWith) {

        List<String> results = new ArrayList<String>();

        for (String proposal : proposals) {
            if (ExpressionTools.startWithIgnoreCase(proposal, startsWith)) {
                results.add(proposal);
            }
        }

        return results;
    }

    protected final Iterable<String> filter(String[] proposals, String startsWith) {
        return filter(Arrays.asList(proposals), startsWith);
    }

    protected final JPQLGrammar getGrammar() {
        return queryHelper.getGrammar();
    }

    protected final JPQLQueryBNFAccessor getQueryBNFAccessor() {
        return bnfAccessor;
    }

    protected final AbstractJPQLQueryHelper getQueryHelper() {
        return queryHelper;
    }

    protected final JavaQuery getVirtualQuery() {
        return virtualQuery;
    }

    private void handleProposal(Iterable<String> expectedProposals,
                                List<String> unexpectedProposals,
                                String possibleProposal) {

        boolean removed = false;

        // Iterate through the expected proposals and see if the class name is expected
        for (Iterator<String> iter = expectedProposals.iterator(); iter.hasNext(); ) {

            String expectedProposal = iter.next();

            // The proposal is an expected proposal
            if (expectedProposal.equalsIgnoreCase(possibleProposal)) {
                // Remove it from the list of expected proposals
                iter.remove();
                // Indicate it was removed, if not, we'll add the proposal
                // to the list of unexpected proposals
                removed = true;
                break;
            }
        }

        // The proposal found by content assist is not expected
        if (!removed) {
            unexpectedProposals.add(possibleProposal);
        }
    }

    protected final boolean isJPA1_0() {
        return jpqlGrammar().getJPAVersion() == JPAVersion.VERSION_1_0;
    }

    protected final boolean isJPA2_0() {
        return jpqlGrammar().getJPAVersion() == JPAVersion.VERSION_2_0;
    }

    protected final boolean isJPA2_1() {
        return jpqlGrammar().getJPAVersion() == JPAVersion.VERSION_2_1;
    }

    protected final List<String> joinIdentifiers() {
        List<String> proposals = new ArrayList<String>();
        proposals.add(INNER_JOIN);
        proposals.add(INNER_JOIN_FETCH);
        proposals.add(JOIN);
        proposals.add(JOIN_FETCH);
        proposals.add(LEFT_JOIN);
        proposals.add(LEFT_JOIN_FETCH);
        proposals.add(LEFT_OUTER_JOIN);
        proposals.add(LEFT_OUTER_JOIN_FETCH);
        return proposals;
    }

    protected final List<String> joinOnlyIdentifiers() {
        List<String> proposals = new ArrayList<String>();
        proposals.add(INNER_JOIN);
        proposals.add(JOIN);
        proposals.add(LEFT_JOIN);
        proposals.add(LEFT_OUTER_JOIN);
        return proposals;
    }

    protected final JPQLGrammar jpqlGrammar() {
        return queryHelper.getGrammar();
    }

    protected final Iterable<String> nonTransientFieldNames(Class<?> persistentType) throws Exception {
        return retrieveMappingNames(persistentType, MappingType.NON_TRANSIENT);
    }

    protected final Iterable<String> relationshipAndCollectionFieldNames(Class<?> persistentType) throws Exception {
        Set<String> uniqueNames = new HashSet<String>();
        CollectionTools.addAll(uniqueNames, relationshipFieldNames(persistentType));
        CollectionTools.addAll(uniqueNames, collectionValuedFieldNames(persistentType));
        return uniqueNames;
    }

    protected final Iterable<String> relationshipFieldNames(Class<?> persistentType) throws Exception {
        return retrieveMappingNames(persistentType, MappingType.RELATIONSHIP_FIELD);
    }

    protected final <T extends Collection<String>> T removeAll(T items1, Iterable<String> items2) {
        for (String item2 : items2) {
            items1.remove(item2);
        }
        return items1;
    }

    protected final Iterable<String> retrieveMappingNames(Class<?> persistentType,
                                                          MappingType mappingType) throws Exception {

        return retrieveMappingNames(persistentType, mappingType, null);
    }

    protected final Iterable<String> retrieveMappingNames(Class<?> persistentType,
                                                          MappingType mappingType,
                                                          Class<?> allowedType) throws Exception {

        IManagedType managedType = getPersistenceUnit().getManagedType(persistentType.getName());

        if (managedType == null) {
            return Collections.emptyList();
        }

        List<String> names = new ArrayList<String>();
        IType type = (allowedType != null) ? getPersistenceUnit().getTypeRepository().getType(allowedType) : null;

        for (IMapping mapping : managedType.mappings()) {

            if (mappingType.isValid(mapping) &&
                ((type == null) || mapping.getType().isAssignableTo(type))) {

                names.add(mapping.getName());
            }
            // Allow incomplete path
            else if (mappingType == MappingType.SINGLE_VALUED_OBJECT_FIELD) {
                if (mapping.isRelationship() && !mapping.isCollection()) {
                    names.add(mapping.getName());
                }
            }
        }

        return names;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();
        virtualQuery = new JavaQuery(getPersistenceUnit(), null);
        bnfAccessor  = new JPQLQueryBNFAccessor(getGrammar().getExpressionRegistry());
    }

    protected final Iterable<String> singledValuedObjectFieldNames(Class<?> persistentType) throws Exception {
        return retrieveMappingNames(persistentType, MappingType.SINGLE_VALUED_OBJECT_FIELD);
    }

    protected final Iterable<String> singledValuedObjectFieldNames(Class<?> persistentType,
                                                                   Class<?> allowedType) throws Exception {

        return retrieveMappingNames(persistentType, MappingType.SINGLE_VALUED_OBJECT_FIELD, allowedType);
    }

    protected final Iterable<String> stateFieldNames(Class<?> persistentType) throws Exception {
        return retrieveMappingNames(persistentType, MappingType.STATE_FIELD);
    }

    protected final Iterable<String> stateFieldNames(Class<?> persistentType, Class<?> allowedType) throws Exception {
        return retrieveMappingNames(persistentType, MappingType.STATE_FIELD, allowedType);
    }

    protected List<String> tableNames() {

        List<String> tableNames = new ArrayList<String>();
        tableNames.add("ADDRESS");
        tableNames.add("EMPLOYEE");
        tableNames.add("EMPLOYEE_SEQ");
        tableNames.add("MANAGER");
        tableNames.add("DEPARTMENT");

        return tableNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        queryHelper.dispose();
        virtualQuery.setExpression(null);
        super.tearDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDownClass() throws Exception {
        bnfAccessor  = null;
        queryHelper  = null;
        virtualQuery = null;
        super.tearDownClass();
    }

    protected final void testDoesNotHaveTheseProposals(String jpqlQuery,
                                                       int position,
                                                       Enum<?>... proposals) {

        testDoesNotHaveTheseProposals(jpqlQuery, position, toString(proposals));
    }

    protected final void testDoesNotHaveTheseProposals(String jpqlQuery,
                                                       int position,
                                                       Iterable<String> proposals) {

        List<String>[] results = buildResults(jpqlQuery, position, Collections.<String>emptyList());
        List<String> expectedEroposals = results[1];
        List<String> unexpectedProposals = new ArrayList<String>();

        for (String proposal : proposals) {
            if (expectedEroposals.remove(proposal)) {
                unexpectedProposals.add(proposal);
            }
        }

        assertTrue(unexpectedProposals + " should not be proposals.", unexpectedProposals.isEmpty());
    }

    protected final void testDoesNotHaveTheseProposals(String jpqlQuery,
                                                       int position,
                                                       String... proposals) {

        testDoesNotHaveTheseProposals(jpqlQuery, position, CollectionTools.list(proposals));
    }

    protected final void testHasNoProposals(String jpqlQuery, int position) {
        List<String>[] results = buildResults(jpqlQuery, position, Collections.<String>emptyList());
        List<String> unexpectedProposals = results[1];
        assertTrue(unexpectedProposals + " should not be proposals.", unexpectedProposals.isEmpty());
    }

    protected final void testHasOnlyTheseProposals(String jpqlQuery,
                                                   int position,
                                                   Enum<?>... proposals) {

        testHasOnlyTheseProposals(jpqlQuery, position, toString(proposals));
    }

    protected final void testHasOnlyTheseProposals(String jpqlQuery, int position, Iterable<String> proposals) {

        List<String>[] results = buildResults(jpqlQuery, position, proposals);
        List<String> proposalsNotRemoved = results[0];
        List<String> unexpectedProposals = results[1];

        // Inconsistent list of proposals
        if (!unexpectedProposals.isEmpty() && !proposalsNotRemoved.isEmpty()) {
            if (proposalsNotRemoved.size() == 1) {
                fail(proposalsNotRemoved.get(0) + " should be a proposal and " + unexpectedProposals + " should not be a proposal.");
            }
            else {
                fail(proposalsNotRemoved + " should be proposals and " + unexpectedProposals + " should not be proposals.");
            }
        }
        // Added more proposals than it should
        else if (!unexpectedProposals.isEmpty() && proposalsNotRemoved.isEmpty()) {
            if (proposalsNotRemoved.size() == 1) {
                fail(unexpectedProposals.get(0) + " should not be a proposal.");
            }
            else {
                fail(unexpectedProposals + " should not be proposals.");
            }
        }
        // Forgot to add some proposals
        else if (!proposalsNotRemoved.isEmpty()) {
            if (proposalsNotRemoved.size() == 1) {
                fail(proposalsNotRemoved.get(0) + " should be a proposal.");
            }
            else {
                fail(proposalsNotRemoved + " should be proposals.");
            }
        }
    }

    protected final void testHasOnlyTheseProposals(String jpqlQuery,
                                                   int position,
                                                   String... proposals) {

        if (proposals.length == 0) {
            fail("The list of expected proposals cannot be empty");
        }

        testHasOnlyTheseProposals(jpqlQuery, position, CollectionTools.list(proposals));
    }

    protected final void testHasTheseProposals(String jpqlQuery,
                                               int position,
                                               Enum<?>... enums) {

        testHasTheseProposals(jpqlQuery, position, toString(enums));
    }

    protected final void testHasTheseProposals(String jpqlQuery,
                                               int position,
                                               Iterable<String> proposals) {

        List<String>[] results = buildResults(jpqlQuery, position, proposals);
        List<String> proposalsNotRemoved = results[0];
        assertTrue(proposalsNotRemoved + " should be proposals.", proposalsNotRemoved.isEmpty());
    }

    protected final void testHasTheseProposals(String jpqlQuery,
                                               int position,
                                               String... proposals) {

        testHasTheseProposals(jpqlQuery, position, CollectionTools.list(proposals));
    }

    protected final String[] toString(Enum<?>[] enums) {

        String[] names = new String[enums.length];

        for (int index = enums.length; --index >= 0; ) {
            names[index] = enums[index].name();
        }

        return names;
    }

    protected final Iterable<String> transientFieldNames(Class<?> persistentType) throws Exception {
        return retrieveMappingNames(persistentType, MappingType.TRANSIENT);
    }

    private enum MappingType {

        COLLECTION_VALUED_FIELD {
            @Override
            public boolean isValid(IMapping mapping) {
                return mapping.isCollection();
            }
        },

        NON_TRANSIENT {
            @Override
            public boolean isValid(IMapping mapping) {
                return !mapping.isTransient();
            }
        },

        RELATIONSHIP_FIELD {
            @Override
            public boolean isValid(IMapping mapping) {
                return mapping.isRelationship() && !mapping.isCollection();
            }
        },

        SINGLE_VALUED_OBJECT_FIELD {
            @Override
            public boolean isValid(IMapping mapping) {
                return !mapping.isTransient() && !mapping.isCollection();
            }
        },

        STATE_FIELD {
            @Override
            public boolean isValid(IMapping mapping) {
                return mapping.isProperty();
            }
        },

        TRANSIENT {
            @Override
            public boolean isValid(IMapping mapping) {
                return mapping.isTransient();
            }
        };

        abstract boolean isValid(IMapping mapping);
    }
}
