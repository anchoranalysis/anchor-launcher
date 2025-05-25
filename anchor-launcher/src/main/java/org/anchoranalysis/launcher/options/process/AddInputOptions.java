/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.index.range.IndexRangeNegativeFactory;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.InputArguments;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import org.anchoranalysis.launcher.options.CommandLineOptions;

/**
 * Adds options relating to inputting from the command-line.
 *
 * @author Owen Feehan
 */
public class AddInputOptions extends AddOptionsFromCommandLine<InputArguments> {

    /** Error message prefix for invalid -il option values. */
    private static final String EXCEPTION_MESSAGE_PREFIX =
            "The -il option must be either a positive integer or a ratio in the interval (0.0, 1.0), but is";

    /**
     * Constructor for {@link AddInputOptions}.
     *
     * @param extract the {@link CommandLineExtracter} to use
     * @param arguments the {@link InputArguments} to modify
     */
    private AddInputOptions(CommandLineExtracter extract, InputArguments arguments) {
        super(extract, arguments);
    }

    /**
     * Adds options to change the inputs from the command-line.
     *
     * @param extract the {@link CommandLineExtracter} to use
     * @param arguments the {@link InputArguments} to modify
     * @throws ExperimentExecutionException if the arguments to the command-line options do not
     *     correspond to expectations.
     */
    public static void addFrom(CommandLineExtracter extract, InputArguments arguments)
            throws ExperimentExecutionException {
        new AddInputOptions(extract, arguments).addOptionsFromCommandLine();
    }

    @Override
    public void addOptionsFromCommandLine() throws ExperimentExecutionException {

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_INPUT_COPY_NON_INPUTS,
                InputArguments::assignCopyNonInputs);

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_INPUT_RELATIVE_PATH,
                arguments -> arguments.getContextParameters().assignRelativeForIdentifier());

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_INPUT_SHUFFLE,
                arguments -> arguments.getContextParameters().assignShuffle());

        ifPresentSingleAssociated(
                CommandLineOptions.SHORT_OPTION_INPUT_SUBSET_IDENTIFIER,
                AddInputOptions::assignIdentifierSubrange);

        ifPresentSingleAssociated(
                CommandLineOptions.SHORT_OPTION_INPUT_LIMIT, AddInputOptions::assignLimit);

        ifPresentSingleAssociated(
                CommandLineOptions.SHORT_OPTION_INPUT_RANDOM_SAMPLE,
                AddInputOptions::assignRandomSample);
    }

    /**
     * Instructs {@link InputArguments} to perform random-sampling.
     *
     * @param arguments the {@link InputArguments} to modify
     * @param parameter the parameter string from the command-line
     * @throws ExperimentExecutionException if the parameter is invalid
     */
    private static void assignRandomSample(InputArguments arguments, String parameter)
            throws ExperimentExecutionException {
        arguments.getContextParameters().assignShuffle();
        assignLimit(arguments, parameter);
    }

    /**
     * Assigns an identifier subrange to the {@link InputArguments}.
     *
     * @param arguments the {@link InputArguments} to modify
     * @param parameter the parameter string from the command-line
     * @throws ExperimentExecutionException if the parameter is invalid
     */
    private static void assignIdentifierSubrange(InputArguments arguments, String parameter)
            throws ExperimentExecutionException {
        try {
            arguments
                    .getContextParameters()
                    .assignIdentifierSubrange(IndexRangeNegativeFactory.parse(parameter));
        } catch (OperationFailedException e) {
            throw new ExperimentExecutionException("Cannot set parameter for subsetting names.", e);
        }
    }

    /**
     * Instructs {@link InputArguments} to assign a limit (fixed or ratio) or throw an exception if
     * it is invalid.
     *
     * @param arguments the {@link InputArguments} to modify
     * @param parameter the parameter string from the command-line
     * @throws ExperimentExecutionException if the parameter is invalid
     */
    private static void assignLimit(InputArguments arguments, String parameter)
            throws ExperimentExecutionException {

        try {
            // First, try and parse it as an integer
            int limit = Integer.parseInt(parameter);

            if (limit <= 0) {
                throw new ExperimentExecutionException(
                        String.format("%s %d", EXCEPTION_MESSAGE_PREFIX, limit));
            }

            arguments.getContextParameters().assignFixedLimit(limit);
        } catch (NumberFormatException e) {
            // Second, try and parse as a ratio. If this fails, an exception is guaranteed to be
            // thrown.
            double ratio = parseAsRatio(parameter);
            arguments.getContextParameters().assignRatioLimit(ratio);
        }
    }

    /**
     * Try and parse {@code parameter} as a ratio in the interval (0.0, 1.0).
     *
     * @param parameter the parameter string from the command-line
     * @return the parsed ratio as a double
     * @throws ExperimentExecutionException if the parameter is invalid
     */
    private static double parseAsRatio(String parameter) throws ExperimentExecutionException {
        try {
            // Try floating-point
            double limit = Double.parseDouble(parameter);

            if (limit <= 0.0 || limit >= 1.0) {
                throw new ExperimentExecutionException(
                        String.format("%s %f", EXCEPTION_MESSAGE_PREFIX, limit));
            }

            return limit;

        } catch (NumberFormatException e) {
            throw new ExperimentExecutionException("The -il option is an invalid number");
        }
    }
}
