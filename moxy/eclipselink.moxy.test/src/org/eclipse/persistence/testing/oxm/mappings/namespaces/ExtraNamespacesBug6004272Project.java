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

import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class ExtraNamespacesBug6004272Project extends Project {
    public ExtraNamespacesBug6004272Project() {
        super();
        this.addDescriptor(getManagerDescriptor());
        this.addDescriptor(getEmployeeDescriptor());
        this.addDescriptor(getRootDescriptor());
        this.addDescriptor(getCompanyDescriptor());
        this.addDescriptor(getDepartmentDescriptor());
        this.addDescriptor(getTeamDescriptor());
        this.addDescriptor(getProjectDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Root.class);
        xmlDescriptor.setDefaultRootElement("ns1:root");

        XMLCompositeCollectionMapping companiesMapping = new XMLCompositeCollectionMapping();
        companiesMapping.setAttributeName("companies");
        companiesMapping.setXPath("ns2:companies/ns2:company");
        companiesMapping.setReferenceClass(Company.class);
        xmlDescriptor.addMapping(companiesMapping);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns1", "http://www.example.com/rootns");
        nsResolver.put("ns2", "http://www.example.com/ns2");
        
        xmlDescriptor.setNamespaceResolver(nsResolver);

        return xmlDescriptor;
    }

    private XMLDescriptor getCompanyDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Company.class);
                
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("companyName");
        nameMapping.setXPath("ns2:companyname/text()");
        xmlDescriptor.addMapping(nameMapping);

        XMLCompositeObjectMapping managerMapping = new XMLCompositeObjectMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.setXPath("ns2:manager");
        managerMapping.setReferenceClass(Manager.class);
        //XMLField xmlFld = (XMLField) managerMapping.getField();      
        //xmlFld.setLeafElementType(new QName("http://www.example.com/ns2","manager-type"));
        xmlDescriptor.addMapping(managerMapping);

        XMLCompositeCollectionMapping departmentsMapping = new XMLCompositeCollectionMapping();
        departmentsMapping.setAttributeName("departments");
        departmentsMapping.setXPath("ns2:departments/ns2:dept");
        departmentsMapping.setReferenceClass(Department.class);
        xmlDescriptor.addMapping(departmentsMapping);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns1", "http://www.example.com/rootns");//keep in resolver 
        nsResolver.put("ns2", "http://www.example.com/ns2");
        nsResolver.put("aaa", "http://www.example.com/aaa");        
        xmlDescriptor.setNamespaceResolver(nsResolver);

        return xmlDescriptor;
    }
        

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("ns4:employee");
        
        XMLField classIndicatorField = new XMLField("@xsi:type");
        xmlDescriptor.getInheritancePolicy().setClassIndicatorField(classIndicatorField);        
        xmlDescriptor.getInheritancePolicy().addClassIndicator(Employee.class, "ns4:emp-type");        
        xmlDescriptor.getInheritancePolicy().addClassIndicator(Manager.class, "ns2:manager-type");        
        xmlDescriptor.getInheritancePolicy().setShouldReadSubclasses(true);
        
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("ns4:personal-info/@ns4:id");
        xmlDescriptor.addMapping(idMapping);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("ns4:personal-info/ns4:name/text()");
        xmlDescriptor.addMapping(nameMapping);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns4", "http://www.example.com/ns4");
        nsResolver.put("ns2", "http://www.example.com/ns2");
        nsResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlDescriptor.setNamespaceResolver(nsResolver);


        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/ns4:emp-type");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(ref);
        
        return xmlDescriptor;
    }

    private XMLDescriptor getManagerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Manager.class);
        xmlDescriptor.setDefaultRootElement("ns3:manager");
        xmlDescriptor.getInheritancePolicy().setParentClass(Employee.class);

        XMLDirectMapping titleMapping = new XMLDirectMapping();
        titleMapping.setAttributeName("title");
        titleMapping.setXPath("ns3:title/text()");
        xmlDescriptor.addMapping(titleMapping);


        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/ns2:manager-type");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(ref);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns3", "http://www.example.com/ns3"); 
        nsResolver.put("ns2", "http://www.example.com/ns2");
        nsResolver.put("ns4", "http://www.example.com/ns4");
        nsResolver.put("ns7", "http://www.example.com/ns7");
        nsResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xmlDescriptor.setNamespaceResolver(nsResolver);
        return xmlDescriptor;
    }
    
    private XMLDescriptor getDepartmentDescriptor() {
      XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Department.class);
        xmlDescriptor.setDefaultRootElement("ns3:department");
      
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("deptName");
        nameMapping.setXPath("ns3:deptName/text()");
        xmlDescriptor.addMapping(nameMapping);

        XMLAnyCollectionMapping teamsMapping = new XMLAnyCollectionMapping();
        teamsMapping.setAttributeName("teams");
        teamsMapping.setXPath("ns3:teams");
        teamsMapping.setUseXMLRoot(true);
        xmlDescriptor.addMapping(teamsMapping);
      
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns3", "http://www.example.com/ns3");
        xmlDescriptor.setNamespaceResolver(nsResolver);
        return xmlDescriptor;
    }
    
     private XMLDescriptor getTeamDescriptor() {
      XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Team.class);
        xmlDescriptor.setDefaultRootElement("ns4:team");
      
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("teamName");
        nameMapping.setXPath("ns4:teamName/text()");
        xmlDescriptor.addMapping(nameMapping);
        
        XMLAnyObjectMapping teamLeaderMapping = new XMLAnyObjectMapping();
        teamLeaderMapping.setAttributeName("teamLeader");
        teamLeaderMapping.setXPath("ns5:leader");
        xmlDescriptor.addMapping(teamLeaderMapping);
        

        XMLCompositeCollectionMapping empsMapping = new XMLCompositeCollectionMapping();
        empsMapping.setAttributeName("employees");
        empsMapping.setXPath("ns6:emp");
        //no reference class        
        xmlDescriptor.addMapping(empsMapping);
      
          
        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/ns4:team");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(ref);
      
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns4", "http://www.example.com/ns4");
        nsResolver.put("ns5", "http://www.example.com/ns5");
        nsResolver.put("ns6", "http://www.example.com/ns6");
        //nsResolver.put("ns2", "http://www.example.com/ns2");
        xmlDescriptor.setNamespaceResolver(nsResolver);
        return xmlDescriptor;
    }
  
   private XMLDescriptor getProjectDescriptor() {
      XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.namespaces.Project.class);
        xmlDescriptor.setDefaultRootElement("ns6Other:project");
      
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("ns6Other:name/text()");
        xmlDescriptor.addMapping(nameMapping);
        
        XMLDirectMapping descMapping = new XMLDirectMapping();
        descMapping.setAttributeName("description");
        descMapping.setXPath("ns6Other:description/text()");
        xmlDescriptor.addMapping(descMapping);
        
        NamespaceResolver nsResolver = new NamespaceResolver();        
        nsResolver.put("ns6Other", "http://www.example.com/ns6");
        
        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/ns6Other:project-type");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(ref);
        
        xmlDescriptor.setNamespaceResolver(nsResolver);
        return xmlDescriptor;
        
   }
}
