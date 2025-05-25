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
package org.anchoranalysis.launcher.options.process;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.io.output.enabled.OutputEnabledMutable;

/**
 * Creates a {@link OutputEnabledMutable} with output-names to use in addition to defaults.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class AdditionalOutputsParser {

    /**
     * What element separates the first-level output from the second-level output.
     *
     * <p>Note this will also be interpreted as a regular-expression in a split.
     */
    private static final String ELEMENT_SEPERATOR = ":";

    /** Part of the exception message for invalid output-name format. */
    private static final String EXCEPTION_MESSAGE_SNIPPET =
            "It must be in the format of either outputName or firstLevelOutputName:secondLevelOutputName.";

    /**
     * Creates a {@link OutputEnabledMutable} from a user-supplied string.
     *
     * <p>The format of the string is one or more comma-separated elements.
     *
     * <p>If the element string contains no colon, then it is interpreted as a first-level
     * output-name to enable.
     *
     * <p>If the element string contains a colon in the format {@code prefix:suffix}, {@code suffix}
     * is interpreted as a second-level output-name to enable, for a corresponding first-level
     * output of {@code prefix}.
     *
     * <p>Examples:
     *
     * <ul>
     *   <li>{@code "csv"}
     *   <li>{@code "csv, outline"}
     *   <li>{@code "outline,stacks:background"}
     *   <li>{@code "stacks:foreground"}
     * </ul>
     *
     * @param optionsArgument a string in the format
     * @param optionName the name of the option the string was inputted in (for error messages)
     * @return the additional outputs
     * @throws ExperimentExecutionException if the argument doesn't correspond to the expected
     *     format.
     */
    public static OutputEnabledMutable parseFrom(String[] optionsArgument, String optionName)
            throws ExperimentExecutionException {

        OutputEnabledMutable outputs = new OutputEnabledMutable();

        for (String argument : optionsArgument) {
            if (argument.isEmpty()) {
                throw new ExperimentExecutionException(
                        String.format("The -%s option requires an argument.", optionName));
            }

            for (String element : argument.split(",")) {
                addElement(outputs, element);
            }
        }
        return outputs;
    }

    /**
     * Adds an element to the {@link OutputEnabledMutable}.
     *
     * @param outputs the {@link OutputEnabledMutable} to add to
     * @param element the element string to parse and add
     * @throws ExperimentExecutionException if the element is invalid
     */
    private static void addElement(OutputEnabledMutable outputs, String element)
            throws ExperimentExecutionException {
        if (element.contains(ELEMENT_SEPERATOR)) {
            addSecondLevelElement(outputs, element);
        } else {
            outputs.addEnabledOutputFirst(element);
        }
    }

    /**
     * Adds a second-level element to the {@link OutputEnabledMutable}.
     *
     * @param outputs the {@link OutputEnabledMutable} to add to
     * @param element the element string to parse and add
     * @throws ExperimentExecutionException if the element is invalid
     */
    private static void addSecondLevelElement(OutputEnabledMutable outputs, String element)
            throws ExperimentExecutionException {
        if (element.startsWith(ELEMENT_SEPERATOR) || element.endsWith(ELEMENT_SEPERATOR)) {
            throw createException("An output-name may not start or end with a colon.", element);
        }

        String[] splits = element.split(ELEMENT_SEPERATOR);

        if (splits.length != 2) {
            throw createException("Invalid output-name.", element);
        }

        outputs.addEnabledOutputSecond(splits[0], splits[1]);
    }

    /**
     * Creates an {@link ExperimentExecutionException} with a formatted error message.
     *
     * @param prefix the prefix for the error message
     * @param element the element that caused the error
     * @return a new {@link ExperimentExecutionException}
     */
    private static ExperimentExecutionException createException(String prefix, String element) {
        return new ExperimentExecutionException(
                String.format("%s%n%s%n%s", prefix, EXCEPTION_MESSAGE_SNIPPET, element));
    }
}
