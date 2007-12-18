package org.eclipse.persistence.testing.oxm.mappings.xmlfragmentcollection;

import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLFragmentCollectionElementDiffPrefixTestCases extends XMLFragmentCollectionNSTestCases {

    private final static String XML_RESOURCE_DIFF_PFX = "org/eclipse/persistence/testing/oxm/mappings/xmlfragmentcollection/employee_element_ns_different_prefix.xml";
    private final static String XML_SUB_ELEMENT_DIFF_PFX = "org/eclipse/persistence/testing/oxm/mappings/xmlfragmentcollection/sub_element_ns_different_prefix.xml";
	
	public XMLFragmentCollectionElementDiffPrefixTestCases(String name) throws Exception {
		super(name);
        NamespaceResolver nsresolver = new NamespaceResolver();
        nsresolver.put("ns1", "http://www.example.com/test-uri");
        setProject(new XMLFragmentCollectionElementProject(nsresolver));
        setControlDocument(XML_RESOURCE_DIFF_PFX);
	}
	
    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.firstName = "Jane";
        employee.lastName = "Doe";

        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(XML_SUB_ELEMENT_DIFF_PFX);
            Document xdoc = parser.parse(inputStream);
            removeEmptyTextNodes(xdoc);
            inputStream.close();
            employee.xmlnodes = new ArrayList<Node>();
            NodeList xmlnodes = xdoc.getElementsByTagName("xml-node");
            for (int i = 0; i < xmlnodes.getLength(); i++) {
            	employee.xmlnodes.add(xmlnodes.item(i));
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        
        return employee;
    }
}