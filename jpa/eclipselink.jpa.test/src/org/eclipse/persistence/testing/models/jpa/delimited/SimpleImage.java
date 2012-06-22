/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/07/2011-2.3 Chris Delahunt
 *       - bug 338585: Issue while inserting blobs with delimited identifiers on Oracle Database 
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.delimited;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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
