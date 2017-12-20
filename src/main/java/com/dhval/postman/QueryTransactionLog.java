package com.dhval.postman;

import com.dhval.Constants;
import com.dhval.pojo.TransactionObj;
import com.dhval.utils.FileUtils;
import com.dhval.utils.SaxonUtils;
import net.sf.saxon.s9api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class QueryTransactionLog {
    // syntax for nesting [[
    public static  final  String XPATH_DOCKET_ID = "//*[local-name()='HeaderField'][*[local-name()='HeaderName']='DocketNumberText']/*[local-name()='HeaderValueText']";

    public static  final String QUERY_FOR_TOP_100_ROWS =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
                    + " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"> "
                    + "    <soapenv:Body> "
                    + "       <TrackingId>DPResponseRouter</TrackingId>"
                    + "    </soapenv:Body> "
                    + " </soapenv:Envelope> ";

    public static  final String QUERY_FOR_TOP_DATA =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
                    + " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"> "
                    + "    <soapenv:Body> "
                    + "       <RecordId>DPResponseRouter</RecordId>"
           //         + "       <QueryType>dhs-medicalassistance-test_forward-ssl-proxy</QueryType> "
                    + "    </soapenv:Body> "
                    + " </soapenv:Envelope> ";

    @Value("${client.ws.url}")
    private String clientURL;

    public List<String> queryForAvailableData(Map<String, String> queryMap) throws IOException, SaxonApiException {
        RestTemplate restTemplate =  new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        if (queryMap != null) {
            for(Map.Entry<String, String> entry : queryMap.entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
        HttpEntity<String> httpEntity = new HttpEntity<String>(QUERY_FOR_TOP_100_ROWS, headers);

        ResponseEntity<String> response = restTemplate.exchange(clientURL, HttpMethod.POST, httpEntity, String.class);
        XPathSelector xPathSelector =  SaxonUtils.getXPathSelectorFromString(response.getBody().toString(),
                "//row/column[1]/value[1]");

        FileUtils.overWriteToDisk(Constants.TMP_DATA_FILE, response.getBody().toString());

        List<String> values = new ArrayList<>();
        for (XdmItem item: xPathSelector) {
            String fileType = item.getStringValue();
            values.add(fileType);
        }
        return values;
    }

    public void saveFilesToDisk(List<String> transactionIds, String directory) throws IOException, SaxonApiException {
        RestTemplate restTemplate =  new RestTemplate();
        for (String transactionId : transactionIds) {
            String query = QUERY_FOR_TOP_DATA.replace("DPResponseRouter", transactionId);
            ResponseEntity<String> response = restTemplate.postForEntity(clientURL, query, String.class);

            String fileType = getFileName(transactionId, response.getBody().toString()).replaceAll(" ", "_");

            FileUtils.overWriteToDisk(directory + fileType + "-data.xml", response.getBody().toString());
            System.out.print(response.toString());
        }
    }

    private String getFileName(String id, String rsp)  throws IOException, SaxonApiException {
        String fileType = "";
        // Clean
        XPathSelector xpSelect =  SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='MessageKeyCodeText']");
        if (xpSelect.iterator().hasNext()) {
            fileType = ((XdmNode) xpSelect.iterator().next()).getStringValue();
            xpSelect =  SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='ControlFieldText']");
            if (xpSelect.iterator().hasNext()) {
                fileType += "-" + ((XdmNode) xpSelect.iterator().next()).getStringValue();
            }
            return fileType + "-" + id;
        }
        // Event M
        xpSelect =  SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='DocumentSubjectText']");
        if (xpSelect.iterator().hasNext()) {
            fileType = ((XdmNode) xpSelect.iterator().next()).getStringValue();
        } else {
            xpSelect = SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='ActivityCategoryText']");
            if (xpSelect.iterator().hasNext()) {
                fileType = ((XdmNode) xpSelect.iterator().next()).getStringValue();
            } else {
                return id;
            }
        }
        xpSelect = SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='CaseDocketID']/*[local-name()='ID']");
        if (xpSelect.iterator().hasNext()) {
            fileType += "-" + ((XdmNode) xpSelect.iterator().next()).getStringValue();
            return fileType;
        }
        xpSelect = SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='EventName']");
        if (xpSelect.iterator().hasNext()) {
            fileType += "-" + ((XdmNode) xpSelect.iterator().next()).getStringValue();
        }
        xpSelect = SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='InmateNumberID']");
        Iterator<XdmItem> itr = xpSelect.iterator();
        if (itr.hasNext()) {
            fileType += "-" + ((XdmNode) itr.next()).getStringValue();
        }
        xpSelect = SaxonUtils.getXPathSelectorFromString(rsp, "//*[local-name()='DateTime']");
        itr = xpSelect.iterator();
        if (itr.hasNext()) {
            fileType += "-" + ((XdmNode) itr.next()).getStringValue();
            return fileType;
        }
        return fileType + "-" + id;
    }

}
