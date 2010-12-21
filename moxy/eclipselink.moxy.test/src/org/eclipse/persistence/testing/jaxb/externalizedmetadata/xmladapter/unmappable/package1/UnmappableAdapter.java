package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package1;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.unmappable.package2.Unmappable;;

public class UnmappableAdapter extends XmlAdapter<String, Unmappable> {

    @Override
    public String marshal(Unmappable v) throws Exception {
        return v != null ? v.getProp() : null;
    }

    @Override
    public Unmappable unmarshal(String v) throws Exception {
        return Unmappable.getInstance(v);
    }
}