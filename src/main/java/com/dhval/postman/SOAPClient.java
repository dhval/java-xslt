package com.dhval.postman;

import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;

public abstract class SOAPClient {
    String clientURL;

    public SOAPClient(String clientURL) {
        this.clientURL = clientURL;
    }

    public ResponseEntity<String> post(String filePath) throws Exception {
        // TODO
        return null;
    };

    public ResponseEntity<String> transform(Map<String, String> queryMap, File xslFile, File xmlFile)
            throws SaxonApiException, IOException, URISyntaxException {
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(xslFile));

        //Serializer out = SaxonUtils.getSerializer("out.xml");

        Serializer out = SaxonUtils.getSerializer();
        StringWriter sw = new StringWriter();
        out.setOutputWriter(sw);


        XsltTransformer trans = exp.load();

        addParam(queryMap, trans);
        if (xmlFile != null) {
            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));
            trans.setInitialContextNode(source);
        } else {
            trans.setInitialTemplate(new QName("main"));
        }
        trans.setDestination(out);
        trans.transform();

        RestTemplate restTemplate =  new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.add("SOAPAction", "");
        return restTemplate.postForEntity(clientURL, new HttpEntity<String>(sw.toString(), headers), String.class);

    }

    private void addParam(Map<String, String> map, XsltTransformer trans) {
        for(Map.Entry<String, String> entry : map.entrySet()) {
            trans.setParameter(new QName(entry.getKey()), new XdmAtomicValue(entry.getValue()));
        }
    }
}
