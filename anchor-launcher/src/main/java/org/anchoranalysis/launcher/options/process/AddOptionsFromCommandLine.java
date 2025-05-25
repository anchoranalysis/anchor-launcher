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
