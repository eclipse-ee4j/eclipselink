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
package org.eclipse.persistence.testing.models.readonly;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class PhoneNumber {
    public String areaCode;
    public String theNumber;
    public String type;

    // The Promoter who owns it. Necessary of for the 1:M in Promoter.
    public ValueHolderInterface thePromoter = new ValueHolder();

    public PhoneNumber() {
        super();
    }

    public static RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();

        descriptor.setJavaClass(org.eclipse.persistence.testing.models.readonly.PhoneNumber.class);
        descriptor.setTableName("RO_PHONE");
        descriptor.addPrimaryKeyFieldName("RO_PHONE.PROMO_ID");
        descriptor.addPrimaryKeyFieldName("RO_PHONE.TYPE");

        descriptor.addDirectMapping("areaCode", "AREA_CODE");
        descriptor.addDirectMapping("theNumber", "THE_NUMBER");
        descriptor.addDirectMapping("type", "TYPE");

        // SECTION: ONETOONEMAPPING
        org.eclipse.persistence.mappings.OneToOneMapping onetoonemapping = new org.eclipse.persistence.mappings.OneToOneMapping();
        onetoonemapping.setAttributeName("thePromoter");
        onetoonemapping.setIsReadOnly(false);
        onetoonemapping.setUsesIndirection(true);
        onetoonemapping.setReferenceClass(Promoter.class);
        onetoonemapping.setIsPrivateOwned(false);
        onetoonemapping.addForeignKeyFieldName("RO_PHONE.PROMO_ID", "RO_PROMO.PROMO_ID");
        descriptor.addMapping(onetoonemapping);

        return descriptor;
    }

    public static PhoneNumber example1(Promoter thePromoter) {
        PhoneNumber example = new PhoneNumber();
        example.areaCode = "613";
        example.theNumber = "745-6732";
        example.type = "V";
        example.thePromoter = new ValueHolder();
        example.thePromoter.setValue(thePromoter);

        return example;
    }

    public static PhoneNumber example2(Promoter thePromoter) {
        PhoneNumber example = new PhoneNumber();
        example.areaCode = "519";
        example.theNumber = "234-0987";
        example.type = "F";
        example.thePromoter = new ValueHolder();
        example.thePromoter.setValue(thePromoter);

        return example;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setName("RO_PHONE");
        definition.addPrimaryKeyField("PROMO_ID", java.math.BigDecimal.class);
        definition.addPrimaryKeyField("TYPE", Integer.class);
        definition.addField("AREA_CODE", String.class, 3);
        definition.addField("THE_NUMBER", String.class, 10);
        return definition;
    }

    public String toString() {
        // Insert code to print the receiver here.
        // This implementation forwards the message to super. You may replace or supplement this.
        return super.toString();
    }
}
