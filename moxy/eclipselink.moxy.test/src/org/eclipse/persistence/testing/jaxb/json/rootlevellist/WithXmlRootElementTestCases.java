/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WithXmlRootElementTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithXmlRootElement.json";
    private static final String CONTROL_JSON_FORMATTED = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithXmlRootElementFormatted.json";
    
    public WithXmlRootElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
    }

    @Override
    protected List<WithXmlRootElementRoot> getControlObject() {
        List<WithXmlRootElementRoot> list = new ArrayList<WithXmlRootElementRoot>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        list.add(foo);
        
        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        list.add(bar);
                
        return list;
    }

    public void testUnmarshalEmptyList() throws Exception {
        List<WithXmlRootElementRoot>  test = (List<WithXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithXmlRootElementRoot.class).getValue();
        assertEquals(0, test.size());
    }
  
    protected boolean shouldRemoveWhitespaceFromControlDocJSON(){
		return false;
	}
    
    public String getWriteControlJSONFormatted(){
    	return CONTROL_JSON_FORMATTED;
    }
}