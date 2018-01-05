package typeset.io.generators;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import typeset.io.generators.util.GeneratorUtilities;
import typeset.io.model.spec.Spec;
import typeset.io.readers.ConfigReader;

/**
 * The Class TestNGGenerator.
 * Generates testng xml - used for co-ordinating tests
 */
public class TestNGGenerator {
	private static final Logger logger = LogManager.getLogger("TestNGGenerator");

	/** The output dir. */
	private String outputDir;
	
	/** The project name to use for test project */
	private String projectName;
	
	/** The home page. */
	private String homePage;
	
	/** The spec list. */
	private List<Spec> specList;

	/**
	 * Instantiates a new test NG generator.
	 *
	 * @param outputDir the output dir
	 * @param specList the spec list
	 * @param projectName the project name
	 * @param homePage the home page
	 */
	public TestNGGenerator(List<Spec> specList, String projectName, String homePage) {
		this.outputDir = ConfigReader.outputDir;
		this.projectName = projectName;
		this.homePage = homePage;
		this.specList = specList;
	}

	/**
	 * Generate testng XML.
	 * @param generatedTests 
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 * @throws TransformerException the transformer exception
	 */
	public void generateXML(Map<String, String> generatedTests) throws ParserConfigurationException, TransformerException {

		logger.info("Generating testNG XMl config file");

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("suite");
		doc.appendChild(rootElement);
		rootElement.setAttribute("name", projectName);

		// add listeners
		Element listeners = doc.createElement("listeners");
		Element listenerScreenshot = doc.createElement("listener");
		listenerScreenshot.setAttribute("class-name", "utils.Screenshot");
		listeners.appendChild(listenerScreenshot);
		
		Element listenerReporter = doc.createElement("listener");
		listenerReporter.setAttribute("class-name", "utils.ExtentReporterNG");
		listeners.appendChild(listenerReporter);

		rootElement.appendChild(listeners);

		// add parameter
		Element parameter = doc.createElement("parameter");
		parameter.setAttribute("name", "url");
		parameter.setAttribute("value", homePage);

		rootElement.appendChild(parameter);

		// test details
		Element test = doc.createElement("test");
		test.setAttribute("name", projectName + "_" + GeneratorUtilities.getTimestamp());

		rootElement.appendChild(test);

		Element testParameter = doc.createElement("parameter");
		testParameter.setAttribute("name", "Browser");
		testParameter.setAttribute("value", "chrome");

		test.appendChild(testParameter);
		
		// add classes
		Element classes = doc.createElement("classes");
		test.appendChild(classes);
		
		for(String testName: generatedTests.keySet()) {
			Element specClass = doc.createElement("class");
			specClass.setAttribute("name", testName);
			classes.appendChild(specClass);
			
			// add method
			Element methodClass = doc.createElement("methods");
			specClass.appendChild(methodClass);
			
			Element include = doc.createElement("include");
			include.setAttribute("name", generatedTests.get(testName));
			methodClass.appendChild(include);
		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		String filename = outputDir + File.separator + projectName + File.separator + "testng.xml";

		StreamResult result = new StreamResult(new File(filename));

		// Output to console for testing
		// StreamResult result = new StreamResult(System.out);

		transformer.transform(source, result);
	}
}
