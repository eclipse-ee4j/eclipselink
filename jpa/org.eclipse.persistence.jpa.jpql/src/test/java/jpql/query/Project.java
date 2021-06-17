/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

@Entity
@NamedQueries
({
    @NamedQuery(name="project.abs",     query="SELECT ABS(p.id) FROM Project p"),
    @NamedQuery(name="product.type1",   query="SELECT p FROM Project p WHERE TYPE(p) = AbstractProduct"),
    @NamedQuery(name="product.type2",   query="SELECT p FROM Project p WHERE TYPE(p) IN(LargeProject, SmallProject)"),
    @NamedQuery(name="project.update1", query="UPDATE Project SET name = 'JPQL'"),
    @NamedQuery(name="project.update2", query="UPDATE Project AS p SET p.name = 'JPQL' WHERE p.completed = TRUE"),
    @NamedQuery(name="project.update3", query="UPDATE Project AS p SET p.name = 'JPQL' WHERE p.completed = FALSE"),
    @NamedQuery(name="project.update4", query="UPDATE Project AS p SET p.name = null")
})
@SuppressWarnings("unused")
public class Project {

    private String name;
    @Id private float id;
    @ManyToMany
    private List<Employee> employees;
    private boolean completed;
}
