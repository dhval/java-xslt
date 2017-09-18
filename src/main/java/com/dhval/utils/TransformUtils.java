package com.dhval.utils;

import com.dhval.task.FlattenWSDL;
import net.sf.saxon.s9api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class TransformUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TransformUtils.class);
    public void transform(File xslFile, File xmlFile, File outFile) throws SaxonApiException, IOException, URISyntaxException {
        LOG.info("Transform(" + xslFile.getName() + ", " + xmlFile.getName() + ") > " + outFile.getName());
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(xslFile));
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));
        Serializer out = new Serializer();
        out.setOutputProperty(Serializer.Property.METHOD, "xml");
        out.setOutputProperty(Serializer.Property.INDENT, "yes");
        out.setOutputFile(outFile);
        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();
    }
}
