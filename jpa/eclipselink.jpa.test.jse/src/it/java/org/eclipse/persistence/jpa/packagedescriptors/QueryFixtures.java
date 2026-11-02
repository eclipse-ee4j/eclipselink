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
package org.eclipse.persistence.jpa.packagedescriptors;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.Id;
import jakarta.persistence.LockModeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedNativeStatement;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedStatement;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.QueryHint;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;

import org.eclipse.persistence.jpa.packagedescriptors.model.PackageModels.Record;

/** Annotation holders are PU members, but not persistent types. */
public final class QueryFixtures {
    private QueryFixtures() {
    }

    @NamedQuery(name = "holderFind", query = "select r from PackageDescriptorRecord r order by r.id")
    @NamedQuery(name = "holderValues", query = "select r.value from PackageDescriptorRecord r order by r.id")
    @NamedNativeQuery(name = "holderNative", query = "select ROW_ID, ROW_VALUE from JPA_PD_RECORD order by ROW_ID",
            resultClass = Record.class)
    @NamedNativeQuery(name = "holderColumns", query = "select ROW_ID from JPA_PD_RECORD order by ROW_ID",
            columns = @ColumnResult(name = "ROW_ID", type = Long.class))
    @NamedStatement(name = "holderRename", statement = "update PackageDescriptorRecord r set r.value = :value where r.id = :id")
    @NamedStatement(name = "holderDelete", statement = "delete from PackageDescriptorRecord r where r.id = :id")
    @NamedNativeStatement(name = "holderNativeRename", statement = "update JPA_PD_RECORD set ROW_VALUE = ? where ROW_ID = ?")
    @NamedNativeStatement(name = "holderNativeDelete", statement = "delete from JPA_PD_RECORD where ROW_ID = ?")
    public static class Holder {
        static {
            if (Boolean.parseBoolean("true")) {
                throw new AssertionError("Named-query collection must not initialize an annotation holder");
            }
        }
    }

    @NamedQuery(name = "duplicateQuery", query = "select r from PackageDescriptorRecord r")
    @NamedNativeQuery(name = "duplicateNative", query = "select ROW_ID from JPA_PD_RECORD",
            columns = @ColumnResult(name = "ROW_ID", type = Long.class))
    public static class FirstDuplicate {
    }

    @NamedQuery(name = "duplicateQuery", query = "select r from PackageDescriptorRecord r")
    @NamedNativeQuery(name = "duplicateNative", query = "select ROW_ID from JPA_PD_RECORD",
            columns = @ColumnResult(name = "ROW_ID", type = Long.class))
    public static class SecondDuplicate {
    }


    @Embeddable
    @NamedQuery(name = "embeddableFind", query = "select r from PackageDescriptorRecord r")
    @NamedNativeQuery(name = "embeddableNative", query = "select ROW_ID, ROW_VALUE from JPA_PD_RECORD", resultClass = Record.class)
    @NamedStatement(name = "embeddableRename", statement = "update PackageDescriptorRecord r set r.value = :value where r.id = :id")
    @NamedStatement(name = "embeddableDelete", statement = "delete from PackageDescriptorRecord r where r.id = :id")
    @NamedNativeStatement(name = "embeddableNativeRename", statement = "update JPA_PD_RECORD set ROW_VALUE = ? where ROW_ID = ?")
    @NamedNativeStatement(name = "embeddableNativeDelete", statement = "delete from JPA_PD_RECORD where ROW_ID = ?")
    public static class EmbeddableHolder {
        public String value;
    }

    @Entity(name = "RichQueryEntity")
    @Table(name = "JPA_PD_RECORD")
    @NamedEntityGraph(name = "holderGraph")
    @SqlResultSetMapping(name = "holderResultMapping", entities = @EntityResult(entityClass = RichEntity.class))
    public static class RichEntity {
        @Id
        @Column(name = "ROW_ID")
        public int id;
        @Column(name = "ROW_VALUE")
        public String value;
    }

    @NamedQuery(name = "holderLocked", query = "select r from RichQueryEntity r",
            lockMode = LockModeType.PESSIMISTIC_READ, lockScope = PessimisticLockScope.EXTENDED,
            flush = QueryFlushMode.NO_FLUSH, entityGraph = "holderGraph",
            hints = @QueryHint(name = "eclipselink.jdbc.fetch-size", value = "9"))
    @NamedNativeQuery(name = "holderMapping", query = "select ROW_ID, ROW_VALUE from JPA_PD_RECORD",
            resultSetMapping = "holderResultMapping", flush = QueryFlushMode.NO_FLUSH)
    @NamedNativeQuery(name = "holderConstructor", query = "select ROW_VALUE from JPA_PD_RECORD",
            classes = @ConstructorResult(targetClass = Value.class, columns = @ColumnResult(name = "ROW_VALUE", type = String.class)))
    public static class RichHolder {
    }

    public record Value(String value) {
    }

    @NamedQuery(name = "explicitDefaultQuery", query = "select r from PackageDescriptorRecord r",
            resultClass = void.class, lockMode = LockModeType.NONE, lockScope = PessimisticLockScope.NORMAL,
            entityGraph = "", flush = QueryFlushMode.DEFAULT)
    @NamedNativeQuery(name = "explicitDefaultNative", query = "select ROW_ID from JPA_PD_RECORD",
            resultClass = void.class, resultSetMapping = "", flush = QueryFlushMode.DEFAULT,
            columns = @ColumnResult(name = "ROW_ID", type = Long.class))
    public static class FirstExplicitDuplicate {
    }

    @NamedQuery(name = "explicitDefaultQuery", query = "select r from PackageDescriptorRecord r",
            resultClass = void.class, lockMode = LockModeType.NONE, lockScope = PessimisticLockScope.NORMAL,
            entityGraph = "", flush = QueryFlushMode.DEFAULT)
    @NamedNativeQuery(name = "explicitDefaultNative", query = "select ROW_ID from JPA_PD_RECORD",
            resultClass = void.class, resultSetMapping = "", flush = QueryFlushMode.DEFAULT,
            columns = @ColumnResult(name = "ROW_ID", type = Long.class))
    public static class SecondExplicitDuplicate {
    }

    @MappedSuperclass
    @NamedQuery(name = "superclassQuery", query = "select r from OwnedQueryEntity r")
    public static class AnnotatedSuperclass {
        @Id
        @Column(name = "ROW_ID")
        public int id;
    }

    @Entity(name = "OwnedQueryEntity")
    @Table(name = "JPA_PD_RECORD")
    @NamedQuery(name = "entityQuery", query = "select r from OwnedQueryEntity r")
    public static class AnnotatedEntity extends AnnotatedSuperclass {
    }

    @NamedQuery(name = "xmlSuperclassAnnotation", query = "select r from PackageDescriptorRecord r")
    @NamedNativeQuery(name = "xmlSuperclassNative", query = "select ROW_ID, ROW_VALUE from JPA_PD_RECORD", resultClass = Record.class)
    @NamedStatement(name = "xmlSuperclassRename", statement = "update PackageDescriptorRecord r set r.value = :value where r.id = :id")
    @NamedStatement(name = "xmlSuperclassDelete", statement = "delete from PackageDescriptorRecord r where r.id = :id")
    @NamedNativeStatement(name = "xmlSuperclassNativeRename", statement = "update JPA_PD_RECORD set ROW_VALUE = ? where ROW_ID = ?")
    @NamedNativeStatement(name = "xmlSuperclassNativeDelete", statement = "delete from JPA_PD_RECORD where ROW_ID = ?")
    public static class XmlSuperclass {
        public int id;
    }

    public static class XmlEntity extends XmlSuperclass {
    }
}
