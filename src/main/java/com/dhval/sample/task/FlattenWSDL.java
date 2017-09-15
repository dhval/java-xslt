package com.dhval.sample.task;

import com.dhval.utils.FileUtils;
import com.dhval.utils.FindSchemaLocations;
import com.dhval.utils.TransformUtils;
import com.dhval.utils.XMLWriter;
import net.sf.saxon.s9api.SaxonApiException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FlattenWSDL {
    private static final Logger LOG = LoggerFactory.getLogger(FlattenWSDL.class);
    private static final String XSL_SORT_NS = "xsl/sort-schema.xsl";
    private static final String XSL_FLATTEN = "xsl/flatten-wsdl.xsl";

    public static void flatten(String wsdlFile) throws SaxonApiException, IOException, URISyntaxException {
        String outFile = FileUtils.addFileExtension(wsdlFile, "_Full");
        List<String> list = new FindSchemaLocations().buildFromWsdl(wsdlFile);
        List<String> xsdFiles = new FindSchemaLocations().buildFromXsd(list);
        LOG.info(("Found (xsd)# " + xsdFiles.size()));
        String basePath = FilenameUtils.getBaseName(new ClassPathResource(XSL_SORT_NS).getFilename());


        new XMLWriter().buildSchemas(Paths.get(new ClassPathResource(XSL_SORT_NS).getFilename()), xsdFiles.toArray(new String[xsdFiles.size()]));
        new TransformUtils().transform(XSL_FLATTEN, wsdlFile, outFile);
        new TransformUtils().transform(XSL_SORT_NS, outFile, outFile);
    }
}
