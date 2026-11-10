/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.querydef;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaStatement;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.PluralExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.platform.database.H2Platform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PluralCriteriaExpressionsTest {
    static class Owner {
    }

    private final CriteriaBuilderImpl builder = new CriteriaBuilderImpl(null);
    private final CriteriaBuilderImpl queryBuilder = new CriteriaBuilderImpl(new MetamodelImpl(new DatabaseSessionImpl(new Project(new DatabaseLogin()))));
    private final RootImpl<Owner> owner = new RootImpl<>(null, null, Owner.class, new ExpressionBuilder(Owner.class), null);

    @SuppressWarnings("unchecked")
    private PluralExpressionImpl<Set<String>, String> collection() {
        return new PluralExpressionImpl<>(null, (Class<Set<String>>) (Class<?>) Set.class,
                owner.getCurrentNode().anyOf("members"), owner, "members", String.class);
    }

    @Test
    void allOperationsRetainCollectionOwnerAndPublicTypes() {
        var collection = collection();
        collection.alias("members");
        assertEquals(Set.class, collection.getJavaType());
        assertEquals(Integer.class, collection.size().getJavaType());
        for (Predicate predicate : new Predicate[] {collection.isEmpty(), collection.isNotEmpty(), collection.contains("a"),
                collection.notContains("a"), collection.contains(builder.literal("a")), collection.notContains(builder.literal("a")),
                collection.contains(builder.nullLiteral(String.class)), collection.notContains(builder.nullLiteral(String.class))}) {
            CriteriaQuery<Boolean> query = queryBuilder.createQuery(Boolean.class).select(predicate);
            assertEquals(Set.of(owner), query.getRoots());
            assertEquals(Boolean.class, predicate.getJavaType());
            assertNotNull(((InternalSelection) predicate).getCurrentNode().clone());
        }
        assertEquals("members", collection.getAlias());
        assertThrows(NullPointerException.class, () -> collection.contains((String) null));
        assertThrows(NullPointerException.class, () -> collection.contains((Expression<String>) null));
    }

    @Test
    void cardinalityOwnsItsQueriesAndIntegerResultMetadata() {
        var collection = collection();
        FunctionExpression first = (FunctionExpression) ((InternalSelection) collection.size()).getCurrentNode();
        FunctionExpression second = (FunctionExpression) ((InternalSelection) collection.size()).getCurrentNode();
        assertEquals(Integer.class, first.getResultType());
        assertNotSame(first, second);
        SubSelectExpression subquery = (SubSelectExpression) first.getChildren().getFirst();
        SubSelectExpression other = (SubSelectExpression) second.getChildren().getFirst();
        assertNotSame(subquery.getSubQuery(), other.getSubQuery());
        assertEquals(1, subquery.getSubQuery().getNonFetchJoinAttributeExpressions().size());
        assertSame(owner.getCurrentNode(), subquery.getBaseExpression());
        assertEquals(Set.of(owner), queryBuilder.createQuery(Integer.class).select(collection.size()).getRoots());
    }

    @Test
    void composedOriginalParametersAreTraversedWithoutSourceMutation() {
        var collection = collection();
        ParameterExpression<String> parameter = builder.parameter(String.class, "member");
        parameter.alias("original");
        var core = ((InternalSelection) parameter).getCurrentNode();
        var before = core.getBuilder();
        Expression<String> composed = builder.concat(parameter, "suffix");
        Predicate first = collection.contains(composed);
        Predicate second = collection.notContains(parameter);
        CriteriaQuery<Boolean> query = queryBuilder.createQuery(Boolean.class).select(first).where(second);
        assertEquals(Set.of(parameter), query.getParameters());
        assertSame(before, core.getBuilder());
        assertEquals("original", parameter.getAlias());
        assertSame(owner.getCurrentNode().getBuilder(), collection.getCurrentNode().getBuilder());
    }

    @Test
    void implicitOwnerRelationshipIsPreparedOutsideEveryCollectionOperation() {
        MappedFixture fixture = mappedFixture();
        List<Function<PluralExpression<Set<String>, String>, Expression<Boolean>>> operations = List.of(
                PluralExpression::isEmpty, PluralExpression::isNotEmpty,
                members -> members.contains("blue"), members -> members.notContains("blue"),
                members -> members.notContains("missing"));
        for (var operation : operations) {
            CriteriaQuery<Boolean> criteria = fixture.builder.createQuery(Boolean.class);
            Root<MappedOwner> root = criteria.from(MappedOwner.class);
            var parent = root.get(fixture.metamodel.entity(MappedOwner.class)
                    .getSingularAttribute("parent", MappedOwner.class));
            var members = parent.get(fixture.metamodel.entity(MappedOwner.class).getSet("members", String.class));
            var originalJoins = Set.copyOf(root.getJoins());
            Expression<Boolean> selected = operation.apply(members);
            selected.alias("membership");
            criteria.select(selected);
            ReportQuery original = ((CriteriaQueryImpl<?>) criteria).transalteToReportQuery();
            original.prepareCall(fixture.session, new DatabaseRecord());
            SQLSelectStatement statement = fixture.platform.lastSelect;
            // Selection alone must normalize the implicit owner relationship in
            // the outer statement, independently of any collection subquery.
            assertNotNull(statement.getWhereClause());
            assertEquals(2, statement.getTableAliases().size());
            assertEquals(Set.of(root), criteria.getRoots());
            assertEquals(originalJoins, root.getJoins());
            assertEquals("membership", selected.getAlias());

            String originalSql = original.getCall().getSQLString();
            criteria.select(fixture.builder.literal(true));
            ReportQuery replacement = ((CriteriaQueryImpl<?>) criteria).transalteToReportQuery();
            replacement.prepareCall(fixture.session, new DatabaseRecord());
            SQLSelectStatement replacementStatement = fixture.platform.lastSelect;
            assertNull(replacementStatement.getWhereClause());
            assertEquals(1, replacementStatement.getTableAliases().size());
            assertEquals(originalSql, original.getCall().getSQLString());
            assertNotNull(statement.getWhereClause());
            assertEquals(originalJoins, root.getJoins());
            assertFalse(fixture.session.isConnected());
        }
    }

    @Test
    void entitySubqueryMembershipPreparationPreservesSourceAndParameterSnapshots() {
        MappedFixture fixture = mappedFixture();
        CriteriaQuery<Integer> criteria = fixture.builder.createQuery(Integer.class);
        Root<MappedOwner> root = criteria.from(MappedOwner.class);
        var elements = root.get(fixture.metamodel.entity(MappedOwner.class).getSet("elements", MappedElement.class));
        ParameterExpression<Integer> firstId = fixture.builder.parameter(Integer.class, "firstId");
        firstId.alias("originalIdentity");
        Subquery<MappedElement> lookup = criteria.subquery(MappedElement.class);
        Root<MappedElement> lookupRoot = lookup.from(MappedElement.class);
        Predicate restriction = fixture.builder.and(fixture.builder.equal(lookupRoot.get("firstId"), firstId),
                fixture.builder.equal(lookupRoot.get("secondId"), 1));
        lookup.select(lookupRoot).where(restriction);
        lookup.alias("lookup");
        SubQueryImpl<?> implementation = (SubQueryImpl<?>) lookup;
        ReportQuery source = implementation.subQuery;
        var sourceCriteria = source.getSelectionCriteria();
        var sourceItem = source.getItems().getFirst();
        var sourceJoin = source.getNonFetchJoinAttributeExpressions().getFirst();
        var parameterNode = ((InternalSelection) firstId).getCurrentNode();
        var parameterBuilder = parameterNode.getBuilder();

        Predicate positive = elements.contains(lookup);
        Predicate negative = elements.notContains(lookup);
        criteria.select(root.get("id")).where(positive);
        ReportQuery original = ((CriteriaQueryImpl<?>) criteria).transalteToReportQuery();
        criteria.where(negative);
        ReportQuery negated = ((CriteriaQueryImpl<?>) criteria).transalteToReportQuery();
        for (ReportQuery query : List.of(original, negated)) {
            assertSame(firstId, parameterHandles(query).get("firstId"));
            assertEquals(List.of("firstId"), query.getArguments());
            assertEquals(List.of(Integer.class), query.getArgumentTypes());
            query.prepareCall(fixture.session, new DatabaseRecord());
            assertSame(lookupRoot, lookup.getSelection());
            assertSame(restriction, lookup.getRestriction());
            assertSame(sourceCriteria, source.getSelectionCriteria());
            assertSame(sourceItem, source.getItems().getFirst());
            assertSame(sourceJoin, source.getNonFetchJoinAttributeExpressions().getFirst());
            assertSame(parameterBuilder, parameterNode.getBuilder());
            assertEquals(Set.of(root), criteria.getRoots());
            assertEquals(Set.of(lookupRoot), lookup.getRoots());
            assertEquals("lookup", lookup.getAlias());
        }
        String positiveSql = original.getCall().getSQLString();
        String negativeSql = negated.getCall().getSQLString();
        ParameterExpression<Integer> replacementId = fixture.builder.parameter(Integer.class, "replacementId");
        lookup.where(fixture.builder.equal(lookupRoot.get("firstId"), replacementId),
                fixture.builder.equal(lookupRoot.get("secondId"), 1));
        criteria.where(positive);
        ReportQuery replacement = ((CriteriaQueryImpl<?>) criteria).transalteToReportQuery();
        replacement.prepareCall(fixture.session, new DatabaseRecord());
        assertEquals(Set.of("replacementId"), parameterHandles(replacement).keySet());
        assertSame(replacementId, parameterHandles(replacement).get("replacementId"));
        assertEquals(Set.of("firstId"), parameterHandles(original).keySet());
        assertEquals(Set.of("firstId"), parameterHandles(negated).keySet());
        assertSame(firstId, parameterHandles(original).get("firstId"));
        assertEquals(positiveSql, original.getCall().getSQLString());
        assertEquals(negativeSql, negated.getCall().getSQLString());
        assertEquals("originalIdentity", firstId.getAlias());
        assertFalse(fixture.session.isConnected());
    }

    @Test
    void entitySubqueryCopiesRetainDirectAndNestedBulkTargets() {
        for (boolean update : List.of(false, true)) {
            for (boolean nested : List.of(false, true)) {
                MappedFixture fixture = mappedFixture();
                CriteriaStatement<MappedOwner> criteria = update
                        ? fixture.builder.createCriteriaUpdate(MappedOwner.class)
                        : fixture.builder.createCriteriaDelete(MappedOwner.class);
                Root<MappedOwner> root = criteria.from(MappedOwner.class);
                ParameterExpression<Integer> secondId = fixture.builder.parameter(Integer.class, "bulkSecondId");
                Subquery<Integer> wrapper = nested ? criteria.subquery(Integer.class) : null;
                Root<MappedOwner> owner = nested ? wrapper.correlate(root) : root;
                Subquery<MappedElement> lookup = (nested ? wrapper : criteria).subquery(MappedElement.class);
                Root<MappedOwner> correlated = lookup.correlate(owner);
                Root<MappedElement> element = lookup.from(MappedElement.class);
                lookup.select(element).where(fixture.builder.equal(element.get("firstId"), correlated.get("id")),
                        fixture.builder.equal(element.get("secondId"), secondId));
                var originalRestriction = lookup.getRestriction();
                var elements = owner.get(fixture.metamodel.entity(MappedOwner.class).getSet("elements", MappedElement.class));
                Predicate restriction = elements.contains(lookup);
                if (nested) {
                    wrapper.select(fixture.builder.literal(1)).where(restriction);
                    restriction = fixture.builder.exists(wrapper);
                }
                if (criteria instanceof CriteriaUpdate<MappedOwner> modification) {
                    modification.set(root.<Integer>get("id"), 9).where(restriction);
                } else {
                    ((CriteriaDelete<MappedOwner>) criteria).where(restriction);
                }
                DatabaseQuery query = ((CommonAbstractCriteriaImpl<?>) criteria).translate();
                assertSame(secondId, parameterHandles(query).get("bulkSecondId"));
                query.prepareCall(fixture.session, new DatabaseRecord());
                assertNotNull(query.getSQLString());
                if (criteria instanceof CriteriaUpdate<MappedOwner> modification) {
                    Predicate legacyRestriction = fixture.builder.exists(lookup);
                    if (nested) {
                        wrapper.where(legacyRestriction);
                        legacyRestriction = fixture.builder.exists(wrapper);
                    }
                    modification.where(legacyRestriction);
                    DatabaseQuery legacy = ((CommonAbstractCriteriaImpl<?>) modification).translate();
                    assertSame(secondId, parameterHandles(legacy).get("bulkSecondId"));
                    legacy.prepareCall(fixture.session, new DatabaseRecord());
                    assertNotNull(legacy.getSQLString());
                }
                assertSame(root, criteria.getRoot());
                assertSame(owner, correlated.getCorrelationParent());
                assertSame(element, lookup.getSelection());
                assertSame(originalRestriction, lookup.getRestriction());
                assertFalse(fixture.session.isConnected());
            }
        }
    }

    @Test
    void nestedRelationTableJoinRetainsTargetAndSourcePredicatesInItsOuterOnClause() {
        MappedFixture fixture = mappedFixture();
        CriteriaQuery<Integer> criteria = fixture.builder.createQuery(Integer.class);
        Root<MappedOwner> root = criteria.from(MappedOwner.class);
        Subquery<Integer> subquery = criteria.subquery(Integer.class);
        Root<MappedOwner> correlated = subquery.correlate(root);
        Root<MappedOwner> lookup = subquery.from(MappedOwner.class);
        var joined = lookup.join(fixture.metamodel.entity(MappedOwner.class).getSet("elements", MappedElement.class), JoinType.LEFT);
        ParameterExpression<Integer> targetId = fixture.builder.parameter(Integer.class, "onTargetId");
        ParameterExpression<Integer> sourceId = fixture.builder.parameter(Integer.class, "onSourceId");
        ParameterExpression<Integer> expected = fixture.builder.parameter(Integer.class, "expectedId");
        joined.on(fixture.builder.equal(joined.get("secondId"), targetId), fixture.builder.equal(lookup.get("id"), sourceId));
        var originalOn = joined.getOn();
        var selection = joined.<Integer>get("secondId");
        subquery.select(selection).where(fixture.builder.equal(lookup.get("id"), correlated.get("id")));
        var restriction = subquery.getRestriction();
        criteria.select(root.get("id")).where(fixture.builder.equal(subquery, expected));
        ReportQuery query = ((CriteriaQueryImpl<?>) criteria).transalteToReportQuery();
        assertEquals(Set.of(targetId, sourceId, expected), Set.copyOf(parameterHandles(query).values()));
        assertSame(targetId, parameterHandles(query).get("onTargetId"));
        assertSame(sourceId, parameterHandles(query).get("onSourceId"));
        query.prepareCall(fixture.session, new DatabaseRecord());
        DatabaseCall call = query.getCall();
        String sql = call.getSQLString();
        int nestedStart = sql.indexOf(" LEFT OUTER JOIN (");
        assertTrue(nestedStart >= 0, sql);
        int open = sql.indexOf('(', nestedStart);
        int close = closingParenthesis(sql, open);
        String nestedJoin = sql.substring(open, close + 1);
        assertFalse(nestedJoin.contains("?"), "User predicates must not move into the relation-to-target inner join");
        assertTrue(sql.substring(close + 1).startsWith(" ON "), sql);
        int end = sql.indexOf('}', close);
        if (end < 0) {
            end = sql.indexOf(" WHERE ", close);
        }
        assertTrue(end > close, sql);
        String outerOn = sql.substring(close + 1, end);
        int source = sql.lastIndexOf("PLURAL_OWNER ", nestedStart) + "PLURAL_OWNER ".length();
        String sourceAlias = sql.substring(source, nestedStart).trim();
        int target = nestedJoin.indexOf("PLURAL_ELEMENT ") + "PLURAL_ELEMENT ".length();
        String targetAlias = nestedJoin.substring(target, nestedJoin.indexOf(" ON ", target)).trim();
        assertTrue(outerOn.contains(targetAlias + ".SECOND_ID = ?"), outerOn);
        assertTrue(outerOn.contains(sourceAlias + ".ID = ?"), outerOn);
        assertEquals(2L, outerOn.chars().filter(character -> character == '?').count());

        DatabaseRecord arguments = new DatabaseRecord();
        arguments.put("onTargetId", 11);
        arguments.put("onSourceId", 22);
        arguments.put("expectedId", 33);
        DatabaseCall execution = (DatabaseCall) call.clone();
        execution.translate(arguments, null, fixture.session);
        assertEquals(List.of(11, 22, 33), execution.getParameters());
        query.setIsPrepared(false);
        query.prepareCall(fixture.session, new DatabaseRecord());
        assertEquals(sql, query.getCall().getSQLString());
        assertSame(originalOn, joined.getOn());
        assertSame(selection, subquery.getSelection());
        assertSame(restriction, subquery.getRestriction());
        assertSame(root, correlated.getCorrelationParent());
        assertEquals(Set.of(root), criteria.getRoots());
        assertFalse(fixture.session.isConnected());
    }

    private static int closingParenthesis(String sql, int open) {
        int depth = 0;
        for (int index = open; index < sql.length(); index++) {
            if (sql.charAt(index) == '(') {
                depth++;
            } else if (sql.charAt(index) == ')' && --depth == 0) {
                return index;
            }
        }
        throw new AssertionError("Unclosed nested join: " + sql);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, ParameterExpression<?>> parameterHandles(DatabaseQuery query) {
        return (Map<String, ParameterExpression<?>>) query.getProperty(ParameterExpressionImpl.QUERY_PARAMETERS_PROPERTY);
    }

    private static MappedFixture mappedFixture() {
        CapturingPlatform platform = new CapturingPlatform();
        platform.setShouldBindAllParameters(true);
        platform.setShouldBindLiterals(false);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin(platform));
        RelationalDescriptor owner = new RelationalDescriptor();
        owner.setJavaClass(MappedOwner.class);
        owner.setAlias("PluralOwner");
        owner.addTableName("PLURAL_OWNER");
        owner.addPrimaryKeyFieldName("PLURAL_OWNER.ID");
        owner.addDirectMapping("id", "PLURAL_OWNER.ID");
        OneToOneMapping parent = new OneToOneMapping();
        parent.setAttributeName("parent");
        parent.setReferenceClass(MappedOwner.class);
        parent.addForeignKeyFieldName("PLURAL_OWNER.PARENT_ID", "PLURAL_OWNER.ID");
        parent.dontUseIndirection();
        owner.addMapping(parent);
        DirectCollectionMapping members = new DirectCollectionMapping();
        members.setAttributeName("members");
        members.setReferenceTableName("PLURAL_MEMBER");
        members.setDirectFieldName("PLURAL_MEMBER.MEMBER_VALUE");
        members.setDirectFieldClassification(String.class);
        members.setAttributeClassification(String.class);
        members.addReferenceKeyFieldName("PLURAL_MEMBER.OWNER_ID", "PLURAL_OWNER.ID");
        members.useCollectionClass(HashSet.class);
        members.dontUseIndirection();
        owner.addMapping(members);
        ManyToManyMapping elements = new ManyToManyMapping();
        elements.setAttributeName("elements");
        elements.setReferenceClass(MappedElement.class);
        elements.setRelationTableName("PLURAL_OWNER_ELEMENT");
        elements.addSourceRelationKeyFieldName("PLURAL_OWNER_ELEMENT.OWNER_ID", "PLURAL_OWNER.ID");
        elements.addTargetRelationKeyFieldName("PLURAL_OWNER_ELEMENT.ELEMENT_FIRST", "PLURAL_ELEMENT.FIRST_ID");
        elements.addTargetRelationKeyFieldName("PLURAL_OWNER_ELEMENT.ELEMENT_SECOND", "PLURAL_ELEMENT.SECOND_ID");
        elements.useCollectionClass(HashSet.class);
        elements.dontUseIndirection();
        owner.addMapping(elements);
        session.addDescriptor(owner);
        RelationalDescriptor element = new RelationalDescriptor();
        element.setJavaClass(MappedElement.class);
        element.setAlias("PluralElement");
        element.addTableName("PLURAL_ELEMENT");
        element.addPrimaryKeyFieldName("PLURAL_ELEMENT.FIRST_ID");
        element.addPrimaryKeyFieldName("PLURAL_ELEMENT.SECOND_ID");
        element.addDirectMapping("firstId", "PLURAL_ELEMENT.FIRST_ID");
        element.addDirectMapping("secondId", "PLURAL_ELEMENT.SECOND_ID");
        session.addDescriptor(element);
        session.initializeDescriptors();
        MetamodelImpl metamodel = new MetamodelImpl(session);
        metamodel.initialize(PluralCriteriaExpressionsTest.class.getClassLoader());
        return new MappedFixture(session, metamodel, new CriteriaBuilderImpl(metamodel), platform);
    }

    private record MappedFixture(DatabaseSessionImpl session, MetamodelImpl metamodel, CriteriaBuilderImpl builder,
            CapturingPlatform platform) {
    }

    private static final class CapturingPlatform extends H2Platform {
        private SQLSelectStatement lastSelect;

        @Override
        public void printSQLSelectStatement(DatabaseCall call, ExpressionSQLPrinter printer, SQLSelectStatement statement) {
            super.printSQLSelectStatement(call, printer, statement);
            lastSelect = statement;
        }
    }

    public static class MappedOwner {
        public Integer id;
        public MappedOwner parent;
        public Set<String> members;
        public Set<MappedElement> elements;
    }

    public static class MappedElement {
        public Integer firstId;
        public Integer secondId;
    }

    @Test
    @SuppressWarnings("unchecked")
    void detachedImplicitOwnerOperationsRetainTheSharedRootAndOriginalHandle() throws Exception {
        FromImpl<Owner, Owner> parent = new FromImpl<>(owner, null, null, Owner.class,
                owner.getCurrentNode().get("parent"), null);
        var members = new PluralExpressionImpl<Set<String>, String>(null, (Class<Set<String>>) (Class<?>) Set.class,
                parent.getCurrentNode().anyOf("members"), parent, "members", String.class);
        ParameterExpression<String> parameter = builder.parameter(String.class, "member");
        Predicate positive = members.contains(parameter);
        positive.alias("membership");
        Object[] original = {owner, members, parameter, positive, members.notContains(parameter),
                members.isEmpty(), members.isNotEmpty(), members.size()};
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(original);
        }
        Object[] restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (Object[]) input.readObject();
        }
        Root<Owner> restoredRoot = (Root<Owner>) restored[0];
        var restoredMembers = (PluralExpression<Set<String>, String>) restored[1];
        ParameterExpression<String> restoredParameter = (ParameterExpression<String>) restored[2];
        for (int index = 3; index < restored.length; index++) {
            Expression<?> expression = (Expression<?>) restored[index];
            CriteriaQuery<Object> query = queryBuilder.createQuery(Object.class).select(expression);
            assertEquals(Set.of(restoredRoot), query.getRoots());
            assertEquals(index < 5 ? Set.of(restoredParameter) : Set.of(), query.getParameters());
            assertNotNull(((InternalSelection) expression).getCurrentNode().clone());
        }
        assertEquals("membership", ((Expression<?>) restored[3]).getAlias());
        assertEquals("member", restoredParameter.getName());
        assertEquals(String.class, restoredParameter.getParameterType());
        assertSame(restoredParameter, queryBuilder.createQuery(Boolean.class)
                .select(restoredMembers.notContains(restoredParameter)).getParameters().iterator().next());
        assertEquals(Set.of(owner), queryBuilder.createQuery(Boolean.class).select(positive).getRoots());
        assertSame(parameter, queryBuilder.createQuery(Boolean.class).select(positive).getParameters().iterator().next());
        assertTrue(owner.getJoins().isEmpty());
        assertTrue(restoredRoot.getJoins().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    void detachedPluralGraphAndHandlesRoundTripTogether() throws Exception {
        var collection = collection();
        ParameterExpression<String> parameter = builder.parameter(String.class, "member");
        Predicate predicate = collection.contains(parameter);
        predicate.alias("membership");
        Object[] original = {collection, parameter, predicate, collection.size()};
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(original);
        }
        Object[] restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (Object[]) input.readObject();
        }
        PluralExpressionImpl<Set<String>, String> restoredCollection = (PluralExpressionImpl<Set<String>, String>) restored[0];
        ParameterExpression<String> restoredParameter = (ParameterExpression<String>) restored[1];
        Predicate restoredPredicate = (Predicate) restored[2];
        CriteriaQuery<Boolean> query = queryBuilder.createQuery(Boolean.class).select(restoredPredicate);
        assertEquals(Set.of(restoredParameter), query.getParameters());
        assertTrue(query.getParameters().stream().allMatch(value -> value == restoredParameter));
        assertEquals("member", restoredParameter.getName());
        assertEquals(String.class, restoredParameter.getParameterType());
        assertEquals("membership", restoredPredicate.getAlias());
        assertEquals(Integer.class, ((Expression<?>) restored[3]).getJavaType());
        assertEquals(Set.class, restoredCollection.getJavaType());
        assertNotSame(collection, restoredCollection);
        assertEquals(Set.of(parameter), queryBuilder.createQuery(Boolean.class).select(predicate).getParameters());
        assertEquals(Boolean.class, restoredCollection.notContains(restoredParameter).getJavaType());
    }
}
