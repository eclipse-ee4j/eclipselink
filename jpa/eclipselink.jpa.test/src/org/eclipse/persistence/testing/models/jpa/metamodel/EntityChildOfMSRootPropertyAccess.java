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
 *     16/06/2010-2.2  mobrien - 316991: Attribute.getJavaMember() requires reflective getMethod call
 *       when only getMethodName is available on accessor for attributes of Embeddable types.
 *       http://wiki.eclipse.org/EclipseLink/Development/JPA_2.0/metamodel_api#DI_95:_20091017:_Attribute.getJavaMember.28.29_returns_null_for_a_BasicType_on_a_MappedSuperclass_because_of_an_uninitialized_accessor
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.metamodel;

import static javax.persistence.GenerationType.TABLE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.TableGenerator;

@Entity
public class EntityChildOfMSRootPropertyAccess extends MSRootPropertyAccess implements java.io.Serializable {
    private static final long serialVersionUID = -3898581601728869378L;
    
    private int id;
    
    @Id
    @GeneratedValue(strategy=TABLE, generator="EChildOfMS_MM_TABLE_GENERATOR")
    @TableGenerator(
        name="EChildOfMS_MM_TABLE_GENERATOR", 
        table="CMP3_MM_EChildOfMS_SEQ", 
        pkColumnName="SEQ_MM_NAME", 
        valueColumnName="SEQ_MM_COUNT",
        pkColumnValue="CUST_MM_SEQ"
    )
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    

}
