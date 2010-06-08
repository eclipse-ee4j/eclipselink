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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryManager;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadQuery;
import org.eclipse.persistence.tools.workbench.utility.TriStateBoolean;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneListIterator;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

public final class MWReportQuery 
	extends MWAbstractQuery 
	implements MWRelationalQuery, MWOrderableQuery {

	private volatile MWRelationalSpecificQueryOptions relationalOptions;

	private ReturnChoiceOption returnChoice;
		public final static String RETURN_CHOICE_PROPERTY = "returnChoice";
		public final static String RETURN_SINGLE_RESULT = "returnSingleResult";
		public final static String RETURN_SINGLE_VALUE = "returnSingleValue";
		public final static String RETURN_SINGLE_ATTRIBUTE = "returnSingleAttribute";
		public final static String RETURN_RESULT_COLLECTION = "returnResultCollection";
		public final static String DEFAULT_RETURN_CHOICE = RETURN_RESULT_COLLECTION;
		private static TopLinkOptionSet returnChoiceOptions;

	private RetrievePrimaryKeysOption retrievePrimaryKeys;
		public final static String NO_PRIMARY_KEY = "noPrimaryKey";
		public final static String FULL_PRIMARY_KEY = "fullPrimaryKey";
		public final static String FIRST_PRIMARY_KEY = "firstPrimaryKey";
		public final static String DEFAULT_RETRIEVE_PRIMARY_KEYS = NO_PRIMARY_KEY;
		public final static String RETRIVE_PRIMARY_KEYS_PROPERTY = "retrievePrimaryKeys";
		private static TopLinkOptionSet retrievePrimaryKeysOptions;
	
		
	private List attributeItems;
		public static final String ATTRIBUTE_ITEMS_LIST = "attributeItems";
	
	private List groupingItems;
		public static final String GROUPING_ITEMS_LIST = "groupingItems";

	private List orderingItems;
		public static final String ORDERING_ITEMS_LIST = "orderingItems";
			
		
	// ******************* Static Methods *******************
		
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWReportQuery.class);
		descriptor.getDescriptorInheritancePolicy().setParentClass(MWAbstractQuery.class);
		
		XMLCompositeCollectionMapping attributeItemsMapping = new XMLCompositeCollectionMapping();
		attributeItemsMapping.setAttributeName("attributeItems");
		attributeItemsMapping.setReferenceClass(MWReportAttributeItem.class);
		attributeItemsMapping.setXPath("attributes/attribute-item");
		descriptor.addMapping(attributeItemsMapping);

		XMLCompositeCollectionMapping groupingItemsMapping = new XMLCompositeCollectionMapping();
		groupingItemsMapping.setAttributeName("groupingItems");
		groupingItemsMapping.setReferenceClass(MWGroupingItem.class);
		groupingItemsMapping.setXPath("groupings/grouping");
		descriptor.addMapping(groupingItemsMapping);

		XMLCompositeCollectionMapping orderingItemsMapping = new XMLCompositeCollectionMapping();
		orderingItemsMapping.setAttributeName("orderingItems");
		orderingItemsMapping.setReferenceClass(MWReportOrderingItem.class);
		orderingItemsMapping.setXPath("orderings/ordering");
		descriptor.addMapping(orderingItemsMapping);

		XMLCompositeObjectMapping relationalOptionsMaping = new XMLCompositeObjectMapping();
		relationalOptionsMaping.setAttributeName("relationalOptions");
		relationalOptionsMaping.setReferenceClass(MWRelationalSpecificQueryOptions.class);
		relationalOptionsMaping.setXPath("relational-options");
		descriptor.addMapping(relationalOptionsMaping);

		XMLDirectMapping returnChoiceMapping = new XMLDirectMapping();
		returnChoiceMapping.setAttributeName("returnChoice");
		returnChoiceMapping.setXPath("return-choice/text()");
		ObjectTypeConverter returnChoiceConverter = new ObjectTypeConverter();
		returnChoiceOptions().addConversionValuesForTopLinkTo(returnChoiceConverter);
		returnChoiceMapping.setConverter(returnChoiceConverter);
		returnChoiceMapping.setNullValue(returnChoiceOptions().topLinkOptionForMWModelOption(DEFAULT_RETURN_CHOICE));
		descriptor.addMapping(returnChoiceMapping);
				
		XMLDirectMapping retrievePrimaryKeysMapping = new XMLDirectMapping();
		retrievePrimaryKeysMapping.setAttributeName("retrievePrimaryKeys");
		retrievePrimaryKeysMapping.setXPath("retrieve-primary-keys/text()");
		ObjectTypeConverter retrievePrimaryKeysConverter = new ObjectTypeConverter();
		retrievePrimaryKeysOptions().addConversionValuesForTopLinkTo(retrievePrimaryKeysConverter);
		retrievePrimaryKeysMapping.setConverter(retrievePrimaryKeysConverter);
		retrievePrimaryKeysMapping.setNullValue(retrievePrimaryKeysOptions().topLinkOptionForMWModelOption(DEFAULT_RETRIEVE_PRIMARY_KEYS));
		descriptor.addMapping(retrievePrimaryKeysMapping);

		
		return descriptor;
	}				

			
	public static class ReturnChoiceOption extends TopLinkOption { 

		private ReturnChoiceOption(String mwModelString, String externalString) {
			super(mwModelString, externalString);
		}
		//There is not api ReportQuery.setReturnChoice, so a String of if/else is
		//the only option
		public void setMWOptionOnTopLinkObject(Object query) {
			String returnChoice = getMWModelOption();
			if (returnChoice == RETURN_SINGLE_ATTRIBUTE) {
				((ReportQuery) query).returnSingleAttribute();
			}
			else if (returnChoice == RETURN_SINGLE_RESULT) {
				((ReportQuery) query).returnSingleResult();
			}
            else if (returnChoice == RETURN_SINGLE_VALUE) {
                ((ReportQuery) query).returnSingleValue();
            }           
            else if (returnChoice == RETURN_RESULT_COLLECTION) {
                //default on runtime ReportQuery
            }
            else {
               throw new IllegalStateException();
            }
		}
	}

	public synchronized static TopLinkOptionSet returnChoiceOptions() {
		if (returnChoiceOptions == null) {
		    List list = new ArrayList();
            list.add(new ReturnChoiceOption(RETURN_RESULT_COLLECTION, "RESULT_COLLECTION_RETURN_OPTION"));
            list.add(new ReturnChoiceOption(RETURN_SINGLE_RESULT, "SINGLE_RESULT_RETURN_OPTION"));
            list.add(new ReturnChoiceOption(RETURN_SINGLE_VALUE, "SINGLE_VALUE_RETURN_OPTION"));
            list.add(new ReturnChoiceOption(RETURN_SINGLE_ATTRIBUTE, "SINGLE_ATTRIBUTE_RETURN_OPTION"));
			returnChoiceOptions = new TopLinkOptionSet(list);
		}
		
		return returnChoiceOptions;
	}
	
	
	public static class RetrievePrimaryKeysOption extends TopLinkOption { 

		private RetrievePrimaryKeysOption(String mwModelString, String externalString) {
			super(mwModelString, externalString);
		}
					
		public void setMWOptionOnTopLinkObject(Object query) {		
			if (getMWModelOption() == FULL_PRIMARY_KEY) {
				((ReportQuery) query).setShouldRetrievePrimaryKeys(true);
			}
			else if (getMWModelOption() == FIRST_PRIMARY_KEY) {
				((ReportQuery) query).setShouldRetrieveFirstPrimaryKey(true);
			}
		}
	}

	public synchronized static TopLinkOptionSet retrievePrimaryKeysOptions() {
		if (retrievePrimaryKeysOptions == null) {
            List list = new ArrayList();
            list.add(new RetrievePrimaryKeysOption(NO_PRIMARY_KEY, "NO_PRIMARY_KEY_OPTION"));
            list.add(new RetrievePrimaryKeysOption(FULL_PRIMARY_KEY, "FULL_PRIMARY_KEY_OPTION"));
            list.add(new RetrievePrimaryKeysOption(FIRST_PRIMARY_KEY, "FIRST_PRIMARY_KEY_OPTION"));
		    retrievePrimaryKeysOptions = new TopLinkOptionSet(list);
		}
		
		return retrievePrimaryKeysOptions;
	}	
	
	
	// ******************* Constructors *******************
	
	private MWReportQuery() {
		super();
	}

	MWReportQuery(MWQueryManager queryManager, String name) {
		super(queryManager, name);
	}

	
	protected void initialize(Node parent) {
		super.initialize(parent);
		this.relationalOptions = new MWRelationalSpecificQueryOptions(this);
		this.returnChoice = (ReturnChoiceOption) returnChoiceOptions().topLinkOptionForMWModelOption(DEFAULT_RETURN_CHOICE);
		this.retrievePrimaryKeys = (RetrievePrimaryKeysOption) retrievePrimaryKeysOptions().topLinkOptionForMWModelOption(DEFAULT_RETRIEVE_PRIMARY_KEYS);
		this.attributeItems = new Vector();
		this.groupingItems = new Vector();
		this.orderingItems = new Vector();		
	}
	
	protected void addChildrenTo(List children) {
		super.addChildrenTo(children);
		children.add(this.relationalOptions);
		synchronized (this.attributeItems) { children.addAll(this.attributeItems); }
		synchronized (this.groupingItems) { children.addAll(this.groupingItems); }
		synchronized (this.orderingItems) { children.addAll(this.orderingItems); }
	}
	
	
	// ******************* Morphing *******************

	
	public MWReadAllQuery asReadAllQuery() {
		getQueryManager().removeQuery(this);
		MWReadAllQuery newQuery = getQueryManager().addReadAllQuery(getName());
		((MWAbstractQuery) newQuery).initializeFrom(this);		
		return newQuery;
	}
	
	public MWReadObjectQuery asReadObjectQuery() {
		getQueryManager().removeQuery(this);
		MWReadObjectQuery newQuery = getQueryManager().addReadObjectQuery(getName());
		((MWAbstractQuery) newQuery).initializeFrom(this);		
		return newQuery;
	}
	
	public MWReportQuery asReportQuery() {
		return this;
	}
	
	public void initializeFrom(MWReadQuery query) {
		super.initializeFrom(query);
		initializeFrom((MWRelationalQuery) query);
	}
	
	public void initializeFrom(MWRelationalQuery query) {
		super.initializeFrom(query);
		getRelationalOptions().initializeFrom(query.getRelationalOptions());
	}

	//TODO morphing support
//	public void initializeFrom(MWReadAllQuery query) {
//		super.initializeFrom(query);
//		for (Iterator i = ((MWReportQuery) query).orderingItems(); i.hasNext();) {
//			MWReportOrderingItem orderingItem = (MWReportOrderingItem) i.next();
//			addOrderingItem(orderingItem);
//			orderingItem.setParent(this);
//		}
//		//partial attributes can become attribute items in a report query
//	}
	
	public String queryType() {
		return REPORT_QUERY;
	}
    
    //*********** MWRelationalQuery implementation  ***************
    
    public void formatSetToEjbql() {
        removeAttributeItems(attributeItems());
        removeOrderingItems(orderingItems());
        removeGroupingItems(groupingItems());
    }
    
    public void formatSetToSql() {
        removeOrderingItems(orderingItems());
        removeGroupingItems(groupingItems());
    }
    
	// ******************* Accessors *******************	

	public MWRelationalSpecificQueryOptions getRelationalOptions() {
		return this.relationalOptions;
	}
	
	public String getQueryFormatType() {
		return this.relationalOptions.getQueryFormatType();
	}
	
	public void setQueryFormatType(String type) {
		this.relationalOptions.setQueryFormatType(type);
	}

	public MWQueryFormat getQueryFormat() {
		return this.relationalOptions.getQueryFormat();
	}
	
	public TriStateBoolean isCacheStatement() {
		return this.relationalOptions.isCacheStatement();	
	}
	
	public void setCacheStatement(TriStateBoolean cacheStatement) {
		this.relationalOptions.setCacheStatement(cacheStatement);	
	}

		
	public TriStateBoolean isBindAllParameters() {
		return this.relationalOptions.isBindAllParameters();	
	}
	
	public void setBindAllParameters(TriStateBoolean bindAllParameters) {
		this.relationalOptions.setBindAllParameters(bindAllParameters);	
	}

	public boolean isPrepare() {
		return this.relationalOptions.isPrepare();	
	}
	
	public void setPrepare(boolean bindAllParameters) {
		this.relationalOptions.setPrepare(bindAllParameters);	
	}
	
	
	// ****************** Return Choice ****************

	public ReturnChoiceOption getReturnChoice() {
		return this.returnChoice;
	}
	
	public void setReturnChoice(ReturnChoiceOption returnChoice) {
		Object oldObject = this.returnChoice;
		this.returnChoice = returnChoice;
		firePropertyChanged(RETURN_CHOICE_PROPERTY, oldObject, this.returnChoice);
	}
	
	public void setReturnChoice(String returnChoiceString) {
		setReturnChoice((ReturnChoiceOption) returnChoiceOptions().topLinkOptionForMWModelOption(returnChoiceString));
	}
	
	// ****************** Retrieve Primary Keys ****************

	public RetrievePrimaryKeysOption getRetrievePrimaryKeys() {
		return this.retrievePrimaryKeys;
	}

	public void setRetrievePrimaryKeys(RetrievePrimaryKeysOption retrievePrimaryKeys) {
		Object oldObject = this.retrievePrimaryKeys;
		this.retrievePrimaryKeys = retrievePrimaryKeys;
		firePropertyChanged(RETRIVE_PRIMARY_KEYS_PROPERTY, oldObject, this.retrievePrimaryKeys);
	}
	
	public void setRetrievePrimaryKeys(String retrievePrimaryKeysString) {
	    setRetrievePrimaryKeys((RetrievePrimaryKeysOption) retrievePrimaryKeysOptions().topLinkOptionForMWModelOption(retrievePrimaryKeysString));
	}

	
	// ********** attributeItems **********
	
	public MWReportAttributeItem addAttributeItem(String itemName, MWQueryable queryable) {
        if (attributeItemsAllowed()) {
    		MWReportAttributeItem item = new MWReportAttributeItem(this, itemName, queryable);
    		addAttributeItem(item);
    		return item;
        }
        throw new IllegalStateException("Attribute Items are not allowed if the QueryFormat is EJBQL");
	}
	
	public MWReportAttributeItem addAttributeItem(String itemName, Iterator queryables) {
        if (attributeItemsAllowed()) {
    		MWReportAttributeItem item = new MWReportAttributeItem(this, itemName, queryables);
    		addAttributeItem(item);
    		return item;
        }
        throw new IllegalStateException("Attribute Items are not allowed if the QueryFormat is EJBQL");
	}
	
	public MWReportAttributeItem addAttributeItem(String itemName, Iterator queryables, Iterator allowsNull) {
        if (attributeItemsAllowed()) {
    		MWReportAttributeItem item = new MWReportAttributeItem(this, itemName, queryables, allowsNull);
    		addAttributeItem(item);
    		return item;
        }
        throw new IllegalStateException("Attribute Items are not allowed if the QueryFormat is EJBQL");
	}
	
	public MWReportAttributeItem addAttributeItem(int index, String itemName, Iterator queryables, Iterator allowsNull) {
        if (attributeItemsAllowed()) {
    		MWReportAttributeItem item = new MWReportAttributeItem(this, itemName, queryables, allowsNull);
    		addAttributeItem(index, item);
    		return item;
        }
        throw new IllegalStateException("Attribute Items are not allowed if the QueryFormat is EJBQL");
	}
    
    private boolean attributeItemsAllowed() {
        return getQueryFormat().reportAttributesAllowed();
    }
    
	private void addAttributeItem(MWReportAttributeItem attributeItem) {
		addAttributeItem(attributeItemsSize(), attributeItem);	
	}
	
	private void addAttributeItem(int index, MWReportAttributeItem attributeItem) {
		addItemToList(index, attributeItem, this.attributeItems, ATTRIBUTE_ITEMS_LIST);	
		
	}
	
	public void removeAttributeItem(MWReportAttributeItem attributeItem) {
		removeAttributeItem(this.attributeItems.indexOf(attributeItem));
	}

	public void removeAttributeItem(int index) {
		MWReportOrderingItem orderingItemToRemove = null;
		for (Iterator i = orderingItems(); i.hasNext(); ) {
			MWReportOrderingItem orderingItem = (MWReportOrderingItem) i.next();
			if (orderingItem.attributeItem() == this.attributeItems.get(index)) {
				orderingItemToRemove = orderingItem; 
			}
		}
		
		removeItemFromList(index, this.attributeItems, ATTRIBUTE_ITEMS_LIST);
		if (orderingItemToRemove != null) {
			removeOrderingItem(orderingItemToRemove);
		}
	}	
    
    public void removeAttributeItems(Iterator attributeItems) {
        while(attributeItems.hasNext()) {
            removeAttributeItem((MWReportAttributeItem) attributeItems.next());
        }
    }
    
	public ListIterator attributeItems() {
		return new CloneListIterator(this.attributeItems);
	}	
	
	public int attributeItemsSize() {
		return this.attributeItems.size();
	}

	public int indexOfAttributeItem(MWReportAttributeItem attributeItem) {
		return this.attributeItems.indexOf(attributeItem);
	}
	
	public void moveAttributeItemUp(MWReportAttributeItem item) {
	    int index = indexOfAttributeItem(item);
	    removeAttributeItem(index);
	    addAttributeItem(index - 1, item);
	}
	
	public void moveAttributeItemDown(MWReportAttributeItem item) {
	    int index = indexOfAttributeItem(item);
	    removeAttributeItem(index);
	    addAttributeItem(index + 1, item);
	}
	
	// ********** groupingItems **********

	public MWGroupingItem addGroupingItem(MWQueryable queryable) {
        if (groupingAttribttesAllowed()) {
    		MWGroupingItem item = new MWGroupingItem(this, queryable);
    		addGroupingItem(item);
    		return item;
        }
        throw new IllegalStateException("Grouping Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public MWGroupingItem addGroupingItem(Iterator queryables) {
        if (groupingAttribttesAllowed()) {
    		MWGroupingItem item = new MWGroupingItem(this, queryables);
    		addGroupingItem(item);
    		return item;
        }
        throw new IllegalStateException("Grouping Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public MWGroupingItem addGroupingItem(Iterator queryables, Iterator allowsNull) {
        if (groupingAttribttesAllowed()) {
    		MWGroupingItem item = new MWGroupingItem(this, queryables, allowsNull);
    		addGroupingItem(item);
    		return item;
        }
        throw new IllegalStateException("Grouping Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public MWGroupingItem addGroupingItem(int index, Iterator queryables, Iterator allowsNull) {
        if (groupingAttribttesAllowed()) {
            MWGroupingItem item = new MWGroupingItem(this, queryables, allowsNull);
            addGroupingItem(index, item);
            return item;
        }
        throw new IllegalStateException("Grouping Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
        
    private boolean groupingAttribttesAllowed() {
        return getQueryFormat().groupingAtributesAllowed();
    }
    
	private void addGroupingItem(MWGroupingItem item) {
		addGroupingItem(groupingItemsSize(), item);	
	}
	
	private void addGroupingItem(int index, MWGroupingItem item) {
		addItemToList(index, item, this.groupingItems, GROUPING_ITEMS_LIST);	
	}
	
	public void removeGroupingItem(MWGroupingItem item) {
		removeGroupingItem(this.groupingItems.indexOf(item));
	}

	public void removeGroupingItem(int index) {
		removeItemFromList(index, this.groupingItems, GROUPING_ITEMS_LIST);	
	}
    
    public void removeGroupingItems(Iterator groupingItems) {
        while(groupingItems.hasNext()) {
            removeGroupingItem((MWGroupingItem) groupingItems.next());
        }
    }
    
	public ListIterator groupingItems() {
		return new CloneListIterator(this.groupingItems);
	}	
	
	public int groupingItemsSize() {
		return this.groupingItems.size();
	}
	
	public int indexOfGroupingItem(MWGroupingItem item) {
		return this.groupingItems.indexOf(item);
	}
	
	public void moveGroupingItemUp(MWGroupingItem item) {
	    int index = indexOfGroupingItem(item);
	    removeGroupingItem(index);
	    addGroupingItem(index-1, item);
	}
	
	public void moveGroupingItemDown(MWGroupingItem item) {
	    int index = indexOfGroupingItem(item);
	    removeGroupingItem(index);
	    addGroupingItem(index + 1, item);
	}

	
	// ********** orderingItems **********

	public Ordering addOrderingItem(MWQueryable queryable) {
        if (orderingAttributesAllowed()) {
    		MWReportOrderingItem orderingItem = new MWReportOrderingItem(this, queryable);
    		addOrderingItem(orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public Ordering addOrderingItem(Iterator queryables) {
        if (orderingAttributesAllowed()) {
    		MWReportOrderingItem orderingItem = new MWReportOrderingItem(this, queryables);
    		addOrderingItem(orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public Ordering addOrderingItem(Iterator queryables, Iterator allowsNull) {
        if (orderingAttributesAllowed()) {
    		MWReportOrderingItem orderingItem = new MWReportOrderingItem(this, queryables, allowsNull);
    		addOrderingItem(orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public Ordering addOrderingItem(int index, Iterator queryables, Iterator allowsNull) {
        if (orderingAttributesAllowed()) {
    		MWReportOrderingItem orderingItem = new MWReportOrderingItem(this, queryables, allowsNull);
    		addOrderingItem(index, orderingItem);
    		return orderingItem;
        }
        throw new IllegalStateException("Ordering Items are not allowed if the QueryFormat is EJBQL or SQL");
	}
	
	public MWReportOrderingItem addOrderingItem(MWReportAttributeItem attributeItem) {
	    MWReportOrderingItem item = new MWReportOrderingItem(this, attributeItem);
	    addOrderingItem(item);
	    return item;
	}
	
	private void addOrderingItem(Ordering orderingItem) {
		addOrderingItem(orderingItemsSize(), orderingItem);	
	}
	
	private void addOrderingItem(int index, Ordering orderingItem) {
        addItemToList(index, orderingItem, this.orderingItems, ORDERING_ITEMS_LIST);
	}	
    
	private boolean orderingAttributesAllowed() {
        return getQueryFormat().orderingAttributesAllowed();
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
	
	
	public void notifyExpressionsToRecalculateQueryables() {
		this.relationalOptions.notifyExpressionsToRecalculateQueryables();	
	}	

	
	// ******************* Runtime Conversion *******************
	
	public DatabaseQuery runtimeQuery() {
		ReportQuery query = (ReportQuery) super.runtimeQuery();
		
		getReturnChoice().setMWOptionOnTopLinkObject(query);
		getRetrievePrimaryKeys().setMWOptionOnTopLinkObject(query);
		
		for (Iterator i = attributeItems(); i.hasNext(); ) {
			((MWReportAttributeItem) i.next()).adjustRuntimeQuery(query);
		}

		for (Iterator i = groupingItems(); i.hasNext(); ) {
			((MWGroupingItem) i.next()).adjustRuntimeQuery(query);
		}
		
		for (Iterator i = orderingItems(); i.hasNext(); ) {
			((MWReportOrderingItem) i.next()).adjustRuntimeQuery(query);
		}
		
		getRelationalOptions().adjustRuntimeQuery(query);
		 		
		return query;
	}
	
	protected ObjectLevelReadQuery buildRuntimeQuery() {
		return new ReportQuery();
	}

}
