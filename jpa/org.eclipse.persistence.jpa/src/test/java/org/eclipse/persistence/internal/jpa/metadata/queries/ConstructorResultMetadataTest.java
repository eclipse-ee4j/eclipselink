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
package org.eclipse.persistence.internal.jpa.metadata.queries;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.LockModeType;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAsmFactory;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsMappingProject;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstructorResultMetadataTest {
    private final ServerSession session = new ServerSession();
    private final MetadataAsmFactory factory = new MetadataAsmFactory(new MetadataLogger(session), getClass().getClassLoader());
    private final MetadataProject project = new MetadataProject(new SEPersistenceUnitInfo(), session,
            false, false, false, false, false);
    private final MetadataClass type = factory.getMetadataClass(Declarations.class.getName());

    @Test
    void annotationMetadataProcessesColumnsAndOrderedEntityArguments() {
        ConstructorResultMetadata metadata = annotation();
        var result = metadata.process();
        result.convertClassNamesToClasses(getClass().getClassLoader());
        assertSame(Dto.class, result.getTargetClass());
        assertEquals(List.of("LABEL"), result.getColumnResults().stream().map(column -> column.getColumn().getName()).toList());
        assertEquals(List.of(FirstEntity.class, SecondEntity.class),
                result.getEntityResults().stream().map(org.eclipse.persistence.queries.EntityResult::getEntityClass).toList());
        assertEquals("PESSIMISTIC_READ", result.getEntityResults().getFirst().getLockMode());
        assertEquals("KIND", result.getEntityResults().getFirst().getDiscriminatorColumn().getName());
        assertEquals(1, result.getEntityResults().getFirst().getFieldResults().size());
    }

    @Test
    void separatelyParsedNestedMetadataHasValueBasedEqualityAndHash() {
        ConstructorResultMetadata first = annotation();
        ConstructorResultMetadata second = annotation();
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first.getEntityResults().getFirst().hashCode(), second.getEntityResults().getFirst().hashCode());
        assertEquals(first.getEntityResults().getFirst().getFieldResults().getFirst().hashCode(),
                second.getEntityResults().getFirst().getFieldResults().getFirst().hashCode());
        second.getEntityResults().getFirst().setLockMode("NONE");
        assertNotEquals(first, second);
        second = annotation();
        second.getEntityResults().getFirst().setDiscriminatorColumn("OTHER_KIND");
        assertNotEquals(first, second);
        second = annotation();
        second.getEntityResults().getFirst().getFieldResults().getFirst().setColumn("OTHER_ID");
        assertNotEquals(first, second);
        second = annotation();
        second.setEntityResults(second.getEntityResults().reversed());
        assertNotEquals(first, second);
    }

    @Test
    void ormFourXmlRoundTripInitializesNestedEntitiesAndMatchesAnnotations() {
        String xml = """
                <entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm" version="4.0">
                  <sql-result-set-mapping name="constructor-entities">
                    <constructor-result target-class="%s">
                      <column name="LABEL" class="java.lang.String"/>
                      <entity entity-class="%s" discriminator-column="KIND">
                        <lock-mode>PESSIMISTIC_READ</lock-mode>
                        <field-result name="id" column="FIRST_ID"/>
                      </entity>
                      <entity entity-class="%s"/>
                    </constructor-result>
                  </sql-result-set-mapping>
                </entity-mappings>
                """.formatted(Dto.class.getName(), FirstEntity.class.getName(), SecondEntity.class.getName());
        XMLContext context = new XMLContext(new XMLEntityMappingsMappingProject(
                "https://jakarta.ee/xml/ns/persistence/orm", "orm_4_0.xsd"));
        XMLEntityMappings mappings = (XMLEntityMappings) context.createUnmarshaller().unmarshal(new StringReader(xml));
        StringWriter output = new StringWriter();
        context.createMarshaller().marshal(mappings, output);
        XMLEntityMappings restored = (XMLEntityMappings) context.createUnmarshaller().unmarshal(new StringReader(output.toString()));
        restored.setProject(project);
        restored.setMetadataFactory(factory);
        ConstructorResultMetadata metadata = restored.getSqlResultSetMappings().getFirst().getConstructorResults().getFirst();
        metadata.initXMLObject(type, restored);
        assertEquals(annotation(), metadata);
        assertEquals(annotation().hashCode(), metadata.hashCode());
        var result = metadata.process();
        result.convertClassNamesToClasses(getClass().getClassLoader());
        assertSame(SecondEntity.class, result.getEntityResults().get(1).getEntityClass());
        assertTrue(result.getEntityResults().getFirst().getFieldResults().containsKey("id"));
    }

    private ConstructorResultMetadata annotation() {
        return new ConstructorResultMetadata(type.getMethods().get("result").getAnnotation(ConstructorResult.class.getName()),
                new EntityAccessor(null, type, project));
    }

    interface Declarations {
        @ConstructorResult(targetClass = Dto.class, columns = @ColumnResult(name = "LABEL", type = String.class),
                entities = {
                    @EntityResult(entityClass = FirstEntity.class, lockMode = LockModeType.PESSIMISTIC_READ,
                            discriminatorColumn = "KIND", fields = @FieldResult(name = "id", column = "FIRST_ID")),
                    @EntityResult(entityClass = SecondEntity.class)
                })
        Dto result();
    }

    public record Dto(String label, FirstEntity first, SecondEntity second) { }
    static class FirstEntity { }
    static class SecondEntity { }
}
