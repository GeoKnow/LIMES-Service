package org.linkeddata.limes;

import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aliasi.util.Files;

public class LimesMainTest {

    private static final Logger log = Logger.getLogger(LimesMainTest.class);

    private String outputFormat = "N3";
    private String configFile = "test_config.xml";
    private String acceptedFile = "test_accepted.n3";
    private String reviewFile = "test_reviewe.n3";
    private String logFile = "test_config.log";

    private String endpointImport = "http://localhost:8080/generator/rest/session/28f53266-7efd-44d4-bc83-cfd1df51bb14";
    private String acceptGraph = "http://generator.geoknow.eu/resource/LimesJob_1416924194991_accepted";
    private String reviewGraph = "http://generator.geoknow.eu/resource/LimesJob_1416924194991_review";
    private String uriBase = "http://ontos.com/resource/";
    private String configurationFileTest = "lgd-lgd.xml";

    /**
     * Test limes. If endpoint for testing are not responding you can change the
     * configurationFileTest configuration file available in the resources if
     * the one set by default is not working
     * 
     * @throws Exception
     */
    @Test
    public void testLimesMain() throws Exception {

        // reads a configuration file from resources
        log.info("testPostXML " + configurationFileTest);

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(configurationFileTest).getFile());
        InputStream is = new FileInputStream(file);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(is);
        Element varElement = document.getDocumentElement();
        JAXBContext context = JAXBContext.newInstance(LimesConfig.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        LimesConfig config = unmarshaller.unmarshal(varElement, LimesConfig.class).getValue();

        config.setConfigurationfile(configFile);
        config.getAcceptance().setFile(acceptedFile);
        config.getReview().setFile(reviewFile);
        config.setOutput(outputFormat);

        LimesMain.executeLimes(config);

        log.info(config.toString());

        File resfile = new File(acceptedFile);

        assertNotSame(0, resfile.length());

        // config.setSaveendpoint(endpointImport);
        // config.setUribase(uriBase);
        // config.setAcceptgraph(acceptGraph);
        // config.setReviewgraph(reviewGraph);

        // LimesMain.saveResults(config);

    }

    @After
    public void deleteOutputFile() {
        // delete testing and temporal files
        (new File(configFile)).delete();
        (new File(logFile)).delete();
        (new File(reviewFile)).delete();
        (new File(acceptedFile)).delete();
        Files.removeRecursive(new File("cache"));

    }
}
