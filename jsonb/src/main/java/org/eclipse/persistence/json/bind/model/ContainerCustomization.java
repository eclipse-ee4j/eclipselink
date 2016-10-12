package org.eclipse.persistence.json.bind.model;

/**
 * Customization for container like types (Maps, Collections, Arrays).
 *
 * @author Roman Grigoriadi
 */
public class ContainerCustomization extends ClassCustomization {


    public ContainerCustomization(CustomizationBuilder builder) {
        super(builder);
    }

    public ContainerCustomization(ClassCustomization other) {
        super(other);
    }

    /**
     * Containers (types mapped to JsonArray) are always nillable by spec.
     * @return always true
     */
    @Override
    public final boolean isNillable() {
        return true;
    }
}
