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
