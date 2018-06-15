/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package jpql.query;

import java.util.Collection;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
