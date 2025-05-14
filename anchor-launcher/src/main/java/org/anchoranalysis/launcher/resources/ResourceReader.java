/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
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
