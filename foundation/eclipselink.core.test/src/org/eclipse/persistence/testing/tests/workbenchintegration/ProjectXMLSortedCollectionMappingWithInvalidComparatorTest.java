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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.Location;


/*
 * Note, this test not intent to verify the function for the sorted collectionMapping. 
 * it just test whether sorted collection mapping with specified comparator 
 * can be written to or read from project.xml correctly.
 */
public class ProjectXMLSortedCollectionMappingWithInvalidComparatorTest extends ProjectXMLSortedCollectionMapping {
    public ProjectXMLSortedCollectionMappingWithInvalidComparatorTest() {
        setDescription("Tests sorted collection mapping with specified comparator can be written to or read from project XML correctly.");
    }

    protected void verify() {
        if (exception != null){
            if((exception.getMessage().indexOf("is not a valid comparator"))==-1){
                throw new TestErrorException("There is problem when read project back from project.xml",exception);
            }
        }else{
            throw new TestErrorException("ValidationException is expected to be caught for this test case");
        }
    }
    
    public Class getComparator(){
        return ProjectXMLSortedCollectionMappingWithInvalidComparatorTest.ProjectComparator.class;
    }
    
    public static class ProjectComparator{
        public int compare(Object object1, Object object2) {
            if ((object1.getClass() != Project.class) || (object2.getClass() != Project.class)) {
                throw new ClassCastException("Invalid comparison : " + object1 + ", " + object2);
            }
            Location loc1 = (Location)object1;
            Location loc2 = (Location)object2;
            return String.CASE_INSENSITIVE_ORDER.compare(loc1.getArea(), loc2.getArea());
        }
    }

}
