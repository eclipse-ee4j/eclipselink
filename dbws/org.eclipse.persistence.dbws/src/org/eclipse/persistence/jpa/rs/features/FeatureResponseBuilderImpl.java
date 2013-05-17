package org.eclipse.persistence.jpa.rs.features;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.mappings.DatabaseMapping;

public class FeatureResponseBuilderImpl {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<JAXBElement> createShellJAXBElementList(List<ReportItem> reportItems, Object record) {
        List<JAXBElement> jaxbElements = new ArrayList<JAXBElement>(reportItems.size());
        if ((reportItems != null) && (reportItems.size() > 0)) {
            for (int index = 0; index < reportItems.size(); index++) {
                ReportItem reportItem = reportItems.get(index);
                Object reportItemValue = record;
                if (record instanceof Object[]) {
                    reportItemValue = ((Object[]) record)[index];
                }
                Class reportItemValueType = null;
                if (reportItemValue != null) {
                    reportItemValueType = reportItemValue.getClass();
                }
                if (reportItemValueType == null) {
                    // try other paths to determine the type of the report item
                    DatabaseMapping dbMapping = reportItem.getMapping();
                    if (dbMapping != null) {
                        reportItemValueType = dbMapping.getAttributeClassification();
                    } else {
                        ClassDescriptor desc = reportItem.getDescriptor();
                        if (desc != null) {
                            reportItemValueType = desc.getJavaClass();
                        }
                    }
                }

                // so, we couldn't determine the type of the report item, stop here...
                if (reportItemValueType == null) {
                    return null;
                }

                JAXBElement element = new JAXBElement(new QName(reportItem.getName()), reportItemValueType, reportItemValue);
                jaxbElements.add(reportItem.getResultIndex(), element);
            }
        }
        return jaxbElements;
    }

}
