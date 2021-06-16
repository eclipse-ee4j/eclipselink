/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package jpql.query;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;

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
