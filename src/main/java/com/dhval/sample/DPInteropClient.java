package com.dhval.sample;
/**
 * DPInteropClient
 *   - send stylesheet and xmlfile to the Interop Test Service on a DataPower
 *   - appliance; and display the returned result of applying stylesheet to
 *   - xmlfile.
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;

public class DPInteropClient {

    private String requestType = null;

    private String zipFile = null;

    private String stylesheet = null;

    private String inputFile = null;

    private String[] inputFiles = null;

    private String mimeFile = null;

    private String xpathExpr = null;

    private String validateType = null;

    private String moreFiles = null;

    private String hostname = null;

    private String userInfo = null;

    private int port = -1;

    private boolean isSecured = false;

    //private boolean isDebugged = false;

    private boolean isMsgOnly = false;


    //the request message to sent
    private byte[] requestData = null;

    private static int DEF_PORT_NUMBER = 9990;

    private static String CRLF = "\r\n";

    private static String BOUNDARY = "540af153-f51d-4c96-a16b-d44bf0dd7925";

    private static String REQUEST_FILE = "tmp.request.swa";


    public DPInteropClient()
    {
    }

    private byte[] prepareFileParts() throws IOException
    {
        if (moreFiles == null)
            return null;

        List<byte[]> fileData = new ArrayList<byte[]>();
        List<Long> fileLengths = new ArrayList<Long>();

        String[] fileNames = moreFiles.split(",");
        for (int i=0; i<fileNames.length; i++)
        {
            File f = new File(fileNames[i]);
            if (!f.isFile())
            {
                System.err.println(">> '" + fileNames[i] + "' is not a file.");
                return null;
            }
            else
            {
                String fileHdr = "--" + BOUNDARY + CRLF +
                        "Content-ID: <" + fileNames[i] + ">" + CRLF +
                        "Content-Type: application/octet-stream" + CRLF +
                        "Content-Transfer-Encoding: binary" + CRLF +
                        CRLF;
                // the fileData may be binary
                //+ fileData + CRLF;

                Long length = fileHdr.length() + f.length() + 2;
                fileLengths.add(length);

                int curr = 0;
                byte[] data = new byte[length.intValue()];
                System.arraycopy(fileHdr.getBytes(), 0, data, curr, fileHdr.length());
                curr += fileHdr.length();

                byte[] file = readFileAsBytes(fileNames[i]);
                System.arraycopy(file, 0, data, curr, file.length);
                curr += file.length;

                System.arraycopy(CRLF.getBytes(), 0, data, curr, 2);

                fileData.add(data);
            }
        }

        long totalLength = 0;
        for (int i=0; i < fileLengths.size(); i++)
            totalLength += fileLengths.get(i);

        int curr = 0;
        byte allData[] = new byte[(int) totalLength];
        for (int i=0; i < fileData.size(); i++)
        {
            byte[] tmp = fileData.get(i);
            System.arraycopy(tmp, 0, allData, curr, tmp.length);
            curr += tmp.length;
        }

        return allData;
    }

    private void prepareXSLTRequest() throws IOException
    {
        byte[] zipData = null;
        String xsltData = null;
        String inputData1 = null;
        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
            zipData = readFileAsBytes(zipFile);
        if (!stylesheet.startsWith("zip:"))
            xsltData = readFileAsString(stylesheet);
        if (!inputFile.startsWith("zip:"))
            inputData1 = readFileAsString(inputFiles[0]);

        String xmlHref =
                "      <XMLData href=\"" + (inputFile.startsWith("zip:") ? "zip:" : "cid:") + inputFiles[0] + "\"/>";
        if (inputFile.startsWith("zip:"))
        {
            for (int i=1; i<inputFiles.length; i++)
                xmlHref += CRLF + "      <Attachment href=\"zip:" + inputFiles[i] + "\"/>";
        }
        else
        {
            for (int i=1; i<inputFiles.length; i++)
                xmlHref += CRLF + "      <Attachment href=\"cid:" + inputFiles[i] + "\"/>";
        }

        String result =
                CRLF + CRLF + "--" + BOUNDARY + CRLF +
                        "Content-Type: text/xml; charset=UTF-8" + CRLF +
                        "Content-Transfer-Encoding: binary" + CRLF +
                        "Content-Id: <request>" + CRLF +
                        CRLF +
                        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + CRLF +
                        "  <soap:Body>" + CRLF +
                        "    <test:XSLTTest " +
                        ((stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
                                ? "zipFile=\"cid:" + zipFile + "\""
                                : "") + CRLF +
                        "      xmlns:test=\"http://www.datapower.com/xslt-test\">" + CRLF +
                        "      <Stylesheet href=\"" + (stylesheet.startsWith("zip:") ? "" : "cid:") + stylesheet + "\"/>" + CRLF +
                        xmlHref + CRLF +
                        "    </test:XSLTTest>" + CRLF +
                        "  </soap:Body>" + CRLF +
                        "</soap:Envelope>" + CRLF;

        if (!stylesheet.startsWith("zip:"))
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + stylesheet + ">" + CRLF +
                            "Content-Type: text/xml" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF +
                            xsltData + CRLF;
        }

        if (!inputFile.startsWith("zip:"))
        {
            //the first input file, and maybe the last
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + inputFiles[0] + ">" + CRLF +
                            "Content-Type: text/xml" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF +
                            inputData1 + CRLF;
        }

        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + zipFile + ">" + CRLF +
                            "Content-Type: application/zip" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF;
            // the zipData is binary
            //+ zipData + CRLF;
        }

        int total = result.length();
        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
            total += zipData.length + 2; //2 for the ending CRLF

        //The rest of input files might be binary.
        //Keep the bytes of the rest of input files in data.
        byte[][] data = null;
        if (!inputFile.startsWith("zip:") && inputFiles.length > 1)
        {
            data = new byte [inputFiles.length - 1][];

            for (int i=1; i<inputFiles.length; i++)
            {
                //The input files may be binary. We can't just concat the input data to the result string.
                String inputHdr =
                        "--" + BOUNDARY + CRLF +
                                "Content-ID: <" + inputFiles[i] + ">" + CRLF +
                                "Content-Type: application/octet-stream" + CRLF +
                                "Content-Transfer-Encoding: binary" + CRLF +
                                CRLF; // + inputData + CRLF;

                byte[] inputData = readFileAsBytes(inputFiles[i]);

                int inputSz = inputHdr.length() + inputData.length + 2; //2 for the ending CRLF

                data[i-1] = new byte[inputSz];

                //copy the inputHdr to data[i-1]
                byte[] tmp = inputHdr.getBytes();
                for (int j=0; j< tmp.length; j++)
                    data[i-1][j] = tmp[j];

                //and then append the inputData to data[i-1]
                for (int j=0; j<inputData.length; j++)
                    data[i-1][tmp.length+j] = inputData[j];

                //don't forget the ending CRLF
                data[i-1][inputSz-2] = '\r';
                data[i-1][inputSz-1] = '\n';
            }

            //update the total length
            for (int i=1; i<inputFiles.length; i++)
                total += data[i-1].length;
        }

        //now the ending boundary
        String endingBdry = "--" + BOUNDARY + "--" + CRLF;
        total += endingBdry.length();

        //it is time to get the bytes for requestData
        requestData = new byte[total];

        //First, the result string
        byte[] tmp = result.getBytes();
        System.arraycopy(tmp, 0, requestData, 0, tmp.length);

        int curr = tmp.length;

        //and the zipData + CRLF
        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
        {
            System.arraycopy(zipData, 0, requestData, curr, zipData.length);
            curr += zipData.length;
            System.arraycopy(CRLF.getBytes(), 0, requestData, curr, 2);
            curr += 2;
        }

        //Second, the input files (except for the first one)
        if (!inputFile.startsWith("zip:"))
        {
            for (int i=1; i<inputFiles.length; i++)
            {
                System.arraycopy(data[i-1], 0, requestData, curr, data[i-1].length);
                curr += data[i-1].length;
            }
        }

        //Last, the ending boundary.
        tmp = endingBdry.getBytes();
        System.arraycopy(tmp, 0, requestData, curr, tmp.length);
    }

    private void prepareXSLTRequest2() throws IOException
    {
        String mimeData = readFileAsString(mimeFile);

        //match the boundary in the given MIME file.
        Pattern pattern = Pattern.compile("--([0-9a-zA-Z_-]+)--");
        Matcher matcher = pattern.matcher(mimeData);

        //find the last match in case there is any embedded MIME message
        while (matcher.find())
            BOUNDARY = matcher.group(1);
        System.err.println(">> Found the MIME boundary '" + BOUNDARY + "' in '" + mimeFile + "'.");

        //find the Content-ID now
        List<String> list = new ArrayList<String>();
        pattern = Pattern.compile("content-id: <([^>]*)>", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(mimeData);
        while (matcher.find())
        {
            String tmp = matcher.group(1);
            //skip the attached stylesheet
            if (!stylesheet.startsWith("cid:") || !tmp.equals(stylesheet.substring(4)))
            {
                list.add(tmp);
                System.err.println(">> Found the part '" + tmp + "' in the MIME file.");
            }
        }
        if (list.size() == 0)
        {
            System.err.println(">> Error: cannot find any part in the MIME file. Exit now.");
            System.exit(1);
        }

        String xmlHref =
                "      <XMLData href=\"cid:" + list.get(0) + "\"/>";
        for (int i=1; i<list.size(); i++)
            xmlHref += CRLF + "      <Attachment href=\"cid:" + list.get(i) + "\"/>";

        boolean readFromLocal = true;
        String xsltData = null;
        if (stylesheet.startsWith("cid:"))
        {
            readFromLocal = false;
            stylesheet = stylesheet.substring(4);
        }
        else
            xsltData = readFileAsString(stylesheet);

        String result =
                CRLF + CRLF + "--" + BOUNDARY + CRLF +
                        "Content-Type: text/xml; charset=UTF-8" + CRLF +
                        "Content-Transfer-Encoding: binary" + CRLF +
                        "Content-Id: <request>" + CRLF +
                        CRLF +
                        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + CRLF +
                        "  <soap:Body>" + CRLF +
                        "    <test:XSLTTest xmlns:test=\"http://www.datapower.com/xslt-test\">" + CRLF +
                        "      <Stylesheet href=\"cid:" + stylesheet + "\"/>" + CRLF +
                        xmlHref + CRLF +
                        "    </test:XSLTTest>" + CRLF +
                        "  </soap:Body>" + CRLF +
                        "</soap:Envelope>" + CRLF;

        //write out the stylesheet from the local file system
        if (readFromLocal)
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + stylesheet + ">" + CRLF +
                            "Content-Type: text/xml" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF +
                            xsltData + CRLF;
        }

        //find out where the first boundary is to trim the preamble
        pattern = Pattern.compile("--" + BOUNDARY);
        matcher = pattern.matcher(mimeData);
        int startingPos = 0;
        if (matcher.find())
        {
            startingPos = matcher.start();
            //System.err.println(">> Trimming the preamble before position: " + startingPos);
        }

        //append the swa file without the preamble.
        //there could be binary data in the MIME file. So copy the bytes instead of string concatenation.
        byte[] resultBytes = result.getBytes();
        byte[] mimeBytes = readFileAsBytes(mimeFile);

        int total = resultBytes.length + mimeBytes.length - startingPos;
        requestData = new byte[total];

        System.arraycopy(resultBytes, 0, requestData, 0, resultBytes.length);

        System.arraycopy(mimeBytes, startingPos, requestData, resultBytes.length, mimeBytes.length - startingPos);
    }

    private void prepareSchemaRequest() throws IOException
    {
        byte[] zipData = null;
        String xsltData = null;
        String inputData = null;

        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
            zipData = readFileAsBytes(zipFile);
        if (!stylesheet.startsWith("zip:"))
            xsltData = readFileAsString(stylesheet);
        if (!inputFile.startsWith("zip:"))
            inputData = readFileAsString(inputFiles[0]);

        String result =
                CRLF + CRLF + "--" + BOUNDARY + CRLF +
                        "Content-Type: text/xml; charset=UTF-8" + CRLF +
                        "Content-Transfer-Encoding: binary" + CRLF +
                        "Content-Id: <request>" + CRLF +
                        CRLF +
                        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + CRLF +
                        "  <soap:Body>" + CRLF +
                        "    <test:SchemaTest type=\"" + validateType +"\" " +
                        ((stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
                                ? "zipFile=\"cid:" + zipFile + "\""
                                : "") + CRLF +
                        "        xmlns:test=\"http://www.datapower.com/xslt-test\">" + CRLF +
                        "      <Schema href=\"" + (stylesheet.startsWith("zip:") ? "" : "cid:") + stylesheet + "\"/>" + CRLF +
                        "      <XMLData href=\"" + (inputFile.startsWith("zip:") ? "zip:" : "cid:") + inputFiles[0] + "\"/>" + CRLF +
                        "    </test:SchemaTest>" + CRLF +
                        "  </soap:Body>" + CRLF +
                        "</soap:Envelope>" + CRLF;

        if (!stylesheet.startsWith("zip:"))
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + stylesheet + ">" + CRLF +
                            "Content-Type: text/xml" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF +
                            xsltData + CRLF;
        }

        if (!inputFile.startsWith("zip:"))
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + inputFiles[0] + ">" + CRLF +
                            "Content-Type: text/xml" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF +
                            inputData + CRLF;
        }

        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + zipFile + ">" + CRLF +
                            "Content-Type: application/zip" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF;
            // the zipData is binary
            //+ zipData + CRLF;
        }

        int total = result.length();
        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
            total += zipData.length + 2; //2 for the ending CRLF

        //now the ending boundary
        String endingBdry = "--" + BOUNDARY + "--" + CRLF;
        total += endingBdry.length();

        //it is time to get the bytes for requestData
        requestData = new byte[total];

        //First, the result string
        byte[] tmp = result.getBytes();
        System.arraycopy(tmp, 0, requestData, 0, tmp.length);

        int curr = tmp.length;

        //and the zipData + CRLF
        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
        {
            System.arraycopy(zipData, 0, requestData, curr, zipData.length);
            curr += zipData.length;
            System.arraycopy(CRLF.getBytes(), 0, requestData, curr, 2);
            curr += 2;
        }

        //Last, the ending boundary.
        tmp = endingBdry.getBytes();
        System.arraycopy(tmp, 0, requestData, curr, tmp.length);
    }


    private void prepareFFDRequest() throws IOException
    {
        byte[] zipData = null;
        String xsltData = null;
        byte[] inputData = null;

        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
            zipData = readFileAsBytes(zipFile);
        if (!stylesheet.startsWith("zip:"))
            xsltData = readFileAsString(stylesheet);
        if (!inputFile.startsWith("zip:"))
            inputData = readFileAsBytes(inputFile);

        String result =
                CRLF + CRLF + "--" + BOUNDARY + CRLF +
                        "Content-Type: text/xml; charset=UTF-8" + CRLF +
                        "Content-Transfer-Encoding: binary" + CRLF +
                        "Content-Id: <request>" + CRLF +
                        CRLF +
                        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + CRLF +
                        "  <soap:Body>" + CRLF +
                        "    <test:FFDTest " +
                        ((stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
                                ? "zipFile=\"cid:" + zipFile + "\""
                                : "") + CRLF +
                        "      xmlns:test=\"http://www.datapower.com/xslt-test\">" + CRLF +
                        "      <Stylesheet href=\"" + (stylesheet.startsWith("zip:") ? "" : "cid:") + stylesheet + "\"/>" + CRLF +
                        "      <BinaryData href=\"" + (inputFile.startsWith("zip:") ? "" : "cid:") + inputFile + "\"/>" + CRLF +
                        "    </test:FFDTest>" + CRLF +
                        "  </soap:Body>" + CRLF +
                        "</soap:Envelope>" + CRLF;

        if (!stylesheet.startsWith("zip:"))
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + stylesheet + ">" + CRLF +
                            "Content-Type: text/xml" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF +
                            xsltData + CRLF;
        }

        if (!inputFile.startsWith("zip:"))
        {
            result +=
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + inputFile + ">" + CRLF +
                            "Content-Type: application/octet-stream" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF;
            // the inputData is binary
            //+ inputData + CRLF;
        }

        String zipHdr = null;
        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
        {
            zipHdr =
                    "--" + BOUNDARY + CRLF +
                            "Content-ID: <" + zipFile + ">" + CRLF +
                            "Content-Type: application/zip" + CRLF +
                            "Content-Transfer-Encoding: binary" + CRLF +
                            CRLF;
            // the zipData is binary
            //+ zipData + CRLF;
        }

        String endingBdry = "--" + BOUNDARY + "--" + CRLF;

        //calculate the total length
        int total = result.length();

        if (!inputFile.startsWith("zip:"))
            total += inputData.length + 2;  //2 for the CRLF

        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
        {
            total += zipHdr.length();
            total += zipData.length + 2;  //2 for the CRLF
        }

        byte[] moreParts = prepareFileParts();
        if (moreParts != null)
            total += moreParts.length;

        total += endingBdry.length();

        requestData = new byte[total];

        //copy the result string to requestData
        byte[] tmp = result.getBytes();
        System.arraycopy(tmp, 0, requestData, 0, tmp.length);
        int curr = tmp.length;

        //copy the input file to requestData
        if (!inputFile.startsWith("zip:"))
        {
            System.arraycopy(inputData, 0, requestData, curr, inputData.length);
            curr += inputData.length;
            System.arraycopy(CRLF.getBytes(), 0, requestData, curr, 2);
            curr += 2;
        }

        //copy the zip file to requestData
        if (stylesheet.startsWith("zip:") || inputFile.startsWith("zip:"))
        {
            System.arraycopy(zipHdr.getBytes(), 0, requestData, curr, zipHdr.length());
            curr += zipHdr.length();
            System.arraycopy(zipData, 0, requestData, curr, zipData.length);
            curr += zipData.length;
            System.arraycopy(CRLF.getBytes(), 0, requestData, curr, 2);
            curr += 2;
        }

        //copy the additional files if any
        if (moreParts != null && moreParts.length > 0)
        {
            System.arraycopy(moreParts, 0, requestData, curr, moreParts.length);
            curr += moreParts.length;
        }

        //copy the ending boundary to requestData
        tmp = endingBdry.getBytes();
        System.arraycopy(tmp, 0, requestData, curr, tmp.length);
    }

    private void prepareXPathRequest() throws IOException
    {
        String inputData = readFileAsString(inputFile);

        String result =
                CRLF + CRLF + "--" + BOUNDARY + CRLF +
                        "Content-Type: text/xml; charset=UTF-8" + CRLF +
                        "Content-Transfer-Encoding: binary" + CRLF +
                        "Content-Id: <request>" + CRLF +
                        CRLF +
                        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" + CRLF +
                        "  <soap:Body>" + CRLF +
                        "    <test:XPathTest xmlns:test=\"http://www.datapower.com/xslt-test\">" + CRLF +
                        "      <XPathExpr>" + xpathExpr + "</XPathExpr>" + CRLF +
                        "      <XMLData href=\"cid:" + inputFile + "\"/>" + CRLF +
                        "    </test:XPathTest>" + CRLF +
                        "  </soap:Body>" + CRLF +
                        "</soap:Envelope>" + CRLF +
                        "--" + BOUNDARY + CRLF +
                        "Content-ID: <" + inputFile + ">" + CRLF +
                        "Content-Type: text/xml" + CRLF +
                        "Content-Transfer-Encoding: binary" + CRLF +
                        CRLF +
                        inputData + CRLF +
                        "--" + BOUNDARY + "--" + CRLF;

        requestData = result.getBytes();
    }

    public void parseArgs(String[] args)
    {
        for (int i=0; i<args.length; i++)
        {
            if (args[i].equals("-t"))
            {
                requestType = args[++i];
            }
            else if (args[i].equals("-z"))
            {
                zipFile = args[++i];
            }
            else if (args[i].equals("-x"))
            {
                stylesheet = args[++i];
            }
            else if (args[i].equals("-zx"))
            {
                stylesheet = "zip:" + args[++i];
            }
            else if (args[i].equals("-i"))
            {
                inputFile = args[++i];
            }
            else if (args[i].equals("-zi"))
            {
                inputFile = "zip:" + args[++i];
            }
            else if (args[i].equals("-a"))
            {
                mimeFile = args[++i];
            }
            else if (args[i].equals("-e"))
            {
                xpathExpr = args[++i];
            }
            else if (args[i].equals("-v"))
            {
                validateType = args[++i];
            }
            else if (args[i].equals("-f"))
            {
                moreFiles = args[++i];
            }
            else if (args[i].equals("-u"))
            {
                userInfo = args[++i];
            }
            else if (args[i].equals("-h"))
            {
                hostname = args[++i];
            }
            else if (args[i].equals("-p"))
            {
                try
                {
                    port = Integer.parseInt(args[++i]);
                }
                catch (NumberFormatException e)
                {
                    port = DEF_PORT_NUMBER;
                    System.err.println(">> Bad port number '" + args[i] + "'. Set to " + DEF_PORT_NUMBER + " instead.");
                }
            }
            else if (args[i].equals("-s"))
            {
                isSecured = true;
            }
            /*
            else if (args[i].equals("-g"))
            {
                isDebugged = true;
            }
            */
            else if (args[i].equals("-m"))
            {
                isMsgOnly = true;
            }
            else
            {
                System.err.println(">> Ignored the unknown arg '" + args[i] + "'.");
            }
        }
    }

    public boolean validateArgs()
    {
        //validate the requestType
        if (requestType == null)
        {
            requestType = "xslt";
        }
        else if (!requestType.equals("xslt") && !requestType.equals("ffd") && !requestType.equals("xpath") && !requestType.equals("schema"))
        {
            System.err.println(">> Bad request type '" + requestType + "'. Set to the 'xslt' instead.");
            requestType = "xslt";
        }
        else if (requestType.equals("schema"))
        {
            if (validateType == null)
                validateType = "schema";
            else if (!validateType.equals("schema") && !validateType.equals("wsdl"))
            {
                System.err.println(">> Bad validation type '" + requestType + "'. Set to the 'schema' instead.");
                requestType = "xslt";
            }
        }

        //validate the stylesheet
        if (!requestType.equals("xpath") && (stylesheet == null || stylesheet.equals("")))
        {
            System.err.println(">> The stylesheet or the schema file is required. Use '-x' to specify it.");
            return false;
        }

        //validate the inputFile
        if (!requestType.equals("xslt") || (mimeFile == null || mimeFile.equals("")))
        {
            if (inputFile == null || inputFile.equals(""))
            {
                System.err.println(">> The input file is required. Use '-i' to specify it.");
                return false;
            }
        }

        //check the file existence
        if (inputFile != null && !inputFile.equals(""))
        {
            if (!inputFile.startsWith("zip:"))
            {
                inputFiles = inputFile.split(",");
                for (int i=0; i<inputFiles.length; i++)
                {
                    File f = new File(inputFiles[i]);
                    if (!f.isFile())
                    {
                        System.err.println(">> '" + inputFiles[i] + "' is not a file.");
                        return false;
                    }
                }
            }
            else
            {
                inputFiles = inputFile.substring(4).split(",");
            }
        }

        //check the file existence
        if (mimeFile != null && !mimeFile.equals(""))
        {
            File f = new File(mimeFile);
            if (!f.isFile())
            {
                System.err.println(">> '" + mimeFile + "' is not a file.");
                return false;
            }
        }

        //check the file existence
        if ((stylesheet != null && stylesheet.startsWith("zip:")) || (inputFile != null && inputFile.startsWith("zip:")))
        {
            File f = new File(zipFile);
            if (!f.isFile())
            {
                System.err.println(">> '" + zipFile + "' is not a file.");
                return false;
            }
        }

        /*
        if (isDebugged)
            isSecured = true;

        if (isDebugged)
        {
            if (userInfo == null)
            {
                System.err.println(">> The authentication info is required. Use '-u' to specify the 'user:passwd'.");
                return false;
            }
        }
        */

        if (port == -1)
        {
            if (isSecured)
                port = DEF_PORT_NUMBER + 1;
            else
                port = DEF_PORT_NUMBER;
        }

        if (!isMsgOnly && (hostname == null || hostname.equals("")))
        {
            System.err.println(">> Hostname is required. Use '-h' to specify it.");
            return false;
        }

        return true;
    }

    public void prepareRequest() throws IOException
    {
        if (requestType.equals("xslt"))
        {
            if (inputFile != null && !inputFile.equals(""))
                prepareXSLTRequest();
            else
                prepareXSLTRequest2();
        }
        else if (requestType.equals("schema"))
        {
            prepareSchemaRequest();
        }
        else if (requestType.equals("ffd"))
        {
            prepareFFDRequest();
        }
        else if (requestType.equals("xpath"))
        {
            prepareXPathRequest();
        }
        else
            System.exit(1);

        System.err.println(">> Creating the " + requestType + " request...");
    }

    //A TrustManager that doesn't validate servers' certificates
    class TrustAnyServerManager implements X509TrustManager
    {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
            //can never be a server. Throw exception here...
            throw new CertificateException("Untrusted client: " + chain + " (" + authType + ").");
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException
        {
            //trust all servers. do nothing here....
            System.err.println(">> Skip the certificate validation.");
        }

        public X509Certificate[] getAcceptedIssuers()
        {
            //return an empty array
            return new X509Certificate[] {};
        }
    }

    //a HostnameVerifier that doesn't verify the hostname.
    class DummyHostnameVerifier implements HostnameVerifier
    {
        public boolean verify(String urlHostName, SSLSession session)
        {
            //doesn't verify at all
            System.err.println(">> Skip the hostname verification (" + urlHostName + ").");
            return true;
        }
    }

    private void setupHttpsConnection()
    {
        //1. setup the SSLSocketFactory with the SSLContext that disables the certificate validation
        try
        {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null,
                    new X509TrustManager[] { new TrustAnyServerManager() },
                    null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e)
        {
            System.err.println(">> Failed to create a SSLContext that disables the certificate validation.");
            System.err.println(e);
            System.exit(1);
        }

        //2. setup the HostnameVerifier that doesn't verify hostname at all.
        HttpsURLConnection.setDefaultHostnameVerifier(new DummyHostnameVerifier());
    }

    public String getEndPoint()
    {
        if (isSecured)
            return "https://" + hostname + ":" + port + "/";

        return "http://" + hostname + ":" + port + "/";
    }

    public void doTheWork() throws IOException
    {
        if (requestData == null)
        {
            System.err.println(">> Failed to generate the request message.");
            System.exit(1);
        }

        if (isMsgOnly)
        {
            //save the request into file and return
            writeBytesToFile(REQUEST_FILE, requestData);

            System.err.println(">> Saving the request into the file '" + REQUEST_FILE + "'.");
            System.exit(0);
        }


        //create the connection to the Interop Test Service
        URL url = new URL(getEndPoint());
        System.err.println(">> Connecting to endpoint: " + getEndPoint());

        if (isSecured)
            setupHttpsConnection();

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the basic authentication
        if (userInfo != null)
        {
            String auth = base64Encode(userInfo.getBytes());
            System.err.println(">> Using authentication: " + auth);
            conn.setRequestProperty("Authorization", "Basic " + auth);
        }
        else
            System.err.println(">> No basic authentication is provided.");


        //set the appropriate HTTP parameters
        conn.setRequestProperty("Content-Type", "multipart/related; type=\"text/xml\"; boundary=\"" + BOUNDARY + "\"");
        /*
        if (isDebugged)
        {
            System.err.println(">> Debug mode is enabled over HTTPS.");
            conn.setRequestProperty("X-DP-INTEROP-TEST-DEBUG", "yes");
        }
        */

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        //everything's set up; send the request data now
        OutputStream out = conn.getOutputStream();
        out.write(requestData);
        out.close();

        //read the response and write it to standard out.
        int responseCode = conn.getResponseCode();
        InputStream is = null;
        System.err.println(">> Receiving HTTP response: " + responseCode);
        try
        {
            is = conn.getInputStream();
        }
        catch (Exception e)
        {
            is = conn.getErrorStream();

            if (is == null)
            {
                System.err.println(">> Neither the input stream nor the error stream can be retrieved. Exit now. (" + responseCode + ")");
                System.exit(1);
            }
        }

        int noOfBytes = 0;
        byte[] buffer = new byte[4096];
        while ((noOfBytes = is.read(buffer)) != -1)
        {
            System.out.write(buffer, 0, noOfBytes);
        }
        is.close();
    }

    public static void printUsage()
    {
        String helpText = "usage: DPInteropClient " +
                "[-t xslt|ffd|xpath|schema] [-z zip-file] [-x stylesheet] [-zx stylesheet] [-i input-file] [-zi input-file] [-a mime-file] [-e xpath-expr] [-v schema|wsdl] [-h hostname] [-p port] [-s] [-u user:password] [-m]\n\n" +
                "\t-t  <xslt|ffd|xpath|schema>  request type (the default is set to xslt)\n" +
                "\t-z  <zip-file>        the zip file that contains the stylesheet and (or) input files needed for transformation.\n" +
                "\t-x  <stylesheet>      the stylesheet file or the schema file\n" +
                "\t-zx <stylesheet>      the stylesheet file or the schema file that can be found in the zip file.\n" +
                "\t-i  <input-file>      the input file for transformation. Use comma to separate multiple input files.\n" +
                "\t-zi <input-file>      the input file that can be found in the zip file.\n" +
                "\t-a  <mime-file>       the SwA or Mime input file for transformation\n" +
                "\t-e  <xpath-expr>      the XPath expression\n" +
                "\t-v  <schema|wsdl>     the validation type (the default is set to schema)\n" +
                "\t-f  <other-files>     to package the additional files in the request message. Use comma to separate them.\n" +
                "\t                      For exapmle, to include the FFD files with the request message.\n" +
                "\t-h  <hostname>        hostname\n" +
                "\t-p  <port>            port number (defaults to " + DEF_PORT_NUMBER + "and " + (DEF_PORT_NUMBER+1) + " for HTTP and HTTPS respectively)\n" +
                "\t-s                    to send request over HTTPS\n" +
                "\t-u  <user:password>   the username and password.\n" +
                "\t                      Check the AAA Policy of Interop Test Service to see if the basic authentication is required.\n" +
                //"\t-g                    Enable the debug mode. Request the Interop Test Service to provide the probe data.\n" +
                //"\t                         1. The request will be sent over HTTPS, and\n" +
                //"\t                         2. Users must provide the basic authentication with the option '-u'.\n" +
                "\t-m                    to generate the request message only instead of sending the request.\n\n" +
                "[XSLT]      DPInteropClient -x transform.xsl -i input.xml -h dpbox.foo.com\n" +
                "            DPInteropClient -x transform.xsl -i input.xml -h dpbox.foo.com -p 9991 -s\n" +
                "            DPInteropClient -z artifacts.zip -zx dir1/transform.xsl -zi dir2/data.xml -h dpbox.foo.com\n\n" +
                "[XSLT-swa]  DPInteropClient -x cid:transform.xsl -a input.swa -h dpbox.foo.com\n" +
                "            DPInteropClient -x transform.xsl -a input.swa -h dpbox.foo.com\n" +
                "            DPInteropClient -x transform.xsl -i file1.xml,file2.xxx,file3.xxx -h dpbox.foo.com\n" +
                "            DPInteropClient -z artifacts.zip -x transform.xsl -zi dir/file1.xml,dir/file2.dat,dir/file3.bin -h dpbox.foo.com\n\n" +
                "[Schema]    DPInteropClient -t schema -x data-type.xsd -i input.xml -h dpbox.foo.com\n" +
                "            DPInteropClient -t schema -v wsdl -x services.wsdl -i request.xml -h dpbox.foo.com -p 2048 -s\n" +
                "            DPInteropClient -t schema -v wsdl -z artifacts.zip -zx services.wsdl -zi request.xml -h dpbox.foo.com\n\n" +
                "[FFD]       DPInteropClient -t ffd -x csv2xml.xsl -i input.csv -h dpbox.foo.com\n" +
                "            DPInteropClient -t ffd -x csv2xml.xsl -i input.csv -f input.ffd,output.ffd -h dpbox.foo.com -u user:pass\n" +
                "            DPInteropClient -t ffd -z artifacts.zip -zx dir/csv2text.xsl -zi dir/input.csv -h dpbox.foo.com -u user:pass\n\n" +
                "[XPATH]     DPInteropClient -t xpath -e \"count(//*)\" -i input.xml -h dpbox.foo.com\n";

        System.err.println(helpText);
    }

    public static void main(String[] args) throws Exception
    {
        DPInteropClient client = new DPInteropClient();

        client.parseArgs(args);
        if (!client.validateArgs())
        {
            printUsage();
            System.exit(1);
        }

        client.prepareRequest();

        client.doTheWork();
    }

    public static void writeBytesToFile(String fileName, byte[] bytes) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(bytes);
        fos.close();
    }

    public static void writeStringToFile(String fileName, String text) throws IOException
    {
        FileWriter fstream = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(text);
        out.close();
    }

    public static String readFileAsString(String fileName) throws IOException
    {
        byte[] buffer = readFileAsBytes(fileName);

        return new String(buffer);
    }

    public static byte[] readFileAsBytes(String fileName) throws IOException
    {
        byte[] buffer = new byte[(int) new File(fileName).length()];
        BufferedInputStream fis = null;
        try
        {
            fis = new BufferedInputStream(new FileInputStream(fileName));
            fis.read(buffer);
        }
        finally
        {
            if (fis != null)
                try
                {
                    fis.close();
                }
                catch (IOException ignored)
                {
                }
        }
        return buffer;
    }

    public static String base64Encode(byte[] bytes) throws IOException
    {
        //base64 encoding characters
        final char b64[] = {
                'A','B','C','D','E','F','G','H','I','J','K','L','M',
                'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'n','o','p','q','r','s','t','u','v','w','x','y','z',
                '0','1','2','3','4','5','6','7','8','9','+','/'
        };

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int i;
        for(i=0; i<bytes.length-2; i+=3)
        {
            bos.write(b64[  ((int)bytes[i+0]&0xFF)      >>2 ]);
            bos.write(b64[((((int)bytes[i+0]&0xFF)&0x03)<<4) |
                    (((int)bytes[i+1]&0xFF)      >>4)]);
            bos.write(b64[((((int)bytes[i+1]&0xFF)&0x0F)<<2) |
                    (((int)bytes[i+2]&0xFF)      >>6)]);
            bos.write(b64[  ((int)bytes[i+2]&0xFF)&0x3F     ]);
        }

        final char pad = '=';
        if (i < bytes.length)
        {
            bos.write(b64[  ((int)bytes[i+0]&0xFF)      >>2 ]);

            if (i+2 == bytes.length)
            {
                bos.write(b64[((((int)bytes[i+0]&0xFF)&0x03)<<4) |
                        (((int)bytes[i+1]&0xFF)      >>4)]);
                bos.write(b64[((((int)bytes[i+1]&0xFF)&0x0F)<<2)]);
            }
            else
            {
                bos.write(b64[((((int)bytes[i+0]&0xFF)&0x03)<<4)]);
                bos.write(pad);
            }
            bos.write(pad);
        }
        bos.close();

        return bos.toString();
    }

}

