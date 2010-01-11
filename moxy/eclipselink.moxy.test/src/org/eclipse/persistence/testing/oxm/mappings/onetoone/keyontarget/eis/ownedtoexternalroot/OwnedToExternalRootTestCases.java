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
package org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget.eis.ownedtoexternalroot;

import java.util.ArrayList;
import java.util.Vector;
import org.eclipse.persistence.eis.interactions.XQueryInteraction;
import org.eclipse.persistence.internal.eis.adapters.xmlfile.XMLFileInteractionSpec;
import org.eclipse.persistence.testing.oxm.mappings.EISMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.onetoone.keyontarget.*;

public class OwnedToExternalRootTestCases extends EISMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/onetoone/keyontarget/eis/ownedtoexternalroot/writing/team_control.xml";
    private final static String XML_TEST_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/onetoone/keyontarget/eis/ownedtoexternalroot/writing/team.xml";
    private final static String CONTROL_EMPLOYEE1_NAME = "Jill";
    private final static String CONTROL_EMPLOYEE2_NAME = "Jane";
    private final static String CONTROL_EMPLOYEE3_NAME = "Bob";
    private final static long CONTROL_PROJECT1_ID = 1;
    private final static String CONTROL_PROJECT1_NAME = "Project1";
    private final static long CONTROL_PROJECT2_ID = 2;
    private final static String CONTROL_PROJECT2_NAME = "Project2";
    private final static long CONTROL_PROJECT3_ID = 3;
    private final static String CONTROL_PROJECT3_NAME = "Project3";

    public OwnedToExternalRootTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new OwnedToExternalRootProject());
    }

    protected Object getControlObject() {
        Team team = new Team();
        team.setId(10);

        Project project1 = new Project();
        project1.setId(CONTROL_PROJECT1_ID);
        project1.setName(CONTROL_PROJECT1_NAME);

        Project project2 = new Project();
        project2.setId(CONTROL_PROJECT2_ID);
        project2.setName(CONTROL_PROJECT2_NAME);

        Project project3 = new Project();
        project3.setId(CONTROL_PROJECT3_ID);
        project3.setName(CONTROL_PROJECT3_NAME);

        Employee employee1 = new Employee();
        employee1.setFirstName(CONTROL_EMPLOYEE1_NAME);

        Employee employee2 = new Employee();
        employee2.setFirstName(CONTROL_EMPLOYEE2_NAME);
        employee2.setProject(project2);

        Employee employee3 = new Employee();
        employee3.setFirstName(CONTROL_EMPLOYEE3_NAME);

        team.addEmployee(employee1);
        team.addEmployee(employee2);
        team.addEmployee(employee3);

        ArrayList objects = new ArrayList();
        objects.add(team);
        objects.add(project1);
        objects.add(project2);
        objects.add(project3);

        return objects;
    }

    protected ArrayList getRootClasses() {
        ArrayList classes = new ArrayList();
        classes.add(Team.class);
        classes.add(Project.class);
        return classes;
    }

    protected Class getSourceClass() {
        return Team.class;
    }

    protected String getTestDocument() {
        return XML_TEST_RESOURCE;
    }

    protected void createTables() {
        // Drop tables
        XQueryInteraction interaction = new XQueryInteraction();
        XMLFileInteractionSpec spec = new XMLFileInteractionSpec();

        interaction = new XQueryInteraction();
        interaction.setFunctionName("drop-PROJECT");
        spec = new XMLFileInteractionSpec();
        spec.setFileName("project.xml");
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        interaction.setInteractionSpec(spec);
        session.executeNonSelectingCall(interaction);

        interaction = new XQueryInteraction();
        interaction.setFunctionName("drop-TEAM");
        spec = new XMLFileInteractionSpec();
        spec.setFileName("team.xml");
        spec.setInteractionType(XMLFileInteractionSpec.DELETE);
        interaction.setInteractionSpec(spec);
        session.executeNonSelectingCall(interaction);
    }

    public void testXMLDocumentToObject() throws Exception {
        updateProjectForReading();

        Vector objects = new Vector();
        for (int i = 0; i < getRootClasses().size(); i++) {
            objects.addAll(session.readAllObjects((Class)getRootClasses().get(i)));
        }

        log("**testXMLDocumentToObject**");
        log("****Expected:");
        log(getControlObject().toString());
        log("***Actual:");
        this.assertTrue(objects.size() == 4);
        log(objects.toString());
        this.assertTrue(((java.util.ArrayList)getControlObject()).size() == objects.size());
        ArrayList control = (ArrayList)getControlObject();
        this.assertEquals(control.get(0), objects.elementAt(0));
        Team team = (Team)objects.get(0);
        Vector emps = team.getEmployees();
        for (int i = 0; i < emps.size(); i++) {
            Employee emp = (Employee)emps.get(i);
            String name = emp.getFirstName();
            Project p = emp.getProject();
            if (name.equalsIgnoreCase(CONTROL_EMPLOYEE1_NAME)) {
                assertEquals(p, null);
            } else if (name.equalsIgnoreCase(CONTROL_EMPLOYEE2_NAME)) {
                assertEquals(p.getName(), CONTROL_PROJECT2_NAME);
                assertEquals(p.getId(), CONTROL_PROJECT2_ID);
            } else if (name.equalsIgnoreCase(CONTROL_EMPLOYEE3_NAME)) {
                assertEquals(p, null);
            }
        }
    }
}
