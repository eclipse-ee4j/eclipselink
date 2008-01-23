/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  


package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;
import javax.persistence.MappedSuperclass;
import static javax.persistence.GenerationType.*;

@MappedSuperclass
public class Beverage {
    private Integer id;
    
    public Beverage() {}
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="BEVERAGE_TABLE_GENERATOR")
	@TableGenerator(
        name="BEVERAGE_TABLE_GENERATOR", 
        table="CMP3_BEVERAGE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="BEVERAGE_SEQ")
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
}
