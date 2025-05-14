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
