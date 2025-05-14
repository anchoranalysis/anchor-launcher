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

package org.anchoranalysis.launcher.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;

/**
 * Determines the path to the directory from where a particular jar resides.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class PathCurrentJarHelper {

    /**
     * Determines the path to the current jar directory (or folder with class files) so we can
     * resolve a properties file
     *
     * @param classLauncher the class which was used to launch the application (or another class
     *     with the same codeSource)
     * @return a path (always a folder) to the current jar (or folder with class files)
     */
    public static Path pathCurrentJAR(Class<?> classLauncher) {
        try {
            URI pathURI = classLauncher.getProtectionDomain().getCodeSource().getLocation().toURI();
            return pathFromUri(pathURI);
        } catch (URISyntaxException e) {
            throw new AnchorFriendlyRuntimeException(
                    "An invalid URI was used in establishing the path to the current JAR", e);
        }
    }

    private static Path pathFromUri(URI uri) {
        Path path = Paths.get(uri);

        if (path.toFile().isDirectory()) {
            // If it's a folder this is good enough, and we return it
            return path;
        } else {
            // If it's a file, then we assume this is path to the jar, and return its parent folder
            return path.getParent();
        }
    }
}
