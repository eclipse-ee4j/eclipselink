/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;



public interface MWOrderableQuery extends MWQuery {
	
	Ordering addOrderingItem(MWQueryable queryable);	
	Ordering addOrderingItem(Iterator queryables);	
	Ordering addOrderingItem(Iterator queryables, Iterator allowsNull);	
	Ordering addOrderingItem(int index, Iterator queryables, Iterator allowsNull);	
	
	void removeOrderingItem(Ordering orderingItem);
	void removeOrderingItem(int index);
	ListIterator orderingItems();	
	int orderingItemsSize();
	int indexOfOrderingItem(Ordering orderingItem);
	void moveOrderingItemUp(Ordering item);
	void moveOrderingItemDown(Ordering item);
	    String ORDERING_ITEMS_LIST = "orderingItems";

}
