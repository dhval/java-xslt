package com.dhval.postman;

import com.dhval.Constants;
import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PutCCEMsg {

    @Value("${client.ws.mq}")
    private String clientURL;


    public ResponseEntity<String> post(Map<String, String> queryMap) throws Exception {
        return transform(queryMap, new ClassPathResource(Constants.XSL_FILE_CCE).getFile(),
                new File(Constants.XML_INPUT_CCE));
    }

    public ResponseEntity<String> transform(Map<String, String> queryMap, File xslFile, File xmlFile)
            throws SaxonApiException, IOException, URISyntaxException {
        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(xslFile));
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));

        //Serializer out = SaxonUtils.getSerializer("out.xml");

        Serializer out = SaxonUtils.getSerializer();
        StringWriter sw = new StringWriter();
        out.setOutputWriter(sw);


        XsltTransformer trans = exp.load();

        addParam(queryMap, trans);
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();

        RestTemplate restTemplate =  new RestTemplate();
        return restTemplate.postForEntity(clientURL, sw.toString(), String.class);

    }

    private void addParam(Map<String, String> map, XsltTransformer trans) {
        for(Map.Entry<String, String> entry : map.entrySet()) {
            trans.setParameter(new QName(entry.getKey()), new XdmAtomicValue(entry.getValue()));
        }
    }
}
