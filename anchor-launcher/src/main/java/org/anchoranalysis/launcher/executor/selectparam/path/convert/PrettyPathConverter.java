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

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Utility class for converting paths to a more user-friendly representation. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrettyPathConverter {

    /** Maximum number of parent directory references (".." or "..") allowed in a relative path. */
    private static final int MAX_DOUBLE_DOTS_CNT = 3;

    /**
     * Converts a string path to either a normalized absolute-path or relative-path depending on
     * which is prettier to the user.
     *
     * @param path the path as a {@link String}
     * @return the prettified path as a {@link String}
     */
    public static String prettyPath(String path) {
        return prettyPath(Paths.get(path));
    }

    /**
     * Converts a Path to either a normalized absolute-path or relative-path depending on which is
     * prettier to the user.
     *
     * @param path the {@link Path} to prettify
     * @return the prettified path as a {@link String}
     */
    public static String prettyPath(Path path) {
        Path workingDir = completelyNormalize(Paths.get(""));
        return prettyPath(completelyNormalize(path), completelyNormalize(workingDir));
    }

    /**
     * Converts a Path to either a normalized absolute-path or relative-path depending on which is
     * prettier to the user, given a specific working directory.
     *
     * @param path the {@link Path} to prettify
     * @param workingDir the current working directory as a {@link Path}
     * @return the prettified path as a {@link String}
     */
    static String prettyPath(Path path, Path workingDir) {

        // First we make both paths absolute and normalized, so they have non-null roots
        path = path.toAbsolutePath().normalize();
        workingDir = workingDir.toAbsolutePath().normalize();

        if (workingDir.equals(path)) {
            // Special case to handle when directories are equal, as for some reason the Java
            //   relativize command returns .. rather than .
            return ".";
        }

        if (workingDir.getRoot().equals(path.getRoot())) {
            // If on the same root, then find a relative path between them

            Path relativePath = workingDir.relativize(path).normalize();

            // Depending on the number of dots in the relative path, we show
            //   either the absolute-path or the relative-path.
            if (countDoubleDotsInRelativePath(relativePath) > MAX_DOUBLE_DOTS_CNT) {
                return path.toString();
            } else {
                return relativePath.toString();
            }

        } else {
            // If on different roots
            return path.toAbsolutePath().normalize().toString();
        }
    }

    /**
     * Completely normalizes a path by making it absolute and normalized.
     *
     * @param path the {@link Path} to normalize
     * @return the completely normalized {@link Path}
     */
    static Path completelyNormalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    /**
     * Counts how many times ".." appears in a relative-path.
     *
     * @param path the relative {@link Path} to check
     * @return the count of ".." occurrences
     */
    private static int countDoubleDotsInRelativePath(Path path) {

        int count = 0;

        for (Path pathComponent : path) {
            if (pathComponent.getFileName().toString().equals("..")) {
                count++;
            }
        }

        return count;
    }
}
