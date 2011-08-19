package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.list;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlAdapterListsTestCases extends JAXBTestCases{
  
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmladapter/list/multiplebar.xml";
    
    public XmlAdapterListsTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{FooWithBars.class, Bar.class});
        setControlDocument(XML_RESOURCE);
    }
  
	protected Object getControlObject() {
		FooWithBars foo = new FooWithBars();
	    List<String> itemlist = new ArrayList<String>();
	    itemlist.add(MyAdapter.VAL0);
	    itemlist.add(MyAdapter.VAL1);
	    itemlist.add(MyAdapter.VAL2);
	    foo.items = itemlist;
	    return foo;
	}

}