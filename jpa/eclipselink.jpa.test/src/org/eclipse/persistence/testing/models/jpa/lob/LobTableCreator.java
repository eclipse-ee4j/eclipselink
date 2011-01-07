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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.lob;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class LobTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {

    public LobTableCreator() {
        setName("lob");

        addTableDefinition(buildCLIPTable());
        addTableDefinition(buildIMAGETable());
    }

    public TableDefinition buildCLIPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_CLIP");

        FieldDefinition fieldAUDIO = new FieldDefinition();
        fieldAUDIO.setName("AUDIO");
        fieldAUDIO.setTypeName("BLOB");
        fieldAUDIO.setSize(4800);
        fieldAUDIO.setSubSize(0);
        fieldAUDIO.setIsPrimaryKey(false);
        fieldAUDIO.setIsIdentity(false);
        fieldAUDIO.setUnique(false);
        fieldAUDIO.setShouldAllowNull(true);
        table.addField(fieldAUDIO);

        FieldDefinition fieldCOMMENTARY = new FieldDefinition();
        fieldCOMMENTARY.setName("COMMENTARY");
        fieldCOMMENTARY.setTypeName("CLOB");
        fieldCOMMENTARY.setSize(4500);
        fieldCOMMENTARY.setSubSize(0);
        fieldCOMMENTARY.setIsPrimaryKey(false);
        fieldCOMMENTARY.setIsIdentity(false);
        fieldCOMMENTARY.setUnique(false);
        fieldCOMMENTARY.setShouldAllowNull(true);
        table.addField(fieldCOMMENTARY);

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        return table;
    }

    public TableDefinition buildIMAGETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_IMAGE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(18);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldPICTURE = new FieldDefinition();
        fieldPICTURE.setName("PICTURE");
        fieldPICTURE.setTypeName("BLOB");
        fieldPICTURE.setSize(4800);
        fieldPICTURE.setSubSize(0);
        fieldPICTURE.setIsPrimaryKey(false);
        fieldPICTURE.setIsIdentity(false);
        fieldPICTURE.setUnique(false);
        fieldPICTURE.setShouldAllowNull(true);
        table.addField(fieldPICTURE);

        FieldDefinition fieldSCRIPT = new FieldDefinition();
        fieldSCRIPT.setName("SCRIPT");
        fieldSCRIPT.setTypeName("CLOB");
        fieldSCRIPT.setSize(4500);
        fieldSCRIPT.setSubSize(0);
        fieldSCRIPT.setIsPrimaryKey(false);
        fieldSCRIPT.setIsIdentity(false);
        fieldSCRIPT.setUnique(false);
        fieldSCRIPT.setShouldAllowNull(true);
        table.addField(fieldSCRIPT);

        FieldDefinition fieldCUSTOMATTRIBUTE1 = new FieldDefinition();
        fieldCUSTOMATTRIBUTE1.setName("CUSTOMATTRIBUTE1");
        fieldCUSTOMATTRIBUTE1.setTypeName("BLOB");
        fieldCUSTOMATTRIBUTE1.setSize(0);
        fieldCUSTOMATTRIBUTE1.setSubSize(0);
        fieldCUSTOMATTRIBUTE1.setIsPrimaryKey(false);
        fieldCUSTOMATTRIBUTE1.setIsIdentity(false);
        fieldCUSTOMATTRIBUTE1.setUnique(false);
        fieldCUSTOMATTRIBUTE1.setShouldAllowNull(true);
        table.addField(fieldCUSTOMATTRIBUTE1);

        FieldDefinition fieldCUSTOMATTRIBUTE2 = new FieldDefinition();
        fieldCUSTOMATTRIBUTE2.setName("CUSTOMATTRIBUTE2");
        fieldCUSTOMATTRIBUTE2.setTypeName("BLOB");
        fieldCUSTOMATTRIBUTE2.setSize(0);
        fieldCUSTOMATTRIBUTE2.setSubSize(0);
        fieldCUSTOMATTRIBUTE2.setIsPrimaryKey(false);
        fieldCUSTOMATTRIBUTE2.setIsIdentity(false);
        fieldCUSTOMATTRIBUTE2.setUnique(false);
        fieldCUSTOMATTRIBUTE2.setShouldAllowNull(true);
        table.addField(fieldCUSTOMATTRIBUTE2);
        return table;
    }

}
