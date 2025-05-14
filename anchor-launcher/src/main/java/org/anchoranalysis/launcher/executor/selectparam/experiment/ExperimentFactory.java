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
