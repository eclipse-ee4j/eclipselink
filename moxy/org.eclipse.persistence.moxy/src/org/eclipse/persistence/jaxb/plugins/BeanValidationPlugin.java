/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.jaxb.plugins;

import static com.sun.xml.xsom.XSFacet.FACET_FRACTIONDIGITS;
import static com.sun.xml.xsom.XSFacet.FACET_LENGTH;
import static com.sun.xml.xsom.XSFacet.FACET_MAXEXCLUSIVE;
import static com.sun.xml.xsom.XSFacet.FACET_MAXINCLUSIVE;
import static com.sun.xml.xsom.XSFacet.FACET_MAXLENGTH;
import static com.sun.xml.xsom.XSFacet.FACET_MINEXCLUSIVE;
import static com.sun.xml.xsom.XSFacet.FACET_MININCLUSIVE;
import static com.sun.xml.xsom.XSFacet.FACET_MINLENGTH;
import static com.sun.xml.xsom.XSFacet.FACET_PATTERN;
import static com.sun.xml.xsom.XSFacet.FACET_TOTALDIGITS;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpressionImpl;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JStringLiteral;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CCustomizable;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CPropertyVisitor2;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.AttributeUseImpl;
import com.sun.xml.xsom.impl.ParticleImpl;
import com.sun.xml.xsom.impl.RestrictionSimpleTypeImpl;
import com.sun.xml.xsom.impl.parser.DelayedRef;


/**
 * XJC Plugin for generation of JSR349 (Bean Validation) annotations.
 * <p>
 * Has two mods:
 * <blockquote><pre>
 *  "jsr303" - enables backward compatibility.
 *  "simpleRegex" - disables translation of UNICODE XML regex into UNICODE Java regex.
 *  Java ASCII regex are shorter to read.
 * </pre></blockquote>
 * Is capable of generating the following annotations:
 * <blockquote><pre>
 *  - @DecimalMax
 *  - @DecimalMin
 *  - @Digits
 *  - @NotNull
 *  - @Pattern
 *  - @Size
 *  - @Valid
 *  - @AssertTrue
 *  - @AssertFalse
 *  - @Future
 *  - @Past
 * </pre></blockquote>
 * Reacts to the following XSD restrictions and facets:
 * <blockquote><pre>
 *  - maxExclusive
 *  - minExclusive
 *  - maxInclusive
 *  - minInclusive
 *  - nillable
 *  - pattern
 *  - length
 *  - maxLength
 *  - minLength
 *  - minOccurs
 *  - maxOccurs
 * </pre></blockquote>
 * Basic usage:
 * <blockquote><pre>
 *  {@code xjc file.xsd -XBeanVal}
 * </pre></blockquote>
 * Example usage with mods:
 * <blockquote><pre>
 *  {@code xjc file.xsd -XBeanVal jsr303 simpleRegex}
 * </pre></blockquote>
 * Programmatic usage, with mods and extensions enabled:
 * <blockquote><pre>
 *  Driver.run(new String[] { schemaPath, "-extension", "-XBeanVal", "jsr303", "simpleRegex" }, System.out,
 *  System.out);
 * </pre></blockquote>
 * Supports setting Target groups and Error message through binding customizations. Example:
 * <blockquote><pre>
 * {@code <xs:appinfo>
 *   <jxb:bindings node="/xs:schema/xs:complexType/xs:sequence/xs:element[@name='generic']">
 *    <bv:facet type="maxLength" message="Hello, world!" groups="Object"/>
 *    <bv:facet type="future" message="Welcome to the Future!"/>
 *   </jxb:bindings>
 *  </xs:appinfo>}
 * </pre></blockquote>
 * <p>
 * Supports custom-created BV annotations. Example:
 * <blockquote><pre>
 * {@code <xs:appinfo>
 *   <jxb:bindings node="/xs:schema/xs:complexType/xs:sequence/xs:element[@name='generic']">
 *    <bv:facet type="org.eclipse.persistence.annotations.AdditionalCriteria" value="This is a real custom annotation."/>
 *   </jxb:bindings>
 *  </xs:appinfo>}
 * </pre></blockquote>
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@java.lang.SuppressWarnings("squid:S1191")
public class BeanValidationPlugin extends Plugin {

    /* ######### LAUNCHING ######### */
    public static final String PLUGIN_OPTION = "XBeanVal";
    public static final String JSR_303_MOD = "jsr303";
    public static final String SIMPLE_REGEX_MOD = "simpleRegex";
    public static final String NS_URI = "http://jaxb.dev.java.net/plugin/bean-validation";
    public static final String FACET = "facet";
    
    private static final String VALUE = "value";

    private boolean jsr303 = false;
    private boolean simpleRegex = false;

    @Override
    public String getOptionName() {
        return PLUGIN_OPTION;
    }

    @Override
    public String getUsage() {
        return "  -" + PLUGIN_OPTION + "           :  convert xsd restrictions to" +
                " javax.validation annotations. Usage with mods: -" + PLUGIN_OPTION
                + " " + JSR_303_MOD + " " + SIMPLE_REGEX_MOD;
    }

    @Override
    public List<String> getCustomizationURIs() {
        return Collections.singletonList(NS_URI);
    }

    @Override
    public boolean isCustomizationTagName(String nsUri, String localName) {
        return nsUri.equals(NS_URI) && localName.equals(FACET);
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
        int mods = 0; 
        int argNumber = i;
        if (("-" + PLUGIN_OPTION).equals(args[i])) {
            while (++argNumber < args.length) {
                if (args[argNumber].contains(JSR_303_MOD)) {
                    jsr303 = true;
                    mods++;
                } else if (args[argNumber].contains(SIMPLE_REGEX_MOD)) {
                    simpleRegex = true;
                    mods++;
                }
            }
            return 1 + mods;
        }
        return 0;
    }

    /* ######### CORE FUNCTIONALITY ######### */
    private static final String PATTERN_ANNOTATION_NOT_APPLICABLE = "Facet \"pattern\" was detected on a DOM node with non-string base type. Annotation was not generated, because it is not supported by the Bean Validation specification.";
    private final boolean securityEnabled = System.getSecurityManager() != null;

    private static final JClass ANNOTATION_VALID;
    private static final JClass ANNOTATION_NOTNULL;
    private static final JClass ANNOTATION_SIZE;
    private static final JClass ANNOTATION_DECIMALMIN;
    private static final JClass ANNOTATION_DECIMALMAX;
    private static final JClass ANNOTATION_DIGITS;
    private static final JClass ANNOTATION_PATTERN;
    private static final JClass ANNOTATION_PATTERNLIST;
    private static final JClass ANNOTATION_ASSERTFALSE;
    private static final JClass ANNOTATION_ASSERTTRUE;
    private static final JClass ANNOTATION_FUTURE;
    private static final JClass ANNOTATION_PAST;
    private static final JClass ANNOTATION_XMLELEMENT;

    // We want this plugin to work without requiring the presence of JSR-303/349 jar.
    private static final JCodeModel CODEMODEL = new JCodeModel();

    static {
        ANNOTATION_VALID = CODEMODEL.ref("javax.validation.Valid");
        ANNOTATION_NOTNULL = CODEMODEL.ref("javax.validation.constraints.NotNull");
        ANNOTATION_SIZE = CODEMODEL.ref("javax.validation.constraints.Size");
        ANNOTATION_DECIMALMIN = CODEMODEL.ref("javax.validation.constraints.DecimalMin");
        ANNOTATION_DECIMALMAX = CODEMODEL.ref("javax.validation.constraints.DecimalMax");
        ANNOTATION_DIGITS = CODEMODEL.ref("javax.validation.constraints.Digits");
        ANNOTATION_PATTERN = CODEMODEL.ref("javax.validation.constraints.Pattern");
        ANNOTATION_PATTERNLIST = CODEMODEL.ref("javax.validation.constraints.Pattern.List");
        ANNOTATION_ASSERTFALSE = CODEMODEL.ref("javax.validation.constraints.AssertFalse");
        ANNOTATION_ASSERTTRUE = CODEMODEL.ref("javax.validation.constraints.AssertTrue");
        ANNOTATION_FUTURE = CODEMODEL.ref("javax.validation.constraints.Future");
        ANNOTATION_PAST = CODEMODEL.ref("javax.validation.constraints.Past");
        ANNOTATION_XMLELEMENT = CODEMODEL.ref("javax.xml.bind.annotation.XmlElement");
    }

    @Override
    public boolean run(Outline outline, Options opts, ErrorHandler errorHandler) {
        final Visitor visitor = this.new Visitor();
        for (ClassOutline classOutline : outline.getClasses()) {
            for (CPropertyInfo property : classOutline.target.getProperties()) {
                property.accept(visitor, classOutline);
            }
        }
        return true;
    }

    /**
     * Processes an xsd value in form of xsd attribute from extended base.
     * <p>
     * Example:
     * <xsd:complexType name="Employee">
     * <xsd:simpleContent>
     * <xsd:extension base="a:ShortId">  <- xsd extension base
     * <xsd:attribute name="id" type="xsd:string" use="optional"/>
     * </xsd:extension>
     * </xsd:simpleContent>
     * </xsd:complexType>
     * <p>
     * <xsd:simpleType name="ShortId">
     * <xsd:restriction base="xsd:string">
     * <xsd:minLength value="1"/>        <- This is a special field that is added to the generated class, called "value" (corresponds to the valuePropertyName),
     * <xsd:maxLength value="5"/>           it gets processed by this method and the "value" field receives @Size(min = 1, max = 5).
     * </xsd:restriction>
     * </xsd:simpleType>
     */
    private void processValueFromExtendedBase(CValuePropertyInfo valueProperty, ClassOutline classOutline, List<FacetCustomization> customizations) {
        String valuePropertyName = valueProperty.getName(false);

        JFieldVar fieldVar = classOutline.implClass.fields().get(valuePropertyName);
        XSSimpleType type = ((RestrictionSimpleTypeImpl) valueProperty.getSchemaComponent()).asSimpleType();

        processSimpleType(type, fieldVar, customizations);
    }

    /**
     * Processes an xsd attribute.
     * <p>
     * Example:
     * <xsd:complexType name="Employee">
     * <xsd:simpleContent>
     * <xsd:extension base="a:Person">
     * <xsd:attribute name="id" type="xsd:string" use="optional"/>   << "id" is the attributePropertyName
     * </xsd:extension>
     * </xsd:simpleContent>
     * </xsd:complexType>
     */
    private void processAttribute(CAttributePropertyInfo attributeProperty, ClassOutline classOutline, List<FacetCustomization> customizations) {
        String attributePropertyName = attributeProperty.getName(false);
        JFieldVar fieldVar = classOutline.implClass.fields().get(attributePropertyName);

        AttributeUseImpl attribute = (AttributeUseImpl) attributeProperty.getSchemaComponent();
        XSSimpleType type = attribute.getDecl().getType();

        // Use="required". It makes sense to annotate a required attribute with @NotNull even though it's not 100 % semantically equivalent.
        if (attribute.isRequired() && !fieldVar.type().isPrimitive()) {
            notNullAnnotate(fieldVar);
        }

        processSimpleType(type, fieldVar, customizations);
    }

    /**
     * Processes an xsd element.
     * <p>
     * Example:
     * <xsd:element name="someCollection" minOccurs="1" maxOccurs="unbounded"/>
     */
    private void processElement(CElementPropertyInfo propertyInfo, ClassOutline co, List<FacetCustomization> customizations) {
        XSParticle particle = (XSParticle) propertyInfo.getSchemaComponent();
        JFieldVar fieldVar = co.implClass.fields().get(propertyInfo.getName(false));

        processMinMaxOccurs(particle, fieldVar);

        XSTerm term = particle.getTerm();
        if (term instanceof XSElementDecl) {
            processTermElement(particle, fieldVar, (XSElementDecl) term, customizations);
        // When a complex type resides inside another complex type and thus gets lazily loaded or processed.
        } else if (term instanceof DelayedRef.Element) { 
            processTermElement(particle, fieldVar, ((DelayedRef.Element) term).get(), customizations);
        }
    }

    private void processTermElement(XSParticle particle, JFieldVar fieldVar, XSElementDecl element, List<FacetCustomization> customizations) {
        final int minOccurs = getOccursValue("minOccurs", particle);
        XSType elementType = element.getType();

        if (elementType.isComplexType()) {
            validAnnotate(fieldVar);
            if (!element.isNillable() && minOccurs > 0) {
                notNullAnnotate(fieldVar);
            }
            if (elementType.getBaseType().isSimpleType()) {
                processSimpleType(elementType.getBaseType().asSimpleType(), fieldVar, customizations);
            }
        } else { 
            processSimpleType(elementType.asSimpleType(), fieldVar, customizations);
        }
    }

    private void processSimpleType(XSSimpleType simpleType, JFieldVar fieldVar, List<FacetCustomization> customizations) {
        Map<JAnnotationUse, FacetType> annotationsAndTheirOrigin = new HashMap<JAnnotationUse, FacetType>();

        applyAnnotations(simpleType, fieldVar, annotationsAndTheirOrigin);
        applyCustomizations(fieldVar, customizations, annotationsAndTheirOrigin);
    }

    /*
     * Reads supported facets from the simpleType.
     * Converts them to corresponding bean validation annotations.
     * Annotations are applied on the fieldVar if it isn't yet annotated by them.
     * Why? If there is something else (e.g. plugin) which generates BV annotations,
     * we mustn't interfere with it. Two annotations of the same kind on a Java field
     * do not compile.
     * Stores the applied annotations and their origin into the map arg.
     */
    private void applyAnnotations(XSSimpleType simpleType, JFieldVar fieldVar, Map<JAnnotationUse, FacetType> a) {
        XSFacet facet = null; // Auxiliary field.
        JType fieldType = fieldVar.type();
        if (notAnnotated(fieldVar, ANNOTATION_SIZE) && isSizeAnnotationApplicable(fieldType)) {
            try {
                if ((facet = simpleType.getFacet(FACET_LENGTH)) != null) {
                    int length = Integer.parseInt(facet.getValue().value);
                    a.put(fieldVar.annotate(ANNOTATION_SIZE).param("min", length).param("max", length), FacetType.length);
                } else {
                    Integer minLength = (facet = simpleType.getFacet(FACET_MINLENGTH)) != null ? Integer.parseInt(facet.getValue().value) : null;
                    Integer maxLength = (facet = simpleType.getFacet(FACET_MAXLENGTH)) != null ? Integer.parseInt(facet.getValue().value) : null;
                    
                    // Note: If using both minLength + maxLength, the minLength's customizations are considered.
                    if (minLength != null && maxLength != null) {
                        a.put(fieldVar.annotate(ANNOTATION_SIZE).param("min", minLength).param("max", maxLength), FacetType.minLength);
                    } else if (minLength != null) {
                        a.put(fieldVar.annotate(ANNOTATION_SIZE).param("min", minLength), FacetType.minLength);
                    } else if (maxLength != null) {
                        a.put(fieldVar.annotate(ANNOTATION_SIZE).param("max", maxLength), FacetType.maxLength);
                    }
                }
            } catch (NumberFormatException nfe) {
                if (facet != null) {
                    String msg = "'" + facet.getName() + "' in '" + simpleType.getName() + "' cannot be parsed.";
                    throw new RuntimeException(new SAXParseException(msg, facet.getLocator(), nfe));
                }
            }
        }
        
        if ((facet = simpleType.getFacet(FACET_MAXINCLUSIVE)) != null && isNumberOrCharSequence(fieldType, false)) {
            String maxIncValue = facet.getValue().value;
            if (notAnnotatedAndNotDefaultBoundary(fieldVar, ANNOTATION_DECIMALMAX, maxIncValue)) {
                a.put(fieldVar.annotate(ANNOTATION_DECIMALMAX).param(VALUE, maxIncValue), FacetType.maxInclusive);
                convertToElement(fieldVar);
            }
        }

        if ((facet = simpleType.getFacet(FACET_MININCLUSIVE)) != null && isNumberOrCharSequence(fieldType, false)) {
            String minIncValue = facet.getValue().value;
            if (notAnnotatedAndNotDefaultBoundary(fieldVar, ANNOTATION_DECIMALMIN, minIncValue)) {
                a.put(fieldVar.annotate(ANNOTATION_DECIMALMIN).param(VALUE, minIncValue), FacetType.minInclusive);
                convertToElement(fieldVar);
            }
        }

        if ((facet = simpleType.getFacet(FACET_MAXEXCLUSIVE)) != null && isNumberOrCharSequence(fieldType, false)) {
            String maxExcValue = facet.getValue().value;
            if (!jsr303) { // ~ if jsr349
                if (notAnnotatedAndNotDefaultBoundary(fieldVar, ANNOTATION_DECIMALMAX, maxExcValue)) {
                    a.put(fieldVar.annotate(ANNOTATION_DECIMALMAX).param(VALUE, maxExcValue).param("inclusive", false), FacetType.maxExclusive);
                    convertToElement(fieldVar);
                }
            } else {
                Integer intMaxExc = Integer.parseInt(maxExcValue) - 1;
                maxExcValue = intMaxExc.toString();
                if (notAnnotatedAndNotDefaultBoundary(fieldVar, ANNOTATION_DECIMALMAX, maxExcValue)) {
                    a.put(fieldVar.annotate(ANNOTATION_DECIMALMAX).param(VALUE, maxExcValue), FacetType.maxExclusive);
                    convertToElement(fieldVar);
                }
            }
        }

        if ((facet = simpleType.getFacet(FACET_MINEXCLUSIVE)) != null && isNumberOrCharSequence(fieldType, false)) {
            String minExcValue = facet.getValue().value;
            if (!jsr303) { // ~ if jsr349
                if (notAnnotatedAndNotDefaultBoundary(fieldVar, ANNOTATION_DECIMALMIN, minExcValue)) {
                    a.put(fieldVar.annotate(ANNOTATION_DECIMALMIN).param(VALUE, minExcValue).param("inclusive", false), FacetType.minExclusive);
                    convertToElement(fieldVar);
                } else {
                    Integer intMinExc = Integer.parseInt(minExcValue) + 1;
                    minExcValue = intMinExc.toString();
                    if (notAnnotatedAndNotDefaultBoundary(fieldVar, ANNOTATION_DECIMALMIN, minExcValue)) {
                        a.put(fieldVar.annotate(ANNOTATION_DECIMALMIN).param(VALUE, minExcValue), FacetType.minExclusive);
                        convertToElement(fieldVar);
                    }
                }
            }
        }

        if ((facet = simpleType.getFacet(FACET_TOTALDIGITS)) != null && isNumberOrCharSequence(fieldType, true)) {
            Integer digits = Integer.valueOf(facet.getValue().value);
            if (digits != null) {
                XSFacet fractionDigits = simpleType.getFacet(FACET_FRACTIONDIGITS);
                int fractionDigs = 0;
                if (fractionDigits != null) {
                    try {
                        fractionDigs = Integer.parseInt(fractionDigits.getValue().value);
                    } catch (NumberFormatException nfe) {
                        fractionDigs = 0;
                    }
                }
                if (notAnnotated(fieldVar, ANNOTATION_DIGITS)) {
                    a.put(fieldVar.annotate(ANNOTATION_DIGITS).param("integer", (digits - fractionDigs)).param("fraction", fractionDigs), FacetType.totalDigits);
                }
            }
        }

        List<XSFacet> patternList = simpleType.getFacets(FACET_PATTERN);
        if (patternList.size() > 1) {
            if (notAnnotated(fieldVar, ANNOTATION_PATTERNLIST)) {
                JAnnotationUse list = fieldVar.annotate(ANNOTATION_PATTERNLIST);
                JAnnotationArrayMember listValue = list.paramArray(VALUE);

                for (XSFacet xsFacet : patternList)
                    // If corresponds to <xsd:restriction base="xsd:string">.
                    if ("String".equals(fieldType.name())) {
                        a.put(listValue.annotate(ANNOTATION_PATTERN).param("regexp", eliminateShorthands(xsFacet.getValue().value)), FacetType.pattern);
                    } else {
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, PATTERN_ANNOTATION_NOT_APPLICABLE);
                    }
            }
        } else if ((facet = simpleType.getFacet(FACET_PATTERN)) != null) {
            if ("String".equals(fieldType.name())) { // <xsd:restriction base="xsd:string">
                if (notAnnotated(fieldVar, ANNOTATION_PATTERN)) {
                    a.put(fieldVar.annotate(ANNOTATION_PATTERN).param("regexp", eliminateShorthands(facet.getValue().value)), FacetType.pattern);
                }
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, PATTERN_ANNOTATION_NOT_APPLICABLE);
            }
        }
    }

    /**
     * Implements the GoF Visitor pattern.
     */
    private final class Visitor implements CPropertyVisitor2<Void, ClassOutline> {
        @Override
        public Void visit(CElementPropertyInfo t, ClassOutline p) {
            processElement(t, p, detectCustomizations(t));
            return null;
        }

        @Override
        public Void visit(CAttributePropertyInfo t, ClassOutline p) {
            processAttribute(t, p, detectCustomizations(t));
            return null;
        }

        @Override
        public Void visit(CValuePropertyInfo t, ClassOutline p) {
            processValueFromExtendedBase(t, p, detectCustomizations(t));
            return null;
        }

        @Override
        public Void visit(CReferencePropertyInfo t, ClassOutline p) {
            return null;
        }

        /*
         * Scans the input DOM node for custom bindings.
         * If found, converts them into {@link FacetCustomization} objects
         * and returns them.
         */
        private List<FacetCustomization> detectCustomizations(CCustomizable ca) {
            List<FacetCustomization> facetCustomizations = new ArrayList<FacetCustomization>();
            List<CPluginCustomization> pluginCustomizations = ca.getCustomizations();
            if (pluginCustomizations != null)
                for (CPluginCustomization c : pluginCustomizations) {
                    c.markAsAcknowledged();

                    String groups = c.element.getAttribute("groups");
                    String message = c.element.getAttribute("message");
                    String type = c.element.getAttribute("type");
                    if ("".equals(type)) {
                        throw new RuntimeException("DOM attribute \"type\" is required in custom facet declarations.");
                    }
                    String value = c.element.getAttribute(VALUE);
                    facetCustomizations.add(new FacetCustomization(groups, message, type, value));
                }

            return facetCustomizations;
        }
    }

    /* ######### CUSTOMIZATIONS FUNCTIONALITY ######### */
    /*
     * Applies the input customizations on the fieldVar:
     *  - Detects custom facets and applies them.
     *  - Matches standard facets with corresponding facet customizations.
     */
    private void applyCustomizations(JFieldVar fieldVar, List<FacetCustomization> customizations, Map<JAnnotationUse, FacetType> a) {
        for (FacetCustomization c : customizations) {
            // Programming by exception is a bad programming practice, however
            // it is the best available solution here. Catching IAE means that
            // we have encountered a custom facet.
            try {
                switch (FacetType.valueOf(c.type)) {
                    case assertFalse:
                        customizeAnnotation(fieldVar.annotate(ANNOTATION_ASSERTFALSE), c);
                        continue;
                    case assertTrue:
                        customizeAnnotation(fieldVar.annotate(ANNOTATION_ASSERTTRUE), c);
                        continue;
                    case future:
                        customizeAnnotation(fieldVar.annotate(ANNOTATION_FUTURE), c);
                        continue;
                    case past:
                        customizeAnnotation(fieldVar.annotate(ANNOTATION_PAST), c);
                        continue;
                }
            } catch (IllegalArgumentException programmingByException) {
                JAnnotationUse annotationUse = fieldVar.annotate(CODEMODEL.ref(c.type));
                if (!c.value.equals("")) annotationUse.param(VALUE, c.value);
                customizeAnnotation(annotationUse, c);
                continue;
            }
            customizeRegularAnnotations(a, c);
        }
    }

    /**
     * Reads attributes "groups" and "message" from facet customization
     * and if they don't contain empty values, applies them to annotation use
     * as a parameter.
     */
    private void customizeAnnotation(JAnnotationUse a, final FacetCustomization c) {
        /**
         * Transposes an array of Strings into a single String containing
         * the Strings, separated by comma + space (i.e. ", ") and with ".class"
         * extension.
         */
        final class GroupsParser extends JExpressionImpl {
            @Override
            public void generate(JFormatter f) {
                if (c.groups.length == 1)
                    f.p(c.groups[0] + ".class");
                else {
                    StringBuilder b = new StringBuilder(c.groups.length * 64);
                    b.append('{');
                    int i = 0;
                    for (; i < c.groups.length - 1; i++)
                        b.append(c.groups[i]).append(".class, ");
                    b.append(c.groups[i]).append(".class}");
                    f.p(b.toString());
                }
            }
        }
        if (c.groups != null && c.groups.length != 0) a.param("groups", new GroupsParser());
        if (!c.message.equals("")) a.param("message", c.message);
    }

    /* Note:
     * Keep in mind that the only facet that has maxOccurs > 1 is xs:pattern.
     * If multiple patterns are present on an element, they all must share the
     * same validation group, because they all are included in the XML
     * Validation process.
     */
    private void customizeRegularAnnotations(Map<JAnnotationUse, FacetType> annotations, FacetCustomization c) {
        for (Map.Entry<JAnnotationUse, FacetType> e : annotations.entrySet())
            if (FacetType.valueOf(c.type) == e.getValue())
                customizeAnnotation(e.getKey(), c);
    }

    /**
     * Value Object for Facet Customizations and Custom Facets.
     */
    private static final class FacetCustomization {
        private final String[]/*@NotEmpty*/groups;
        private final String message;
        private final String type;
        private final String value;

        private FacetCustomization(String groups, String message, String type, String value) {
            this.groups = groups.isEmpty() ? null : groups(groups);
            this.message = message;
            this.type = type;
            this.value = value;
        }

        private String[] groups(String groups) {
            return ClassNameTrimmer.trim(groups).split(",");
        }

        private static final class ClassNameTrimmer {
            private static final Pattern ws = Pattern.compile("[\\u0009-\\u000D\\u0020\\u0085\\u00A0\\u1680\\u180E\\" +
                    "u2000-\\u200A\\u2028\\u2029\\u202F\\u205F\\u3000]+");

            private static String trim(String s) {
                return ws.matcher(s).replaceAll("").replace('$', '.');
            }
        }
    }

    private static enum FacetType {
        // XML Facets + Restrictions.
        maxExclusive,
        minExclusive,
        maxInclusive,
        minInclusive,
        nillable,
        pattern,
        length,
        maxLength,
        minLength,
        minOccurs,
        maxOccurs,
        totalDigits,
        fractionDigits,
        // Custom facets.
        assertFalse,
        assertTrue,
        future,
        past
    }

    /* ######### AUXILIARY FUNCTIONALITY ######### */

    /**
     * Processes minOccurs and maxOccurs attributes of XS Element.
     * If the values are not default, the property will be annotated with @Size.
     */
    private void processMinMaxOccurs(XSParticle particle, JFieldVar fieldVar) {
        final int maxOccurs = getOccursValue("maxOccurs", particle);
        final int minOccurs = getOccursValue("minOccurs", particle);
        if (maxOccurs > 1) {
            if (notAnnotated(fieldVar, ANNOTATION_SIZE))
                fieldVar.annotate(ANNOTATION_SIZE).param("min", minOccurs).param("max", maxOccurs);
        } else if (maxOccurs == -1) // maxOccurs -1 = "unbounded"
            if (notAnnotated(fieldVar, ANNOTATION_SIZE))
                fieldVar.annotate(ANNOTATION_SIZE).param("min", minOccurs);
    }


    /**
     * Annotates the field with @XmlElement. Without this functionality, the
     * field wouldn't be recognized by Schemagen. @XmlElement annotation may
     * also trigger change of the field's type from primitive to object.
     */
    private void convertToElement(JFieldVar fieldVar) {
        if (notAnnotated(fieldVar, ANNOTATION_XMLELEMENT)) {
            fieldVar.annotate(XmlElement.class);
            notNullAnnotate(fieldVar);
        }
    }

    private void validAnnotate(JFieldVar fieldVar) {
        if (notAnnotated(fieldVar, ANNOTATION_VALID))
            fieldVar.annotate(ANNOTATION_VALID);
    }

    private void notNullAnnotate(JFieldVar fieldVar) {
        if (notAnnotated(fieldVar, ANNOTATION_NOTNULL))
            fieldVar.annotate(ANNOTATION_NOTNULL);
    }

    private boolean notAnnotated(JFieldVar fieldVar, JClass annotationClass) {
        for (JAnnotationUse annotationUse : fieldVar.annotations())
            if ((annotationUse.getAnnotationClass().toString().equals(annotationClass.toString())))
                return false;
        return true;
    }

    /**
     * Checks if the desired annotation is not a boundary of a primitive type and
     * Checks if the fieldVar is already annotated with the desired annotation.
     *
     * @return true if the fieldVar should be annotated, false if the fieldVar is
     * already annotated or the value is a default boundary.
     */
    private boolean notAnnotatedAndNotDefaultBoundary(JFieldVar fieldVar, JClass annotationClass, String boundaryValue) {
        if (isDefaultBoundary(fieldVar.type().name(), annotationClass.fullName(), boundaryValue))
            return false;

        for (JAnnotationUse annotationUse : fieldVar.annotations()) {
            if ((annotationUse.getAnnotationClass().toString().equals(annotationClass.toString()))) {
                boolean previousAnnotationRemoved = false;
                String annotationName = annotationUse.getAnnotationClass().fullName();
                if (annotationName.equals(ANNOTATION_DECIMALMIN.fullName()))
                    previousAnnotationRemoved = isMoreSpecificBoundary(fieldVar, boundaryValue, annotationUse, false);
                else if (annotationName.equals(ANNOTATION_DECIMALMAX.fullName()))
                    previousAnnotationRemoved = isMoreSpecificBoundary(fieldVar, boundaryValue, annotationUse, true);
                // If the previous field's annotation was removed, the fieldVar
                // now is not annotated and should be given a new annotation,
                // i.e. return true.
                return previousAnnotationRemoved;
            }
        }
        return true;
    }

    /**
     * @param xorComplement - flips the result of compareTo (set to true for decimalMaxAnn and false for decimalMinAnn).
     */
    private boolean isMoreSpecificBoundary(JFieldVar fieldVar, String boundaryValue, JAnnotationUse annotationUse,
                                           boolean xorComplement) {
        String existingBoundaryValue = getExistingBoundaryValue(annotationUse);

        if (existingBoundaryValue == null) return true;
        else if (Long.valueOf(boundaryValue).compareTo(Long.valueOf(existingBoundaryValue)) > 0 ^ xorComplement)
            return fieldVar.removeAnnotation(annotationUse);
        return false;
    }

    private boolean isSizeAnnotationApplicable(JType jType) {
        if (jType.isArray()) return true;

        Class<?> clazz = loadClass(jType.fullName());
        return clazz != null && (CharSequence.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz));
    }

    /* ######### GENERAL UTILITIES ######### */
    private Class<?> loadClass(String className) {
        Class<?> clazz = null;
        if (securityEnabled) try {
            clazz = AccessController.doPrivileged(ForNameActionExecutor.INSTANCE.with(className));
        } catch (PrivilegedActionException ignored) {
            // - Can be only of type ClassNotFoundException, no check needed, see AccessController.doPrivileged().
            /* - ClassNotFoundException for us "means" that the fieldVar is of some unknown class - not an issue to be
             solved by this plugin. */
        }
        else try {
            clazz = loadClassInternal(className);
        } catch (ClassNotFoundException ignored) {
            /* - ClassNotFoundException for us "means" that the fieldVar is of some unknown class - not an issue to be
             solved by this plugin. */
        }
        return clazz;
    }

    private int getOccursValue(final String attributeName, final XSParticle xsParticle) {
        return securityEnabled
            ? AccessController.doPrivileged(OccursValueActionExecutor.INSTANCE.with(attributeName, xsParticle)).intValue()
            : loadOccursValue(attributeName, xsParticle).intValue();
    }

    private String getExistingBoundaryValue(final JAnnotationUse jAnnotationUse) {
        return securityEnabled
            ? AccessController.doPrivileged(ExistingBoundaryValueActionExecutor.INSTANCE.with(jAnnotationUse))
            : loadExistingBoundaryValue(jAnnotationUse);
    }

    private String eliminateShorthands(String regex) {
        return regexMutator.mutate(regex);
    }

    private final RegexMutator regexMutator = this.new RegexMutator();

    /**
     * Provides means for maintaining compatibility between XML Schema regex and Java Pattern regex.
     * Replaces Java regex shorthands, which support only ASCII encoding, with full UNICODE equivalent.
     * <p>
     * Also replaces the two special XML regex character sets which aren't supported in Java, \i and \c.
     * <p>
     * Replaced shorthands and their negations:
     * <blockquote><pre>
     * \i - Matches any character that may be the first character of an XML name.
     * \c - Matches any character that may occur after the first character in an XML name.
     * \d - All digits.
     * \w - Word character.
     * \s - Whitespace character.
     * \b, \B - Boundary definitions.
     * \h - Horizontal whitespace character - Java does not support, changed in Java 8 though.
     * \v - Vertical whitespace character - Java translates the shorthand to \cK only, meaning changed in Java 8 though.
     * \X - Extended grapheme cluster.
     * \R - Carriage return.
     * </pre></blockquote>
     * <p>
     * Changes to this class should also be reflected in the opposite
     *  {@link org.eclipse.persistence.jaxb.compiler.SchemaGenerator.RegexMutator} class within SchemaGen.
     *
     * @see <a href="http://stackoverflow.com/questions/4304928/unicode-equivalents-for-w-and-b-in-java-regular-expressions">tchrist's work</a>
     * @see <a href="http://www.regular-expressions.info/shorthand.html#xml">Special shorthands in XML Schema.</a>
     */
    private final class RegexMutator {
        private final Map<Pattern, String> shorthandReplacements = simpleRegex
                ? new LinkedHashMap<Pattern, String>(8) {{
            put(Pattern.compile("\\\\i"), "[_:A-Za-z]");
            put(Pattern.compile("\\\\I"), "[^:A-Z_a-z]");
            put(Pattern.compile("\\\\c"), "[-.0-9:A-Z_a-z]");
            put(Pattern.compile("\\\\C"), "[^-.0-9:A-Z_a-z]");
        }}
                : new LinkedHashMap<Pattern, String>(32) {{
            put(Pattern.compile("\\\\i"), "[:A-Z_a-z\\\\u00C0-\\\\u00D6\\\\u00D8-\\\\u00F6\\\\u00F8-\\\\u02FF\\\\u0370-\\\\u037D\\\\u037F-\\\\u1FFF\\\\u200C-\\\\u200D\\\\u2070-\\\\u218F\\\\u2C00-\\\\u2FEF\\\\u3001-\\\\uD7FF\\\\uF900-\\\\uFDCF\\\\uFDF0-\\\\uFFFD]");
            put(Pattern.compile("\\\\I"), "[^:A-Z_a-z\\\\u00C0-\\\\u00D6\\\\u00D8-\\\\u00F6\\\\u00F8-\\\\u02FF\\\\u0370-\\\\u037D\\\\u037F-\\\\u1FFF\\\\u200C-\\\\u200D\\\\u2070-\\\\u218F\\\\u2C00-\\\\u2FEF\\\\u3001-\\\\uD7FF\\\\uF900-\\\\uFDCF\\\\uFDF0-\\\\uFFFD]");
            put(Pattern.compile("\\\\c"), "[-.0-9:A-Z_a-z\\\\u00B7\\\\u00C0-\\\\u00D6\\\\u00D8-\\\\u00F6\\\\u00F8-\\\\u037D\\\\u037F-\\\\u1FFF\\\\u200C-\\\\u200D\\\\u203F\\\\u2040\\\\u2070-\\\\u218F\\\\u2C00-\\\\u2FEF\\\\u3001-\\\\uD7FF\\\\uF900-\\\\uFDCF\\\\uFDF0-\\\\uFFFD]");
            put(Pattern.compile("\\\\C"), "[^-.0-9:A-Z_a-z\\\\u00B7\\\\u00C0-\\\\u00D6\\\\u00D8-\\\\u00F6\\\\u00F8-\\\\u037D\\\\u037F-\\\\u1FFF\\\\u200C-\\\\u200D\\\\u203F\\\\u2040\\\\u2070-\\\\u218F\\\\u2C00-\\\\u2FEF\\\\u3001-\\\\uD7FF\\\\uF900-\\\\uFDCF\\\\uFDF0-\\\\uFFFD]");
            put(Pattern.compile("\\\\s"), "[\\\\u0009-\\\\u000D\\\\u0020\\\\u0085\\\\u00A0\\\\u1680\\\\u180E\\\\u2000-\\\\u200A\\\\u2028\\\\u2029\\\\u202F\\\\u205F\\\\u3000]");
            put(Pattern.compile("\\\\S"), "[^\\\\u0009-\\\\u000D\\\\u0020\\\\u0085\\\\u00A0\\\\u1680\\\\u180E\\\\u2000-\\\\u200A\\\\u2028\\\\u2029\\\\u202F\\\\u205F\\\\u3000]");
            put(Pattern.compile("\\\\v"), "[\\\\u000A-\\\\u000D\\\\u0085\\\\u2028\\\\u2029]");
            put(Pattern.compile("\\\\V"), "[^\\\\u000A-\\\\u000D\\\\u0085\\\\u2028\\\\u2029]");
            put(Pattern.compile("\\\\h"), "[\\\\u0009\\\\u0020\\\\u00A0\\\\u1680\\\\u180E\\\\u2000-\\\\u200A\\\\u202F\\\\u205F\\\\u3000]");
            put(Pattern.compile("\\\\H"), "[^\\\\u0009\\\\u0020\\\\u00A0\\\\u1680\\\\u180E\\\\u2000\\\\u2001-\\\\u200A\\\\u202F\\\\u205F\\\\u3000]");
            put(Pattern.compile("\\\\w"), "[\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]]");
            put(Pattern.compile("\\\\W"), "[^\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]]");
            put(Pattern.compile("\\\\b"), "(?:(?<=[\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]])(?![\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]])|(?<![\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]])(?=[\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]]))");
            put(Pattern.compile("\\\\B"), "(?:(?<=[\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]])(?=[\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]])|(?<![\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]])(?![\\\\pL\\\\pM\\\\p{Nd}\\\\p{Nl}\\\\p{Pc}[\\\\p{InEnclosedAlphanumerics}&&\\\\p{So}]]))");
            put(Pattern.compile("\\\\d"), "\\\\p{Nd}");
            put(Pattern.compile("\\\\D"), "\\\\P{Nd}");
            put(Pattern.compile("\\\\R"), "(?:(?>\\\\u000D\\\\u000A)|[\\\\u000A\\\\u000B\\\\u000C\\\\u000D\\\\u0085\\\\u2028\\\\u2029])");
            put(Pattern.compile("\\\\X"), "(?:(?:\\\\u000D\\\\u000A)|(?:[\\\\u0E40\\\\u0E41\\\\u0E42\\\\u0E43\\\\u0E44\\\\u0EC0\\\\u0EC1\\\\u0EC2\\\\u0EC3\\\\u0EC4\\\\uAAB5\\\\uAAB6\\\\uAAB9\\\\uAABB\\\\uAABC]*(?:[\\\\u1100-\\\\u115F\\\\uA960-\\\\uA97C]+|([\\\\u1100-\\\\u115F\\\\uA960-\\\\uA97C]*((?:[[\\\\u1160-\\\\u11A2\\\\uD7B0-\\\\uD7C6][\\\\uAC00\\\\uAC1C\\\\uAC38]][\\\\u1160-\\\\u11A2\\\\uD7B0-\\\\uD7C6]*|[\\\\uAC01\\\\uAC02\\\\uAC03\\\\uAC04])[\\\\u11A8-\\\\u11F9\\\\uD7CB-\\\\uD7FB]*))|[\\\\u11A8-\\\\u11F9\\\\uD7CB-\\\\uD7FB]+|[^[\\\\p{Zl}\\\\p{Zp}\\\\p{Cc}\\\\p{Cf}&&[^\\\\u000D\\\\u000A\\\\u200C\\\\u200D]]\\\\u000D\\\\u000A])[[\\\\p{Mn}\\\\p{Me}\\\\u200C\\\\u200D\\\\u0488\\\\u0489\\\\u20DD\\\\u20DE\\\\u20DF\\\\u20E0\\\\u20E2\\\\u20E3\\\\u20E4\\\\uA670\\\\uA671\\\\uA672\\\\uFF9E\\\\uFF9F][\\\\p{Mc}\\\\u0E30\\\\u0E32\\\\u0E33\\\\u0E45\\\\u0EB0\\\\u0EB2\\\\u0EB3]]*)|(?s:.))");
        }};

        /**
         * @param xmlRegex XML regex
         * @return Java regex
         */
        private String mutate(String xmlRegex) {
            for (Map.Entry<Pattern, String> entry : shorthandReplacements.entrySet()) {
                Matcher m = entry.getKey().matcher(xmlRegex);
                xmlRegex = m.replaceAll(entry.getValue());
            }
            return xmlRegex;
        }
    }

    private boolean isDefaultBoundary(String fieldVarType, String annotationClass, String boundaryValue) {
        return ANNOTATION_DECIMALMIN.fullName().equals(annotationClass)
                && nonFloatingDigitsClassesBoundaries.get(fieldVarType).min.equals(boundaryValue)
                || (ANNOTATION_DECIMALMAX.fullName().equals(annotationClass)
                && nonFloatingDigitsClassesBoundaries.get(fieldVarType).max.equals(boundaryValue));
    }

    private boolean isNumberOrCharSequence(JType jType, boolean supportsFloating) {
        String shortClazzName = jType.name();
        if (nonFloatingDigitsClasses.contains(shortClazzName))
            return true;
        if (supportsFloating && floatingDigitsClasses.contains(shortClazzName))
            return true;

        Class<?> clazz = loadClass(jType.fullName());
        return clazz != null && CharSequence.class.isAssignableFrom(clazz);
    }

    private static final Set<String> nonFloatingDigitsClasses;

    static {
        Set<String> set = new HashSet<String>();
        set.add("byte");
        set.add("Byte");
        set.add("short");
        set.add("Short");
        set.add("int");
        set.add("Integer");
        set.add("long");
        set.add("Long");
        set.add("BigDecimal");
        set.add("BigInteger");
        nonFloatingDigitsClasses = Collections.unmodifiableSet(set);
    }

    private static final Set<String> floatingDigitsClasses;

    static {
        Set<String> set = new HashSet<String>();
        set.add("float");
        set.add("Float");
        set.add("double");
        set.add("Double");
        floatingDigitsClasses = Collections.unmodifiableSet(new HashSet<String>(set));
    }

    private static final Map<String, MinMaxTuple> nonFloatingDigitsClassesBoundaries;

    static {
        HashMap<String, MinMaxTuple> map = new HashMap<String, MinMaxTuple>();
        map.put("byte", new MinMaxTuple<Byte>(Byte.MIN_VALUE, Byte.MAX_VALUE));
        map.put("Byte", new MinMaxTuple<Byte>(Byte.MIN_VALUE, Byte.MAX_VALUE));
        map.put("short", new MinMaxTuple<Short>(Short.MIN_VALUE, Short.MAX_VALUE));
        map.put("Short", new MinMaxTuple<Short>(Short.MIN_VALUE, Short.MAX_VALUE));
        map.put("int", new MinMaxTuple<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE));
        map.put("Integer", new MinMaxTuple<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE));
        map.put("long", new MinMaxTuple<Long>(Long.MIN_VALUE, Long.MAX_VALUE));
        map.put("Long", new MinMaxTuple<Long>(Long.MIN_VALUE, Long.MAX_VALUE));
        nonFloatingDigitsClassesBoundaries = Collections.unmodifiableMap(map);
    }

    private static final class MinMaxTuple<T extends Number> {
        private final String min;
        private final String max;

        private MinMaxTuple(T min, T max) {
            this.min = String.valueOf(min);
            this.max = String.valueOf(max);
        }
    }

    private static final class ForNameActionExecutor {

        private interface PrivilegedExceptionActionWith<T> extends PrivilegedExceptionAction<T> {
            PrivilegedExceptionAction<T> with(String className);
        }

        private static final PrivilegedExceptionActionWith<Class<?>> INSTANCE = new PrivilegedExceptionActionWith<Class<?>>() {
            private String className;

            @Override
            public Class<?> run() throws ClassNotFoundException {
                return loadClassInternal(className);
            }

            @Override
            public PrivilegedExceptionActionWith<Class<?>> with(String className) {
                this.className = className;
                return this;
            }
        };
    }

    private static Class<?> loadClassInternal(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }

    private static final class OccursValueActionExecutor {

        private interface PrivilegedActionWith<T> extends PrivilegedAction<T> {
            PrivilegedAction<T> with(String fieldName, XSParticle xsParticle);
        }

        private static final PrivilegedActionWith<BigInteger> INSTANCE = new PrivilegedActionWith<BigInteger>() {
            private String fieldName;
            private XSParticle xsParticle;

            @Override
            public BigInteger run() {
                return loadOccursValue(fieldName, xsParticle);
            }

            @Override
            public PrivilegedActionWith<BigInteger> with(String className, XSParticle xsParticle) {
                this.fieldName = className;
                this.xsParticle = xsParticle;
                return this;
            }
        };
    }

    private static BigInteger loadOccursValue(String fieldName, XSParticle xsParticle) {
        try {
            Field field = ParticleImpl.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return ((BigInteger) field.get(xsParticle));
        } catch (Exception e) {
            // Nothing we can do, the user should be notified that his app is unable to
            // execute this plugin correctly and not should not receive generated default values.
            throw new RuntimeException(e);
        }
    }

    private static final class ExistingBoundaryValueActionExecutor {

        private interface PrivilegedActionWith<T> extends PrivilegedAction<T> {
            PrivilegedAction<T> with(JAnnotationUse jAnnotationUse);
        }

        private static final PrivilegedActionWith<String> INSTANCE = new PrivilegedActionWith<String>() {
            private JAnnotationUse jAnnotationUse;

            @Override
            public String run() {
                return loadExistingBoundaryValue(jAnnotationUse);
            }

            @Override
            public PrivilegedAction<String> with(JAnnotationUse jAnnotationUse) {
                this.jAnnotationUse = jAnnotationUse;
                return this;
            }
        };
    }

    private static String loadExistingBoundaryValue(JAnnotationUse jAnnotationUse) {
        JAnnotationValue jAnnotationValue = jAnnotationUse.getAnnotationMembers().get(VALUE);
        Class<? extends JAnnotationValue> clazz = jAnnotationValue.getClass();
        try {
            Field theValueField = clazz.getDeclaredField(VALUE);
            theValueField.setAccessible(true);
            return ((JStringLiteral) theValueField.get(jAnnotationValue)).str;
        } catch (Exception e) {
            // Nothing we can do, user should be notified that his app is unable to
            // execute this plugin correctly and not should not receive generated default values.
            throw new RuntimeException(e);
        }
    }

}
