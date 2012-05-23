/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     11/17/2010-2.2 Guy Pelletier 
 *       - 329008: Support dynamic context creation without persistence.xml
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class DynamicTableCreator extends TogglingFastTableCreator {

    public DynamicTableCreator() {
        setName("DynamicProjectWithoutPersistenceXML");

        addTableDefinition(buildDYNAMICENTITYTable());
    }
    
    public static TableDefinition buildDYNAMICENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_DYNAMIC_ENTITY");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
        
        FieldDefinition fieldFIRSTNAME = new FieldDefinition();
        fieldFIRSTNAME.setName("F_NAME");
        fieldFIRSTNAME.setTypeName("VARCHAR");
        fieldFIRSTNAME.setSize(40);
        fieldFIRSTNAME.setShouldAllowNull(true);
        fieldFIRSTNAME.setIsPrimaryKey(false);
        fieldFIRSTNAME.setUnique(false);
        fieldFIRSTNAME.setIsIdentity(false);
        table.addField(fieldFIRSTNAME);
        
        FieldDefinition fieldLASTNAME = new FieldDefinition();
        fieldLASTNAME.setName("L_NAME");
        fieldLASTNAME.setTypeName("VARCHAR");
        fieldLASTNAME.setSize(40);
        fieldLASTNAME.setShouldAllowNull(true);
        fieldLASTNAME.setIsPrimaryKey(false);
        fieldLASTNAME.setUnique(false);
        fieldLASTNAME.setIsIdentity(false);
        table.addField(fieldLASTNAME);   
    
        return table;
    }    
}
