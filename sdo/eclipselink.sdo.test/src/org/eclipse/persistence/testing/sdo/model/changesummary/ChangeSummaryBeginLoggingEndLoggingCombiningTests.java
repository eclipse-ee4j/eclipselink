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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;

public class ChangeSummaryBeginLoggingEndLoggingCombiningTests extends ChangeSummaryCreatedModifiedDeletedTestCase {
    public ChangeSummaryBeginLoggingEndLoggingCombiningTests(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryBeginLoggingEndLoggingCombiningTests" };
        TestRunner.main(arguments);
    }

    // verify recursive delete sets isSet properly
    public void testBehaviorsAfterLoginThenLogoff() {
        buildTree();
        changeSummaryB = (SDOChangeSummary)dataObjectB.getChangeSummary();
        changeSummaryC = (SDOChangeSummary)dataObjectC.getChangeSummary();

        // two ChangeSummary logon now.
        changeSummaryB.beginLogging();
        changeSummaryC.beginLogging();

        // do a sequence of actions
        assertEquals(0, changeSummaryB.getChangedDataObjects().size());
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertUnchanged(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertUnchanged(dataObjectD, changeSummaryB);

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings should not be set
        checkOldSettingsSizeTree("0000", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        // remove leaf from CS-B off of B
        /*dataObjectB.detach();

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());// 1-->2
        assertEquals(0, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertDeleted(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDeleted(dataObjectD, changeSummaryB);// un-del

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertUnchanged(dataObjectC, changeSummaryC);
        assertUnchanged(dataObjectD, changeSummaryC);

        // oldSettings 
        checkOldSettingsSizeTree("0201", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);// 0000
        checkOldSettingsSizeTree("0000", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope
*/
        // add leaf (move) to CS-C off of C
        dataObjectC.set(propertyC, dataObjectD);

        assertEquals(2, changeSummaryB.getChangedDataObjects().size());
        assertEquals(2, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB, false); // set after a detach will result in a non-null container

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertModified(dataObjectC, changeSummaryC);
        assertCreated(dataObjectD, changeSummaryC);// set propagates recursively

        // oldSettings
        checkOldSettingsSizeTree("0101", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);//0100
        checkOldSettingsSizeTree("0010", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope

        // two ChangeSummary log off now.           
        changeSummaryB.endLogging();
        changeSummaryC.endLogging();
        
             assertEquals(2, changeSummaryB.getChangedDataObjects().size());
        assertEquals(2, changeSummaryC.getChangedDataObjects().size());

        assertUnchanged(root, changeSummaryB);
        assertModified(dataObjectB, changeSummaryB);
        assertUnchanged(dataObjectC, changeSummaryB);
        assertDetached(dataObjectD, changeSummaryB, false); // set after a detach will result in a non-null container

        assertUnchanged(root, changeSummaryC);
        assertUnchanged(dataObjectB, changeSummaryC);
        assertModified(dataObjectC, changeSummaryC);
        assertCreated(dataObjectD, changeSummaryC);// set propagates recursively

        // oldSettings
        checkOldSettingsSizeTree("0101", changeSummaryB, root, dataObjectB, dataObjectC, dataObjectD);//0100
        checkOldSettingsSizeTree("0010", changeSummaryC, root, dataObjectB, dataObjectC, dataObjectD);

        // root
        //   -> B
        //        -> CS-B
        //        -> D (String)
        //   -> C
        //        -> CS-C
        // check oldContainer all should be set after beginLogging()
        checkOldContainer(changeSummaryB,//
                          root, null,// root is above csB and csC
                          dataObjectB, root,// B is a child of root at the level of csB 
                          dataObjectC, null,// C is not in csB scope
                          dataObjectD, dataObjectB);// D is a child of B inside csB scope
        checkOldContainer(changeSummaryC,//
                          root, null,// root is above csB and csC
                          dataObjectB, null,// B is not in csC scope
                          dataObjectC, root,// C is in csC scope
                          dataObjectD, null);// D is inside csB scope      
    }
}
