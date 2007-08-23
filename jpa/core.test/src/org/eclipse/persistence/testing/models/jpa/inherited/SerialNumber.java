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
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.TableGenerator;
import static javax.persistence.GenerationType.*;
import static javax.persistence.CascadeType.*;

@Entity
@Table(name="CMP3_SERIAL_NUMBER")
public class SerialNumber {
    private Alpine alpine;
    private Integer number;
    
    public SerialNumber() {}
    
    @Id
    @Column(name="S_NUMBER")
    @GeneratedValue(strategy=TABLE, generator="SERIALNUMBER_TABLE_GENERATOR")
    @TableGenerator(
        name="SERIALNUMBER_TABLE_GENERATOR", 
        table="CMP3_SERIAL_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="SERIAL_SEQ")
    public Integer getNumber() {
        return number;
    }
    
    @OneToOne(mappedBy="serialNumber")
    public Alpine getAlpine() {
        return alpine;
    }
    
    public void setAlpine(Alpine alpine) {
        this.alpine = alpine;
    }
    
    protected void setNumber(Integer number) {
        this.number = number;
    }
}