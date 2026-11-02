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
package org.eclipse.persistence.jpa.jpqlconstructor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity(name = "ICREdition")
@Table(name = "JPA_ICR_EDITION")
@IdClass(Edition.Key.class)
public class Edition {
    @Id
    @Column(name = "EDITION_NO")
    public Integer number;
    @Id
    @Column(name = "LANG_CODE")
    public String language;

    public Edition() {
    }

    public Edition(Integer number, String language) {
        this.number = number;
        this.language = language;
    }

    public static class Key implements Serializable {
        public Integer number;
        public String language;

        public Key() {
        }

        public Key(Integer number, String language) {
            this.number = number;
            this.language = language;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Key key && Objects.equals(number, key.number) && Objects.equals(language, key.language);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, language);
        }
    }
}
