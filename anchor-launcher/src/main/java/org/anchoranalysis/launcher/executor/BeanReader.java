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

package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.xml.BeanXMLLoader;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.output.bean.OutputManager;

/**
 * Reads beans from BeanXML stored on the file-system.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BeanReader {

    public static Experiment readExperimentFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "experiment", true);
    }

    public static InputManager<InputFromManager> readInputManagerFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "bean", false);
    }

    public static OutputManager readOutputManagerFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "bean", false);
    }

    public static Task<InputFromManager, Object> readTaskFromXML(Path configPath)
            throws ExperimentExecutionException {
        return readBeanFromXML(configPath, "bean", false);
    }

    /**
     * Read bean from xml
     *
     * @param configPath the path where the xml file exists
     * @param xmlPath the xpath inside the xmlpath specifying the root-element
     * @param associateXml if true, the xml is associated with the object (see BeanXmlLoader). if
     *     false, it is not.
     * @return an object created from the read BeanXML
     * @throws ExperimentExecutionException
     */
    private static <T> T readBeanFromXML(Path configPath, String xmlPath, boolean associateXml)
            throws ExperimentExecutionException {

        // To avoid any .. or . in error reporting
        configPath = configPath.normalize();

        if (!configPath.toFile().exists()) {
            throw new ExperimentExecutionException(
                    String.format("Error: a file does not exist at \"%s\"", configPath));
        }

        try {
            if (associateXml) {
                return BeanXMLLoader.loadBeanAssociatedXml(configPath, xmlPath);
            } else {
                return BeanXMLLoader.loadBean(configPath, xmlPath);
            }

        } catch (BeanXMLException e) {
            String errorMsg =
                    String.format(
                            "An error occurred reading the experiment bean XML at \"%s\".%nPlease ensure this is validly-formatted BeanXML for an experiment.",
                            configPath);
            throw new ExperimentExecutionException(errorMsg, e);
        }
    }
}
