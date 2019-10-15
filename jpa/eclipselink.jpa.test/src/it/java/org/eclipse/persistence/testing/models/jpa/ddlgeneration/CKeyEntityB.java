/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     11/06/2009-2.0 Guy Pelletier
//       - 286317: UniqueConstraint xml element is changing (plus couple other fixes, see bug)
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import javax.persistence.*;

import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.annotations.Index;

import java.util.Collection;
import java.util.List;

/**
 * Composite Key Entity.
 *
 * @author Wonseok Kim
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "DDL_CKENTB",
    uniqueConstraints = {
        @UniqueConstraint(columnNames={"UNQ2, UNQ1"}) //The order of columns should not be changed. See CKeyEntityA.
})
public class CKeyEntityB {
    @EmbeddedId
    private CKeyEntityBPK key;

    @Column(name = "UNQ1", nullable = false)
    @Index
    private String unq1;

    @Column(name = "UNQ2", nullable = false)
    private String unq2;


    @OneToMany(mappedBy="bs")
    @CascadeOnDelete
    private Collection<CKeyEntityA> as;

    @ManyToMany(mappedBy="bs")
    private Collection<CKeyEntityC> cs;

    @OneToOne(mappedBy="uniqueB")
    @CascadeOnDelete
    private CKeyEntityA uniqueA;

    @OneToMany(cascade={PERSIST, MERGE})
    @JoinColumns({
        @JoinColumn(name="FK_SEQ", referencedColumnName="SEQ"),
        @JoinColumn(name="FK_CODE", referencedColumnName="CODE")
    })
    @CascadeOnDelete
    private List<Comment<String>> comments;

    @ManyToOne
    private Comment<String> comment;

    public CKeyEntityB() {
    }

    public CKeyEntityB(CKeyEntityBPK key) {
        this.key = key;
    }

    public CKeyEntityBPK getKey() {
        return key;
    }

    public void setKey(CKeyEntityBPK key) {
        this.key = key;
    }

    public String getUnq1() {
        return unq1;
    }

    public void setUnq1(String unq1) {
        this.unq1 = unq1;
    }

    public String getUnq2() {
        return unq2;
    }

    public void setUnq2(String unq2) {
        this.unq2 = unq2;
    }

    public Collection<CKeyEntityA> getAs() {
        return as;
    }

    public void setAs(Collection<CKeyEntityA> as) {
        this.as = as;
    }

    public Collection<CKeyEntityC> getCs() {
        return cs;
    }

    public void setCs(Collection<CKeyEntityC> cs) {
        this.cs = cs;
    }

    public CKeyEntityA getUniqueA() {
        return uniqueA;
    }

    public void setUniqueA(CKeyEntityA uniqueA) {
        this.uniqueA = uniqueA;
    }
    public List<Comment<String>> getComments() {
        return comments;
    }

    public void setComments(List<Comment<String>> comments) {
        this.comments = comments;
    }

    public Comment<String> getComment() {
        return comment;
    }

    public void setComment(Comment<String> comment) {
        this.comment = comment;
    }
}
