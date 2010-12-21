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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.util.Comparator;
import java.util.TreeSet;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.collections.Location;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.SortedCollectionContainerPolicy;


/*
 * Note, this test not intent to verify the function for the sorted collectionMapping. 
 * it just test whether sorted collection mapping with specified comparator 
 * can be written to or read from project.xml correctly.
 */
public class ProjectXMLSortedCollectionMapping extends TestCase {
    protected static final String TEMP_FILE = "TempProjectSafeToDelete.xml";
    protected Exception exception = null;

    protected Project readBackProject;

    public ProjectXMLSortedCollectionMapping() {
        setDescription("Tests sorted collection mapping with specified comparator can be written to or read from project XML correctly.");
    }

    public void reset() {
    }

    public void setup() {
    }

    public void test() {
        try{
            Project writeToProject = new EmployeeProject();
            
            ClassDescriptor descriptorToModify = writeToProject.getDescriptors().get(Employee.class);
            DatabaseMapping mappingToModify = descriptorToModify.getMappingForAttributeName("projects");
    
            if (mappingToModify.isForeignReferenceMapping()) {
                if (((ForeignReferenceMapping)mappingToModify).isCollectionMapping()) {
                    CollectionMapping collectionMapping = (CollectionMapping)(((ForeignReferenceMapping)mappingToModify));
                    collectionMapping.useSortedSetClassName(TreeSet.class.getName(),getComparator().getName());
                }
            }else{
                throw new Exception("The test must have sorted collection mapping specified in the class descriptor in order to test.");
            }
            
            // Write out the project with changes and read back in again.
            XMLProjectWriter.write(TEMP_FILE, writeToProject);
            readBackProject = XMLProjectReader.read(TEMP_FILE, getClass().getClassLoader());
        }catch(Exception e){
            exception = e;
        }
    }

    protected void verify() {
        if (exception != null){
            throw new TestErrorException("There is problem when read project back from project.xml",exception);
        }
        ClassDescriptor readBackDescriptor = readBackProject.getDescriptors().get(Employee.class);
        DatabaseMapping readBackMapping = readBackDescriptor.getMappingForAttributeName("projects");
        CollectionMapping collectionMapping = (CollectionMapping)readBackMapping;
        ContainerPolicy containerPolciy = collectionMapping.getContainerPolicy();
        if(containerPolciy.isCollectionPolicy()){
            Class conatinerClass = ((SortedCollectionContainerPolicy)containerPolciy).getContainerClass();
            Class comparatorClass = ((SortedCollectionContainerPolicy)containerPolciy).getComparatorClass();
            if(!conatinerClass.equals(TreeSet.class) ){
                throw new TestErrorException("The container class read was not equal to the conatiner class set originally, which expected as the java.util.TreeSet class. ");
            }
            if(!comparatorClass.equals(ProjectXMLSortedCollectionMapping.ProjectComparator.class)){
                throw new TestErrorException("The comparator class read was not equal to the comparator class set originally, which expected as the ProjectXMLCollectionMappingUseSortedClassNameTest.ProjectComparator class. ");
            }
        }else{
            throw new TestErrorException("The container policy expect to set as SortedCollectionContainerPolicy.");
        }
    }
    
    public Class getComparator(){
        return ProjectXMLSortedCollectionMapping.ProjectComparator.class;
    }
    
    public static class ProjectComparator implements Comparator{
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
