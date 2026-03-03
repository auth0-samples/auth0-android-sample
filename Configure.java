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
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Replaces %AUTH0_DOMAIN%, %AUTH0_CLIENT_ID%, %AUTH0_SCHEME%, and %APPLICATION_ID% placeholders
 * in strings.xml and build.gradle.kts with real Auth0 tenant values.
 *
 * Usage (requires JDK 11+, no compilation needed):
 *   java quickstart/scripts/Configure.java --domain AUTH0_DOMAIN --client-id AUTH0_CLIENT_ID --scheme AUTH0_SCHEME --application-id APPLICATION_ID
 *
 * Example:
 *   java quickstart/scripts/Configure.java --domain dev-abc123.us.auth0.com --client-id aBcDeFgHiJkLmNoPqRsTuVwXyZ012345 --scheme https --application-id com.example.myapp
 */
class Configure {
    public static void main(String[] args) throws Exception {
        String domain = null;
        String clientId = null;
        String scheme = null;
        String applicationId = null;

        for (int i = 0; i < args.length - 1; i++) {
            switch (args[i]) {
                case "--domain":         domain = args[++i]; break;
                case "--client-id":      clientId = args[++i]; break;
                case "--scheme":         scheme = args[++i]; break;
                case "--application-id": applicationId = args[++i]; break;
            }
        }

        if (domain == null || clientId == null || scheme == null || applicationId == null) {
            System.err.println("Usage: java Configure.java --domain AUTH0_DOMAIN --client-id AUTH0_CLIENT_ID --scheme AUTH0_SCHEME --application-id APPLICATION_ID");
            System.exit(1);
        }

        if (!applicationId.matches("[a-zA-Z][a-zA-Z0-9]*(\\.[a-zA-Z][a-zA-Z0-9]*)+")) {
            System.err.println("Error: applicationId \"" + applicationId + "\" is not a valid Java package name.");
            System.err.println("Expected format: com.example.myapp");
            System.exit(1);
        }

        File stringsFile = new File("app/src/main/res/values/strings.xml");
        if (!stringsFile.exists()) {
            System.err.println("Error: " + stringsFile.getPath() + " not found.");
            System.err.println("Run this script from the project root directory.");
            System.exit(1);
        }

        File gradleFile = new File("app/build.gradle.kts");
        if (!gradleFile.exists()) {
            System.err.println("Error: " + gradleFile.getPath() + " not found.");
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

        updateGradleApplicationId(gradleFile, applicationId);

        System.out.println("Auth0 SDK settings configured:");
        System.out.println("  auth0Domain    = " + domain);
        System.out.println("  auth0ClientId  = " + clientId);
        System.out.println("  auth0Scheme    = " + scheme);
        System.out.println("  applicationId  = " + applicationId);
    }

    private static void updateGradleApplicationId(File gradleFile, String applicationId) throws Exception {
        String content = Files.readString(gradleFile.toPath());
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("applicationId = \"[^\"]+\"");
        java.util.regex.Matcher matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()) count++;
        if (count == 0) {
            System.err.println("Error: applicationId not found in " + gradleFile.getPath());
            System.exit(1);
        }
        if (count > 1) {
            System.err.println("Error: found " + count + " applicationId occurrences in " + gradleFile.getPath() + "; expected exactly 1.");
            System.exit(1);
        }
        String updated = content.replaceAll(
            "applicationId = \"[^\"]+\"",
            "applicationId = \"" + applicationId + "\""
        );
        Files.writeString(gradleFile.toPath(), updated);
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
