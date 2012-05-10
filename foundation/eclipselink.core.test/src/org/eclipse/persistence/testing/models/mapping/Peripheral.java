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
 *     dminsky - initial API and implementation
 *        * added for EL bug 375463
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * Added for EL bug 375463
 * (ObjectTypeMapping Boolean->Char/Char->Boolean and additionalJoinCriteria)
 * See the addToDescriptor method
 */
public class Peripheral {

    protected long id;
    protected String name;
    protected boolean valid;
    
    public Peripheral() {
        super();
    }
    
    public Peripheral(long id) {
        super();
        setId(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    /*
     * Amendment method - essential for demonstrating this regression
     * @param descriptor
     */
    public static void addToDescriptor(ClassDescriptor descriptor) {
        descriptor.getQueryManager().setAdditionalJoinExpression(new ExpressionBuilder().get("valid").equal(true));
    }
    
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("MAP_PERIPHERAL");
        definition.addIdentityField("ID", Long.class, 10);
        definition.addField("NAME", String.class, 25);
        definition.addField("VALID", Character.class, 1);

        return definition;
    }
    
}
