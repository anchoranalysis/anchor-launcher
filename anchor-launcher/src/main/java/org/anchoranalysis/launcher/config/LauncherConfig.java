package org.anchoranalysis.launcher.config;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import java.nio.file.Path;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.executor.ExperimentExecutorFactory;
import org.anchoranalysis.launcher.resources.Resources;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * Specifies a configuration of the launcher for a particular application.
 *
 * <p>As the launcher class is used for starting both the command-line tool and the Anchor GUI, this
 * provides the necessary application-specific configuration for each.
 *
 * @author Owen Feehan
 */
public abstract class LauncherConfig {

    /**
     * @return the {@link Resources} configuration used by the launcher
     */
    public abstract Resources resources();

    /**
     * @return the {@link HelpConfig} for displaying help messages
     */
    public abstract HelpConfig help();

    /**
     * Determines if extra newlines should be inserted before error messages.
     *
     * <p>This is useful for the GUI client in Windows due to WinRun4j running as a Windows app, and
     * not as a shell app. This changes how output is displayed.
     *
     * @return true if newlines should be inserted before error messages, false otherwise
     */
    public abstract boolean newlinesBeforeError();

    /**
     * Creates execution arguments from the command line.
     *
     * @param line the {@link CommandLine} containing parsed command-line arguments
     * @return the {@link ExecutionArguments} created from the command line
     * @throws ExperimentExecutionException if there's an error creating the arguments
     */
    public abstract ExecutionArguments createArguments(CommandLine line)
            throws ExperimentExecutionException;

    /**
     * Adds additional options to the command-line parser.
     *
     * @param options the {@link Options} object to which additional options should be added
     */
    public abstract void addAdditionalOptions(Options options);

    /**
     * Creates an experiment executor from the command line.
     *
     * @param line the {@link CommandLine} containing parsed command-line arguments
     * @return the created {@link ExperimentExecutor}
     * @throws ExperimentExecutionException if there's an error creating the executor
     */
    public ExperimentExecutor createExperimentExecutor(CommandLine line)
            throws ExperimentExecutionException {

        Path pathCurrentJARDir = PathCurrentJarHelper.pathCurrentJAR(classInCurrentJar());

        Path pathDefaultExperiment = pathDefaultExperiment(pathCurrentJARDir);

        // Assumes config-dir is always the directory of defaultExperiment.xml
        return ExperimentExecutorFactory.create(
                line, pathDefaultExperiment, pathDefaultExperiment.getParent());
    }

    /**
     * Customizes the experiment executor with additional configuration.
     *
     * @param executor the {@link ExperimentExecutor} to be customized
     * @param line the {@link CommandLine} containing parsed command-line arguments
     * @throws ExperimentExecutionException if there's an error during customization
     */
    public abstract void customizeExperimentExecutor(ExperimentExecutor executor, CommandLine line)
            throws ExperimentExecutionException;

    /**
     * Provides the path to a property file that defines a relative-path to the default experiment
     * in bean XML.
     *
     * @return the path to the properties file as a {@link String}
     */
    protected abstract String pathRelativeProperties();

    /**
     * Provides a class which is used to determine the base location for {@link
     * #pathRelativeProperties()}.
     *
     * @return the {@link Class} in the current JAR
     */
    protected abstract Class<?> classInCurrentJar();

    /**
     * Determines the path to the default experiment.
     *
     * @param pathCurrentJARDir the {@link Path} to the current JAR directory
     * @return a {@link Path} to the default experiment
     * @throws ExperimentExecutionException if there's an error determining the path
     */
    private Path pathDefaultExperiment(Path pathCurrentJARDir) throws ExperimentExecutionException {
        return PathDeriver.pathDefaultExperiment(pathCurrentJARDir, pathRelativeProperties());
    }
}
