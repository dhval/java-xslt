package com.dhval.task;

import com.dhval.utils.FileUtils;
import com.dhval.utils.FindSchemaLocations;
import com.dhval.utils.TransformUtils;
import com.dhval.utils.XMLWriter;
import net.sf.saxon.s9api.SaxonApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlattenWSDL {
    private static final Logger LOG = LoggerFactory.getLogger(FlattenWSDL.class);
    private static final String XSL_SORT_NS = "xsl/sort-schema.xsl";
    private static final String XSL_FLATTEN = "xsl/flatten-wsdl.xsl";

    public static void flatten(String wsdlLocation) throws SaxonApiException, IOException, URISyntaxException {
        if (!FileUtils.isFilePresent(wsdlLocation)) {
            throw new IllegalArgumentException("Not found, WSDL -" + wsdlLocation);
        }
        String outLocation = FileUtils.addFileExtension(wsdlLocation, "_Full");
        File outFile = new File(outLocation);

        List<String> xsdFiles = getXSDFilesByLocation(wsdlLocation);

        LOG.info(("Found (xsd)# " + xsdFiles.size()));
        Path basePath = Paths.get(new ClassPathResource(XSL_SORT_NS).getFile().getAbsolutePath()).getParent();
        File schemas = basePath.resolveSibling("schemas.xml").toFile();

        new XMLWriter().buildSchemas(schemas, xsdFiles.toArray(new String[xsdFiles.size()]));
        new TransformUtils().transform(new ClassPathResource(XSL_FLATTEN).getFile(), new File(wsdlLocation), outFile);
        new TransformUtils().transform(new ClassPathResource(XSL_SORT_NS).getFile(), outFile, outFile);
    }

    private static List<String> getXSDFilesByLocation(String wsdlFile) throws IOException {
        String[] files = FileUtils.allFilesByType(Paths.get(wsdlFile).getParent().toString(), "xsd");
        return Arrays.asList(files);
    }

    private static List<String> getXSDFilesByWSDLRef(String wsdlLocation) throws SaxonApiException {
        List<String> list = new FindSchemaLocations().buildFromWsdl(wsdlLocation);
        List<String> xsdFiles = new FindSchemaLocations().buildFromXsd(list);
        return xsdFiles;
    }
}
