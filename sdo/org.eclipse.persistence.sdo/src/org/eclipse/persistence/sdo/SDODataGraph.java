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
package org.eclipse.persistence.sdo;

import commonj.sdo.DataObject;
import commonj.sdo.ChangeSummary;
import commonj.sdo.Type;
import commonj.sdo.DataGraph;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.impl.HelperProvider;

/**
 * <p><b>Purpose</b>:A data graph is used to package a graph of {@link DataObject data objects} along with their
 * metadata, that is, data describing the data.
 * A data graph also contains a {@link #getChangeSummary change summary}
 * which is used to record changes made to the objects in the graph.
 */
public class SDODataGraph implements DataGraph {
    private HelperContext helperContext;
    private SDODataObject rootObject;
    private SDOChangeSummary changeSummary;

    public SDODataGraph(HelperContext helperContext) {
        super();
        if(null == helperContext) {
            this.helperContext = HelperProvider.getDefaultContext();
        } else {
            this.helperContext = helperContext;
        }
        this.changeSummary = new SDOChangeSummary(this, helperContext);
    }

    /**
     * Returns the root {@link DataObject data object} of this data graph.
     * @return the root data object.
     * @see DataObject#getDataGraph
     */
    public SDODataObject getRootObject() {
        return this.rootObject;
    }

    /**
     * Returns the {@link ChangeSummary change summary} associated with this data graph.
     * @return the change summary.
     * @see ChangeSummary#getDataGraph
     */
    public SDOChangeSummary getChangeSummary() {
        return this.changeSummary;
    }

    /**
     * Returns the {@link Type type} with the given the {@link Type#getURI() URI},
     * or contained by the resource at the given URI,
     * and with the given {@link Type#getName name}.
     * @param uri the namespace URI of a type or the location URI of a resource containing a type.
     * @param typeName name of a type.
     * @return the type with the corresponding namespace and name.
     */
    public SDOType getType(String uri, String typeName) {
        return (SDOType) helperContext.getTypeHelper().getType(uri, typeName);
    }

    /**
     * Creates a new root data object of the {@link #getType specified type}.
     * An exception is thrown if a root object exists.
     * @param namespaceURI namespace of the type.
     * @param typeName name of the type.
     * @return the new root.
     * @throws IllegalStateException if the root object already exists.
     * @see #createRootObject(Type)
     * @see #getType(String, String)
     */
    public SDODataObject createRootObject(String namespaceURI, String typeName) {
        if(null != rootObject) {
            throw new IllegalStateException();
        }
        rootObject = (SDODataObject) helperContext.getDataFactory().create(namespaceURI, typeName);
        rootObject.setDataGraph(this);
        rootObject._setChangeSummary(changeSummary);
        changeSummary.setRootDataObject(rootObject);
        return rootObject;
    }

    /**
     * Creates a new root data object of the specified type.
     * An exception is thrown if a root object exists.
     * @param type the type of the new root.
     * @return the new root.
     * @throws IllegalStateException if the root object already exists.
     * @see #createRootObject(String, String)
     */
    public SDODataObject createRootObject(Type type) {
        if(null != rootObject) {
            throw new IllegalStateException();
        }
        rootObject = (SDODataObject) helperContext.getDataFactory().create(type);
        rootObject.setDataGraph(this);
        rootObject._setChangeSummary(changeSummary);
        changeSummary.setRootDataObject(rootObject);
        return rootObject;
    }
}
