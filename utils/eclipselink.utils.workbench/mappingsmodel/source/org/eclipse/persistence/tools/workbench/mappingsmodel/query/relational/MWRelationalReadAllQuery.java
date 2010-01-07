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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;


import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;

public final class MWRelationalReadAllQuery 
	extends MWAbstractRelationalReadQuery
	implements MWReadAllQuery, MWOrderableQuery
{

	private List batchReadItems;
		public static final String BATCH_READ_ITEMS_LIST = "batchReadItems";
	
	private List orderingItems;
		public static final String ORDERING_ITEMS_LIST = "orderingItems";
	
	
	// **************** static methods ****************	
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalReadAllQuery.class);	
		descriptor.getInheritancePolicy().setParentClass(MWAbstractRelationalReadQuery.class);


		XMLCompositeCollectionMapping batchReadItemsMapping = new XMLCompositeCollectionMapping();
		batchReadItemsMapping.setAttributeName("batchReadItems");
		batchReadItemsMapping.setReferenceClass(MWOrderingItem.class);
		batchReadItemsMapping.setXPath("batch-reads/batch-read-item");
		descriptor.addMapping(batchReadItemsMapping);
		

		XMLCompositeCollectionMapping orderingItemsMapping = new XMLCompositeCollectionMapping();
		orderingItemsMapping.setAttributeName("orderingItems");
		orderingItemsMapping.setReferenceClass(MWOrderingItem.class);
		orderingItemsMapping.setXPath("orderings/ordering-item");
		descriptor.addMapping(orderingItemsMapping);
		
		return descriptor;
	}
		
	/** Default constructor - for TopLink use only. */			
	private MWRelationalReadAllQuery() {
		super();
	}
	
	MWRelationalReadAllQuery(MWRelationalQueryManager parent, String name) {
		super(parent, name);
	}
	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.batchReadItems = new Vector();
		this.orderingItems = new Vector();		
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		synchronized (this.batchReadItems) { children.addAll(this.batchReadItems); }
		synchronized (this.orderingItems) { children.addAll(this.orderingItems); }
	}
	
	
	// ******************* Morphing *******************
	
	
	public MWReportQuery asReportQuery() {
		getQueryManager().removeQuery(this);
		MWReportQuery newQuery = ((MWRelationalQueryManager) getQueryManager()).addReportQuery(getName());
		newQuery.initializeFrom((MWReadAllQuery) this);		
		return newQuery;
	}
	
	public MWReadObjectQuery asReadObjectQuery() {
		getQueryManager().removeQuery(this);
		MWReadObjectQuery newQuery = getQueryManager().addReadObjectQuery(getName());
		((MWAbstractQuery) newQuery).initializeFrom((MWReadAllQuery) this);		
		return newQuery;
	}

	public MWReadAllQuery asReadAllQuery() {
		return this;
	}
	
	public String queryType() {
		return READ_ALL_QUERY;
	}	

	// ********** orderingItems **********

	public Ordering addOrderingItem(MWQueryable queryable) {
        if (orderingAttributesAllowed()) {
            MWOrderingItem orderingItem = new MWOrderingItem(this, queryable);
    		addOrderingItem(orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public Ordering addOrderingItem(Iterator queryables) {
        if (orderingAttributesAllowed()) {
    		MWOrderingItem orderingItem = new MWOrderingItem(this, queryables);
    		addOrderingItem(orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public Ordering addOrderingItem(Iterator queryables, Iterator allowsNull) {
        if (orderingAttributesAllowed()) {
    		MWOrderingItem orderingItem = new MWOrderingItem(this, queryables, allowsNull);
    		addOrderingItem(orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public Ordering addOrderingItem(int index, Iterator queryables, Iterator allowsNull) {
        if (orderingAttributesAllowed()) {
    		MWOrderingItem orderingItem = new MWOrderingItem(this, queryables, allowsNull);
    		addOrderingItem(index, orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
    
    private boolean orderingAttributesAllowed() {
        return getQueryFormat().orderingAttributesAllowed();
    }
    
	private void addOrderingItem(Ordering orderingItem) {
		addOrderingItem(orderingItemsSize(), orderingItem);	
	}
	
	private void addOrderingItem(int index, Ordering orderingItem) {
		addItemToList(index, orderingItem, this.orderingItems, ORDERING_ITEMS_LIST);	
	}	
	
	public void removeOrderingItem(Ordering orderingItem) {
		removeOrderingItem(this.orderingItems.indexOf(orderingItem));
	}
	
	public void removeOrderingItem(int index) {
		removeItemFromList(index, this.orderingItems, ORDERING_ITEMS_LIST);
	}
    
	public void removeOrderingItems(Iterator orderingItems) {
        while(orderingItems.hasNext()) {
            removeOrderingItem((Ordering) orderingItems.next());
        }
    }
    
	public ListIterator orderingItems() {
		return new CloneListIterator(this.orderingItems);
	}	
	
	public int orderingItemsSize() {
		return this.orderingItems.size();
	}

	public int indexOfOrderingItem(Ordering orderingItem) {
		return this.orderingItems.indexOf(orderingItem);
	}
	
	public void moveOrderingItemUp(Ordering item) {
	    int index = indexOfOrderingItem(item);
	    removeOrderingItem(index);
	    addOrderingItem(index - 1, item);
	}
	
	public void moveOrderingItemDown(Ordering item) {
	    int index = indexOfOrderingItem(item);
	    removeOrderingItem(index);
	    addOrderingItem(index + 1, item);
	}	
	
	
	// ********** batchReadItems **********

	public MWBatchReadItem addBatchReadItem(MWQueryable queryable) {
        if (batchReadAttributesAllowed()) {
    		MWBatchReadItem item = new MWBatchReadItem(this, queryable);
    		addBatchReadItem(item);
    		return item;
        }
        throw new IllegalStateException("BatchRead Items are not allowed if the QueryFormat is SQL");
	}
	
	public MWBatchReadItem addBatchReadItem(Iterator queryables) {
        if (batchReadAttributesAllowed()) {
    		MWBatchReadItem item = new MWBatchReadItem(this, queryables);
    		addBatchReadItem(item);
    		return item;
        }
        throw new IllegalStateException("BatchRead Items are not allowed if the QueryFormat is SQL");
	}
	
	public MWBatchReadItem addBatchReadItem(Iterator queryables, Iterator allowsNull) {
        if (batchReadAttributesAllowed()) {
    		MWBatchReadItem item = new MWBatchReadItem(this, queryables, allowsNull);
    		addBatchReadItem(item);
    		return item;
        }
        throw new IllegalStateException("BatchRead Items are not allowed if the QueryFormat is SQL");
	}
	
	public MWBatchReadItem addBatchReadItem(int index, Iterator queryables, Iterator allowsNull) {
        if (batchReadAttributesAllowed()) {
    		MWBatchReadItem item = new MWBatchReadItem(this, queryables, allowsNull);
    		addBatchReadItem(index, item);
    		return item;
        }
        throw new IllegalStateException("BatchRead Items are not allowed if the QueryFormat is SQL");
	}
	
    private boolean batchReadAttributesAllowed() {
        return getQueryFormat().batchReadAttributesAllowed();
    }
    
	private void addBatchReadItem(MWBatchReadItem item) {
		addBatchReadItem(batchReadItemsSize(), item);	
	}
	
	private void addBatchReadItem(int index, MWBatchReadItem item) {
		addItemToList(index, item, this.batchReadItems, BATCH_READ_ITEMS_LIST);	
	}
	
	public void removeBatchReadItem(MWBatchReadItem batchReadItem) {
		removeBatchReadItem(this.batchReadItems.indexOf(batchReadItem));
	}
	
	public void removeBatchReadItem(int index) {
		removeItemFromList(index, this.batchReadItems, BATCH_READ_ITEMS_LIST);
	}
    
    public void removeBatchReadItems(Iterator batchReadItems) {
        while(batchReadItems.hasNext()) {
            removeBatchReadItem((MWBatchReadItem) batchReadItems.next());
        }
    }
    
	public void moveBatchReadItemUp(MWBatchReadItem batchReadItem) {
	    int index = indexOfBatchReadItem(batchReadItem);
	    removeBatchReadItem(index);
	    addBatchReadItem(index-1, batchReadItem);
	}
	
	public void moveBatchReadItemDown(MWBatchReadItem batchReadItem) {
	    int index = indexOfBatchReadItem(batchReadItem);
	    removeBatchReadItem(index);
	    addBatchReadItem(index + 1, batchReadItem);
	}
	
	
	public ListIterator batchReadItems() {
		return new CloneListIterator(this.batchReadItems);
	}	
	
	public int batchReadItemsSize() {
		return this.batchReadItems.size();
	}

	public int indexOfBatchReadItem(MWBatchReadItem item) {
		return this.batchReadItems.indexOf(item);
	}

    //*********** MWRelationalQuery implementation  ***************
    
    public void formatSetToEjbql() {
        removeOrderingItems(orderingItems());
    }
    
    public void formatSetToSql() {
        removeOrderingItems(orderingItems());
        removeBatchReadItems(batchReadItems());
    }
	
	// **************** Runtime conversion ****************	

	public DatabaseQuery runtimeQuery() {
		ReadAllQuery query = (ReadAllQuery) super.runtimeQuery();
		for (Iterator i = batchReadItems(); i.hasNext();) {
			((MWBatchReadItem) i.next()).adjustRuntimeQuery(query);
		}
		
		for (Iterator i = orderingItems(); i.hasNext(); ) {
			((MWOrderingItem) i.next()).adjustRuntimeQuery(query);
		}		
		return query;
	}
	
	protected ObjectLevelReadQuery buildRuntimeQuery() {
		return new ReadAllQuery();
	}
	
}
