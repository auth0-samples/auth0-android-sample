// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

tasks.register("configureAuth0") {
    group = "quickstart"
    description = "Configure Auth0 credentials in strings.xml and app/build.gradle.kts"

    doLast {
        val domain = findProperty("domain") as String?
            ?: error("Missing required property: -Pdomain=<your-auth0-domain>")
        val clientId = findProperty("clientId") as String?
            ?: error("Missing required property: -PclientId=<your-client-id>")
        val scheme = (findProperty("scheme") as String?) ?: "https"
        val applicationId = (findProperty("applicationId") as String?) ?: "com.auth0.samples"

        if (!applicationId.matches(Regex("[a-zA-Z][a-zA-Z0-9]*(\\.[a-zA-Z][a-zA-Z0-9]*)+"))) {
            error("applicationId \"$applicationId\" is not a valid Java package name (expected format: com.example.myapp)")
        }

        val stringsFile = file("app/src/main/res/values/strings.xml")
        if (!stringsFile.exists()) {
            error("${stringsFile.path} not found — run this task from the project root directory")
        }

        val gradleFile = file("app/build.gradle.kts")
        if (!gradleFile.exists()) {
            error("${gradleFile.path} not found — run this task from the project root directory")
        }

        // Update strings.xml via XML DOM
        val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        val doc = factory.newDocumentBuilder().parse(stringsFile)

        fun setStringValue(name: String, value: String) {
            val nodes = doc.getElementsByTagName("string")
            for (i in 0 until nodes.length) {
                val el = nodes.item(i) as org.w3c.dom.Element
                if (el.getAttribute("name") == name) {
                    el.textContent = value
                    return
                }
            }
            logger.warn("Warning: <string name=\"$name\"> not found in strings.xml")
        }

        fun stripWhitespace(node: org.w3c.dom.Node) {
            val toRemove = mutableListOf<org.w3c.dom.Node>()
            val children = node.childNodes
            for (i in 0 until children.length) {
                val child = children.item(i)
                if (child.nodeType == org.w3c.dom.Node.TEXT_NODE && child.textContent.isBlank()) {
                    toRemove.add(child)
                } else {
                    stripWhitespace(child)
                }
            }
            toRemove.forEach { node.removeChild(it) }
        }

        setStringValue("com_auth0_domain", domain)
        setStringValue("com_auth0_client_id", clientId)
        setStringValue("com_auth0_scheme", scheme)

        stripWhitespace(doc.documentElement)

        val transformer = javax.xml.transform.TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8")
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
        transformer.transform(javax.xml.transform.dom.DOMSource(doc), javax.xml.transform.stream.StreamResult(stringsFile))

        // Update applicationId in app/build.gradle.kts via regex
        val gradleContent = gradleFile.readText()
        val pattern = Regex("""applicationId = "[^"]+"""")
        val matches = pattern.findAll(gradleContent).toList()
        when {
            matches.isEmpty() -> error("applicationId not found in ${gradleFile.path}")
            matches.size > 1  -> error("Found ${matches.size} applicationId occurrences in ${gradleFile.path}; expected exactly 1")
        }
        gradleFile.writeText(gradleContent.replace(pattern, """applicationId = "$applicationId""""))

        logger.lifecycle("Auth0 SDK settings configured:")
        logger.lifecycle("  auth0Domain    = $domain")
        logger.lifecycle("  auth0ClientId  = $clientId")
        logger.lifecycle("  auth0Scheme    = $scheme")
        logger.lifecycle("  applicationId  = $applicationId")
    }
}