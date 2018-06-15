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

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries
({
    @NamedQuery(name = "dept.and.multiple", query = "DELETE FROM Dept d WHERE d.dname = 'DEPT A' AND d.role = 'ROLE A' AND d.loc = 'LOCATION A'"),
    @NamedQuery(name = "dept.findAll",      query = "select o from Dept o"),
    @NamedQuery(name = "dept.dname",        query = "select o from Dept o where o.dname in (:dname1, :dname2, :dname3)"),
    @NamedQuery(name = "dept.floorNumber",  query = "select d.floorNumber from Dept d"),
    @NamedQuery(name = "dept.new1",         query = "SELECT NEW java.util.Vector(d.dname) FROM Dept d")
})
@SuppressWarnings("unused")
public class Dept implements Serializable {
    @Id
    @Column(nullable = false)
    private Long deptno;
    @Column(length = 14)
    private String dname;
    @OneToMany(mappedBy = "dept")
    private List<Employee> empList;
    private int floorNumber;
    @Column(length = 13)
    private String loc;
    private String role;
}
