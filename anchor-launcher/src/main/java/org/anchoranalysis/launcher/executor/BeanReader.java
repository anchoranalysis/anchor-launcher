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
