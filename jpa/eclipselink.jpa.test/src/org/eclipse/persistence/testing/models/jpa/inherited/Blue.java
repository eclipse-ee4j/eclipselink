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
 *     05/30/2008-1.0M8 Guy Pelletier 
 *       - 230213: ValidationException when mapping to attribute in MappedSuperClass
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     08/11/2010-2.2 Guy Pelletier 
 *       - 312123: JPA: Validation error during Id processing on parameterized generic OneToOne Entity relationship from MappedSuperclass
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Inheritance;

import javax.persistence.Table;
import javax.persistence.Entity;

import static javax.persistence.InheritanceType.SINGLE_TABLE;

@Entity
@Inheritance(strategy=SINGLE_TABLE)
@Table(name="CMP3_BLUE")
public class Blue extends Beer<BigDecimal, Float, Blue> implements Bluish<BigInteger, BigInteger>, Cloneable  {
    private BigInteger uniqueKey;
    public Blue() {}
    
    // This class is intentionally left with no annotations to test that
    // it picks us the access type from the mapped superclass.
    
    public boolean equals(Object anotherBlue) {
        if (anotherBlue.getClass() != Blue.class) {
            return false;
        }
        
        return (getId().equals(((Blue)anotherBlue).getId()));
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public BigInteger getUniqueKey() {
        return uniqueKey;
    }
    
    public void setUniqueKey(BigInteger uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
