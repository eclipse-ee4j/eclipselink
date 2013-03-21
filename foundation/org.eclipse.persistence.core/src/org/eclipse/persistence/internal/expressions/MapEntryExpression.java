/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.expressions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.InterfaceContainerPolicy;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.querykeys.ForeignReferenceQueryKey;
import org.eclipse.persistence.mappings.querykeys.QueryKey;

public class MapEntryExpression extends QueryKeyExpression {

    protected boolean returnMapEntry = false;
    
    public MapEntryExpression(Expression base) {
        this();
        this.baseExpression = base;
    }
    
    public MapEntryExpression() {
        this.shouldQueryToManyRelationship = true;
        this.hasQueryKey = true;
        this.hasMapping = false;
    }
    
    /**
     * INTERNAL:
     * Find the alias for a given table.  A TableEntry is a place holder and its base expression holds
     * all the relevant information.  Get the alias from the baseExpression
     */
    @Override
    public DatabaseTable aliasForTable(DatabaseTable table) {
        return ((DataExpression)getBaseExpression()).aliasForTable(table);
    }
    
    /**
     * Set this expression to represent a Map.Entry rather than the Map's key
     */
    public void returnMapEntry(){
        returnMapEntry = true;
    }
    
    /**
     * INTERNAL:
     * This expression is built on a different base than the one we want. Rebuild it and
     * return the root of the new tree
     */
    @Override
    public Expression rebuildOn(Expression newBase) {
        Expression newLocalBase = getBaseExpression().rebuildOn(newBase);
        Expression result = null;
        
        if (returnMapEntry){
            result = newLocalBase.mapEntry();
        } else {
            result = newLocalBase.mapKey();
        }

        result.setSelectIfOrderedBy(selectIfOrderedBy());
        return result;
    }
    
    /**
     * INTERNAL:
     * A special version of rebuildOn where the newBase need not be a new
     * ExpressionBuilder but any expression.
     * <p>
     * For nested joined attributes, the joined attribute query must have
     * its joined attributes rebuilt relative to it.
     */
    @Override
    public Expression rebuildOn(Expression oldBase, Expression newBase) {
        if (this == oldBase) {
            return newBase;
        }
        Expression newLocalBase = ((QueryKeyExpression)getBaseExpression()).rebuildOn(oldBase, newBase);
        Expression result = null;

        if (returnMapEntry){
            result = newLocalBase.mapEntry();
        } else {
            result = newLocalBase.mapKey();
        }
        result.setSelectIfOrderedBy(selectIfOrderedBy());
        return result;
    }
    
    /**
     * INTERNAL:
     * Used for debug printing.
     */
    @Override
    public String descriptionOfNodeType() {
        if (returnMapEntry){
            return "MapEntry";
        } else {
            return "MapKey";
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public Expression existingDerivedTable(DatabaseTable table) {
        if (baseExpression.isDataExpression()){
            return ((DataExpression)baseExpression).existingDerivedTable(table);
        }
        return super.existingDerivedTable(table);
    }
    
    /**
     * Exclude any tables defined by base.
     */
    @Override
    public List<DatabaseTable> getOwnedTables() {
        return null;
    }

    @Override
    public ClassDescriptor getDescriptor() {
        if (isAttribute()) {
            return null;
        }
        if (descriptor == null) {
            // Look first for query keys, then mappings. Ultimately we should have query keys
            // for everything and can dispense with the mapping part.
            ForeignReferenceQueryKey queryKey = (ForeignReferenceQueryKey)getQueryKeyOrNull();
            if (queryKey != null) {
                descriptor = getSession().getDescriptor(queryKey.getReferenceClass());
                return descriptor;
            }
            if (getMapping() == null) {
                throw QueryException.invalidQueryKeyInExpression(this);
            }

            // We assume this is either a foreign reference or an aggregate mapping
            descriptor = getMapping().getContainerPolicy().getDescriptorForMapKey();
            if (getMapping().isVariableOneToOneMapping()) {
                throw QueryException.cannotQueryAcrossAVariableOneToOneMapping(getMapping(), descriptor);
            }
        }
        return descriptor;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public DatabaseField getField() {
        if (!isAttribute()) {
            return null;
        }
        DatabaseField field = getInterfaceContainerPolicy().getDirectKeyField(getMapping());
        return field;
    }
    
    /**
     * INTERNAL:
     * Return all the fields
     */
    @Override
    public Vector getFields() {
        Vector result = new Vector();
        InterfaceContainerPolicy icp = getInterfaceContainerPolicy();
        // if this is a map entry get all the fields for both the key and the value
        if (returnMapEntry || !icp.isMappedKeyMapPolicy()){
            result.addAll(getBaseExpression().getFields());
        } else if (isAttribute()) {
            DatabaseField field = getField();
            if (field != null) {
                result.add(field);
            }
        } else {
            result.addAll(getInterfaceContainerPolicy().getAdditionalFieldsForJoin(getMapping()));
        }
        return result;
    }

    @Override
    public CollectionMapping getMapping() {
        return (CollectionMapping)((QueryKeyExpression)getBaseExpression()).getMapping();
    }

    @Override
    public QueryKey getQueryKeyOrNull() {
        if (!hasQueryKey) {
            return null;
        }
        
        InterfaceContainerPolicy cp = getInterfaceContainerPolicy();
        if (queryKey == null) {
            if (returnMapEntry){
                return null;
            } else {
                queryKey = cp.createQueryKeyForMapKey();
            }
        }
        return queryKey;

    }

    /**
     * INTERNAL:
     * Return if the expression is for a direct mapped attribute.
     */
    @Override
    public boolean isAttribute() {
        if (isAttributeExpression == null) {
            if (returnMapEntry){
                return false;
            }
            InterfaceContainerPolicy containerPolicy = getInterfaceContainerPolicy();
            return containerPolicy.isMapKeyAttribute();

        }
        return isAttributeExpression.booleanValue();
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isMapEntryExpression(){
        return true;
    }

    private InterfaceContainerPolicy getInterfaceContainerPolicy(){
        DatabaseMapping mapping = getMapping();
        if (mapping == null) {
            throw QueryException.noMappingForMapEntryExpression(getBaseExpression());
        }

        if (!mapping.isCollectionMapping()){
            throw QueryException.mapEntryExpressionForNonCollection(getBaseExpression(), getMapping());
        }
        InterfaceContainerPolicy cp = null;
        try{
            cp = (InterfaceContainerPolicy)getMapping().getContainerPolicy();
        } catch (ClassCastException e){
            throw QueryException.mapEntryExpressionForNonMap(getBaseExpression(), getMapping());
        }
        return cp;
    }
    
    /**
     * INTERNAL:
     * Mapping criteria will be provided by the base expression
     */
    @Override
    public Expression mappingCriteria(Expression base) {
        return null;
    }

    public boolean shouldReturnMapEntry(){
        return returnMapEntry;
    }

    
    /**
     * Do any required validation for this node. Throw an exception if it's incorrect.
     */
    @Override
    public void validateNode() {
        if ((getQueryKeyOrNull() == null) && (getMapping() == null)) {
            throw QueryException.invalidQueryKeyInExpression(getName());
        }
        if (!getMapping().isCollectionMapping()) {
            throw QueryException.mapEntryExpressionForNonCollection(getBaseExpression(), getMapping());
        }
        ContainerPolicy cp = getMapping().getContainerPolicy();
        if ((cp == null) || !cp.isMapPolicy()) {
            throw QueryException.mapEntryExpressionForNonMap(getBaseExpression(), getMapping());
        }
    }
    
    /**
     * INTERNAL:
     * Used to print a debug form of the expression tree.
     */
    @Override
    public void writeDescriptionOn(BufferedWriter writer) throws IOException {
        writer.write(descriptionOfNodeType());
    }

}

