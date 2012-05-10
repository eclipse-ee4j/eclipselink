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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;

public class ChildAndGeneratedPrefixClashProject extends org.eclipse.persistence.sessions.Project {
    public ChildAndGeneratedPrefixClashProject() {
        super();
        
        this.addDescriptor(getRootDescriptor());
        this.addDescriptor(getCompanyDescriptor());        
        this.addDescriptor(getDepartmentDescriptor());
        this.addDescriptor(getTeamDescriptor());
        
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Root.class);
        xmlDescriptor.setDefaultRootElement("aaa:root");

        //XMLCompositeCollectionMapping companiesMapping = new XMLCompositeCollectionMapping();
        XMLAnyCollectionMapping companiesMapping = new XMLAnyCollectionMapping();
        companiesMapping.setAttributeName("companies");
        companiesMapping.setXPath("aaa:companies");
        companiesMapping.setUseXMLRoot(true);
        //companiesMapping.setReferenceClass(Company.class);
        xmlDescriptor.addMapping(companiesMapping);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("aaa", "http://www.example.com/aaa");        
        
        xmlDescriptor.setNamespaceResolver(nsResolver);

        return xmlDescriptor;
    }
    
     private XMLDescriptor getCompanyDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Company.class);
                
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("companyName");
        nameMapping.setXPath("aaa:companyname/text()");
        xmlDescriptor.addMapping(nameMapping);

        //XMLCompositeCollectionMapping departmentsMapping = new XMLCompositeCollectionMapping();
         XMLAnyCollectionMapping departmentsMapping = new XMLAnyCollectionMapping();
        departmentsMapping.setAttributeName("departments");
        departmentsMapping.setXPath("aaa:departments");
        departmentsMapping.setUseXMLRoot(true);
        //departmentsMapping.setReferenceClass(Department.class);
        xmlDescriptor.addMapping(departmentsMapping);

        NamespaceResolver nsResolver = new NamespaceResolver();        
        nsResolver.put("aaa", "http://www.example.com/aaa");          
        xmlDescriptor.setNamespaceResolver(nsResolver);

       XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/aaa:company");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(ref);

        return xmlDescriptor;
    }
 
    private XMLDescriptor getDepartmentDescriptor() {
      XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Department.class);
        xmlDescriptor.setDefaultRootElement("aaa:department");
      
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("deptName");
        nameMapping.setXPath("aaa:deptName/text()");
        xmlDescriptor.addMapping(nameMapping);

//        XMLAnyCollectionMapping teamsMapping = new XMLAnyCollectionMapping();
        XMLCompositeCollectionMapping teamsMapping = new XMLCompositeCollectionMapping();
        teamsMapping.setAttributeName("teams");
        teamsMapping.setXPath("aaa:teams/aaa:team");
        teamsMapping.setReferenceClass(Team.class);
        //steamsMapping.setUseXMLRoot(true);
        xmlDescriptor.addMapping(teamsMapping);
      
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("aaa", "http://www.example.com/aaa");        
        xmlDescriptor.setNamespaceResolver(nsResolver);
        
         XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/aaa:dept");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(ref);
        return xmlDescriptor;
    }
    
     private XMLDescriptor getTeamDescriptor() {
      XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Team.class);
        xmlDescriptor.setDefaultRootElement("aaa:team");
      
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("teamName");
        nameMapping.setXPath("ns0:teamName/text()");
        xmlDescriptor.addMapping(nameMapping);
        
          
        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference();
        ref.setSchemaContext("/aaa:team");
        ref.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(ref);
      
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("aaa", "http://www.example.com/aaa");        
        nsResolver.put("ns0", "http://www.example.com/teamNS0");        
        nsResolver.put("ns1", "http://www.example.com/teamNS1");        
        xmlDescriptor.setNamespaceResolver(nsResolver);
        return xmlDescriptor;
    }
  

}
