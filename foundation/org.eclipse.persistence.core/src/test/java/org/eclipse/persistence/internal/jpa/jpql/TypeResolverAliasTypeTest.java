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
package org.eclipse.persistence.internal.jpa.jpql;

import java.util.Collection;
import java.util.Map;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.SelectClause;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectMapMapping;
import org.eclipse.persistence.mappings.converters.TypeConversionConverter;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TypeResolverAliasTypeTest {

    @Test
    void mapAliasesAndValueUseTheDeclaredObjectType() {
        DirectMapMapping mapping = mapMapping();
        mapping.setValueClass(String.class);
        mapping.setDirectFieldClassification(Integer.class);
        JPQLQueryContext context = context(mapping);
        assertSame(String.class, mapping.getValueClass());
        assertSame(Integer.class, mapping.getDirectField().getType());
        assertMapType(context, String.class);
    }

    @Test
    void mapAliasesAndValueFallBackToTheFieldType() {
        DirectMapMapping mapping = mapMapping();
        mapping.setDirectFieldClassification(String.class);
        JPQLQueryContext context = context(mapping);
        assertNull(mapping.getValueClass());
        assertMapType(context, String.class);
    }

    @Test
    void mapAliasesAndValuePreferDomainClassificationToStorageType() {
        DirectMapMapping mapping = mapMapping();
        mapping.setAttributeClassification(Long.class);
        mapping.setDirectFieldClassification(String.class);
        JPQLQueryContext context = context(mapping);
        assertNull(mapping.getValueClass());
        assertSame(Long.class, mapping.getAttributeClassification());
        assertSame(String.class, mapping.getDirectField().getType());
        assertSame(Long.class, type(context, "v"));
        assertSame(Long.class, type(context, "VALUE(v)"));
        assertSame(Map.class, type(context, "o.elements"));
    }

    @Test
    void mapKeyConverterPreservesTheValueClassification() {
        DirectMapMapping mapping = mapMapping();
        mapping.setKeyClass(String.class);
        mapping.setAttributeClassification(Long.class);
        mapping.setDirectFieldClassification(String.class);
        JPQLQueryContext context = context(mapping);
        assertSame(Long.class, mapping.getAttributeClassification());
        assertSame(String.class, mapping.getDirectField().getType());
        assertSame(Long.class, type(context, "v"));
        assertSame(Long.class, type(context, "VALUE(v)"));
    }

    @Test
    void sharedMapKeyAndValueConverterStillInitializesTheValueType() {
        DirectMapMapping mapping = mapMapping();
        TypeConversionConverter converter = new TypeConversionConverter();
        converter.setObjectClass(Long.class);
        converter.setDataClass(String.class);
        mapping.setKeyConverter(converter);
        mapping.setValueConverter(converter);
        mapping.setDirectFieldClassification(String.class);
        JPQLQueryContext context = context(mapping);
        assertSame(Long.class, mapping.getAttributeClassification());
        assertSame(Long.class, type(context, "v"));
        assertSame(Long.class, type(context, "VALUE(v)"));
    }

    @Test
    void standaloneCollectionConverterStillInitializesTheElementType() {
        DirectCollectionMapping mapping = collectionMapping();
        TypeConversionConverter converter = new TypeConversionConverter();
        converter.setObjectClass(Long.class);
        converter.setDataClass(String.class);
        converter.initialize(mapping, new DatabaseSessionImpl(new DatabaseLogin()));
        assertNull(mapping.getValueConverter());
        assertSame(Long.class, mapping.getAttributeClassification());
    }

    @Test
    void unresolvedMapValueTypesRemainUnresolved() {
        DirectMapMapping mapping = mapMapping();
        JPQLQueryContext context = context(mapping);
        mapping.getDirectField().setType(null);
        assertNull(mapping.getValueClass());
        assertMapType(context, null);
    }

    @Test
    void basicCollectionAliasesUseTheElementClassification() {
        DirectCollectionMapping mapping = collectionMapping();
        mapping.setAttributeClassification(String.class);
        mapping.setDirectFieldClassification(String.class);
        JPQLQueryContext context = context(mapping);
        assertSame(String.class, type(context, "v"));
        assertSame(String.class, type(context, "NULLIF(v, 'other')"));
        assertSame(Collection.class, type(context, "o.elements"));
    }

    @Test
    void basicCollectionAliasesPreserveTheConverterObjectType() {
        DirectCollectionMapping mapping = collectionMapping();
        TypeConversionConverter converter = new TypeConversionConverter();
        converter.setObjectClass(String.class);
        converter.setDataClass(Integer.class);
        mapping.setValueConverter(converter);
        mapping.setDirectFieldClassification(Integer.class);
        JPQLQueryContext context = context(mapping);
        assertSame(String.class, mapping.getAttributeClassification());
        assertSame(Integer.class, mapping.getDirectField().getType());
        assertSame(String.class, type(context, "v"));
    }

    @Test
    void basicCollectionAliasesFallBackToTheFieldType() {
        DirectCollectionMapping mapping = collectionMapping();
        mapping.setDirectFieldClassification(String.class);
        JPQLQueryContext context = context(mapping);
        mapping.setAttributeClassification(null);
        assertSame(String.class, type(context, "v"));
    }

    @Test
    void unresolvedCollectionElementTypesRemainUnresolved() {
        DirectCollectionMapping mapping = collectionMapping();
        JPQLQueryContext context = context(mapping);
        mapping.setAttributeClassification(null);
        mapping.getDirectField().setType(null);
        assertNull(type(context, "v"));
    }

    @Test
    void rangeVirtualAndUndeclaredAliasesKeepTheirExistingTypes() {
        JPQLQueryContext context = context(mapMapping());
        assertSame(Owner.class, type(context, "o"));
        assertSame(Object.class, type(context, "missing"));
        IdentificationVariable virtual = new IdentificationVariable(context.getJPQLExpression(), "id", false);
        virtual.setVirtualIdentificationVariable("o");
        assertSame(Long.class, context.getType(virtual));
        assertSame(Map.class, type(context, "o.elements"));
        assertSame(Owner.class, type(context, "o"));
    }

    private static void assertMapType(JPQLQueryContext context, Class<?> expected) {
        assertSame(expected, type(context, "VALUE(v)"));
        assertSame(expected, type(context, "v"));
        assertSame(expected, type(context, "NULLIF(v, 'other')"));
        assertSame(Map.class, type(context, "o.elements"));
    }

    private static Class<?> type(JPQLQueryContext context, String selection) {
        JPQLExpression expression = new JPQLExpression("SELECT " + selection
                + " FROM AliasOwner o JOIN o.elements v", DefaultEclipseLinkJPQLGrammar.instance());
        SelectStatement statement = (SelectStatement) expression.getQueryStatement();
        return context.getType(((SelectClause) statement.getSelectClause()).getSelectExpression());
    }

    private static DirectMapMapping mapMapping() {
        DirectMapMapping mapping = new DirectMapMapping();
        mapping.setDirectFieldName("ELEMENT_VALUE");
        mapping.setDirectKeyFieldName("ELEMENT_KEY");
        mapping.setDirectKeyFieldClassification(String.class);
        return mapping;
    }

    private static DirectCollectionMapping collectionMapping() {
        DirectCollectionMapping mapping = new DirectCollectionMapping();
        mapping.setDirectFieldName("ELEMENT_VALUE");
        return mapping;
    }

    private static JPQLQueryContext context(DirectCollectionMapping mapping) {
        mapping.setAttributeName("elements");
        mapping.dontUseIndirection();
        mapping.setReferenceTableName("ALIAS_ELEMENT");
        mapping.addReferenceKeyFieldName("OWNER_ID", "ID");

        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(mapping.isDirectMapMapping() ? Owner.class : CollectionOwner.class);
        descriptor.setAlias("AliasOwner");
        descriptor.setTableName("ALIAS_OWNER");
        descriptor.addPrimaryKeyFieldName("ID");
        descriptor.addDirectMapping("id", "ID");
        descriptor.addMapping(mapping);
        DatabaseSessionImpl session = new DatabaseSessionImpl(new DatabaseLogin());
        session.addDescriptor(descriptor);
        session.initializeDescriptors();

        String query = "SELECT v FROM AliasOwner o JOIN o.elements v";
        JPQLQueryContext context = new JPQLQueryContext(DefaultEclipseLinkJPQLGrammar.instance());
        context.cache(session, null, new JPQLExpression(query, DefaultEclipseLinkJPQLGrammar.instance()), query);
        return context;
    }

    public static class Owner {
        public Long id;
        public Map<String, String> elements;
    }

    public static class CollectionOwner {
        public Long id;
        public Collection<String> elements;
    }
}
