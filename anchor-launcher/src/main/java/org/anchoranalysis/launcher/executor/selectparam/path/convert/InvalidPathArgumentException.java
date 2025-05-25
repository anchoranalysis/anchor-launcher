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
package org.anchoranalysis.launcher.executor.selectparam.path.convert;

import java.nio.file.InvalidPathException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyCheckedException;
import org.anchoranalysis.launcher.CommandLineException;

/**
 * An exception thrown if an invalid path is inputted as an argument.
 *
 * @author Owen Feehan
 */
public class InvalidPathArgumentException extends AnchorFriendlyCheckedException {

    /** Serialization version ID. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a custom message.
     *
     * @param message the error message
     */
    public InvalidPathArgumentException(String message) {
        super(message);
    }

    /**
     * Creates an exception for an invalid path argument.
     *
     * @param argument the invalid path argument
     * @param exception the {@link InvalidPathException} that was caught
     */
    public InvalidPathArgumentException(String argument, InvalidPathException exception) {
        super(
                String.format(
                        "A path passed as an argument is invalid.%nArgument:\t%s%nError:\t\t%s",
                        argument, exception.getMessage()));
    }

    /**
     * Converts this exception to a {@link CommandLineException}.
     *
     * @return a new {@link CommandLineException} with the same message as this exception
     * @throws CommandLineException always thrown with this exception's message
     */
    public CommandLineException toCommandLineException() {
        throw new CommandLineException(getMessage());
    }
}
