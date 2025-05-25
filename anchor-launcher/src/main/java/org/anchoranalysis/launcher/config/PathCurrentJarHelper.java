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
