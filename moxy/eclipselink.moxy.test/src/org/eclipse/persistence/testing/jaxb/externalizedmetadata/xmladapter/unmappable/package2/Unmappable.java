package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2;

public class Unmappable {
    private String prop;

    private Unmappable(String prop) {
        this.prop = prop;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public static Unmappable getInstance(String value) {
        return new Unmappable(value);
    }
    
    public boolean equals(Object obj) {
        return (obj instanceof Unmappable) && ((Unmappable)obj).getProp().equals(this.getProp());
    }
}
