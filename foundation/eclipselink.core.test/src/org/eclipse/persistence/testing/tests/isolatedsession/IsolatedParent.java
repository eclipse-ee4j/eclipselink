/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class IsolatedParent {
    
    protected String id;
    protected String serial;
    protected List<IsolatedChild> children;
    
    public IsolatedParent() {
        super();
        this.children = new ArrayList<IsolatedChild>();
    }
    
    public List<IsolatedChild> getChildren() {
        return children;
    }

    public void setChildren(List<IsolatedChild> children) {
        this.children = children;
    }
    
    public void addChild(IsolatedChild child) {
        child.setParent(this);
        getChildren().add(child);
    }
    
    public void removeChild(IsolatedChild child) {
        child.setParent(null);
        getChildren().remove(child);
    }
    
    public String getSerial() {
        return this.serial;
    }
    
    public void setSerial(String serial) {
        this.serial = serial;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public static IsolatedParent buildIsolatedParentExample1() {
        IsolatedParent parent = new IsolatedParent();
        parent.setId("100");
        parent.setSerial("parent-1");
        IsolatedChild child = new IsolatedChild();
        child.setId("200");
        child.setSerial("child-1");
        child.setDeleted("N");
        parent.addChild(child);
        return parent;
    }
    
    public static IsolatedParent buildIsolatedParentExample2() {
        IsolatedParent parent = new IsolatedParent();
        parent.setId("200");
        parent.setSerial("parent-2");
        IsolatedChild child = new IsolatedChild();
        child.setId("300");
        child.setSerial("child-2");
        child.setDeleted("N");
        parent.addChild(child);
        return parent;
    }
    
    public static TableDefinition buildISOLATEDPARENTTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_PARENT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("SERIAL");
        field1.setTypeName("VARCHAR");
        field1.setSize(100);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);
        
        return tabledefinition;
    }
    
    public static void afterLoad(ClassDescriptor descriptor) {
        OneToManyMapping childrenMapping = (OneToManyMapping)descriptor.getMappingForAttributeName("children");
        Expression selectionCriteria = childrenMapping.buildSelectionCriteria();
        ExpressionBuilder builder = new ExpressionBuilder();
        childrenMapping.setSelectionCriteria(selectionCriteria.and(builder.get("deleted").equal("N")));
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " id: [" + getId() + "] hashcode: [" + System.identityHashCode(this) + "]";
    }

}

