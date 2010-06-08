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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLLogin;
import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ExtraNamespacesBug6004272TestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/namespaces/ExtraNamespaces.xml";

    public ExtraNamespacesBug6004272TestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        //setProject(new ExtraNamespacesBug6004272Project());
          org.eclipse.persistence.sessions.Project p =new ExtraNamespacesBug6004272Project();
        XMLLogin login = new XMLLogin();
        login.setEqualNamespaceResolvers(false);
        p.setDatasourceLogin(login);
        setProject(p);
        //xmlMarshaller.setShouldWriteExtraNamespaces(true);
    }

    protected Object getControlObject() {
        Root theRoot = new Root();
                
        Company company = new Company();
        company.setCompanyName("theCompany1");
        
        Manager manager = new Manager();
        manager.setId(123);
        manager.setName("Jane Doe");
        manager.setTitle("theJobTitle");
        company.setManager(manager);
        
        List depts = new ArrayList();
        Department dept1 = new Department();
        dept1.setDeptName("dept1");
        
        List teams = new ArrayList();
        Team team1 = new Team();
        team1.setTeamName("team1");
        Employee leader1 = new Employee();
        leader1.setId(111);
        leader1.setName("Sally Smith");
        team1.setTeamLeader(leader1);
        
        List emps = new ArrayList();
        Employee emps1 = new Employee();
        emps1.setId(222);
        emps1.setName("Bob Jones");
        
        Manager emps2 = new Manager();
        emps2.setId(333);
        emps2.setName("Colleen Jones");
        emps2.setTitle("Colleen's Job");
        
        emps.add(emps1);
        emps.add(emps2);
        team1.setEmployees(emps);
        
        Team team2 = new Team();
        team2.setTeamName("team2");
        
        teams.add(team1);
        teams.add(team2);
        dept1.setTeams(teams);
        
        Department dept2 = new Department();
        dept2.setDeptName("dept2");
        
        List teams2 = new ArrayList();
        Team team3 = new Team();
        team3.setTeamName("team3");
        
        XMLRoot xmlRoot1 = new XMLRoot();
        Team team4 = new Team();
        team4.setTeamName("team4");
        xmlRoot1.setObject(team4);
        xmlRoot1.setLocalName("xmlroot1");
        xmlRoot1.setNamespaceURI("http://www.example.com/ns3"); //uri from parent desc
        
        XMLRoot xmlRoot2 = new XMLRoot();
        Team team5 = new Team();
        team5.setTeamName("team5");
        xmlRoot2.setObject(team5);
        xmlRoot2.setLocalName("xmlroot2");
        xmlRoot2.setNamespaceURI("someNewUri");

        XMLRoot xmlRoot3 = new XMLRoot();                
        Team team6 = new Team();
        team6.setTeamName("team6");
        xmlRoot3.setObject(team6);
        xmlRoot3.setLocalName("xmlroot3");//uri from target
        xmlRoot3.setNamespaceURI("http://www.example.com/ns6");
        
        XMLRoot xmlRoot4 = new XMLRoot();
        Project project = new Project();
        project.setName("theName");
        project.setDescription("theDescription");
        xmlRoot4.setObject(project);
        xmlRoot4.setLocalName("xmlroot4");
        xmlRoot4.setNamespaceURI("http://www.example.com/ns6"); //uri from parent desc
        
        XMLRoot xmlRoot1Simple = new XMLRoot();        
        xmlRoot1Simple.setObject("xmlroot1simple");
        xmlRoot1Simple.setLocalName("xmlroot1simple");
        xmlRoot1Simple.setNamespaceURI("http://www.example.com/ns3"); //uri from parent desc
        
        XMLRoot xmlRoot2Simple = new XMLRoot();        
        xmlRoot2Simple.setObject("xmlroot2simple");
        xmlRoot2Simple.setLocalName("xmlroot2simple");
        xmlRoot2Simple.setNamespaceURI("someNewUri"); 
        
        teams2.add(team3);
        teams2.add(xmlRoot1);
        teams2.add(xmlRoot2);
        teams2.add(xmlRoot3);
        teams2.add(xmlRoot4);
        teams2.add(xmlRoot1Simple);
        teams2.add(xmlRoot2Simple);
        
        dept2.setTeams(teams2);
        
        
        depts.add(dept1);
        depts.add(dept2);
        
        
        company.setDepartments(depts);
        
        
        Company company2 = new Company();
        company2.setCompanyName("theCompany2");
        
        
        List companies = new ArrayList();
        companies.add(company);
        companies.add(company2);
        theRoot.setCompanies(companies);
        
        return theRoot ;
    }

    public void testObjectToXMLStreamWriter() throws Exception {
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.mappings.namespaces.ExtraNamespacesBug6004272TestCases" };
        junit.textui.TestRunner.main(arguments);
    }
}
