package org.eclipse.persistence.testing.oxm.inheritance;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class InheritanceCarNoPrefixTestCases extends XMLMappingTestCases {
    public InheritanceCarNoPrefixTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceProject());
        setControlDocument("org/eclipse/persistence/testing/oxm/inheritance/car_no_prefix.xml");
    }

    public Object getControlObject() {
        Car car = new Car();
        car.numberOfDoors = 2;
        car.milesPerGallon = 30;
        car.model = "Grand Am";
        car.manufacturer = "Pontiac";
        car.topSpeed = 220;

        return car;
    }

    public Document getWriteControlDocument() throws Exception {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/inheritance/car.xml");
        Document doc = parser.parse(inputStream);
        removeEmptyTextNodes(doc);
        inputStream.close();
        return doc;
    }
}