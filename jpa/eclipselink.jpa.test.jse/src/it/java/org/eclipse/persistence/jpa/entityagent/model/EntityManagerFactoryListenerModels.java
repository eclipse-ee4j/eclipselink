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
package org.eclipse.persistence.jpa.entityagent.model;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ExcludeDefaultListeners;
import jakarta.persistence.ExcludeSuperclassListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PreInsert;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/** Assigned-ID fixtures with per-entity traces; no shared mutable callback recorder. */
public final class EntityManagerFactoryListenerModels {
    private EntityManagerFactoryListenerModels() {
    }

    @Entity(name = "EmfListenerPlain")
    @Table(name = "EA_EMFL_PLAIN")
    @Cacheable
    public static class Plain {
        @Id
        @Column(name = "ROW_ID")
        public int id;
        @Version
        @Column(name = "ROW_VERSION")
        public int version;
        @Column(name = "ROW_VALUE", length = 80)
        public String value;
        @Embedded
        public Details details = new Details();

        public Plain() {
        }

        public Plain(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Embeddable
    public static class Details {
        @Column(name = "DETAIL_VALUE", length = 80)
        public String note = "embedded detail";
    }

    public interface Marker {
    }

    public interface Left extends Marker {
    }

    public interface Right extends Marker {
    }

    @MappedSuperclass
    public static class Base {
        @Id
        @Column(name = "ROW_ID")
        public int id;
        @Version
        @Column(name = "ROW_VERSION")
        public int version;
        @Transient
        public List<String> trace = new ArrayList<>();
    }

    @Entity(name = "EmfListenerRoot")
    @Table(name = "EA_EMFL_TYPED")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "ROW_KIND")
    @DiscriminatorValue("ROOT")
    @EntityListeners(RootListener.class)
    public static class Root extends Base implements Left, Right {
        @Column(name = "ROW_VALUE", length = 80)
        public String value;
    }

    @Entity(name = "EmfListenerChild")
    @DiscriminatorValue("CHILD")
    @ExcludeDefaultListeners
    @ExcludeSuperclassListeners
    public static class Child extends Root {
    }

    public static class RootListener {
        @PreInsert
        public void inserting(Root entity) {
            entity.trace.add("root-metadata");
        }
    }

    @Entity(name = "EmfListenerOrdered")
    @Table(name = "EA_EMFL_ORDERED")
    @EntityListeners(DeclaredListener.class)
    @ValidOrdered
    public static class Ordered extends Base {
        @Column(name = "ROW_VALUE", length = 80)
        public String value;
        @ManyToOne(cascade = CascadeType.REFRESH)
        @JoinColumn(name = "RELATED_ID")
        public Ordered related;
        @ManyToOne(cascade = CascadeType.REFRESH)
        @JoinColumn(name = "SECONDARY_ID")
        public Ordered secondary;
        @Transient
        public String loadTrace = "";
        @Transient
        public Object metadataLoadTarget;
        @Transient
        public Object entityLoadTarget;
        @Transient
        public String metadataLoadValue;
        @Transient
        public String entityLoadValue;

        public void setValue(String value) {
            this.value = value;
        }

        public void clearLoadTrace() {
            loadTrace = "";
            metadataLoadTarget = null;
            entityLoadTarget = null;
            metadataLoadValue = null;
            entityLoadValue = null;
        }

        public void recordLoadPhase(String phase) {
            loadTrace = (loadTrace == null ? "" : loadTrace) + phase + ">";
        }

        @PreInsert
        public void inserting() {
            trace.add("entity");
        }

        @PostLoad
        public void loaded() {
            entityLoadTarget = this;
            entityLoadValue = value;
            recordLoadPhase("entity");
        }
    }

    public static class DeclaredListener {
        @PreInsert
        public void inserting(Ordered entity) {
            entity.trace.add("metadata");
        }

        @PostLoad
        public void loaded(Ordered entity) {
            entity.metadataLoadTarget = entity;
            entity.metadataLoadValue = entity.value;
            entity.recordLoadPhase("metadata");
            // Dedicated metadata-only cases prove mutation without a runtime registration.
            if (entity.value != null && entity.value.startsWith("metadata-only:")) {
                entity.setValue("metadata:" + entity.value);
            }
        }
    }

    @MappedSuperclass
    @EntityListeners(PolymorphicListener.class)
    public static class PolymorphicRecord extends Base {
        @Column(name = "ROW_VALUE", length = 80)
        public String value;
        @Transient
        public String loadTrace = "";
        @Transient
        public Object metadataLoadTarget;
        @Transient
        public Object entityLoadTarget;

        public void setValue(String value) {
            this.value = value;
        }

        public void clearLoadTrace() {
            loadTrace = "";
            metadataLoadTarget = null;
            entityLoadTarget = null;
        }

        public void recordLoadPhase(String phase) {
            loadTrace = (loadTrace == null ? "" : loadTrace) + phase + ">";
        }

        @PostLoad
        public void loaded() {
            entityLoadTarget = this;
            recordLoadPhase("entity");
        }
    }

    @Entity(name = "EmfListenerPolymorphicChild")
    @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
    public abstract static class PolymorphicChild extends PolymorphicRecord {
    }

    @Entity(name = "EmfListenerPolymorphicFirst")
    @Table(name = "EA_EMFL_TPC_FIRST")
    @Cacheable
    @EntityListeners(PolymorphicListener.class)
    public static class PolymorphicFirst extends PolymorphicChild {
        @Override
        @PostLoad
        public void loaded() {
            super.loaded();
        }
    }

    @Entity(name = "EmfListenerPolymorphicSecond")
    @Table(name = "EA_EMFL_TPC_SECOND")
    @Cacheable
    @EntityListeners(PolymorphicListener.class)
    public static class PolymorphicSecond extends PolymorphicChild {
        @Override
        @PostLoad
        public void loaded() {
            super.loaded();
        }
    }

    @Entity(name = "EmfListenerPolymorphicParent")
    @Table(name = "EA_EMFL_TPC_PARENT")
    @Cacheable
    public static class PolymorphicParent extends PolymorphicRecord {
        @ManyToOne(cascade = CascadeType.REFRESH)
        @JoinColumn(name = "RELATED_ID")
        public PolymorphicChild related;
    }

    public static class PolymorphicListener {
        @PostLoad
        public void loaded(PolymorphicRecord entity) {
            entity.metadataLoadTarget = entity;
            entity.recordLoadPhase("metadata");
            if (entity.value != null && entity.value.startsWith("metadata-only:")) {
                entity.setValue("metadata:" + entity.value);
            }
        }
    }

    @Target(TYPE)
    @Retention(RUNTIME)
    @Constraint(validatedBy = OrderValidator.class)
    public @interface ValidOrdered {
        String message() default "Listener value must contain at least three characters";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    public static class OrderValidator implements ConstraintValidator<ValidOrdered, Ordered> {
        @Override
        public boolean isValid(Ordered entity, ConstraintValidatorContext context) {
            if (entity == null) {
                return true;
            }
            entity.trace.add("validation");
            return entity.value != null && entity.value.length() >= 3;
        }
    }
}
