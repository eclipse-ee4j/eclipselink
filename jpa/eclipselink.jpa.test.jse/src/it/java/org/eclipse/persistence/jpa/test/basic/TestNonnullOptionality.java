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
package org.eclipse.persistence.jpa.test.basic;

import java.util.Set;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.eclipse.persistence.internal.jpa.metamodel.AttributeImpl;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.eclipse.persistence.jpa.test.framework.Emf;
import org.eclipse.persistence.jpa.test.framework.EmfRunner;
import org.eclipse.persistence.jpa.test.framework.Property;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullBase;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullDetails;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullFieldOwner;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullPrimaryKeyOwner;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullPropertyOwner;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullPropertyBase;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullTarget;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullXmlBase;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullXmlDetails;
import org.eclipse.persistence.jpa.test.basic.model.nonnull.NonnullXmlOwner;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EmfRunner.class)
public class TestNonnullOptionality {

    @Emf(name = "NonnullAnnotations", classes = {NonnullBase.class, NonnullDetails.class,
            NonnullFieldOwner.class, NonnullPropertyOwner.class, NonnullPropertyBase.class, NonnullTarget.class, NonnullPrimaryKeyOwner.class},
            properties = {@Property(name = "eclipselink.cache.shared.default", value = "false")})
    private EntityManagerFactory emf;

    @Emf(name = "NonnullXmlOverrides", classes = {NonnullXmlOwner.class, NonnullXmlBase.class, NonnullXmlDetails.class, NonnullTarget.class,
            NonnullFieldOwner.class, NonnullBase.class, NonnullDetails.class},
            mappingFiles = {"META-INF/nonnull-overrides.xml"})
    private EntityManagerFactory xml;

    @Emf(name = "NonnullOwnerComplete", classes = {NonnullXmlOwner.class, NonnullXmlBase.class, NonnullXmlDetails.class, NonnullTarget.class,
            NonnullFieldOwner.class, NonnullBase.class, NonnullDetails.class},
            mappingFiles = {"META-INF/nonnull-owner-complete.xml"})
    private EntityManagerFactory ownerComplete;

    @Emf(name = "NonnullDeclarersComplete", classes = {NonnullXmlOwner.class, NonnullXmlBase.class, NonnullXmlDetails.class, NonnullTarget.class,
            NonnullFieldOwner.class, NonnullBase.class, NonnullDetails.class},
            mappingFiles = {"META-INF/nonnull-declarers-complete.xml"})
    private EntityManagerFactory declarersComplete;

    @Emf(name = "NonnullPuComplete", classes = {NonnullXmlOwner.class, NonnullXmlBase.class, NonnullXmlDetails.class, NonnullTarget.class},
            mappingFiles = {"META-INF/nonnull-pu-complete.xml"})
    private EntityManagerFactory puComplete;

    @Before
    public void verifyFactories() {
        for (EntityManagerFactory factory : new EntityManagerFactory[] {emf, xml, ownerComplete, declarersComplete, puComplete}) {
            Assert.assertNotNull("The factory must be injected", factory);
            Assert.assertTrue("The factory must be open", factory.isOpen());
        }
    }

    @Test
    public void basicFieldOnlyNonnullIsRequired() {
        assertOptional(emf, NonnullFieldOwner.class, "implicitBasic", false);
    }

    @Test
    public void basicFieldExplicitOptionalityHonorsNonnull() {
        assertOptional(emf, NonnullFieldOwner.class, "explicitBasic", false);
        assertOptional(emf, NonnullFieldOwner.class, "requiredBasic", false);
    }

    @Test
    public void basicPropertyOnlyNonnullIsRequired() {
        assertOptional(emf, NonnullPropertyOwner.class, "implicitBasic", false);
    }

    @Test
    public void basicPropertyExplicitOptionalityHonorsNonnull() {
        assertOptional(emf, NonnullPropertyOwner.class, "explicitBasic", false);
        assertOptional(emf, NonnullPropertyOwner.class, "requiredBasic", false);
    }

    @Test
    public void manyToOneFieldHonorsNonnull() {
        assertOptional(emf, NonnullFieldOwner.class, "requiredMany", false);
    }

    @Test
    public void manyToOnePropertyHonorsNonnull() {
        assertOptional(emf, NonnullPropertyOwner.class, "requiredMany", false);
    }

    @Test
    public void oneToOneFieldHonorsNonnull() {
        assertOptional(emf, NonnullFieldOwner.class, "requiredOne", false);
    }

    @Test
    public void oneToOnePropertyHonorsNonnull() {
        assertOptional(emf, NonnullPropertyOwner.class, "requiredOne", false);
    }

    @Test
    public void inverseOneToOneHonorsNonnull() {
        assertOptional(emf, NonnullTarget.class, "inverseOwner", false);
    }

    @Test
    public void unannotatedMappingsKeepOptionalDefaults() {
        assertOptional(emf, NonnullFieldOwner.class, "nullableBasic", true);
        assertOptional(emf, NonnullFieldOwner.class, "optionalMany", true);
        assertOptional(emf, NonnullFieldOwner.class, "optionalOne", true);
        assertOptional(emf, NonnullPropertyOwner.class, "nullableBasic", true);
        assertOptional(emf, NonnullPropertyOwner.class, "optionalMany", true);
        assertOptional(emf, NonnullPropertyOwner.class, "optionalOne", true);
    }

    @Test
    public void explicitFalseControlsRemainRequired() {
        assertOptional(emf, NonnullFieldOwner.class, "requiredControl", false);
        assertOptional(emf, NonnullFieldOwner.class, "requiredManyControl", false);
        assertOptional(emf, NonnullFieldOwner.class, "requiredOneControl", false);
    }

    @Test
    public void inactiveAndTransientMembersRemainIgnored() {
        assertOptional(emf, NonnullFieldOwner.class, "ignoredGetterValue", true);
        assertOptional(emf, NonnullPropertyOwner.class, "ignoredFieldValue", true);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> emf.getMetamodel().entity(NonnullFieldOwner.class).getAttribute("transientValue"));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> emf.getMetamodel().entity(NonnullPropertyOwner.class).getAttribute("transientValue"));
    }

    @Test
    public void nonpersistentNonnullMethodsRemainIgnored() {
        Assert.assertThrows(IllegalArgumentException.class,
                () -> emf.getMetamodel().entity(NonnullPropertyOwner.class).getAttribute("noSetterValue"));
        Assert.assertThrows(IllegalArgumentException.class,
                () -> emf.getMetamodel().entity(NonnullPropertyOwner.class).getAttribute("argumentValue"));
    }

    @Test
    public void nonnullDoesNotChooseMappedSuperclassAccess() {
        assertOptional(emf, NonnullFieldOwner.class, "inheritedValue", false);
        assertOptional(emf, NonnullPropertyOwner.class, "inheritedProperty", false);
        Assert.assertTrue(emf.getMetamodel().entity(NonnullFieldOwner.class)
                .getAttribute("inheritedValue").getJavaMember() instanceof java.lang.reflect.Field);
        Assert.assertTrue(emf.getMetamodel().entity(NonnullPropertyOwner.class)
                .getAttribute("inheritedProperty").getJavaMember() instanceof java.lang.reflect.Method);
    }

    @Test
    public void actualMappingAnnotationsRemainIncompatibleWithTransient() {
        MetadataAnnotatedElement element = new MetadataAnnotatedElement(null);
        for (String name : new String[] {"jakarta.persistence.Transient", "jakarta.persistence.Basic",
                "jakarta.annotation.Nonnull"}) {
            MetadataAnnotation annotation = new MetadataAnnotation();
            annotation.setName(name);
            element.addAnnotation(annotation);
            if (!"jakarta.persistence.Transient".equals(name)) {
                Assert.assertFalse("A real mapping must remain incompatible with Transient",
                        element.areAnnotationsCompatibleWithTransient(null));
            }
        }
    }

    @Test
    public void mappedSuperclassContributesNonnull() {
        assertOptional(emf, NonnullFieldOwner.class, "inheritedValue", false);
    }

    @Test
    public void embeddableContributesNonnull() {
        assertOptional(emf, NonnullDetails.class, "embeddedValue", false);
        assertOptional(emf, NonnullDetails.class, "nullableEmbedded", true);
    }

    @Test
    public void explicitXmlOptionalityOverridesNonnull() {
        assertOptional(xml, NonnullXmlOwner.class, "basicTrue", true);
        assertOptional(xml, NonnullXmlOwner.class, "basicOmitted", true);
        assertOptional(xml, NonnullXmlOwner.class, "manyTrue", true);
        assertOptional(xml, NonnullXmlOwner.class, "oneTrue", true);
        assertOptional(xml, NonnullXmlOwner.class, "basicFalse", false);
        assertOptional(xml, NonnullXmlOwner.class, "manyFalse", false);
        assertOptional(xml, NonnullXmlOwner.class, "oneFalse", false);
    }

    @Test
    public void xmlLeavesUnmappedAnnotationEffective() {
        assertOptional(xml, NonnullXmlOwner.class, "annotationOnly", false);
        assertOptional(xml, NonnullXmlOwner.class, "inheritedValue", false);
        assertOptional(xml, NonnullXmlDetails.class, "detailValue", false);
    }

    @Test
    public void classCompleteSuppressesOwnerAnnotations() {
        assertOptional(ownerComplete, NonnullXmlOwner.class, "annotationOnly", true);
        assertOptional(ownerComplete, NonnullXmlOwner.class, "basicTrue", true);
        assertOptional(ownerComplete, NonnullXmlOwner.class, "basicOmitted", true);
        assertOptional(ownerComplete, NonnullXmlOwner.class, "manyTrue", true);
        assertOptional(ownerComplete, NonnullXmlOwner.class, "oneTrue", true);
        assertOptional(ownerComplete, NonnullXmlOwner.class, "basicFalse", false);
        assertOptional(ownerComplete, NonnullXmlOwner.class, "manyFalse", false);
        assertOptional(ownerComplete, NonnullXmlOwner.class, "oneFalse", false);
    }

    @Test
    public void ownerCompletionPreservesSeparateDeclarers() {
        assertOptional(ownerComplete, NonnullXmlOwner.class, "inheritedValue", false);
        assertOptional(ownerComplete, NonnullXmlDetails.class, "detailValue", false);
    }

    @Test
    public void completedDeclarersSuppressTheirAnnotations() {
        assertOptional(declarersComplete, NonnullXmlOwner.class, "inheritedValue", true);
        assertOptional(declarersComplete, NonnullXmlDetails.class, "detailValue", true);
        assertOptional(declarersComplete, NonnullXmlOwner.class, "annotationOnly", false);
    }

    @Test
    public void puCompleteSuppressesAllAnnotationContributions() {
        assertOptional(puComplete, NonnullXmlOwner.class, "annotationOnly", true);
        assertOptional(puComplete, NonnullXmlOwner.class, "inheritedValue", true);
        assertOptional(puComplete, NonnullXmlOwner.class, "basicTrue", true);
        assertOptional(puComplete, NonnullXmlOwner.class, "basicOmitted", true);
        assertOptional(puComplete, NonnullXmlOwner.class, "manyTrue", true);
        assertOptional(puComplete, NonnullXmlOwner.class, "oneTrue", true);
        assertOptional(puComplete, NonnullXmlDetails.class, "detailValue", true);
        assertOptional(puComplete, NonnullXmlOwner.class, "basicFalse", false);
        assertOptional(puComplete, NonnullXmlOwner.class, "manyFalse", false);
        assertOptional(puComplete, NonnullXmlOwner.class, "oneFalse", false);
    }

    @Test
    public void primaryKeyJoinConstraintFollowsOptionality() {
        assertOptional(emf, NonnullPrimaryKeyOwner.class, "requiredTarget", false);
        assertOptional(emf, NonnullPrimaryKeyOwner.class, "optionalOwner", true);
        var session = emf.unwrap(JpaEntityManagerFactory.class).getServerSession();
        var tables = new DefaultTableGenerator(session.getProject(), true)
                .generateDefaultTableCreator().getTableDefinitions();
        TableDefinition table = tables.stream().filter(t -> "NNOPT_PK".equals(t.getName())).findFirst().orElseThrow();
        Set<String> targets = table.getForeignKeys().stream().map(ForeignKeyConstraint::getTargetTable)
                .collect(Collectors.toSet());
        Assert.assertTrue("Required primary-key join must retain its existing foreign-key generation", targets.contains("NNOPT_TARGET"));
        Assert.assertFalse("Optional primary-key join must not acquire a foreign key", targets.contains("NNOPT_FIELD"));
    }

    @Test
    public void nonnullMappingsAllowValidPersistenceAndReload() {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                NonnullTarget target = new NonnullTarget();
                target.id = 405201L;
                target.label = "nonnull-target";
                NonnullFieldOwner field = new NonnullFieldOwner();
                field.id = 405202L;
                field.inheritedValue = "inherited";
                field.implicitBasic = "field-value";
                field.explicitBasic = "explicit";
                field.requiredBasic = "required";
                field.requiredControl = "control";
                field.requiredMany = target;
                field.requiredManyControl = target;
                field.requiredOne = target;
                field.requiredOneControl = target;
                field.details = new NonnullDetails();
                field.details.embeddedValue = "embedded";
                target.inverseOwner = field;
                NonnullPropertyOwner property = new NonnullPropertyOwner();
                property.setId(405203L);
                property.setImplicitBasic("property-value");
                property.setInheritedProperty("inherited-property");
                property.setExplicitBasic("explicit");
                property.setRequiredBasic("required");
                property.setRequiredMany(target);
                property.setRequiredOne(target);
                em.persist(target);
                em.persist(field);
                em.persist(property);
                em.flush();
                em.clear();
                Assert.assertEquals("field-value", em.createQuery(
                        "SELECT o.implicitBasic FROM NonnullFieldOwner o WHERE o.id = :id", String.class)
                        .setParameter("id", 405202L).getSingleResult());
                NonnullFieldOwner reloadedField = em.find(NonnullFieldOwner.class, 405202L);
                Assert.assertNotNull(reloadedField);
                Assert.assertEquals("inherited", reloadedField.inheritedValue);
                Assert.assertEquals("embedded", reloadedField.details.embeddedValue);
                Assert.assertEquals("nonnull-target", reloadedField.requiredMany.label);
                Assert.assertEquals(405201L, reloadedField.requiredOne.id);
                Assert.assertNull(reloadedField.nullableBasic);
                Assert.assertNull(reloadedField.optionalMany);
                Assert.assertNull(reloadedField.optionalOne);
                NonnullPropertyOwner reloadedProperty = em.find(NonnullPropertyOwner.class, 405203L);
                Assert.assertNotNull(reloadedProperty);
                Assert.assertEquals("property-value", reloadedProperty.getImplicitBasic());
                Assert.assertEquals("inherited-property", reloadedProperty.getInheritedProperty());
                Assert.assertEquals(405201L, reloadedProperty.getRequiredMany().id);
                Assert.assertEquals(405201L, reloadedProperty.getRequiredOne().id);
                Assert.assertNull(reloadedProperty.getOptionalMany());
                Assert.assertNull(reloadedProperty.getOptionalOne());
            } finally {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }
    }

    private static void assertOptional(EntityManagerFactory factory, Class<?> type, String name, boolean optional) {
        var attribute = factory.getMetamodel().managedType(type).getSingularAttribute(name);
        Assert.assertEquals(type.getSimpleName() + "." + name + " public metamodel", optional, attribute.isOptional());
        Assert.assertEquals(type.getSimpleName() + "." + name + " mapping", optional,
                ((AttributeImpl<?, ?>) attribute).getMapping().isOptional());
    }
}
