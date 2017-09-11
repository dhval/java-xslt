package com.dhval.utils;

import net.sf.saxon.s9api.*;

import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class TransformUtils {

    public void transform(String xslFile, String xmlFile, String outFile) throws SaxonApiException {
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
