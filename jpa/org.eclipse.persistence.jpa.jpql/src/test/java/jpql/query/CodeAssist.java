/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package jpql.query;

import java.util.Collection;
import java.util.Map;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class CodeAssist {

    @Basic
    private String name;
    @Id
    private long id;
    @ManyToOne
    private Employee manager;
    @ManyToMany
    private Collection<Employee> employees;
    @OneToMany
    private Map<Customer, String> customerMap;
    @OneToMany
    private Map<Customer, Address> customerMapAddress;
}
