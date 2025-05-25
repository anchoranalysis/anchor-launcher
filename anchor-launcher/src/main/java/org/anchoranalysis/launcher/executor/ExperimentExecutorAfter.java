package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.bean.xml.factory.AnchorDefaultBeanFactory;
import org.anchoranalysis.core.collection.StringSetTrie;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.experiment.io.ReplaceInputManager;
import org.anchoranalysis.experiment.io.ReplaceOutputManager;
import org.anchoranalysis.experiment.io.ReplaceTask;
import org.anchoranalysis.feature.bean.RegisterFeatureBeanFactories;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.input.bean.InputManager;
import org.anchoranalysis.io.output.bean.OutputManager;

/*
 * #%L
 * anchor-browser
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

/**
 * Executes an experiment in different ways - AFTER an experiment bean exists.
 *
 * <p>We do not print any error messages to the console, but throw any errors in the form of {@link
 * ExperimentExecutionException} which can be translated elsewhere into nice error messages
 *
 * @author Owen Feehan
 */
class ExperimentExecutorAfter {

    /** Default file extensions for input filtering. */
    private static Optional<StringSetTrie> defaultExtensions = Optional.empty();

    /**
     * Creates with needed initial state.
     *
     * @param pathConfigurationDirectory a {@link Path} where configuration files are stored.
     * @throws ExperimentExecutionException if initialization fails
     */
    public ExperimentExecutorAfter(Path pathConfigurationDirectory)
            throws ExperimentExecutionException {
        initializeIfNecessary(pathConfigurationDirectory, true, true);
    }

    /**
     * Initializes our factories if not already done.
     *
     * @param pathConfigurationDirectory a {@link Path} where configuration files are stored.
     * @param includeDefaultInstances if true, default instances are included.
     * @param includeRootPaths if true, a root bank is sought among the configurations and loaded.
     * @throws ExperimentExecutionException if initialization fails
     */
    static void initializeIfNecessary(
            Path pathConfigurationDirectory,
            boolean includeDefaultInstances,
            boolean includeRootPaths)
            throws ExperimentExecutionException {
        if (!RegisterBeanFactories.isCalledRegisterAllPackage()) {

            // We first register all bean-factories without any default instances, so we can load
            //  the default-instances from beans in a config-file
            AnchorDefaultBeanFactory defaultFactory =
                    RegisterBeanFactories.registerAllPackageBeanFactories();
            RegisterFeatureBeanFactories.registerBeanFactories();

            if (includeDefaultInstances) {
                // After loading the defaults, we add them to the factory
                defaultFactory
                        .getDefaultInstances()
                        .addFrom(
                                HelperLoadAdditionalConfig.loadDefaultInstances(
                                        pathConfigurationDirectory));
            }

            if (includeRootPaths) {
                HelperLoadAdditionalConfig.loadRootPaths(pathConfigurationDirectory);
            }

            if (!defaultExtensions.isPresent()) {
                defaultExtensions =
                        HelperLoadAdditionalConfig.loadDefaultExtensions(
                                pathConfigurationDirectory);
            }
        }
    }

    /**
     * Executes an experiment, possibly replacing the input and output manager
     *
     * @param experiment the {@link Experiment} to execute
     * @param executionArguments {@link ExecutionArguments} for the experiment
     * @param pathInput if defined, the {@link Path} to an input-manager to replace the
     *     input-manager specified in the experiment. If empty(), ignored.
     * @param pathOutput if defined, the {@link Path} to an output-manager to replace the
     *     output-manager specified in the experiment. If empty(), ignored.
     * @param pathTask if defined, the {@link Path} to a task to replace the task specified in the
     *     experiment. If empty(), ignored.
     * @throws ExperimentExecutionException if the execution ends early
     */
    public void executeExperiment(
            Experiment experiment,
            ExecutionArguments executionArguments,
            Optional<Path> pathInput,
            Optional<Path> pathOutput,
            Optional<Path> pathTask)
            throws ExperimentExecutionException {

        // If no input-filter extensions have been specified and defaults are available, they
        // are inserted in
        executionArguments
                .inputContextParameters()
                .assignInputFilterExtensionsIfMissing(() -> defaultExtensions);

        OptionalUtilities.ifPresent(pathInput, path -> replaceInputManager(experiment, path));

        OptionalUtilities.ifPresent(pathOutput, path -> replaceOutputManager(experiment, path));

        OptionalUtilities.ifPresent(pathTask, path -> replaceTask(experiment, path));

        executeExperiment(experiment, executionArguments);
    }

    /**
     * Replaces the input-manager of an experiment with an input-manager declared at pathInput
     *
     * @param experiment {@link Experiment} whose input-manager will be replaced
     * @param pathInput a {@link Path} to a BeanXML file defining the replacement input-manager
     * @throws ExperimentExecutionException if replacement fails
     */
    private void replaceInputManager(Experiment experiment, Path pathInput)
            throws ExperimentExecutionException {

        // As path could be a folder, we make sure we get a file
        InputManager<InputFromManager> inputManager = BeanReader.readInputManagerFromXML(pathInput);

        try {
            if (experiment instanceof ReplaceInputManager) {
                @SuppressWarnings("unchecked")
                ReplaceInputManager<InputFromManager> experimentCasted =
                        (ReplaceInputManager<InputFromManager>) experiment;
                experimentCasted.replaceInputManager(inputManager);
            } else {
                throw new ExperimentExecutionException(
                        String.format(
                                "To override the input of an experiment, it must implement %s.%nThe current experiment does not: %s",
                                ReplaceInputManager.class.getName(),
                                experiment.getClass().getName()));
            }

        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot override the input of an experiment %s with input-manager type %s",
                            experiment.getClass().getName(), inputManager.getClass().getName()),
                    e);
        }
    }

    /**
     * Replaces the output-manager of an experiment with an output-manager declared at pathOutput
     *
     * @param experiment {@link Experiment} whose output-manager will be replaced
     * @param pathOutput a {@link Path} to a BeanXML file defining the replacement output-manager
     * @throws ExperimentExecutionException if replacement fails
     */
    private void replaceOutputManager(Experiment experiment, Path pathOutput)
            throws ExperimentExecutionException {

        // As path could be a folder, we make sure we get a file
        OutputManager outputManager = BeanReader.readOutputManagerFromXML(pathOutput);

        try {
            if (experiment instanceof ReplaceOutputManager experimentCasted) {
                experimentCasted.replaceOutputManager(outputManager);
            } else {
                throw new ExperimentExecutionException(
                        String.format(
                                "To override the output of an experiment, it must implement %s.%nThe current experiment does not: %s",
                                ReplaceOutputManager.class.getName(),
                                experiment.getClass().getName()));
            }

        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot override the output of an experiment %s with input-manager type %s",
                            experiment.getClass().getName(), outputManager.getClass().getName()),
                    e);
        }
    }

    /**
     * Replaces the task of an experiment with a task declared at pathTask
     *
     * @param experiment {@link Experiment} whose task will be replaced
     * @param pathTask a {@link Path} to a BeanXML file defining the replacement task
     * @throws ExperimentExecutionException if replacement fails
     */
    @SuppressWarnings("unchecked")
    private void replaceTask(Experiment experiment, Path pathTask)
            throws ExperimentExecutionException {

        // As path could be a folder, we make sure we get a file
        Task<InputFromManager, Object> task = BeanReader.readTaskFromXML(pathTask);

        try {
            if (experiment instanceof ReplaceTask) {
                ReplaceTask<InputFromManager, Object> experimentCasted =
                        (ReplaceTask<InputFromManager, Object>) experiment;
                experimentCasted.replaceTask(task);
            } else {
                throw new ExperimentExecutionException(
                        String.format(
                                "To override the task of an experiment, it must implement %s.%nThe current experiment does not: %s",
                                ReplaceTask.class.getName(), experiment.getClass().getName()));
            }

        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot override the input of an experiment %s with task type %s",
                            experiment.getClass().getName(), task.getClass().getName()),
                    e);
        }
    }

    /**
     * Executes an experiment.
     *
     * @param experiment the {@link Experiment} to execute
     * @param executionArguments additional {@link ExecutionArguments} that describe the Experiment
     * @throws ExperimentExecutionException if the experiment cannot be executed
     */
    private void executeExperiment(Experiment experiment, ExecutionArguments executionArguments)
            throws ExperimentExecutionException {
        try {
            experiment.executeExperiment(executionArguments);
        } catch (ExperimentExecutionException e) {
            throw new ExperimentExecutionException("Experiment execution ended with failure", e);
        }
    }
}
