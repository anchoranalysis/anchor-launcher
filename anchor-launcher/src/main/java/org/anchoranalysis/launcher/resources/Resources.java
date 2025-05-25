package org.anchoranalysis.launcher.resources;

import java.io.IOException;
import java.util.Optional;
import lombok.AllArgsConstructor;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

/**
 * Accesses resource-files associated with this application.
 *
 * <p>When specifying paths to resources, one should avoid a leading / on the resource path as it
 * uses a {@link ClassLoader} to load resources, which is different behavior to {@code
 * getClass().getResourceAsStream()}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class Resources {

    /** Which class-loader to use for loading resources. */
    private ClassLoader classLoader;

    /** Path to the footer-message that accompanies a version message. */
    private String pathVersionFooter;

    /** Path to the maven-properties file (from which a version can be extracted). */
    private String pathMavenProperties;

    /** Path to the header-message that describes usage. */
    private String pathUsageHeader;

    /** Path to the footer-message that describes usage. */
    private String pathUsageFooter;

    /** Path to the footer-message used after "show tasks" */
    private Optional<String> pathTasksFooter;

    /**
     * Header-message that describes usage.
     *
     * @return the message
     */
    public String usageHeader() {
        return readTextFile(pathUsageHeader);
    }

    /**
     * Footer-message that describes usage.
     *
     * @return the message
     */
    public String usageFooter() {
        return readTextFile(pathUsageFooter);
    }

    /**
     * Footer-message used after "show tasks".
     *
     * @return the message if it exists, or else an empty string.
     */
    public String tasksFooter() {
        return pathTasksFooter.map(this::readTextFile).orElse("");
    }

    /**
     * Footer-message that accompanies a version message, if it exists.
     *
     * @return the message
     */
    public String versionFooter() {
        return readTextFile(pathVersionFooter);
    }

    /**
     * Gets the current version of the software by reading a properties-file provided by the Maven
     * build
     *
     * <p>NOTE that this pom.proper
     *
     * @return string describing the current version number of anchor-launcher
     * @throws IOException if the properties file cannot be read, or is missing the appropriate
     *     version key
     */
    public String versionFromMavenProperties() throws IOException {
        return ResourceReader.keyFromMavenProperties(
                "version", "<unknown>", pathMavenProperties, classLoader);
    }

    private String readTextFile(String path) {
        try {
            return ResourceReader.readStringFromResource(path, classLoader);
        } catch (IOException e) {
            return "Error: Cannot read a string from the file: " + path;
        }
    }
}
