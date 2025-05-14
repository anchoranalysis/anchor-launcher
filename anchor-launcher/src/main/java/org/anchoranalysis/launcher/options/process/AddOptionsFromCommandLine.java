/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.functional.checked.CheckedBiConsumer;
import org.anchoranalysis.core.functional.checked.CheckedConsumer;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.options.CommandLineExtracter;

/**
 * Base class for adding options from command-line arguments.
 *
 * @author Owen Feehan
 * @param <T> the type of the associated entity
 */
@AllArgsConstructor
public abstract class AddOptionsFromCommandLine<T> {

    /** Extracts options/arguments from the command-line. */
    private final CommandLineExtracter extract;

    /** An associated entity which consumers accept. */
    protected final T associated;

    /**
     * Maybe add options to the arguments from the command-line.
     *
     * @throws ExperimentExecutionException if an error occurs while processing the command-line
     *     options
     */
    public abstract void addOptionsFromCommandLine() throws ExperimentExecutionException;

    /**
     * Executes {@code consumer} if an option exists <b>without any argument</b>.
     *
     * @param optionShort name of the option in short form.
     * @param consumer called with the associated element, if the option is present.
     * @return true if the option is present, false otherwise.
     */
    protected boolean ifOptionWithoutArgument(String optionShort, Consumer<T> consumer) {
        if (extract.hasOption(optionShort)) {
            consumer.accept(associated);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Executes {@code consumer} if <b>maximally one option</b> exists <b>with a single argument</b>
     * - passing {@code associated}.
     *
     * @param optionShort name of the option in short form.
     * @param consumer called with the associated object and the extracted single-argument, if the
     *     option is present.
     * @throws ExperimentExecutionException if {@code consumer} throws it.
     */
    protected void ifPresentSingleAssociated(
            String optionShort, CheckedBiConsumer<T, String, ExperimentExecutionException> consumer)
            throws ExperimentExecutionException {
        extract.ifPresentSingle(optionShort, value -> consumer.accept(associated, value));
    }

    /**
     * Executes {@code consumer} if <b>maximally one option</b> exists <b>with a single argument</b>
     * - without passing {@code associated}.
     *
     * @param optionShort name of the option in short form.
     * @param consumer called with the extracted single-argument, if the option is present.
     * @throws ExperimentExecutionException if {@code consumer} throws it.
     */
    protected void ifPresentSingle(
            String optionShort, CheckedConsumer<String, ExperimentExecutionException> consumer)
            throws ExperimentExecutionException {
        extract.ifPresentSingle(optionShort, consumer);
    }

    /**
     * Executes {@code consumer} if <b>one or more</b> options exists <b>with a single argument</b>.
     *
     * @param optionShort name of the option in short form.
     * @param consumer called with an array of extracted single-arguments, if the option is present.
     * @throws ExperimentExecutionException if {@code consumer} throws it.
     */
    protected void ifPresentMultiple(
            String optionShort, CheckedConsumer<String[], ExperimentExecutionException> consumer)
            throws ExperimentExecutionException {
        extract.ifPresentMultiple(optionShort, consumer);
    }
}
