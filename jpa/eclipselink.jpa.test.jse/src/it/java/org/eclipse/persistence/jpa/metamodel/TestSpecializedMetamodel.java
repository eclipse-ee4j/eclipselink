/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.jpa.metamodel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.PersistenceUnitTransactionType;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.BooleanAttribute;
import jakarta.persistence.metamodel.ComparableAttribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.NumericAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.TemporalAttribute;
import jakarta.persistence.metamodel.TextAttribute;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryDelegate;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metamodel.proxy.AttributeProxyImpl;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.PersistenceProvider;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Absent;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Base;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Base_;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Code;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.CodeConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Details;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Details_;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericInnerConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericNumber;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericNumberConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericOuter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericValue;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericValueConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericWrong;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericWrongConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.GenericWrongInnerConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Invalid;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Legacy;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Legacy_;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Level;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.LevelConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Owner;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Owner_;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Partial;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Partial_;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.PropertyOwner;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.PropertyOwner_;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.StringNumberConverter;
import org.eclipse.persistence.jpa.metamodel.model.SpecializedMetamodelModels.Target;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/** Public bootstrap, metadata and database coverage for specialized attributes. */
public class TestSpecializedMetamodel {
    private static final AtomicLong IDENTITIES = new AtomicLong();
    private static final List<Class<?>> CLASSES = List.of(Base.class, Details.class, Target.class,
            Owner.class, PropertyOwner.class, Legacy.class, Partial.class, Absent.class,
            CodeConverter.class, StringNumberConverter.class, LevelConverter.class,
            GenericValueConverter.class, GenericNumberConverter.class, GenericWrongConverter.class,
            GenericInnerConverter.class, GenericWrongInnerConverter.class);
    private static final List<String> TABLES = List.of("JPA_SM_OWNER", "JPA_SM_TARGET", "JPA_SM_PROPERTY",
            "JPA_SM_LEGACY", "JPA_SM_PARTIAL", "JPA_SM_ABSENT", "JPA_SM_TAGS", "JPA_SM_LIST",
            "JPA_SM_COLL", "JPA_SM_MAP");
    private static EntityManagerFactory fixture;
    private static SchemaManager schema;
    private static boolean ownsSchema;

    @BeforeClass
    public static void createFixture() {
        fixture = open(identity("fixture"));
        schema = new SchemaManager(session(fixture));
        for (String table : TABLES) {
            assertFalse("Refusing to acquire a pre-existing fixture table: " + table, exists(table));
        }
        ownsSchema = true;
        schema.createDefaultTables(false);
        for (String table : TABLES) {
            assertTrue("Provider DDL must create " + table, exists(table));
        }
        try (EntityManager manager = fixture.createEntityManager()) {
            manager.getTransaction().begin();
            try {
                Target target = new Target(1, "target");
                manager.persist(target);
                Owner owner = new Owner();
                owner.id = 1;
                owner.inheritedLabel = "base";
                owner.text = "Alpha";
                owner.quantity = 7;
                owner.active = true;
                owner.published = LocalDate.of(2026, 10, 29);
                owner.amount = new BigDecimal("12.50");
                owner.letter = 'A';
                owner.ordinalLevel = Level.HIGH;
                owner.stringLevel = Level.LOW;
                owner.convertedLevel = Level.HIGH;
                owner.convertedCode = new Code("domain-code");
                owner.numericText = "42";
                owner.genericValue = new GenericValue<>("field-value");
                owner.genericNumber = new GenericNumber<>(11);
                owner.genericWrong = new GenericWrong<>("field-wrong");
                owner.inheritedValue = new GenericValue<>("inherited-value");
                owner.inheritedNumber = new GenericNumber<>(13);
                owner.inheritedWrong = new GenericWrong<>("inherited-wrong");
                owner.innerValue = new GenericOuter<String>().new Inner<Integer>("inner-value");
                owner.wrongInnerValue = new GenericOuter<String>().new WrongInner<Integer>("inner-wrong");
                owner.details = new Details("details", 3);
                owner.target = target;
                owner.tags.add("tag");
                owner.items.add("first");
                owner.entries.add("entry");
                owner.scores.put("score", 9);
                manager.persist(owner);
                Legacy legacy = new Legacy();
                legacy.id = 1;
                legacy.text = "legacy";
                legacy.quantity = 8;
                legacy.active = true;
                legacy.published = LocalDate.of(2026, 1, 1);
                manager.persist(legacy);
                PropertyOwner property = new PropertyOwner();
                property.setId(1);
                property.setText("property");
                property.setQuantity(6);
                property.setGenericValue(new GenericValue<>("property-value"));
                property.setGenericNumber(new GenericNumber<>(17));
                property.setGenericWrong(new GenericWrong<>("property-wrong"));
                manager.persist(property);
                manager.getTransaction().commit();
            } finally {
                if (manager.getTransaction().isActive()) {
                    manager.getTransaction().rollback();
                }
            }
        }
    }

    @AfterClass
    public static void removeFixture() {
        try {
            if (ownsSchema) {
                schema.dropDefaultTables();
                for (String table : TABLES) {
                    assertFalse("Owned fixture table must be removed: " + table, exists(table));
                }
            }
        } finally {
            if (fixture != null && fixture.isOpen()) {
                fixture.close();
            }
        }
    }

    @Before
    public void clearCanonicalFields() throws Exception {
        // Isolate bootstrap paths without relying on JUnit's method order.
        for (Class<?> type : SpecializedMetamodelModels.class.getDeclaredClasses()) {
            if (type.getSimpleName().endsWith("_")) {
                for (Field field : type.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                        field.set(null, null);
                    }
                }
            }
        }
    }

    @Test
    public void runtimeTextAttributes() {
        try (EntityManagerFactory factory = open(identity("text"))) {
            assertKind(owner(factory).getSingularAttribute("text"), TextAttribute.class);
            assertKind(owner(factory).getSingularAttribute("inheritedLabel"), TextAttribute.class);
        }
    }

    @Test
    public void runtimeNumericAttributes() {
        try (EntityManagerFactory factory = open(identity("numeric"))) {
            assertKind(owner(factory).getSingularAttribute("quantity"), NumericAttribute.class);
            assertKind(owner(factory).getSingularAttribute("amount"), NumericAttribute.class);
            assertKind(owner(factory).getSingularAttribute("id"), NumericAttribute.class);
        }
    }

    @Test
    public void runtimeBooleanAttributes() {
        try (EntityManagerFactory factory = open(identity("boolean"))) {
            assertKind(owner(factory).getSingularAttribute("active"), BooleanAttribute.class);
        }
    }

    @Test
    public void runtimeTemporalAttributes() {
        try (EntityManagerFactory factory = open(identity("temporal"))) {
            assertKind(owner(factory).getSingularAttribute("published"), TemporalAttribute.class);
        }
    }

    @Test
    public void runtimeComparableAttributes() {
        try (EntityManagerFactory factory = open(identity("comparable"))) {
            assertKind(owner(factory).getSingularAttribute("letter"), ComparableAttribute.class);
            assertKind(owner(factory).getSingularAttribute("ordinalLevel"), ComparableAttribute.class);
        }
    }

    @Test
    public void staticAttributesMatchRuntimeObjects() {
        try (EntityManagerFactory factory = open(identity("static"))) {
            EntityType<Owner> type = owner(factory);
            assertSame(type, Owner_.class_);
            assertSame(type.getSingularAttribute("text"), Owner_.text);
            assertSame(type.getSingularAttribute("quantity"), Owner_.quantity);
            assertSame(type.getSingularAttribute("active"), Owner_.active);
            assertSame(type.getSingularAttribute("published"), Owner_.published);
            assertSame(type.getSingularAttribute("letter"), Owner_.letter);
            assertSame(factory.getMetamodel().managedType(Base.class).getSingularAttribute("id"), Base_.id);
        }
    }

    @Test
    public void primitiveTypeMetadataRemainsUnboxed() {
        try (EntityManagerFactory factory = open(identity("primitives"))) {
            SingularAttribute<? super Owner, ?> quantity = owner(factory).getSingularAttribute("quantity");
            assertEquals(int.class, quantity.getJavaType());
            assertEquals(int.class, quantity.getBindableJavaType());
            assertEquals(int.class, quantity.getType().getJavaType());
            SingularAttribute<? super Owner, ?> active = owner(factory).getSingularAttribute("active");
            assertEquals(boolean.class, active.getJavaType());
            assertEquals(boolean.class, active.getType().getJavaType());
        }
    }

    @Test
    public void converterClassificationUsesDomainType() {
        try (EntityManagerFactory factory = open(identity("conversion")); EntityManager manager = factory.createEntityManager()) {
            EntityType<Owner> type = owner(factory);
            assertKind(type.getSingularAttribute("convertedCode"), ComparableAttribute.class);
            assertEquals(Code.class, type.getSingularAttribute("convertedCode").getJavaType());
            assertKind(type.getSingularAttribute("numericText"), TextAttribute.class);
            assertEquals(String.class, type.getSingularAttribute("numericText").getJavaType());
            Owner value = manager.find(Owner.class, 1);
            assertEquals(new Code("domain-code"), value.convertedCode);
            assertEquals("42", value.numericText);
        }
    }

    @Test
    public void enumMappingsRemainComparable() {
        try (EntityManagerFactory factory = open(identity("enums")); EntityManager manager = factory.createEntityManager()) {
            for (String attribute : List.of("ordinalLevel", "stringLevel", "convertedLevel")) {
                assertKind(owner(factory).getSingularAttribute(attribute), ComparableAttribute.class);
                assertEquals(Level.class, owner(factory).getSingularAttribute(attribute).getJavaType());
            }
            Owner value = manager.find(Owner.class, 1);
            assertEquals(Level.HIGH, value.ordinalLevel);
            assertEquals(Level.LOW, value.stringLevel);
            assertEquals(Level.HIGH, value.convertedLevel);
        }
    }

    @Test
    public void embeddedAndRelationshipReferencesRemainGeneric() {
        try (EntityManagerFactory factory = open(identity("references"))) {
            SingularAttribute<? super Owner, ?> embedded = owner(factory).getSingularAttribute("details");
            SingularAttribute<? super Owner, ?> relation = owner(factory).getSingularAttribute("target");
            assertKind(embedded, SingularAttribute.class);
            assertKind(relation, SingularAttribute.class);
            assertEquals(Attribute.PersistentAttributeType.EMBEDDED, embedded.getPersistentAttributeType());
            assertFalse(embedded.isAssociation());
            assertEquals(Attribute.PersistentAttributeType.MANY_TO_ONE, relation.getPersistentAttributeType());
            assertTrue(relation.isAssociation());
        }
    }

    @Test
    public void inheritedAndEmbeddableBasicMembersAreSpecialized() throws Exception {
        try (EntityManagerFactory factory = open(identity("inheritance"))) {
            EntityType<Owner> type = owner(factory);
            assertKind(type.getSingularAttribute("inheritedLabel"), TextAttribute.class);
            // EclipseLink keeps descriptor-specific copies of inherited attributes.
            assertSame(type, type.getSingularAttribute("inheritedLabel").getDeclaringType());
            assertEquals(Base.class.getField("inheritedLabel"), type.getSingularAttribute("inheritedLabel").getJavaMember());
            assertEquals(Base.class, Base_.inheritedLabel.getDeclaringType().getJavaType());
            assertSame(factory.getMetamodel().managedType(Base.class), Base_.class_);
            assertKind(factory.getMetamodel().embeddable(Details.class).getSingularAttribute("text"), TextAttribute.class);
            assertSame(factory.getMetamodel().embeddable(Details.class), Details_.class_);
            assertSame(factory.getMetamodel().embeddable(Details.class).getSingularAttribute("rank"), Details_.rank);
        }
    }

    @Test
    public void fieldAccessMetadataIsPreserved() throws Exception {
        try (EntityManagerFactory factory = open(identity("field"))) {
            SingularAttribute<? super Owner, ?> text = owner(factory).getSingularAttribute("text");
            assertEquals(Owner.class.getField("text"), text.getJavaMember());
            assertSame(owner(factory), text.getDeclaringType());
            assertEquals("text", text.getName());
            assertEquals(Attribute.PersistentAttributeType.BASIC, text.getPersistentAttributeType());
            assertFalse(text.isCollection());
            assertFalse(text.isAssociation());
            assertSame(text.getType(), owner(factory).getSingularAttribute("inheritedLabel").getType());
        }
    }

    @Test
    public void propertyAccessMetadataIsPreserved() throws Exception {
        try (EntityManagerFactory factory = open(identity("property")); EntityManager manager = factory.createEntityManager()) {
            EntityType<PropertyOwner> type = factory.getMetamodel().entity(PropertyOwner.class);
            SingularAttribute<? super PropertyOwner, ?> text = type.getSingularAttribute("text");
            assertKind(text, TextAttribute.class);
            assertTrue(text.getJavaMember() instanceof Method);
            assertEquals(PropertyOwner.class.getMethod("getText"), text.getJavaMember());
            assertSame(text, PropertyOwner_.text);
            assertEquals("property", manager.find(PropertyOwner.class, 1).getText());
        }
    }

    @Test
    public void parameterizedFieldTypesRetainSpecialization() throws Exception {
        try (EntityManagerFactory factory = open(identity("generic-field"))) {
            EntityType<Owner> type = owner(factory);
            assertGenericMetadata(type.getSingularAttribute("genericValue"), ComparableAttribute.class, GenericValue.class);
            assertGenericMetadata(type.getSingularAttribute("genericNumber"), NumericAttribute.class, GenericNumber.class);
            assertGenericMetadata(type.getSingularAttribute("genericWrong"), SingularAttribute.class, GenericWrong.class);
            assertSame(type.getSingularAttribute("genericValue"), Owner_.genericValue);
            assertSame(type.getSingularAttribute("genericNumber"), Owner_.genericNumber);
            assertSame(type.getSingularAttribute("genericWrong"), Owner_.genericWrong);
            for (String name : List.of("genericValue", "genericNumber", "genericWrong")) {
                assertEquals(Owner.class.getField(name), type.getSingularAttribute(name).getJavaMember());
            }
        }
    }

    @Test
    public void parameterizedPropertyTypesRetainSpecialization() throws Exception {
        try (EntityManagerFactory factory = open(identity("generic-property"))) {
            EntityType<PropertyOwner> type = factory.getMetamodel().entity(PropertyOwner.class);
            assertGenericMetadata(type.getSingularAttribute("genericValue"), ComparableAttribute.class, GenericValue.class);
            assertGenericMetadata(type.getSingularAttribute("genericNumber"), NumericAttribute.class, GenericNumber.class);
            assertGenericMetadata(type.getSingularAttribute("genericWrong"), SingularAttribute.class, GenericWrong.class);
            assertSame(type.getSingularAttribute("genericValue"), PropertyOwner_.genericValue);
            assertSame(type.getSingularAttribute("genericNumber"), PropertyOwner_.genericNumber);
            assertSame(type.getSingularAttribute("genericWrong"), PropertyOwner_.genericWrong);
            assertEquals(PropertyOwner.class.getMethod("getGenericValue"), PropertyOwner_.genericValue.getJavaMember());
            assertEquals(PropertyOwner.class.getMethod("getGenericNumber"), PropertyOwner_.genericNumber.getJavaMember());
            assertEquals(PropertyOwner.class.getMethod("getGenericWrong"), PropertyOwner_.genericWrong.getJavaMember());
        }
    }

    @Test
    public void inheritedGenericMemberBindingsAreResolved() throws Exception {
        try (EntityManagerFactory factory = open(identity("generic-inheritance"))) {
            EntityType<Owner> type = owner(factory);
            assertGenericMetadata(type.getSingularAttribute("inheritedValue"), ComparableAttribute.class, GenericValue.class);
            assertGenericMetadata(type.getSingularAttribute("inheritedNumber"), NumericAttribute.class, GenericNumber.class);
            assertGenericMetadata(type.getSingularAttribute("inheritedWrong"), SingularAttribute.class, GenericWrong.class);
            for (String name : List.of("inheritedValue", "inheritedNumber", "inheritedWrong")) {
                assertEquals(Base.class.getField(name), type.getSingularAttribute(name).getJavaMember());
            }
        }
    }

    @Test
    public void unresolvedMappedSuperclassBindingsRemainGeneric() {
        try (EntityManagerFactory factory = open(identity("generic-unresolved"))) {
            ManagedType<Base> type = factory.getMetamodel().managedType(Base.class);
            assertGenericMetadata(type.getSingularAttribute("inheritedValue"), SingularAttribute.class, GenericValue.class);
            assertGenericMetadata(type.getSingularAttribute("inheritedNumber"), SingularAttribute.class, GenericNumber.class);
            assertGenericMetadata(type.getSingularAttribute("inheritedWrong"), SingularAttribute.class, GenericWrong.class);
            assertSame(type.getSingularAttribute("inheritedValue"), Base_.inheritedValue);
            assertSame(type.getSingularAttribute("inheritedNumber"), Base_.inheritedNumber);
            assertSame(type.getSingularAttribute("inheritedWrong"), Base_.inheritedWrong);
        }
    }

    @Test
    public void parameterizedOwnerBindingsArePreserved() throws Exception {
        try (EntityManagerFactory factory = open(identity("generic-owner"))) {
            EntityType<Owner> type = owner(factory);
            assertGenericMetadata(type.getSingularAttribute("innerValue"), ComparableAttribute.class, GenericOuter.Inner.class);
            assertGenericMetadata(type.getSingularAttribute("wrongInnerValue"), SingularAttribute.class, GenericOuter.WrongInner.class);
            assertSame(type.getSingularAttribute("innerValue"), Owner_.innerValue);
            assertSame(type.getSingularAttribute("wrongInnerValue"), Owner_.wrongInnerValue);
            assertEquals(Owner.class.getField("innerValue"), Owner_.innerValue.getJavaMember());
            assertEquals(Owner.class.getField("wrongInnerValue"), Owner_.wrongInnerValue.getJavaMember());
        }
    }

    @Test
    public void parameterizedConvertersRoundTripValues() {
        try (EntityManagerFactory factory = open(identity("generic-roundtrip")); EntityManager manager = factory.createEntityManager()) {
            Owner value = manager.find(Owner.class, 1);
            assertEquals(new GenericValue<String>("field-value"), value.genericValue);
            assertEquals(new GenericNumber<String>(11), value.genericNumber);
            assertEquals(new GenericWrong<String>("field-wrong"), value.genericWrong);
            assertEquals(new GenericValue<String>("inherited-value"), value.inheritedValue);
            assertEquals(new GenericNumber<String>(13), value.inheritedNumber);
            assertEquals(new GenericWrong<String>("inherited-wrong"), value.inheritedWrong);
            assertEquals("inner-value", value.innerValue.value());
            assertEquals("inner-wrong", value.wrongInnerValue.value());
            PropertyOwner property = manager.find(PropertyOwner.class, 1);
            assertEquals(new GenericValue<String>("property-value"), property.getGenericValue());
            assertEquals(new GenericNumber<String>(17), property.getGenericNumber());
            assertEquals(new GenericWrong<String>("property-wrong"), property.getGenericWrong());
        }
    }

    @Test
    public void idVersionAndOptionalityArePreserved() {
        try (EntityManagerFactory factory = open(identity("flags"))) {
            EntityType<Owner> type = owner(factory);
            assertTrue(type.getSingularAttribute("id").isId());
            assertFalse(type.getSingularAttribute("id").isVersion());
            assertTrue(type.getSingularAttribute("version").isVersion());
            assertFalse(type.getSingularAttribute("text").isOptional());
            assertTrue(type.getSingularAttribute("amount").isOptional());
            assertFalse(type.getSingularAttribute("quantity").isId());
        }
    }

    @Test
    public void pluralAttributesAndValuesArePreserved() {
        try (EntityManagerFactory factory = open(identity("plural")); EntityManager manager = factory.createEntityManager()) {
            EntityType<Owner> type = owner(factory);
            assertSame(type.getSet("tags", String.class), Owner_.tags);
            assertSame(type.getList("items", String.class), Owner_.items);
            assertSame(type.getCollection("entries", String.class), Owner_.entries);
            assertSame(type.getMap("scores", String.class, Integer.class), Owner_.scores);
            Owner value = manager.find(Owner.class, 1);
            assertEquals(List.of("first"), value.items);
            assertTrue(value.tags.contains("tag"));
            assertTrue(value.entries.contains("entry"));
            assertEquals(Integer.valueOf(9), value.scores.get("score"));
        }
    }

    @Test
    public void legacyCanonicalDeclarationsExecuteCriteriaQueries() {
        try (EntityManagerFactory factory = open(identity("legacy")); EntityManager manager = factory.createEntityManager()) {
            assertSame(factory.getMetamodel().entity(Legacy.class), Legacy_.class_);
            assertKind(Legacy_.text, TextAttribute.class);
            CriteriaBuilder builder = manager.getCriteriaBuilder();
            CriteriaQuery<Legacy> query = builder.createQuery(Legacy.class);
            Root<Legacy> root = query.from(Legacy.class);
            query.select(root).where(builder.equal(root.get(Legacy_.text), "legacy"),
                    builder.gt(root.get(Legacy_.quantity), 5));
            assertEquals(Integer.valueOf(1), manager.createQuery(query).getSingleResult().id);
        }
    }

    @Test
    public void lazyBootstrapInstallsAllSpecializedProxies() {
        try (EntityManagerFactory factory = open(identity("lazy"))) {
            assertFalse(setup(factory).isDeployed());
            for (Attribute<?, ?> attribute : List.of(Owner_.text, Owner_.quantity, Owner_.active, Owner_.published, Owner_.letter)) {
                assertTrue(attribute instanceof AttributeProxyImpl);
                assertSame(null, ((AttributeProxyImpl<?, ?>) attribute).getAttributeInternal());
            }
            assertFalse("Inspecting installed proxies must not deploy the persistence unit", setup(factory).isDeployed());
        }
    }

    @Test public void retainedTextProxyTriggersDeployment() { assertRetainedProxy("text"); }
    @Test public void retainedNumericProxyTriggersDeployment() { assertRetainedProxy("quantity"); }
    @Test public void retainedBooleanProxyTriggersDeployment() { assertRetainedProxy("active"); }
    @Test public void retainedTemporalProxyTriggersDeployment() { assertRetainedProxy("published"); }
    @Test public void retainedComparableProxyTriggersDeployment() { assertRetainedProxy("letter"); }

    @Test
    public void retainedLegacyProxyTriggersDeployment() {
        try (EntityManagerFactory factory = open(identity("legacy-proxy"))) {
            SingularAttribute<Legacy, String> retained = Legacy_.text;
            assertTrue(retained instanceof AttributeProxyImpl);
            assertEquals("text", retained.getName());
            assertEquals(String.class, retained.getJavaType());
            assertSame(factory.getMetamodel().entity(Legacy.class).getSingularAttribute("text"),
                    ((AttributeProxyImpl<?, ?>) retained).getAttributeInternal());
        }
    }

    @Test
    public void sharedFactoriesRemainUsableAfterOneCloses() {
        String name = identity("shared");
        EntityManagerFactory first = open(name);
        try (EntityManagerFactory second = open(name)) {
            assertSame(setup(first), setup(second));
            TextAttribute<Owner> retained = Owner_.text;
            first.close();
            assertTrue(second.isOpen());
            assertEquals("text", retained.getName());
            assertSame(owner(second).getSingularAttribute("text"), Owner_.text);
        } finally {
            if (first.isOpen()) { first.close(); }
        }
    }

    @Test
    public void factoryReopeningReinitializesCompatibleAttributes() {
        String name = identity("reopen");
        try (EntityManagerFactory first = open(name)) {
            assertKind(owner(first).getSingularAttribute("text"), TextAttribute.class);
        }
        try (EntityManagerFactory second = open(name)) {
            EntityType<Owner> type = owner(second);
            assertSame(type.getSingularAttribute("text"), Owner_.text);
            assertEquals("text", Owner_.text.getName());
        }
    }

    @Test
    public void consistentPersistenceUnitsShareUsableStaticMetadata() {
        EntityManagerFactory first = open(identity("unit-one"));
        try (EntityManagerFactory second = open(identity("unit-two"))) {
            owner(first);
            EntityType<Owner> secondType = owner(second);
            first.close();
            assertEquals("text", Owner_.text.getName());
            assertSame(secondType.getSingularAttribute("text"), Owner_.text);
            assertKind(secondType.getSingularAttribute("published"), TemporalAttribute.class);
        } finally {
            if (first.isOpen()) { first.close(); }
        }
    }

    @Test
    public void repeatedMetamodelAccessKeepsIdentity() {
        try (EntityManagerFactory factory = open(identity("repeat"))) {
            EntityType<Owner> type = owner(factory);
            TextAttribute<Owner> text = Owner_.text;
            assertSame(type, owner(factory));
            assertSame(text, Owner_.text);
            assertSame(type.getSingularAttribute("text"), text);
        }
    }

    @Test
    public void missingCanonicalClassesAndFieldsRemainOptional() {
        try (EntityManagerFactory factory = open(identity("missing"))) {
            assertKind(factory.getMetamodel().entity(Absent.class).getSingularAttribute("text"), TextAttribute.class);
            assertKind(factory.getMetamodel().entity(Partial.class).getSingularAttribute("text"), TextAttribute.class);
            assertSame(factory.getMetamodel().entity(Partial.class).getSingularAttribute("id"), Partial_.id);
        }
    }

    @Test
    public void incompatibleCanonicalFieldStillFails() {
        PersistenceConfiguration configuration = baseConfiguration(identity("invalid"))
                .managedClass(Invalid.class)
                .mappingFile("META-INF/specialized-metamodel-invalid-orm.xml");
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                configuration::createEntityManagerFactory);
        assertTrue("An unsupported canonical interface must retain the assignment failure",
                failure.getMessage().contains("UnsupportedAttribute"));
    }

    @Test
    public void containerBootstrapInitializesSpecializedAttributes() {
        SEPersistenceUnitInfo info = new SEPersistenceUnitInfo();
        info.setClassLoader(Thread.currentThread().getContextClassLoader());
        info.setPersistenceUnitName(identity("container"));
        info.setPersistenceUnitRootUrl(Owner.class.getProtectionDomain().getCodeSource().getLocation());
        info.setManagedClassNames(CLASSES.stream().map(Class::getName).toList());
        try (EntityManagerFactory factory = new PersistenceProvider().createContainerEntityManagerFactory(info, properties())) {
            assertKind(owner(factory).getSingularAttribute("text"), TextAttribute.class);
            assertSame(owner(factory).getSingularAttribute("published"), Owner_.published);
        }
    }

    private static void assertRetainedProxy(String name) {
        try (EntityManagerFactory factory = open(identity("retained-" + name))) {
            SingularAttribute<Owner, ?> retained = switch (name) {
                case "text" -> Owner_.text;
                case "quantity" -> Owner_.quantity;
                case "active" -> Owner_.active;
                case "published" -> Owner_.published;
                case "letter" -> Owner_.letter;
                default -> throw new AssertionError(name);
            };
            assertTrue(retained instanceof AttributeProxyImpl);
            assertFalse(setup(factory).isDeployed());
            assertEquals(name, retained.getName());
            assertTrue(setup(factory).isDeployed());
            SingularAttribute<? super Owner, ?> runtime = owner(factory).getSingularAttribute(name);
            assertSame(runtime, ((AttributeProxyImpl<?, ?>) retained).getAttributeInternal());
            assertEquals(runtime.getJavaType(), retained.getJavaType());
            assertSame(runtime.getType(), retained.getType());
            assertEquals(runtime.isId(), retained.isId());
            assertEquals(runtime.isVersion(), retained.isVersion());
            assertEquals(runtime.isOptional(), retained.isOptional());
        }
    }

    private static void assertKind(Attribute<?, ?> attribute, Class<?> kind) {
        assertTrue(kind.getSimpleName() + " required for " + attribute.getName(), kind.isInstance(attribute));
        for (Class<?> specialized : List.of(BooleanAttribute.class, TextAttribute.class, NumericAttribute.class,
                TemporalAttribute.class)) {
            assertEquals("Unexpected specialization " + specialized.getSimpleName() + " for " + attribute.getName(),
                    specialized == kind, specialized.isInstance(attribute));
        }
        assertEquals(kind != SingularAttribute.class, attribute instanceof ComparableAttribute);
    }

    private static void assertGenericMetadata(SingularAttribute<?, ?> attribute, Class<?> kind, Class<?> javaType) {
        assertKind(attribute, kind);
        assertSame(javaType, attribute.getJavaType());
        assertSame(javaType, attribute.getBindableJavaType());
        assertSame(javaType, attribute.getType().getJavaType());
        assertEquals(Attribute.PersistentAttributeType.BASIC, attribute.getPersistentAttributeType());
        assertFalse(attribute.isAssociation());
        assertFalse(attribute.isCollection());
    }

    private static EntityType<Owner> owner(EntityManagerFactory factory) {
        return factory.getMetamodel().entity(Owner.class);
    }

    private static EntityManagerSetupImpl setup(EntityManagerFactory factory) {
        return factory.unwrap(EntityManagerFactoryDelegate.class).getSetupImpl();
    }

    private static PersistenceConfiguration baseConfiguration(String name) {
        return new PersistenceConfiguration(name).provider(PersistenceProvider.class.getName())
                .transactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL)
                .validationMode(ValidationMode.NONE).sharedCacheMode(SharedCacheMode.NONE)
                .properties(properties());
    }

    private static EntityManagerFactory open(String name) {
        PersistenceConfiguration configuration = baseConfiguration(name);
        CLASSES.forEach(configuration::managedClass);
        EntityManagerFactory factory = configuration.createEntityManagerFactory();
        assertNotNull(factory);
        return factory;
    }

    private static Map<String, Object> properties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(PersistenceUnitProperties.WEAVING, "false");
        properties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        properties.put(PersistenceUnitProperties.VALIDATION_MODE, "NONE");
        properties.put(PersistenceUnitProperties.LOGGING_LEVEL, "WARNING");
        for (String name : List.of(PersistenceUnitProperties.JDBC_DRIVER, PersistenceUnitProperties.JDBC_URL,
                PersistenceUnitProperties.JDBC_USER, PersistenceUnitProperties.JDBC_PASSWORD,
                PersistenceUnitProperties.TARGET_DATABASE)) {
            String value = System.getProperty(name);
            if (value != null) { properties.put(name, value); }
        }
        return properties;
    }

    private static ServerSession session(EntityManagerFactory factory) {
        return factory.unwrap(JpaEntityManagerFactory.class).getServerSession();
    }

    private static String identity(String kind) {
        return "specialized-metamodel-" + kind + "-" + IDENTITIES.incrementAndGet();
    }

    private static boolean exists(String name) {
        TableDefinition table = new TableDefinition();
        table.setName(name);
        return schema.checkTableExists(table);
    }
}
