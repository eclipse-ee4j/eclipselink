package org.eclipse.persistence.testing.jaxb.xmlAnyMixed;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    public RootAnyMixed createRootAnyMixed() {
        return new RootAnyMixed();
    }

    public RootAny createRootAny() {
        return new RootAny();
    }

    public Main createMain() {
        return new Main();
    }
}