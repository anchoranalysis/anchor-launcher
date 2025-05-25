package org.anchoranalysis.launcher.config;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/** Utility class for deriving paths related to the default experiment configuration. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PathDeriver {

    /** A property that indicates a relative path to a default properties file. */
    private static final String PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT =
            "default.config.path.relative";

    /**
     * Derives the path to the default experiment configuration.
     *
     * @param pathCurrentJARDir the {@link Path} to the directory containing the current JAR
     * @param pathRelativeProperties the relative path to the properties file
     * @return a {@link Path} to the default experiment configuration
     * @throws ExperimentExecutionException if an error occurs while deriving the path
     */
    public static Path pathDefaultExperiment(Path pathCurrentJARDir, String pathRelativeProperties)
            throws ExperimentExecutionException {

        String relativePathDefaultExperiment =
                relativePathDefaultExperiment(
                        propertyPath(pathCurrentJARDir, pathRelativeProperties));

        return pathCurrentJARDir.resolve(relativePathDefaultExperiment);
    }

    /**
     * Extracts the relative path to the default experiment from the properties file.
     *
     * @param propertyPath the {@link Path} to the properties file
     * @return the relative path to the default experiment as a {@link String}
     * @throws ExperimentExecutionException if an error occurs while reading the properties file
     */
    private static String relativePathDefaultExperiment(Path propertyPath)
            throws ExperimentExecutionException {

        Properties props = new Properties();

        try (FileInputStream stream = new FileInputStream(propertyPath.toFile())) {
            props.load(stream);
        } catch (IOException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "An error occurred loading properties from the properties file at %s)",
                            propertyPath),
                    e);
        }

        if (!props.containsKey(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT)) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Properties file is missing key: %s",
                            PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT));
        }

        return props.getProperty(PROPERTY_PATH_RELATIVE_DEFAULT_EXPERIMENT);
    }

    /**
     * Resolves the path to the properties file.
     *
     * @param currentJARDir the {@link Path} to the directory containing the current JAR
     * @param pathRelativeProperties the relative path to the properties file
     * @return the resolved {@link Path} to the properties file
     * @throws ExperimentExecutionException if the properties file does not exist
     */
    private static Path propertyPath(Path currentJARDir, String pathRelativeProperties)
            throws ExperimentExecutionException {

        Path pathProperties = currentJARDir.resolve(pathRelativeProperties);

        if (!pathProperties.toFile().exists()) {
            throw new ExperimentExecutionException(
                    String.format("Cannot find properties file at: %s", pathProperties));
        }

        return pathProperties;
    }
}
