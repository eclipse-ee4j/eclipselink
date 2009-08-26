/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - add for testing JPA 2.0 delimited identifiers
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.delimited;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

public class DelimitedSessionCustomizer implements SessionCustomizer {
    
    public void customize(Session session) {
        // Fix already-delimited field names for MySql because of different default delimiter
        if (session.getPlatform().isMySQL()){
            ClassDescriptor descriptor = session.getDescriptor(Employee.class);
            try{
                DatabaseTable table = descriptor.getTable("\"CMP3_DEL_EMPLOYEE\"");
                table.setName("`CMP3_DEL_EMPLOYEE`");
            } catch (Exception e){
                System.out.println("Caught Exception while trying to set Employee table name on org.eclipse.persistence.testing.models.jpa.delimited.DelimitedSessionCustomizer. " + 
                        "This will not necesarily cause problems with tests, but is being logged in case it does: " + e);
            }
            DirectToFieldMapping mapping = (DirectToFieldMapping)descriptor.getMappingForAttributeName("firstName");
            mapping.setFieldName("`F_NAME`");
        }
    }
}

