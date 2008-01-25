/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.descriptor.relational;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOption;
import org.eclipse.persistence.tools.workbench.mappingsmodel.TopLinkOptionSet;

import org.eclipse.persistence.mappings.ForeignReferenceMapping;

public interface MWJoinFetchableMapping {

	// Join Fetch options
	String JOIN_FETCH_NONE  = "NONE";
	String JOIN_FETCH_INNER = "INNER";
	String JOIN_FETCH_OUTER = "OUTER";
	
	public static class JoinFetchOptionSet {
		private static List<JoinFetchOption> list = null;
	    public synchronized static TopLinkOptionSet joinFetchOptions() {
	    	if (list == null) {
		    	list = new ArrayList<JoinFetchOption>();
		        list.add(new JoinFetchOption(JOIN_FETCH_NONE, "JOIN_FETCH_OPTION_NONE", 0));
		        list.add(new JoinFetchOption(JOIN_FETCH_INNER, "JOIN_FETCH_OPTION_INNER", 1));
		        list.add(new JoinFetchOption(JOIN_FETCH_OUTER, "JOIN_FETCH_OPTION_OUTER", 2));
	    	}
	        return new TopLinkOptionSet(list);
	    } 
	}
	
	public static class JoinFetchOption extends TopLinkOption { 

        public JoinFetchOption(String mwModelString, String externalString, int toplinkJoinFetchOption) {
            super(mwModelString, externalString, new Integer(toplinkJoinFetchOption));
        }
                    
        public void setMWOptionOnTopLinkObject(Object mapping) {
            ((ForeignReferenceMapping) mapping).setJoinFetch(((Integer) getTopLinkModelOption()).intValue());
        }
    }

    JoinFetchOption getJoinFetchOption();
    void setJoinFetchOption(JoinFetchOption joinFetching);
    void setJoinFetchOption(String joinFetchOption);
		String JOIN_FETCH_PROPERTY = "joinFetchOption";
		
	public  TopLinkOptionSet joinFetchOptions();

}
