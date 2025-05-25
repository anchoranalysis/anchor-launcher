/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.launcher.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

/**
 * Reads different type of entities from a resource file.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ResourceReader {

    /**
     * Reads a string from a resource, or displays an error message.
     *
     * @param resourceFileName the file-name to identify the resource (in the root directory)
     * @param classLoader {@link ClassLoader} where resource is found
     * @return the content of the resource as a {@link String}, or an error message if the resource
     *     is not found
     * @throws IOException if an I/O error occurs while reading the resource
     */
    public static String readStringFromResource(String resourceFileName, ClassLoader classLoader)
            throws IOException {
        InputStream helpDisplayResource = classLoader.getResourceAsStream(resourceFileName);
        if (helpDisplayResource != null) {
            return IOUtils.toString(helpDisplayResource, StandardCharsets.UTF_8);
        } else {
            return resourceFileName + " is missing, so cannot display.";
        }
    }

    /**
     * Gets the current version of the software by reading a properties-file provided by the Maven
     * build.
     *
     * @param key the key to read from the maven properties file
     * @param fallback the string to return if the maven properties file doesn't exist
     * @param resourceFileName the name of the resource file containing the maven properties
     * @param classLoader {@link ClassLoader} where resource is found
     * @return string describing the key, or {@code fallback} if the resource file doesn't exist
     * @throws IOException if the properties file cannot be read, or is missing the appropriate
     *     version key
     */
    public static String keyFromMavenProperties(
            String key, String fallback, String resourceFileName, ClassLoader classLoader)
            throws IOException {
        Properties properties = new Properties();

        InputStream mavenPropertiesResource = classLoader.getResourceAsStream(resourceFileName);

        if (mavenPropertiesResource == null) {
            return fallback;
        }

        try (InputStream resourceStream = mavenPropertiesResource) {
            properties.load(resourceStream);
        }

        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else {
            throw new IOException("a property is missing from maven properties: " + key);
        }
    }
}
