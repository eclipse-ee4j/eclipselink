package org.eclipse.persistence.testing.jaxb.json.array;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root2DArray {

    public String[][] array2d;

    @XmlElement(name="array2d")
    public String[][] getArray2d() {
        return array2d;
    }

    public void setArray2d(String[][] array2d) {
        this.array2d = array2d;
    }
}
