/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.models.jpa.persistence32;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

public class InnerEntitiesContainer {

    @Entity
    @Table(name="PERSISTENCE32_INNER_TEAM")
    public static class InnerTeamEntity {

        @EmbeddedId
        private InnerPK innerPK;

        private String name;
        private InnerEmbeddableClass  embeddableAttribute;

        public InnerTeamEntity() {
        }

        public InnerTeamEntity(int id, String name, InnerEmbeddableClass embeddableAttribute) {
            this.innerPK = new InnerPK(id);
            this.name = name;
            this.embeddableAttribute = embeddableAttribute;
        }

        public InnerPK getInnerPK() {
            return innerPK;
        }

        public void setInnerPK(InnerPK innerPK) {
            this.innerPK = innerPK;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Embedded
        public InnerEmbeddableClass getEmbeddableAttribute() {
            return embeddableAttribute;
        }

        public void setEmbeddableAttribute(InnerEmbeddableClass embeddableAttribute) {
            this.embeddableAttribute = embeddableAttribute;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            InnerTeamEntity that = (InnerTeamEntity) o;
            return Objects.equals(innerPK, that.innerPK) && Objects.equals(name, that.name) && Objects.equals(embeddableAttribute, that.embeddableAttribute);
        }

        @Override
        public int hashCode() {
            return Objects.hash(innerPK, name, embeddableAttribute);
        }
    }

    //It's not public and non - Serializable to verify "Primary key classes are no longer required to be public and serializable" from Jakarta Persistence 3.2.0
    @Embeddable
    static class InnerPK {

        InnerPK() {
        }

        InnerPK(int id) {
            this.id = id;
        }

        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            InnerPK innerPK = (InnerPK) o;
            return id == innerPK.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }


    @Embeddable
    public static class InnerEmbeddableClass {

        private String value1;
        private String value2;

        public InnerEmbeddableClass() {}

        public InnerEmbeddableClass(String value1, String value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public String getValue1() {
            return value1;
        }

        public void setValue1(String value1) {
            this.value1 = value1;
        }

        public String getValue2() {
            return value2;
        }

        public void setValue2(String value2) {
            this.value2 = value2;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            InnerEmbeddableClass that = (InnerEmbeddableClass) o;
            return Objects.equals(value1, that.value1) && Objects.equals(value2, that.value2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value1, value2);
        }
    }
}
