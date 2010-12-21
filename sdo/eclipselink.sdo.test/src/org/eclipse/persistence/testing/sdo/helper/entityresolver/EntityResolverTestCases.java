package org.eclipse.persistence.testing.sdo.helper.entityresolver;

import commonj.sdo.impl.HelperProvider;

import java.io.ByteArrayInputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;

import org.xml.sax.InputSource;

/**
 * Test use of org.eclipse.persistence.sdo.helper.SchemaResolver implementation as an entity resolver.
 */
public class EntityResolverTestCases extends TestCase {
    public EntityResolverTestCases(String name) {
        super(name);
    }

    public void testResolveEntities() {
        ByteArrayInputStream bis = new ByteArrayInputStream(getSchema().getBytes());
        ((SDOXSDHelper) HelperProvider.getDefaultContext().getXSDHelper()).define(new StreamSource(bis), new SchemaEntityResolver());
    }

    /**
     * The main schema simply has a DOCTYPE definition that will trigger the resolveEntity method on the
     * internal SchemaResolver class
     * @return
     */
    private String getSchema() {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" + "<!DOCTYPE xs:schema PUBLIC \"-//W3C//DTD XMLSCHEMA 200102//EN\" \"SomeSchema.dtd\" []>\n"
                + "<xs:schema targetNamespace=\"http://www.example.org\" elementFormDefault=\"qualified\" version=\"1.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"/>\n";
    }

    /**
     * The schema dtd simply has an ENTITY declaration that will trigger the resolveEntity method on the
     * internal SchemaResolver class
     * @return
     */
    private String getSchemaDtd() {
        return "<!ENTITY % xs-datatypes PUBLIC 'somedatatypes' 'SomeDatatypes.dtd' >\n" + "%xs-datatypes;";
    }

    /**
     * The datatype dtd simply has an ENTITY declaration that affects nothing
     * @return
     */
    private String getDatatypesDtd() {
        return "<!ENTITY % simpleThings \"\">";
    }

    /**
     * This resolver will handle entity resolution.
     */
    private class SchemaEntityResolver implements SchemaResolver {
        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            return null;
        }

        public InputSource resolveEntity(String publicId, String systemId) {
            try {
                if (systemId.endsWith("SomeSchema.dtd")) {
                    return new InputSource(new ByteArrayInputStream(getSchemaDtd().getBytes()));
                } else if (systemId.endsWith("SomeDatatypes.dtd")) {
                    return new InputSource(new ByteArrayInputStream(getDatatypesDtd().getBytes()));
                }
            } catch (Exception e) {
            }
            return null;
        }
    }
}
