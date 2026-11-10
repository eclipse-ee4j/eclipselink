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
package org.eclipse.persistence.jpa.test.criteria.model;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

@Entity
@Table(name = "JPA_CQS_EMPLOYEE")
@SecondaryTable(name = "JPA_CQS_SALARY",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID"))
public class SnapshotCriteriaEmployee {
    @Id
    @Column(name = "EMPLOYEE_ID")
    public int id;

    @Column(name = "EMPLOYEE_NAME")
    public String name;

    @Column(name = "SALARY_VALUE", table = "JPA_CQS_SALARY")
    public int salary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID")
    public SnapshotCriteriaEmployee manager;

    @ManyToOne
    @JoinColumn(name = "DEPARTMENT_ID")
    @JoinFetch(JoinFetchType.OUTER)
    public SnapshotCriteriaDepartment department;

    @ElementCollection
    @CollectionTable(name = "JPA_CQS_TAG", joinColumns = @JoinColumn(name = "EMPLOYEE_ID"))
    @Column(name = "TAG_VALUE")
    public Set<String> tags = new LinkedHashSet<>();

    public SnapshotCriteriaEmployee() {
    }

    public SnapshotCriteriaEmployee(int id, String name, int salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
}
