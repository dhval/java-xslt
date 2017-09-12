package com.dhval.utils;

import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

public class TransformUtils {

    public void transform(String xslFile, String xmlFile, String outFile) throws SaxonApiException, IOException {
        if (!FileUtils.isFilePresent(xslFile)) {
            throw new IllegalArgumentException("Not found, xslFile -" + xslFile);
        }
        if (!FileUtils.isFilePresent(xmlFile)) {
            throw new IllegalArgumentException("Not found, xmlFile -" + xmlFile);
        }
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(new File(xslFile)));
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
