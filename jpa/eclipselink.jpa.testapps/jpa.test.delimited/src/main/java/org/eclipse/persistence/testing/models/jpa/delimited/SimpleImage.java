/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     03/07/2011-2.3 Chris Delahunt
//       - bug 338585: Issue while inserting blobs with delimited identifiers on Oracle Database
package org.eclipse.persistence.testing.models.jpa.delimited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.io.Serializable;

/**
 * @author cdelahun
 *
 */
@Entity
@Table(name="CMP3_DEL_IMAGE")
public class SimpleImage implements Serializable {
    private int id;
    private Byte[] picture;
    private String script;

    @Id
    @GeneratedValue()
    public int getId(){
        return id;
    }

    @Lob
    @Column(length=4000)
    public Byte[] getPicture(){
        return picture;
    }

    @Lob
    @Column(length=4000)
    public String getScript()    {
        return script;
    }

    public void setId(int id)    {
        this.id = id;
    }

    public void setPicture(Byte[] picture)    {
        this.picture = picture;
    }

    public void setScript(String script)    {
        this.script = script;
    }

}
