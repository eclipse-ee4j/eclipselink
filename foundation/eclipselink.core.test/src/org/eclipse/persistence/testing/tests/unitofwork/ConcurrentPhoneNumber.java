/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - test for bug 324459
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class ConcurrentPhoneNumber {

    protected String type;
    protected String areaCode;
    protected String number;
    protected ValueHolderInterface owner;
    
    public ConcurrentPhoneNumber(){
        owner = new ValueHolder();
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAreaCode() {
        return areaCode;
    }
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public ConcurrentPerson getOwner() {
        return (ConcurrentPerson)owner.getValue();
    }
    public void setOwner(ConcurrentPerson owner) {
        this.owner.setValue(owner);
    }
    
    public ValueHolderInterface getOwnerVH() {
        return owner;
    }
    public void setOwnerVH(ValueHolderInterface owner) {
        this.owner = owner;
    }
    
    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        /* First define the class, table and descriptor properties. */
        descriptor.setJavaClass(ConcurrentPhoneNumber.class);
        descriptor.setTableName("CONCURRENT_PHONE");
        descriptor.addPrimaryKeyFieldName("CONCURRENT_PHONE.EMP_ID");
        descriptor.addPrimaryKeyFieldName("CONCURRENT_PHONE.TYPE");

        /* Next define the attribute mappings. */
        descriptor.addDirectMapping("type", "TYPE");
        descriptor.addDirectMapping("areaCode", "AREA_CODE");
        descriptor.addDirectMapping("number", "PHONE_NUM");
        
        OneToOneMapping ownerMapping = new OneToOneMapping();
        ownerMapping.setAttributeName("owner");
        ownerMapping.setReferenceClass(ConcurrentPerson.class);
        ownerMapping.setGetMethodName("getOwnerVH");
        ownerMapping.setSetMethodName("setOwnerVH");
        ownerMapping.useBasicIndirection();
        ownerMapping.addForeignKeyFieldName("CONCURRENT_PHONE.EMP_ID", "CONCURRENT_EMP.ID");
        descriptor.addMapping(ownerMapping);

        return descriptor;
    }
    
    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("CONCURRENT_PHONE");

        definition.addIdentityField("TYPE", java.lang.String.class, 20);
        definition.addField("AREA_CODE", String.class, 20);
        definition.addField("PHONE_NUM", java.lang.String.class, 20);
        definition.addField("EMP_ID", java.math.BigDecimal.class, 15);

        return definition;
    }
}
