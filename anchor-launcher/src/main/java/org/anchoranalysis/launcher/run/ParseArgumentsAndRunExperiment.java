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
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.executor.ExperimentExecutor;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Parses command-line arguments and runs an experiment.
 *
 * <p>The parser includes:
 *
 * <ol>
 *   <li>a help option, that prints help information
 *   <li>a version option, that prints version information
 *   <li>a logError option, that records certain errors (parsing errors) in a log-file with more
 *       detail
 *   <li>and take an argument of a single path that represents an experiment BeanXML file (or path
 *       to a directory containing experiment BeanXML)
 * </ol>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class ParseArgumentsAndRunExperiment {

    // START REQUIRED ARGUMENTS
    /** For reporting messages on what goes wrong */
    private final Logger logger;

    // END REQUIRED ARGUMENTS

    /**
     * Parses the arguments to a command-line experiment and runs an experiment.
     *
     * @param arguments arguments from command-line
     * @param config a configuration for the command-line executor.
     */
    public void parseAndRun(String[] arguments, LauncherConfig config) {

        Options options = createOptions(config);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, arguments);

            MessagePrinter messagePrinter = new MessagePrinter(config.resources());

            if (messagePrinter.maybePrintHelp(line, options, config.help())) {
                return;
            }

            if (messagePrinter.maybePrintVersion(line)) {
                return;
            }

            if (line.getArgs().length > 1) {
                ErrorPrinter.printTooManyArguments();
                return;
            }

            processExperimentShowErrors(line, config, messagePrinter);

        } catch (ParseException e) {
            // Something went wrong
            logger.messageLogger()
                    .logFormatted(
                            "Parsing of command-line arguments failed.  Reason: %s%n",
                            e.getMessage());
        } catch (IOException e) {
            logger.errorReporter().recordError(ParseArgumentsAndRunExperiment.class, e);
            logger.messageLogger()
                    .logFormatted("An I/O error occurred.  Reason: %s%n", e.getMessage());
        } catch (AnchorFriendlyRuntimeException e) {
            logger.messageLogger().logFormatted(e.friendlyMessageHierarchy());
        }
    }

    /**
     * Calls processExperiment() but displays any error messages in a user-friendly way on
     * System.err
     *
     * @param line
     */
    private void processExperimentShowErrors(
            CommandLine line, LauncherConfig config, MessagePrinter messagePrinter) {

        try {
            processExperiment(line, logger, config, messagePrinter);

        } catch (ExperimentExecutionException e) {

            if (config.newlinesBeforeError()) {
                logger.messageLogger().logFormatted("%n");
            }

            // Let's print a simple (non-word wrapped message) to the console
            logger.messageLogger().log(e.friendlyMessageHierarchy());

            // Unless it's enabled, we record a more detailed error log to the filesystem
            if (line.hasOption(CommandLineOptions.SHORT_OPTION_LOG_ERROR)) {
                Path errorLogPath =
                        Paths.get(line.getOptionValue(CommandLineOptions.SHORT_OPTION_LOG_ERROR));
                logger.messageLogger()
                        .logFormatted("Logging error in \"%s\"%n", errorLogPath.toAbsolutePath());
                ErrorPrinter.printErrorLog(e, errorLogPath);
            }
        }
    }

    /**
     * Some operation is executed on an an experiment after considering the help/version options
     *
     * @param line remaining-command line arguments after options are removed
     * @param logger logger
     * @throws ExperimentExecutionException if processing ends early
     */
    private void processExperiment(
            CommandLine line, Logger logger, LauncherConfig config, MessagePrinter messagePrinter)
            throws ExperimentExecutionException {

        ExperimentExecutor executor = config.createExperimentExecutor(line);

        if (messagePrinter.maybeShowTasks(line, executor.taskDirectory())) {
            // Exit early if we've shown the available tasks.
            return;
        }

        config.customizeExperimentExecutor(executor, line);

        executor.executeExperiment(
                config.createArguments(line),
                line.hasOption(CommandLineOptions.SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS),
                logger);
    }

    /**
     * Create options for the command-line client, returning default options always available for
     * this class
     *
     * @return the options that can be used
     */
    private Options createOptions(LauncherConfig config) {
        Options options = new Options();
        CommandLineOptions.addBasicOptions(options);
        config.addAdditionalOptions(options);
        return options;
    }
}
