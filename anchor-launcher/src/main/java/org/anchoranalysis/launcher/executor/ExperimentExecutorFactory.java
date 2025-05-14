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
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.apache.commons.cli.CommandLine;

/**
 * Creates an {@link ExperimentExecutor}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExperimentExecutorFactory {

    /**
     * Creates an experiment-executor from a command line that EITHER: uses a default-experiment OR
     * accepts a path passed as the first command-line argument.
     *
     * @param line the {@link CommandLine} containing the command-line arguments
     * @param defaultExperiment {@link Path} to the default-experiment
     * @param configDirectory {@link Path} to the configuration directory
     * @return a new {@link ExperimentExecutor} instance
     * @throws ExperimentExecutionException if there's an error creating the executor
     */
    public static ExperimentExecutor create(
            CommandLine line, Path defaultExperiment, Path configDirectory)
            throws ExperimentExecutionException {
        return new ExperimentExecutor(
                SelectParamFactory.experimentSelectParam(line, defaultExperiment), configDirectory);
    }
}
