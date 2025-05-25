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
package org.anchoranalysis.launcher.run;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedRunnable;
import org.anchoranalysis.launcher.config.HelpConfig;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.resources.Resources;
import org.anchoranalysis.launcher.run.tasks.PredefinedTasks;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 * Prints messages if certain command-line options are selected.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class MessagePrinter {

    /** How many characters to assume the console is (at least) for help messages. */
    private static final int CONSOLE_WIDTH = 160;

    /** Where to print messages to. */
    private static final PrintStream PRINT_TO = System.out; // NOSONAR

    /** Resources messages can be loaded from. */
    private final Resources resources;

    /**
     * Prints a help message to the screen if the command-line option is selected.
     *
     * @param line the {@link CommandLine} containing parsed command-line arguments
     * @param options the {@link Options} object containing all possible command-line options
     * @param helpConfig the {@link HelpConfig} containing help message configuration
     * @return true if it prints the message, false otherwise
     */
    public boolean maybePrintHelp(CommandLine line, Options options, HelpConfig helpConfig) {
        return runIfOption(
                line,
                CommandLineOptions.SHORT_OPTION_HELP,
                () ->
                        printHelp(
                                options,
                                helpConfig.getCommandName(),
                                helpConfig.getFirstArgument()));
    }

    /**
     * Prints the version information if the command-line option is selected.
     *
     * @param line the {@link CommandLine} containing parsed command-line arguments
     * @return true if it prints the version information, false otherwise
     * @throws IOException if there's an error reading the version information
     */
    public boolean maybePrintVersion(CommandLine line) throws IOException {
        return runIfOption(line, CommandLineOptions.SHORT_OPTION_VERSION, this::printVersion);
    }

    /**
     * Prints the available pre-defined tasks if the command-line option is selected.
     *
     * <p>The predefined tasks come from the .xml files found in the config/tasks/ directory.
     *
     * @param line the {@link CommandLine} containing parsed command-line arguments
     * @param tasksDirectory the {@link Path} to the directory containing task definitions
     * @return true if it prints the message, false otherwise
     */
    public boolean maybeShowTasks(CommandLine line, Path tasksDirectory) {
        CommandLineExtracter extract = new CommandLineExtracter(line);
        if (extract.hasOptionWithoutArgument(CommandLineOptions.SHORT_OPTION_TASK)) {
            PredefinedTasks.printTasksToConsole(tasksDirectory, resources, PRINT_TO);
            return true;
        }

        return runIfOption(
                line,
                CommandLineOptions.SHORT_OPTION_SHOW_TASKS,
                () -> PredefinedTasks.printTasksToConsole(tasksDirectory, resources, PRINT_TO));
    }

    /**
     * Describes which version of anchor is being used, and what version number.
     *
     * @throws IOException if it's not possible to determine the version number
     */
    private void printVersion() throws IOException {
        PRINT_TO.printf(
                "anchor version %s by Owen Feehan (ETH Zurich, University of Zurich, 2016)%n",
                resources.versionFromMavenProperties());
        PRINT_TO.println();
        PRINT_TO.print(resources.versionFooter());
    }

    /**
     * Prints help message to guide usage to standard-output.
     *
     * @param options possible user-options
     * @param commandNameInHelp the name of the command to display in the help message
     * @param firstArgumentInHelp the description of the first argument to display in the help
     *     message
     */
    private void printHelp(Options options, String commandNameInHelp, String firstArgumentInHelp) {
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();

        formatter.setWidth(CONSOLE_WIDTH);

        String firstLine =
                String.format("%s [options] [%s]", commandNameInHelp, firstArgumentInHelp);
        formatter.printHelp(firstLine, resources.usageHeader(), options, resources.usageFooter());
    }

    /**
     * Runs some code if a particular command-line option is activated.
     *
     * @param <E> an exception that may be thrown by {@code runnable}
     * @param line the {@link CommandLine} containing parsed command-line arguments
     * @param option the option to check if it has been run
     * @param runnable code to run if {@code option} is activated in {@code line}
     * @return true if the code was run, false otherwise
     * @throws E if thrown by {@code runnable}
     */
    private static <E extends Exception> boolean runIfOption(
            CommandLine line, String option, CheckedRunnable<E> runnable) throws E {
        if (line.hasOption(option)) {
            runnable.run();
            return true;
        } else {
            return false;
        }
    }
}
