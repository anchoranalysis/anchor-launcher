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

import java.util.Optional;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.checked.CheckedConsumer;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.apache.commons.cli.CommandLine;

/**
 * Adds methods to {@link CommandLine} for querying and extracting options, with and without
 * arguments.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class CommandLineExtracter {

    /** The command-line from which options are extracted. */
    private CommandLine line;

    /**
     * Identical to {@link CommandLine#hasOption}.
     *
     * @param option short-name of option
     * @return true iff option exists on the command-line, irrespective of if arguments are present
     *     or not
     */
    public boolean hasOption(String option) {
        return line.hasOption(option);
    }

    /**
     * Checks if an option exists, but without any argument(s) specified.
     *
     * @param option short-name of option
     * @return true iff option exists on the command-line <i>and</i> no argument is specified.
     */
    public boolean hasOptionWithoutArgument(String option) {
        if (line.hasOption(option)) {
            String argument = line.getOptionValue(option);
            return (argument == null || argument.isEmpty());
        } else {
            return false;
        }
    }

    /**
     * Executes a {@link Consumer} if an option is present - allowing that option to occur only
     * <b>once</b>.
     *
     * @param optionName the short-name of the option, which should be capable of accepting a
     *     single-argument
     * @param consumer a consumer that accepts a single argument as a string, an empty-string if no
     *     argument is provided.
     * @throws ExperimentExecutionException if thrown by {@code consumer} or if the option is
     *     specified multiple times.
     */
    public void ifPresentSingle(
            String optionName, CheckedConsumer<String, ExperimentExecutionException> consumer)
            throws ExperimentExecutionException {
        OptionalUtilities.ifPresent(single(optionName, true), consumer);
    }

    /**
     * Executes a {@link Consumer} if an option is present - allowing that option to occur
     * <b>multiple times</b>.
     *
     * @param optionName the short-name of the option, which should be capable of accepting a
     *     single-argument
     * @param consumer a consumer that accepts a single argument as a string, an empty-string if no
     *     argument is provided.
     * @throws ExperimentExecutionException if thrown by {@code consumer} or if the option is
     *     specified multiple times.
     */
    public void ifPresentMultiple(
            String optionName, CheckedConsumer<String[], ExperimentExecutionException> consumer)
            throws ExperimentExecutionException {
        OptionalUtilities.ifPresent(multiple(optionName, true), consumer);
    }

    /**
     * Extracts an option requiring a single argument, that may occur only <b>once</b>.
     *
     * <p>If no argument exists, it may be treated as if it is the empty string.
     *
     * @param optionName the particular option (short-name).
     * @return the single-argument if the option is present.
     * @throws ExperimentExecutionException if an option is used more times than allowed.
     */
    private Optional<String> single(String optionName, boolean convertNullToEmptyString)
            throws ExperimentExecutionException {
        if (hasOption(optionName)) {
            String[] elements = line.getOptionValues(optionName);
            if (elements != null) {
                if (elements.length == 1) {
                    return Optional.of(elements[0]);
                } else {
                    throw new ExperimentExecutionException(
                            String.format("The option -%s may only be used once!", optionName));
                }
            } else {
                return maybeConvertNullString(convertNullToEmptyString);
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Extracts an option requiring a single argument, that may occur only <b>once</b>.
     *
     * <p>If no argument exists after any option, it may be treated as if it is the empty string.
     *
     * @param optionName the particular option (short-name).
     * @return the single-argument if the option is present.
     */
    private Optional<String[]> multiple(String optionName, boolean convertNullToEmptyString) {
        if (hasOption(optionName)) {
            String[] elements = line.getOptionValues(optionName);
            if (elements != null) {
                return Optional.of(elements);
            } else {
                return maybeConvertNullString(convertNullToEmptyString)
                        .map(str -> new String[] {str});
            }
        } else {
            return Optional.empty();
        }
    }

    private static Optional<String> maybeConvertNullString(boolean convertNullToEmptyString) {
        if (convertNullToEmptyString) {
            return Optional.of("");
        } else {
            return Optional.empty();
        }
    }
}
