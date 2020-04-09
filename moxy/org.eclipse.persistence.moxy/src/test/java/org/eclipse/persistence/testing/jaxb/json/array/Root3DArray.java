package org.eclipse.persistence.testing.jaxb.json.array;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root3DArray {

    public Integer[][][] array3d;

    @XmlElement(name="array3d")
    public Integer[][][] getArray3d() {
        return array3d;
    }

    public void setArray3d(Integer[][][] array3d) {
        this.array3d = array3d;
    }
}
