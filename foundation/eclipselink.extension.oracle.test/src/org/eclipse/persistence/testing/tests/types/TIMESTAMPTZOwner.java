/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.types;

import java.io.Serializable;
import java.util.*;
import oracle.sql.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

// Bug#4364359  This object is added to test the fix to overcome TIMESTAMPTZ not serializable 
// as of jdbc 9.2.0.5 and 10.1.0.2.  It has been fixed in the next version for both streams

public class TIMESTAMPTZOwner implements Serializable {

    public int id;
    public Calendar tstzCal;
    public Calendar tsltzCal;        
	public ValueHolderInterface tstz;
    
    public TIMESTAMPTZOwner() {
        this.tstz = new ValueHolder();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TIMESTAMPTZOwner getTstz() {
        return (TIMESTAMPTZOwner)tstz.getValue();
    }
    
    public void setTstz(TIMESTAMPTZOwner aTstz) {
        tstz.setValue(aTstz);
    }

    public Calendar getTstzCal() {
        return tstzCal;
    }
    
    public void setTstzCal(Calendar tstzCal) {
        this.tstzCal = tstzCal;
    }

    public Calendar getTsltzCal() {
        return tsltzCal;
    }
    
    public void setTsltzCal(Calendar tsltzCal) {
        this.tsltzCal = tsltzCal;
    }

	public static TableDefinition tableDefinition()
	{
        TableDefinition definition = new TableDefinition();

		definition.setName("TSTZ_OWNER");

		definition.addField("ID", Integer.class);
		definition.addField("TSTZ", TIMESTAMPTZ.class);
		definition.addField("TSLTZ", TIMESTAMPLTZ.class);
        
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("TSTZ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true );
        field.setIsPrimaryKey(false );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("TSTZ_OBJ.ID");
        definition.addField(field);

		return definition;
	}

    public static RelationalDescriptor descriptor()
	{
		RelationalDescriptor descriptor = new RelationalDescriptor();

		/* First define the class, table and descriptor properties. */
		descriptor.setJavaClass(TIMESTAMPTZOwner.class);
		descriptor.setTableName("TSTZ_OWNER");
		descriptor.setPrimaryKeyFieldName("ID");

		/* Next define the attribute mappings. */
		descriptor.addDirectMapping("id", "ID");
		descriptor.addDirectMapping("tstzCal", "TSTZ");
		descriptor.addDirectMapping("tsltzCal", "TSLTZ");

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("tstz");
        addressMapping.setReferenceClass(TIMESTAMPTZOwner.class);
        addressMapping.useBasicIndirection();
        addressMapping.addForeignKeyFieldName("TSTZ_OWNER.TSTZ_ID", "TSTZ_OWNER.ID");
        descriptor.addMapping(addressMapping);
	
		return descriptor;
    }

    public static TIMESTAMPTZOwner example1() {
        TIMESTAMPTZOwner tstz = new TIMESTAMPTZOwner();
        tstz.setId(1);
        tstz.setTstzCal(Calendar.getInstance());
        tstz.setTsltzCal(Calendar.getInstance());
        return tstz;
    }

    public static TIMESTAMPTZOwner example2() {
        TIMESTAMPTZOwner tstz = new TIMESTAMPTZOwner();
        tstz.setId(2);
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));		
		cal.set(1997, 11, 31, 23, 59, 59);
        tstz.setTstzCal(cal);
        tstz.setTsltzCal(cal);
        tstz.setTstz(example1());
        return tstz;
    }
}
