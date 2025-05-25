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

package org.anchoranalysis.launcher.executor.selectparam.experiment;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.apache.commons.cli.CommandLine;

/**
 * {@code SelectParam<Path>} factory for experiments.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExperimentFactory {

    /**
     * Chooses either a path to the default-experiment or a path to a custom experiment.
     *
     * @param line the {@link CommandLine} containing the command-line arguments
     * @param defaultExperiment path to the default experiment
     * @return a {@link SelectParam} for a {@link Path} for the chosen experiment
     * @throws ExperimentExecutionException if there's an error in selecting the experiment
     */
    public static SelectParam<Path> defaultExperimentOrCustom(
            CommandLine line, Path defaultExperiment) throws ExperimentExecutionException {

        // It should only be possible to have 0 or 1 args, due to prior check
        if (line.getArgs().length == 1) {
            return new UseExperimentPassedAsPath(extractPath(line));

        } else {
            // We check to see if a defaultExperimentPath is passed and use this instead
            //
            // This is a useful workaround to allow a helper application (e.g. WinRunJ) to always
            // pass a defaultExperimentPath into the application
            //  on the command-line, rather than through a properties file or some other method
            // (which might not have the correct
            //  path relative to the working directory.
            //
            // The default path is simply ignored if the user specifies their own explicit path
            return new UseDefaultExperiment(defaultExperiment);
        }
    }

    /**
     * Extracts a {@link Path} from the command-line arguments.
     *
     * @param line the {@link CommandLine} containing the command-line arguments
     * @return the extracted {@link Path}
     * @throws ExperimentExecutionException if the path is invalid or contains wildcards
     */
    private static Path extractPath(CommandLine line) throws ExperimentExecutionException {
        String str = line.getArgs()[0];

        if (str.contains("*")) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Error: Cannot accept a wildcard in path to experiment BeanXML: %s",
                            str));
        }
        try {
            return Paths.get(str);
        } catch (InvalidPathException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Error: The argument \"%s\" should be a path to experiment BeanXML, but is invalid.",
                            str));
        }
    }
}
