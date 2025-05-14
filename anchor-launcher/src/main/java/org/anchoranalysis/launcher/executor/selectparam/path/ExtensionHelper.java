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

package org.anchoranalysis.launcher.executor.selectparam.path;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.NonImageFileFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ExtensionHelper {

    public static boolean hasXmlExtension(String path) {
        return NonImageFileFormat.XML.matches(path);
    }

    public static boolean isFileExtension(String argument) {
        return startsWithPeriod(argument)
                && !isFileSeperator(argument)
                && !isDirectoryChange(argument);
    }

    private static boolean startsWithPeriod(String argument) {
        return argument.startsWith(".");
    }

    private static boolean isDirectoryChange(String argument) {
        return argument.equals(".") || argument.equals("..");
    }

    private static boolean isFileSeperator(String argument) {
        return argument.contains("/") || argument.contains("\\");
    }
}
