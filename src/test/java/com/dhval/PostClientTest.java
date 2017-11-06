package com.dhval;

import com.dhval.utils.FileUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class PostClientTest {

    static {
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
    }

    public static  final String xmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
                    + " <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"> "
                    + "    <soapenv:Body> "
                    + "       <!-- <TrackingId>shreyas-5afa-4e39-b21b-fd3f390837f4</TrackingId> --> "
                    + "       <RecordId>93329845</RecordId> "
                    + "    </soapenv:Body> "
                    + " </soapenv:Envelope> ";

    @Value("${client.ws.url}")
    private String clientURL;


    @Test
    public void run() throws Exception {
        RestTemplate restTemplate =  new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("header_name", "header_value");
        for(String f : data) {
            String query = xmlString.replace("93329845", f);
            HttpEntity<String> request = new HttpEntity<String>(xmlString, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(clientURL, query, String.class);
            FileUtils.writeToDisk("tmp/" + f + ".xml", response.toString());
            System.out.print(response.toString());

        }
    }

    private String[] data = {
            "93329845",
            "93329507",
            "93329504",
            "93329429",
            "93329425",
            "93329418",
            "93329414",
            "93329301",
            "93329279",
            "93329267",
            "93329057",
            "93328875",
            "93328873",
            "93328422",
            "93328045",
            "93328000",
            "93327851",
            "93327282",
            "93327245",
            "93325384",
            "93324889",
            "93324388",
            "93324386",
            "93324385",
            "93323637",
            "93323219",
            "93323212",
            "93323204",
            "93323201"
    };
}
