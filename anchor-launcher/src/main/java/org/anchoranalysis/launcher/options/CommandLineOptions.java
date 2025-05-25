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
package org.anchoranalysis.launcher.options;

import static org.anchoranalysis.launcher.options.CustomArgumentOptions.optionalStringArgument;
import static org.anchoranalysis.launcher.options.CustomArgumentOptions.requiredNumberArgument;
import static org.anchoranalysis.launcher.options.CustomArgumentOptions.requiredStringArgument;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.Options;

/**
 * All command-line options used by the launcher.
 *
 * @see <a href="https://www.anchoranalysis.org/user_guide_command_line.html">User Guide - Command
 *     Line</a>
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandLineOptions {

    // START: SHORT input options
    /** Changes inputs. */
    public static final String SHORT_OPTION_INPUT = "i";

    /** Additionally copies any files in the input directory, unused as inputs. */
    public static final String SHORT_OPTION_INPUT_COPY_NON_INPUTS = "ic";

    /** Subsets the identifier. */
    public static final String SHORT_OPTION_INPUT_SUBSET_IDENTIFIER = "ii";

    /**
     * Derives the unique identifier from the **entire relative filename or path** (excluding file
     * extension).
     */
    public static final String SHORT_OPTION_INPUT_RELATIVE_PATH = "ip";

    /** Shuffles the order of the inputs. */
    public static final String SHORT_OPTION_INPUT_SHUFFLE = "is";

    /** Imposes an upper limit on the number of inputs. */
    public static final String SHORT_OPTION_INPUT_LIMIT = "il";

    /** Randomly samples a number of inputs. */
    public static final String SHORT_OPTION_INPUT_RANDOM_SAMPLE = "ir";

    // END: SHORT input options

    // START: SHORT task options
    /** Changes task. */
    public static final String SHORT_OPTION_TASK = "t";

    /** Suggests a maximum number of processors to use for the task. */
    public static final String SHORT_OPTION_TASK_NUMBER_PROCESSORS = "tp";

    /** Suggests dimensions or a scaling factor for certain tasks. */
    public static final String SHORT_OPTION_TASK_SIZE = "ps";

    /** Activates grouping from a subset of the identifier's elements. */
    public static final String SHORT_OPTION_GROUP = "pg";

    // END: SHORT task options

    // START: SHORT output options
    /** Changes output manager. */
    public static final String SHORT_OPTION_OUTPUT = "o";

    /** Enables specific output(s). Multiple outputs are comma-separated. */
    public static final String SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL = "oe";

    /** Disables specific output(s). Multiple outputs are comma-separated. */
    public static final String SHORT_OPTION_OUTPUT_DISABLE_ADDITIONAL = "od";

    /** Enables all outputs. */
    public static final String SHORT_OPTION_OUTPUT_ENABLE_ALL = "oa";

    /**
     * Output console <i>only</i>. By default, the output directory is opened in the desktop after
     * completion. This disables that behaviour.
     */
    public static final String SHORT_OPTION_OUTPUT_CONSOLE_ONLY = "oc";

    /** Suggests an output image file format: e.g -of jpg or -of ome.xml. */
    public static final String SHORT_OPTION_OUTPUT_IMAGE_FILE_FORMAT = "of";

    /**
     * Outputs with an incrementing number instead of the input identifier.
     *
     * <p>(useful for creating sequences of images)
     */
    public static final String SHORT_OPTION_OUTPUT_INCREMENTING_NUMBER = "on";

    /**
     * Suppressed the subdirectory structure when outputting file identifiers.
     *
     * <p>This replaces subdirectories in the outputted filenames with an underscore.
     */
    public static final String SHORT_OPTION_OUTPUT_SUPPRESS_DIRECTORIES = "os";

    /**
     * Ignores the experiment identifier, when forming an output-path.
     *
     * <p>This writes directorly to the output-directory, but not if it already exists.
     */
    public static final String SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER = "oo";

    // END: SHORT output options

    // START: SHORT debug options
    /** Enables debug-mode: runs only the first available input [whose name contains string]. */
    public static final String SHORT_OPTION_DEBUG = "d";

    /** Logs initial BeanXML errors in greater detail to a file-path. */
    public static final String SHORT_OPTION_LOG_ERROR = "l";

    /** Shows additional argument information, otherwise executes as normal. */
    public static final String SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS = "sa";

    /** Prints the names of predefined tasks that can be easily used with -t. */
    public static final String SHORT_OPTION_SHOW_TASKS = "st";

    // END: SHORT debug options

    // START: SHORT application information options
    /** Displays help message with all command-line options. */
    public static final String SHORT_OPTION_HELP = "h";

    /** Displays version and authorship information. */
    public static final String SHORT_OPTION_VERSION = "v";

    // END: SHORT application information options

    // START: All LONG options
    private static final String LONG_OPTION_HELP = "help";
    private static final String LONG_OPTION_VERSION = "version";
    private static final String LONG_OPTION_LOG_ERROR = "logError";
    private static final String LONG_OPTION_SHOW_EXPERIMENT_ARGUMENTS = "showArguments";
    private static final String LONG_OPTION_SHOW_TASKS = "showTasks";

    private static final String LONG_OPTION_DEBUG = "debug";
    private static final String LONG_OPTION_INPUT = "input";

    /** Additionally copies any files in the input directory unused as inputs. */
    private static final String LONG_OPTION_INPUT_COPY_NON_INPUTS = "inputCopy";

    private static final String LONG_OPTION_INPUT_RELATIVE = "inputRelative";

    /** Subsets the identifier. */
    private static final String LONG_OPTION_INPUT_SUBSET_IDENTIFIER = "inputSubsetIdentifier";

    /** Shuffles the order of the inputs. */
    private static final String LONG_OPTION_INPUT_SHUFFLE = "inputShuffle";

    /** Imposes an upper limit on the number of inputs. */
    private static final String LONG_OPTION_INPUT_LIMIT = "inputLimit";

    /** Randomly samples a number of inputs. */
    public static final String LONG_OPTION_INPUT_RANDOM_SAMPLE = "inputRandom";

    /** Changes output manager. */
    public static final String LONG_OPTION_OUTPUT = "output";

    /**
     * Output console <i>only</i>. By default, the output directory is opened in the desktop after
     * completion. This disables that behavior.
     */
    public static final String LONG_OPTION_OUTPUT_CONSOLE_ONLY = "outputConsoleOnly";

    private static final String LONG_OPTION_OUTPUT_ENABLE_ADDITIONAL = "outputEnable";
    private static final String LONG_OPTION_OUTPUT_DISABLE_ADDITIONAL = "outputDisable";
    private static final String LONG_OPTION_OUTPUT_ENABLE_ALL = "outputEnableAll";
    private static final String LONG_OPTION_OUTPUT_IMAGE_FILE_FORMAT = "outputFileFormat";
    private static final String LONG_OPTION_OUTPUT_INCREMENTING_NUMBER = "outputIncrementingNumber";
    private static final String LONG_OPTION_OUTPUT_SUPPRESS_DIRECTORIES =
            "outputSuppressDirectories";
    private static final String LONG_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER =
            "outputOmitExperimentIdentifier";

    private static final String LONG_OPTION_TASK = "task";
    private static final String LONG_OPTION_TASK_NUMBER_PROCESSORS = "taskNumberProcessors";
    private static final String LONG_OPTION_TASK_SIZE = "paramSize";

    /** Activates grouping from a subset of the identifier's elements. */
    public static final String LONG_OPTION_GROUP = "paramGroup";

    // END: All LONG options

    /**
     * Adds basic options to the {@link Options} object.
     *
     * @param options the {@link Options} object to add the basic options to
     */
    public static void addBasicOptions(Options options) {
        options.addOption(
                SHORT_OPTION_HELP, LONG_OPTION_HELP, false, "print this message and exit");

        options.addOption(
                SHORT_OPTION_VERSION,
                LONG_OPTION_VERSION,
                false,
                "print version information and exit");

        // This logs the errors in greater detail
        options.addOption(
                SHORT_OPTION_LOG_ERROR,
                LONG_OPTION_LOG_ERROR,
                true,
                "log BeanXML parsing errors to file");

        options.addOption(
                SHORT_OPTION_SHOW_EXPERIMENT_ARGUMENTS,
                LONG_OPTION_SHOW_EXPERIMENT_ARGUMENTS,
                false,
                "print experiment path arguments");

        options.addOption(
                SHORT_OPTION_SHOW_TASKS,
                LONG_OPTION_SHOW_TASKS,
                false,
                "print task-names as useful for -t <name>");
    }

    /**
     * Adds additional options to the {@link Options} object.
     *
     * @param options the {@link Options} object to add the additional options to
     */
    public static void addAdditionalOptions(Options options) {

        options.addOption(
                optionalStringArgument(
                        SHORT_OPTION_DEBUG, LONG_OPTION_DEBUG, "enables debug mode"));

        addInputOptions(options);
        addOutputOptions(options);
        addTaskOptions(options);
    }

    /**
     * Adds input-related options to the {@link Options} object.
     *
     * @param options the {@link Options} object to add the input options to
     */
    public static void addInputOptions(Options options) {

        options.addOption(
                requiredStringArgument(
                        SHORT_OPTION_INPUT,
                        LONG_OPTION_INPUT,
                        "an input-directory OR glob (e.g. small_*.jpg) OR file extension (e.g. .png) OR path to BeanXML"));

        options.addOption(
                SHORT_OPTION_INPUT_COPY_NON_INPUTS,
                LONG_OPTION_INPUT_COPY_NON_INPUTS,
                false,
                "copies any unused files (as inputs) to the output directory");

        options.addOption(
                SHORT_OPTION_INPUT_RELATIVE_PATH,
                LONG_OPTION_INPUT_RELATIVE,
                false,
                "derives identifier from relative filename or path");

        options.addOption(
                SHORT_OPTION_INPUT_SUBSET_IDENTIFIER,
                LONG_OPTION_INPUT_SUBSET_IDENTIFIER,
                true,
                "subsets the identifier e.g. 2 OR -2 OR 3:-2 OR 2: OR :2 (zero-indexed, negatives count backwards)");

        options.addOption(
                SHORT_OPTION_INPUT_SHUFFLE,
                LONG_OPTION_INPUT_SHUFFLE,
                false,
                "shuffles (randomizes) the order of the inputs");

        options.addOption(
                SHORT_OPTION_INPUT_LIMIT,
                LONG_OPTION_INPUT_LIMIT,
                true,
                "imposes upper limit on number of inputs");

        options.addOption(
                SHORT_OPTION_INPUT_RANDOM_SAMPLE,
                LONG_OPTION_INPUT_RANDOM_SAMPLE,
                true,
                "randomly samples a number/portion of inputs");
    }

    /**
     * Adds output-related options to the {@link Options} object.
     *
     * @param options the {@link Options} object to add the output options to
     */
    private static void addOutputOptions(Options options) {

        options.addOption(
                requiredStringArgument(
                        SHORT_OPTION_OUTPUT,
                        LONG_OPTION_OUTPUT,
                        "an output-directory OR path to BeanXML"));

        options.addOption(
                requiredStringArgument(
                        SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL,
                        LONG_OPTION_OUTPUT_ENABLE_ADDITIONAL,
                        "enables specific additional output(s)"));

        options.addOption(
                requiredStringArgument(
                        SHORT_OPTION_OUTPUT_DISABLE_ADDITIONAL,
                        LONG_OPTION_OUTPUT_DISABLE_ADDITIONAL,
                        "disables specific additional output(s)"));

        options.addOption(
                SHORT_OPTION_OUTPUT_ENABLE_ALL,
                LONG_OPTION_OUTPUT_ENABLE_ALL,
                false,
                "enables all outputs");

        options.addOption(
                SHORT_OPTION_OUTPUT_CONSOLE_ONLY,
                LONG_OPTION_OUTPUT_CONSOLE_ONLY,
                false,
                "disables opening the output directory in the desktop");

        options.addOption(
                optionalStringArgument(
                        SHORT_OPTION_OUTPUT_IMAGE_FILE_FORMAT,
                        LONG_OPTION_OUTPUT_IMAGE_FILE_FORMAT,
                        "suggested image-format for writing"));

        options.addOption(
                SHORT_OPTION_OUTPUT_INCREMENTING_NUMBER,
                LONG_OPTION_OUTPUT_INCREMENTING_NUMBER,
                false,
                "outputs with incrementing number sequence");

        options.addOption(
                SHORT_OPTION_OUTPUT_SUPPRESS_DIRECTORIES,
                LONG_OPTION_OUTPUT_SUPPRESS_DIRECTORIES,
                false,
                "supresses subdirectory in output file identifiers");

        options.addOption(
                SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER,
                LONG_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER,
                true,
                "like -o but omits experiment name and version in output directory");
    }

    /**
     * Adds task-related options to the {@link Options} object.
     *
     * @param options the {@link Options} object to add the task options to
     */
    private static void addTaskOptions(Options options) {
        options.addOption(
                optionalStringArgument(
                        SHORT_OPTION_TASK, LONG_OPTION_TASK, "a task-name OR path to BeanXML"));

        options.addOption(
                requiredStringArgument(
                        SHORT_OPTION_TASK_SIZE,
                        LONG_OPTION_TASK_SIZE,
                        "suggests an image size or scaling factor"));

        options.addOption(
                optionalStringArgument(
                        SHORT_OPTION_GROUP,
                        LONG_OPTION_GROUP,
                        "groups inputs by subsetting the identifier e.g. 2 OR -2 OR 3:-2 OR 2: OR :2 (zero-indexed, negatives count backwards)"));

        options.addOption(
                requiredNumberArgument(
                        SHORT_OPTION_TASK_NUMBER_PROCESSORS,
                        LONG_OPTION_TASK_NUMBER_PROCESSORS,
                        "suggests a maximum number of CPU processors"));
    }
}
