package org.eclipse.persistence.testing.jaxb.xmlAnyMixed;

import junit.framework.TestCase;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringWriter;

public class XmlAnyMixedTestCases extends TestCase {
    private static final String EXPECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><main><RootAnyMixed>\n" +
            "        <SomeTag>\n" +
            "            <AChildTag>\n" +
            "                <AnotherChildTag/>\n" +
            "                <AnotherChildTag/>\n" +
            "            </AChildTag>\n" +
            "        </SomeTag>\n" +
            "    </RootAnyMixed><RootAny><VeryGood><TheBest><MegaSuper/><MegaSuper/>\n" +
            "            </TheBest>\n" +
            "        </VeryGood></RootAny></main>";

    public static void main(String[] args) {
        String[] arguments = {"-c", "org.eclipse.persistence.testing.jaxb.xmlAnyMixed.XmlAnyMixedTestCases"};
        junit.textui.TestRunner.main(arguments);
    }

    public void testRoundTrip() {
        try {
            JAXBContext jc = JAXBContextFactory.createContext(new Class[]{Main.class}, null);
            Unmarshaller um = jc.createUnmarshaller();
            Object o = um.unmarshal(new File("org/eclipse/persistence/testing/jaxb/xmlAnyMixed/rootAnyMixed.xml"));
            Marshaller m = jc.createMarshaller();
            StringWriter sw = new StringWriter();
            m.marshal(o, sw);
            assertEquals(EXPECTED, sw.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}