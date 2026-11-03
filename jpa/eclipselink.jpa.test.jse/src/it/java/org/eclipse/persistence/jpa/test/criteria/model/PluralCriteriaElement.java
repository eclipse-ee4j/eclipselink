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
package org.eclipse.persistence.jpa.test.criteria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "JPA_PC_ELEMENT")
@IdClass(PluralCriteriaElementId.class)
public class PluralCriteriaElement {
    @Id
    @Column(name = "FIRST_ID")
    public int firstId;
    @Id
    @Column(name = "SECOND_ID")
    public int secondId;
    @Column(name = "ELEMENT_TEXT")
    public String text;

    public PluralCriteriaElement() {
    }

    public PluralCriteriaElement(int firstId, int secondId, String text) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.text = text;
    }
}
