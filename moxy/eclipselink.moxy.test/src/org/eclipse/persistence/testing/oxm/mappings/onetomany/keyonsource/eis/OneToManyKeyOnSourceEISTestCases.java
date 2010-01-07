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
package org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.roottoroot.RootToRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.ownedtoexternalroot.OwnedToExternalRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nestedownedtoexternalroot.NestedOwnedToExternalRootTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.selectionquery.SelectionQueryExceptionTestCases;

public class OneToManyKeyOnSourceEISTestCases extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite("One To Many KeyOnSource EIS Test Cases");

        suite.addTestSuite(RootToRootTestCases.class);
        suite.addTestSuite(OwnedToExternalRootTestCases.class);
        suite.addTestSuite(NestedOwnedToExternalRootTestCases.class);

        //OnetoMany Key on Source composite key Test cases
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.compositekey.roottoroot.RootToRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.compositekey.ownedtoexternalroot.OwnedToExternalRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.compositekey.nestedownedtoexternalroot.NestedOwnedToExternalRootTestCases.class);

        //OnetoMany Key on Source no grouping element single key Test cases
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.roottoroot.RootToRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.ownedtoexternalroot.OwnedToExternalRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.nestedownedtoexternalroot.NestedOwnedToExternalRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.indirection.NoGroupingElementIndirectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.indirection.arraylist.NoGroupingElementIndirectionArrayListTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.arraylist.NoGroupingElementArrayListTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.map.NoGroupingElementMapTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nogroupingelement.indirection.map.NoGroupingElementIndirectionMapTestCases.class);

        //OnetoMany Key on Source nested grouping element  Test cases
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nestedgroupingelement.roottoroot.RootToRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nestedgroupingelement.ownedtoexternalroot.OwnedToExternalRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nestedgroupingelement.nestedownedtoexternalroot.NestedOwnedToExternalRootTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.nestedgroupingelement.nestedforeignkey.NestedForeignKeyTestCases.class);

        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.indirection.IndirectionTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.indirection.arraylist.IndirectionArrayListTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.arraylist.ArrayListTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.map.MapTestCases.class);
        suite.addTestSuite(org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.indirection.map.IndirectionMapTestCases.class);

        suite.addTestSuite(SelectionQueryExceptionTestCases.class);
        return suite;
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.onetomany.keyonsource.eis.OneToManyKeyOnSourceEISTestCases" };
        junit.textui.TestRunner.main(arguments);

    }
}
