import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.ArrayList;

/**
 * Replaces %AUTH0_DOMAIN%, %AUTH0_CLIENT_ID%, and %AUTH0_SCHEME% placeholders in strings.xml
 * with real Auth0 tenant values.
 *
 * Usage (requires JDK 11+, no compilation needed):
 *   java quickstart/scripts/Configure.java AUTH0_DOMAIN AUTH0_CLIENT_ID AUTH0_SCHEME
 *
 * Example:
 *   java quickstart/scripts/Configure.java dev-abc123.us.auth0.com aBcDeFgHiJkLmNoPqRsTuVwXyZ012345 https
 */
class Configure {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: java Configure.java AUTH0_DOMAIN AUTH0_CLIENT_ID AUTH0_SCHEME");
            System.exit(1);
        }

        String domain = args[0];
        String clientId = args[1];
        String scheme = args[2];

        File stringsFile = new File("app/src/main/res/values/strings.xml");
        if (!stringsFile.exists()) {
            System.err.println("Error: " + stringsFile.getPath() + " not found.");
            System.err.println("Run this script from the project root directory.");
            System.exit(1);
        }

        var factory = DocumentBuilderFactory.newInstance();
        var builder = factory.newDocumentBuilder();
        var doc = builder.parse(stringsFile);

        setStringValue(doc, "com_auth0_domain", domain);
        setStringValue(doc, "com_auth0_client_id", clientId);
        setStringValue(doc, "com_auth0_scheme", scheme);

        // Strip whitespace-only text nodes so INDENT=yes produces clean output
        stripWhitespace(doc.getDocumentElement());

        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc), new StreamResult(stringsFile));

        System.out.println("Auth0 SDK settings configured:");
        System.out.println("  auth0Domain    = " + domain);
        System.out.println("  auth0ClientId  = " + clientId);
        System.out.println("  auth0Scheme    = " + scheme);
    }

    private static void setStringValue(Document doc, String name, String value) {
        NodeList nodes = doc.getElementsByTagName("string");
        for (int i = 0; i < nodes.getLength(); i++) {
            var el = (Element) nodes.item(i);
            if (name.equals(el.getAttribute("name"))) {
                el.setTextContent(value);
                return;
            }
        }
        System.err.println("Warning: <string name=\"" + name + "\"> not found in strings.xml");
    }

    private static void stripWhitespace(Node node) {
        var toRemove = new ArrayList<Node>();
        var children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            var child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().isBlank()) {
                toRemove.add(child);
            } else {
                stripWhitespace(child);
            }
        }
        for (var child : toRemove) {
            node.removeChild(child);
        }
    }
}
