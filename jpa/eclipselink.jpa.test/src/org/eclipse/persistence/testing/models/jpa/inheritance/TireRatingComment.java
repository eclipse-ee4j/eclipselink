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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.inheritance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import static javax.persistence.GenerationType.TABLE;

@Entity
@Table(name="CMP3_TIRE_RATING_COMMENT")
public class TireRatingComment  {
    private Integer id;
    private String comment;
    
    public TireRatingComment() {}
    
    public TireRatingComment(String comment) {
        setComment(comment);
    }
    
    @Column(name="DESCRIP")
    public String getComment() {
        return comment;
    }
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="TIRE_RATING_COMMENT_TABLE_GENERATOR")
	@TableGenerator(
        name="TIRE_RATING_COMMENT_TABLE_GENERATOR", 
        table="CMP3_INHERITANCE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="TIRE_RATING_COMMENT_SEQ")
    @Column(name="ID")
    public Integer getId() {
        return id;
    }

    public void setComment(String comment) {
        this.comment = comment;    
    }
    
	public void setId(Integer id) { 
        this.id = id; 
    }
}
