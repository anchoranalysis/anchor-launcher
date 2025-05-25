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

package org.anchoranalysis.launcher;

import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.executor.selectparam.SelectParamFactory;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.options.process.AddInputOptions;
import org.anchoranalysis.launcher.options.process.AddOutputOptions;
import org.anchoranalysis.launcher.resources.Resources;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

/**
 * A command-line interface for executing experiments
 *
 * @author Owen Feehan
 */
class LauncherConfigCommandLine extends LauncherConfig {

    // START: Resource PATHs
    private static final String RESOURCE_VERSION_FOOTER =
            "org/anchoranalysis/launcher/versionFooterDisplayMessage.txt";
    private static final String RESOURCE_MAVEN_PROPERTIES =
            "META-INF/maven/org.anchoranalysis.anchor/anchor-launcher/pom.properties";
    private static final String RESOURCE_USAGE_HEADER =
            "org/anchoranalysis/launcher/usageHeaderDisplayMessage.txt";
    private static final String RESOURCE_USAGE_FOOTER =
            "org/anchoranalysis/launcher/usageFooterDisplayMessage.txt";
    private static final String RESOURCE_TASKS_FOOTER =
            "org/anchoranalysis/launcher/tasksFooterDisplayMessage.txt";

    // END: Resource PATHs

    /**
     * The default first-argument passed as an experiment, if none is specified on the command line.
     */
    private static final String DEFAULT_FIRST_ARGUMENT = "experimentFile.xml";

    /** The name of the command as printed in the help. */
    private static final String COMMAND_NAME = "anchor";

    /** A path relative to the current JAR where a properties file can be found */
    private static final String PATH_RELATIVE_PROPERTIES = "anchor.properties";

    /** a string is printed in the description if the default-experiment is used. */
    private static final String BEHAVIOUR_MESSAGE_FOR_DEFAULT_EXPERIMENT =
            "Searching recursively for image files. CTRL+C cancels";

    /** Adds additional options unique to this implementation */
    @Override
    public void addAdditionalOptions(Options options) {
        CommandLineOptions.addAdditionalOptions(options);
    }

    @Override
    public Resources resources() {
        return new Resources(
                getClass().getClassLoader(),
                RESOURCE_VERSION_FOOTER,
                RESOURCE_MAVEN_PROPERTIES,
                RESOURCE_USAGE_HEADER,
                RESOURCE_USAGE_FOOTER,
                Optional.of(RESOURCE_TASKS_FOOTER));
    }

    @Override
    public ExecutionArguments createArguments(CommandLine line)
            throws ExperimentExecutionException {
        ExecutionArguments arguments = new ExecutionArguments();

        CommandLineExtracter extract = new CommandLineExtracter(line);
        extract.ifPresentSingle(
                CommandLineOptions.SHORT_OPTION_DEBUG, arguments::activateDebugMode);

        extract.ifPresentSingle(
                CommandLineOptions.SHORT_OPTION_TASK_NUMBER_PROCESSORS,
                arguments.task()::assignMaxNumberProcessors);

        extract.ifPresentSingle(
                CommandLineOptions.SHORT_OPTION_TASK_SIZE, arguments.task()::assignSize);

        extract.ifPresentSingle(
                CommandLineOptions.SHORT_OPTION_GROUP, arguments.task()::assignGroup);

        AddInputOptions.addFrom(extract, arguments.input());
        AddOutputOptions.addFrom(extract, arguments.output());

        return arguments;
    }

    @Override
    protected Class<?> classInCurrentJar() {
        return LauncherConfigCommandLine.class;
    }

    @Override
    public boolean newlinesBeforeError() {
        return false;
    }

    @Override
    public HelpConfig help() {
        return new HelpConfig(COMMAND_NAME, DEFAULT_FIRST_ARGUMENT);
    }

    @Override
    protected String pathRelativeProperties() {
        return PATH_RELATIVE_PROPERTIES;
    }

    @Override
    public void customizeExperimentExecutor(ExperimentExecutor executor, CommandLine line)
            throws ExperimentExecutionException {
        executor.setInput(SelectParamFactory.inputSelectParam(line));
        executor.setOutput(SelectParamFactory.outputSelectParam(line));
        executor.setTask(
                SelectParamFactory.pathOrTaskNameOrDefault(
                        line, CommandLineOptions.SHORT_OPTION_TASK, executor.taskDirectory()));
        executor.setDefaultBehaviourString(Optional.of(BEHAVIOUR_MESSAGE_FOR_DEFAULT_EXPERIMENT));

        maybeShowInDesktop(executor, line);
    }

    private static void maybeShowInDesktop(ExperimentExecutor executor, CommandLine line) {
        boolean showInDesktop =
                !line.hasOption(CommandLineOptions.SHORT_OPTION_OUTPUT_CONSOLE_ONLY);
        executor.setOpenInDesktop(showInDesktop);
    }
}
