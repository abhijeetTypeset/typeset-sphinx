package typeset.io.generators;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import typeset.io.generators.util.GeneratorUtilities;
import typeset.io.model.spec.Spec;

public class TestNGGenerator {

	private String outputDir;
	private String projectName;
	private String homePage;
	private List<Spec> specList;

	public TestNGGenerator(String outputDir, List<Spec> specList, String projectName, String homePage) {
		this.outputDir = outputDir;
		this.projectName = projectName;
		this.homePage = homePage;
		this.specList = specList;
	}

	public void generateXML() throws ParserConfigurationException, TransformerException {

		System.out.println("Generating testNG XMl config file");

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
		testParameter.setAttribute("value", "chrome-headless");

		test.appendChild(testParameter);
		
		// add classes
		Element classes = doc.createElement("classes");
		test.appendChild(classes);
		
		for(Spec spec : specList) {
			Element specClass = doc.createElement("class");
			specClass.setAttribute("name", "tests."+GeneratorUtilities.firstLetterCaptial(spec.getName()));
			classes.appendChild(specClass);
			
			// add method
			Element methodClass = doc.createElement("methods");
			specClass.appendChild(methodClass);
			
			Element include = doc.createElement("include");
			include.setAttribute("name", "execute");
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
