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
