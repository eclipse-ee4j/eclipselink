/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.models.jpa.advanced;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ConcurrencyA implements Serializable{

    @Id
    @GeneratedValue
    protected int id;

    protected String name;

    @OneToOne(fetch=FetchType.LAZY)
    protected ConcurrencyB concurrencyB;

    @OneToOne(fetch=FetchType.LAZY)
    protected ConcurrencyC concurrencyC;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the concurrencyB
     */
    public ConcurrencyB getConcurrencyB() {
        return concurrencyB;
    }

    /**
     * @param concurrencyB the concurrencyB to set
     */
    public void setConcurrencyB(ConcurrencyB concurrencyB) {
        this.concurrencyB = concurrencyB;
    }

    /**
     * @return the concurrencyC
     */
    public ConcurrencyC getConcurrencyC() {
        return concurrencyC;
    }

    /**
     * @param concurrencyC the concurrencyC to set
     */
    public void setConcurrencyC(ConcurrencyC concurrencyC) {
        this.concurrencyC = concurrencyC;
    }

}
