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
package org.anchoranalysis.launcher;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/**
 * An exception thrown at run-time while processing command-line arguments.
 *
 * <p>This exception extends {@link AnchorFriendlyRuntimeException} to provide user-friendly error
 * messages.
 */
public class CommandLineException extends AnchorFriendlyRuntimeException {

    /** Serialization version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new CommandLineException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link
     *     #getMessage()} method)
     */
    public CommandLineException(String message) {
        super(message);
    }
}
