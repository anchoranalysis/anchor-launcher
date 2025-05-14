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

package org.anchoranalysis.launcher.executor.selectparam;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.experiment.ExperimentFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.InputFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.OutputFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.TaskFactory;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.apache.commons.cli.CommandLine;

/**
 * Creates an appropriate {@link SelectParam} based upon the options passed to the command-line.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SelectParamFactory {

    /**
     * Creates a {@link SelectParam} that uses the default manager.
     *
     * @return a {@link SelectParam} that uses the default path
     */
    public static SelectParam<Optional<Path>> useDefault() {
        return new UseDefaultManager();
    }

    /**
     * Creates a {@link SelectParam} for a path, task name, or default.
     *
     * @param line the {@link CommandLine} to consider if certain options have been selected or not
     * @param optionName the option name to consider
     * @param tasksDirectory the path to the "tasks" directory in the anchor configuration files
     * @return an appropriate {@link SelectParam} object
     */
    public static SelectParam<Optional<Path>> pathOrTaskNameOrDefault(
            CommandLine line, String optionName, Path tasksDirectory) {
        return ifOptionOrDefault(
                line, optionName, args -> TaskFactory.pathOrTaskName(args, tasksDirectory));
    }

    /**
     * Creates a {@link SelectParam} for input selection.
     *
     * <p>Can point to either:
     *
     * <ol>
     *   <li>a path ending in <i>.xml</i>, assumed to BeanXML for an input manager
     *   <li>a directory, set as an the inputDirectory in the input-context
     *   <li>a string with a wild-card, assumed to be a glob, set into the input-context as a glob
     *   <li>a string with a period and without any forward or backwards slashes, set into the
     *       input-context as an extension to match
     * </ol>
     *
     * @param line the {@link CommandLine} to consider if certain options have been selected or not
     * @return an appropriate {@link SelectParam} object
     */
    public static SelectParam<Optional<Path>> inputSelectParam(CommandLine line) {
        try {
            return ifOptionOrDefault(
                    line,
                    CommandLineOptions.SHORT_OPTION_INPUT,
                    InputFactory::pathOrDirectoryOrGlobOrExtension);
        } catch (InvalidPathArgumentException e) {
            throw e.toCommandLineException();
        }
    }

    /**
     * Creates a {@link SelectParam} for output selection.
     *
     * <p>Can point to either:
     *
     * <ol>
     *   <li>a path ending in <i>.xml</i>, assumed to BeanXML for an output manager
     *   <li>a directory, set as the outputDirectory in the input-context
     * </ol>
     *
     * @param line the {@link CommandLine} to consider if certain options have been selected or not
     * @return an appropriate {@link SelectParam} object
     * @throws ExperimentExecutionException if both output options are present
     */
    public static SelectParam<Optional<Path>> outputSelectParam(CommandLine line)
            throws ExperimentExecutionException {

        if (line.hasOption(CommandLineOptions.SHORT_OPTION_OUTPUT)
                && line.hasOption(
                        CommandLineOptions.SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER)) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Only one of command-line options -%s and -%s may be present, but both are!",
                            CommandLineOptions.SHORT_OPTION_OUTPUT,
                            CommandLineOptions.SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER));
        }

        // First try the option that omits the experiment-identifier
        Optional<SelectParam<Optional<Path>>> selected =
                ifOption(
                        line,
                        CommandLineOptions.SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER,
                        arg -> OutputFactory.pathOrDirectory(arg, true));

        if (selected.isPresent()) {
            return selected.get();
        }

        // Then otherwise try the usual output option
        return ifOptionOrDefault(
                line,
                CommandLineOptions.SHORT_OPTION_OUTPUT,
                arg -> OutputFactory.pathOrDirectory(arg, false));
    }

    /**
     * Creates a {@link SelectParam} for experiment selection.
     *
     * <p>Can point to either:
     *
     * <ol>
     *   <li>a path ending in <i>.xml</i>, assumed to BeanXML for an experiment
     *   <li>nothing, then default experiment is used
     * </ol>
     *
     * @param line the {@link CommandLine} to consider if certain options have been selected or not
     * @param defaultExperiment the path to the default experiment
     * @return an appropriate {@link SelectParam} object
     * @throws ExperimentExecutionException if there's an error creating the experiment select param
     */
    public static SelectParam<Path> experimentSelectParam(CommandLine line, Path defaultExperiment)
            throws ExperimentExecutionException {
        return ExperimentFactory.defaultExperimentOrCustom(line, defaultExperiment);
    }

    /**
     * If {@code optionName} is present, then apply {@code func}, otherwise use the default-manager.
     *
     * @param <E> the type of exception that may be thrown
     * @param line the {@link CommandLine} to check for options
     * @param optionName the name of the option to check
     * @param func the function to apply if the option is present
     * @return a {@link SelectParam} based on the option presence
     * @throws E if an exception occurs during function application
     */
    private static <E extends Exception> SelectParam<Optional<Path>> ifOptionOrDefault(
            CommandLine line,
            String optionName,
            CheckedFunction<String[], SelectParam<Optional<Path>>, E> func)
            throws E {
        return ifOption(line, optionName, func).orElseGet(UseDefaultManager::new);
    }

    /**
     * If {@code optionName} is present, then apply {@code func}, otherwise return {@link
     * Optional#empty}.
     *
     * @param <E> the type of exception that may be thrown
     * @param line the {@link CommandLine} to check for options
     * @param optionName the name of the option to check
     * @param func the function to apply if the option is present
     * @return an {@link Optional} containing the result of {@code func} if the option is present,
     *     otherwise empty
     * @throws E if an exception occurs during function application
     */
    private static <E extends Exception> Optional<SelectParam<Optional<Path>>> ifOption(
            CommandLine line,
            String optionName,
            CheckedFunction<String[], SelectParam<Optional<Path>>, E> func)
            throws E {
        if (line.hasOption(optionName)) {
            return Optional.of(func.apply(line.getOptionValues(optionName)));
        } else {
            return Optional.empty();
        }
    }
}
