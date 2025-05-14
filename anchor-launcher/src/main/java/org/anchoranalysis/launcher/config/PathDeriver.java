package org.anchoranalysis.launcher.config;

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
