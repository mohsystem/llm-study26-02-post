package com.um.springbootprojstructure.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class SecureXmlParser {

    public Document parse(InputStream in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // Secure processing
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // Disable DTDs and external entities to prevent XXE
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setNamespaceAware(false);

            // JAXP 1.5+ properties to restrict external access
            try {
                dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            } catch (IllegalArgumentException ignored) {
                // Some parsers don't support these; features above still protect.
            }

            DocumentBuilder builder = dbf.newDocumentBuilder();

            // Avoid resolving external entities
            builder.setEntityResolver((publicId, systemId) -> new InputSource(new java.io.StringReader("")));

            InputSource source = new InputSource(new InputStreamReader(in, StandardCharsets.UTF_8));
            return builder.parse(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid_xml");
        }
    }
}
