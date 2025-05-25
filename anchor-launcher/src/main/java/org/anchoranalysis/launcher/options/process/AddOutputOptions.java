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

import java.util.function.BiConsumer;
import org.anchoranalysis.core.format.FileFormatFactory;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.OutputArguments;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.recorded.OutputEnabledDelta;
import org.anchoranalysis.launcher.options.CommandLineExtracter;
import org.anchoranalysis.launcher.options.CommandLineOptions;

/**
 * Adds options relating to outputting from the command-line.
 *
 * @author Owen Feehan
 */
public class AddOutputOptions extends AddOptionsFromCommandLine<OutputArguments> {

    /**
     * Creates a new {@link AddOutputOptions}.
     *
     * @param extract the {@link CommandLineExtracter} to use
     * @param arguments the {@link OutputArguments} to modify
     */
    private AddOutputOptions(CommandLineExtracter extract, OutputArguments arguments) {
        super(extract, arguments);
    }

    /**
     * Adds options to add/remove change the outputs the inputs from the command-line.
     *
     * @param extract the {@link CommandLineExtracter} to use
     * @param arguments the {@link OutputArguments} to modify
     * @throws ExperimentExecutionException if the arguments to the command-line options do not
     *     correspond to expectations.
     */
    public static void addFrom(CommandLineExtracter extract, OutputArguments arguments)
            throws ExperimentExecutionException {
        new AddOutputOptions(extract, arguments).addOptionsFromCommandLine();
    }

    @Override
    public void addOptionsFromCommandLine() throws ExperimentExecutionException {
        if (!ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ALL,
                outputArguments ->
                        outputArguments
                                .getOutputEnabledDelta()
                                .enableAdditionalOutputs(Permissive.INSTANCE))) {
            ifAdditionalOptionsPresent(
                    CommandLineOptions.SHORT_OPTION_OUTPUT_ENABLE_ADDITIONAL,
                    OutputEnabledDelta::enableAdditionalOutputs);
        }

        ifAdditionalOptionsPresent(
                CommandLineOptions.SHORT_OPTION_OUTPUT_DISABLE_ADDITIONAL,
                OutputEnabledDelta::disableAdditionalOutputs);

        ifOutputFormatPresent(
                CommandLineOptions.SHORT_OPTION_OUTPUT_IMAGE_FILE_FORMAT,
                OutputArguments::assignSuggestedImageOutputFormat);

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_OUTPUT_INCREMENTING_NUMBER,
                OutputArguments::requestOutputIncrementingNumberSequence);

        ifOptionWithoutArgument(
                CommandLineOptions.SHORT_OPTION_OUTPUT_SUPPRESS_DIRECTORIES,
                OutputArguments::requestOutputSuppressDirectories);

        ifPresentSingleAssociated(
                CommandLineOptions.SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER,
                OutputArguments::requestOmitExperimentIdentifier);
    }

    /**
     * Processes additional output options if present.
     *
     * @param optionName the name of the option to check
     * @param function the function to apply if the option is present
     * @throws ExperimentExecutionException if an error occurs while processing the option
     */
    private void ifAdditionalOptionsPresent(
            String optionName, BiConsumer<OutputEnabledDelta, MultiLevelOutputEnabled> function)
            throws ExperimentExecutionException {
        ifPresentMultiple(
                optionName,
                outputs ->
                        function.accept(
                                associated.getOutputEnabledDelta(),
                                AdditionalOutputsParser.parseFrom(outputs, optionName)));
    }

    /**
     * Processes output format option if present.
     *
     * @param optionName the name of the option to check
     * @param consumer the consumer to apply if the option is present
     * @throws ExperimentExecutionException if an error occurs while processing the option
     */
    private void ifOutputFormatPresent(
            String optionName, BiConsumer<OutputArguments, ImageFileFormat> consumer)
            throws ExperimentExecutionException {
        ifPresentSingle(
                optionName,
                identifier -> {
                    ImageFileFormat format =
                            FileFormatFactory.createImageFormat(identifier)
                                    .orElseThrow(
                                            () ->
                                                    new ExperimentExecutionException(
                                                            String.format(
                                                                    "No file format identified by %s is supported.",
                                                                    identifier)));
                    consumer.accept(associated, format);
                });
    }
}
