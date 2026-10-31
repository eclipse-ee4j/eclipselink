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
package org.eclipse.persistence.jpa.packagedescriptors.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.ExcludeDefaultListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostCreate;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreInsert;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/** Small independent models for package membership and listener ordering. */
public final class PackageModels {
    private PackageModels() {
    }

    public interface Tracked {
        List<String> events();
    }

    @Entity(name = "PackageDescriptorRecord")
    @Table(name = "JPA_PD_RECORD")
    @EntityListeners(DeclaredListener.class)
    public static class Record implements Tracked {
        @Id
        @Column(name = "ROW_ID")
        public int id;
        @Column(name = "ROW_VALUE", length = 40)
        public String value;
        @Transient
        public List<String> events = new ArrayList<>();

        public Record() {
        }

        public Record(int id) {
            this.id = id;
            value = "original";
        }

        @Override
        public List<String> events() {
            return events;
        }

        @PreInsert
        public void inserting() {
            events.add("entity-pre");
        }
    }

    @Entity(name = "PackageDescriptorExcluded")
    @Table(name = "JPA_PD_EXCLUDED")
    @ExcludeDefaultListeners
    @EntityListeners(DeclaredListener.class)
    public static class Excluded implements Tracked {
        @Id
        @Column(name = "ROW_ID")
        public int id;
        @Transient
        public List<String> events = new ArrayList<>();

        @Override
        public List<String> events() {
            return events;
        }

        @PreInsert
        public void inserting() {
            events.add("entity-pre");
        }
    }

    /** Its package may belong to a unit without this entity belonging to it. */
    @Entity(name = "PackageDescriptorUnlisted")
    public static class Unlisted {
        @Id
        public int id;
    }

    @Entity(name = "PackageDescriptorGenerated")
    @Table(name = "JPA_PD_GENERATED")
    public static class Generated implements Tracked {
        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "packageDescriptorGenerator")
        @Column(name = "ROW_ID")
        public Integer id;
        @Transient
        public List<String> events = new ArrayList<>();

        @Override
        public List<String> events() {
            return events;
        }
    }

    public static class FirstPackageListener {
        @PreInsert
        public void inserting(Object entity) {
            ((Tracked) entity).events().add("package-first-pre");
        }

        @PostUpdate
        public void updated(Object entity) {
            ((Tracked) entity).events().add("package-first-post-update");
        }
    }

    public static class SecondPackageListener {
        @PreInsert
        public void inserting(Object entity) {
            ((Tracked) entity).events().add("package-second-pre");
        }

        @PostUpdate
        public void updated(Object entity) {
            ((Tracked) entity).events().add("package-second-post-update");
        }
    }

    public static class DeclaredListener {
        @PreInsert
        public void inserting(Object entity) {
            ((Tracked) entity).events().add("declared-pre");
        }
    }

    @EntityListener
    public static class DiscoveredListener {
        public static final AtomicInteger FACTORIES = new AtomicInteger();

        @PreInsert
        public void inserting(Object entity) {
            ((Tracked) entity).events().add("discovered-pre");
        }

        @PostCreate
        public void created(EntityManagerFactory factory) {
            FACTORIES.incrementAndGet();
        }
    }
}
