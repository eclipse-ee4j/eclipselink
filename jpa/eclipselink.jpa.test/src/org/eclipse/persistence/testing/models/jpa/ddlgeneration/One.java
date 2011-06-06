/*******************************************************************************
 * Copyright (c) 2010  Laird Nelson . All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     01/07/2010-2.0.1 Laird Nelson  
 *       - 282075: DDL generation is unpredictable
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.ddlgeneration;

import java.io.Serializable;

import javax.persistence.*;

import org.eclipse.persistence.annotations.Index;

@Entity(name = "One")
@Table(name = "o")
public class One implements Serializable {
    @Id
    @Column(name = "id", length = 4, scale = 10, precision = 0, columnDefinition = "NUMERIC(10)", nullable = false)
    private long id;

    @Column(name = "text", length = 10)
    @Index
    private String text;

    public One() {
        super();
    }

    public One(final long id) {
        super();
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}