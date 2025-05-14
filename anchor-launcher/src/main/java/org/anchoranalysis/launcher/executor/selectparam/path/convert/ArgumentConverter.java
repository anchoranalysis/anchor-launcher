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
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Converts string passed as arguments.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArgumentConverter {

    /**
     * Converts the string passed as a command-line argument to a path.
     *
     * @param argument the string to convert
     * @return the path
     * @throws InvalidPathArgumentException if the argument is not a valid path.
     */
    public static Path pathFromArgument(String argument) throws InvalidPathArgumentException {
        try {
            return Paths.get(argument).toAbsolutePath();
        } catch (InvalidPathException e) {
            throw new InvalidPathArgumentException(argument, e);
        }
    }
}
