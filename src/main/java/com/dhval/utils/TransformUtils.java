package com.dhval.utils;

import net.sf.saxon.s9api.*;
import org.springframework.core.io.ClassPathResource;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class TransformUtils {

    public void transform(String xslFile, String xmlFile, String outFile) throws SaxonApiException, IOException, URISyntaxException {
        if (!FileUtils.isFilePresent(xmlFile)) {
            throw new IllegalArgumentException("Not found, xmlFile -" + xmlFile);
        }
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(new ClassPathResource(xslFile).getFile()));
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File(xmlFile)));
        Serializer out = new Serializer();
        out.setOutputProperty(Serializer.Property.METHOD, "xml");
        out.setOutputProperty(Serializer.Property.INDENT, "yes");
        out.setOutputFile(new File(outFile));
        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();
    }
}
