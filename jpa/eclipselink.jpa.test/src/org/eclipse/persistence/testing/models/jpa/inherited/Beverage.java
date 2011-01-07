/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     06/20/2008-1.0 Guy Pelletier 
 *       - 232975: Failure when attribute type is generic
 *     07/15/2010-2.2 Guy Pelletier 
 *       -311395 : Multiple lifecycle callback methods for the same lifecycle event
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.inherited;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.PostPersist;
import javax.persistence.TableGenerator;
import javax.persistence.MappedSuperclass;
import static javax.persistence.GenerationType.*;

@MappedSuperclass
public class Beverage<T> {
    public static int BEVERAGE_POST_PERSIST_COUNT = 0;
    
    private T id;
    
    public Beverage() {}
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="BEVERAGE_TABLE_GENERATOR")
	@TableGenerator(
        name="BEVERAGE_TABLE_GENERATOR", 
        table="CMP3_BEVERAGE_SEQ", 
        pkColumnName="SEQ_NAME", 
        valueColumnName="SEQ_COUNT",
        pkColumnValue="BEVERAGE_SEQ")
    public T getId() {
        return id;
    }
    
    public void setId(T id) {
        this.id = id;
    }
    
    @PostPersist
    public void celebrateAgain() {
        BEVERAGE_POST_PERSIST_COUNT++;
    }
}
