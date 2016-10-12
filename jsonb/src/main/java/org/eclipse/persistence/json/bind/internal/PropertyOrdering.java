package org.eclipse.persistence.json.bind.internal;

import org.eclipse.persistence.json.bind.internal.internalOrdering.PropOrderStrategy;
import org.eclipse.persistence.json.bind.internal.properties.MessageKeys;
import org.eclipse.persistence.json.bind.internal.properties.Messages;
import org.eclipse.persistence.json.bind.model.ClassModel;
import org.eclipse.persistence.json.bind.model.PropertyModel;

import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;
import javax.json.bind.config.PropertyOrderStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Order properties in bean object. {@link javax.json.bind.annotation.JsonbPropertyOrder} have always precedence.
 * If configured with {@link JsonbConfig} provided property order strategy will be used.
 *
 * @author Roman Grigoriadi
 */
public class PropertyOrdering {

    private PropOrderStrategy propertyOrderStrategy;

    /**
     * Property ordering.
     *
     * @param propertyOrderStrategy not null
     */
    public PropertyOrdering(PropOrderStrategy propertyOrderStrategy) {
        Objects.requireNonNull(propertyOrderStrategy);
        this.propertyOrderStrategy = propertyOrderStrategy;
    }

    /**
     * Sorts class properties either, by class {@link javax.json.bind.annotation.JsonbPropertyOrder} annotation,
     * or by {@link PropertyOrderStrategy} if set in JsonbConfig.
     * @param properties
     * @param classModel
     * @return
     */
    public List<PropertyModel> orderProperties(Map<String, PropertyModel> properties, ClassModel classModel) {
        String[] order = classModel.getClassCustomization().getPropertyOrder();
        if (order != null) {
            //if @JsonbPropertyOrder annotation is defined on a class
            List<PropertyModel> sortedProperties = new ArrayList<>();
            for (String propName : order) {
                final PropertyModel remove = properties.remove(propName);
                if (remove != null) {
                    sortedProperties.add(remove);
                }
            }
            /* TODO currently disabled, should remaining fields (unspecified in JsonbPropertyOrder) appear in json?
            for (Map.Entry<String, PropertyModel> entry : properties.entrySet()) {
                sortedProperties.add(entry.getValue());
            }*/
            return sortedProperties;
        }

        //No annotation, check JsonbConfig for ordering strategy use LEXICOGRAPHICAL as default
        return propertyOrderStrategy.sortProperties(properties.values());
    }

    /**
     * Property order strategy from jsonbconfig (if present)
     * @return
     */
    public PropOrderStrategy getPropertyOrderStrategy() {
        return propertyOrderStrategy;
    }
}
