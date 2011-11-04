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
 *     Denise Smith - EclipseLink 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlidref.inheritance;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlIdRefInheritanceTestCases extends JAXBWithJSONTestCases { 
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/instance_inheritance.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/instance_inheritance.json";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlidref/control_inheritance_schema.xsd";
  
    public XmlIdRefInheritanceTestCases(String name) throws Exception {
        super(name);
        Class[] classes = new Class[2];
        classes[0] = School.class;
        classes[1] = TransferStudent.class;
        setClasses(classes);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    protected Object getControlObject() {
        School school = new School();
        
        Student student1 = new Student();
        student1.setId(1);
        student1.setStudentNumber("B123");
        
        Student student2 = new Student();
        student2.setId(2);
        student2.setStudentNumber("B456");
        
        TransferStudent student3 = new TransferStudent();
        student3.setId(3);
        student3.setStudentNumber("B789");
        student3.setPreviousSchool("Carleton");

        Student student4 = new Student();
        student4.setId(4);
        student4.setStudentNumber("B987");
        
        List<Student> students = new ArrayList<Student>();
        students.add(student1);
        students.add(student2);
        students.add(student3);
        students.add(student4);
        
        
        school.setClassPresident(student3);
        school.setStudents(students);
        return school;
    }

    
    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        controlSchemas.add(ClassLoader.getSystemResourceAsStream(XSD_RESOURCE));
        
        this.testSchemaGen(controlSchemas);
        
    }
    
}
