/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.launcher.run.tasks;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.system.path.FilePathToUnixStyleConverter;

/**
 * Builds an index of available predefined-task names, indexed by their subdirectory-name.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TasksIndexer {

    /**
     * Indexes each identifier by its directory component.
     *
     * <p>If no directory component exists, the empty string is used as a key.
     *
     * @param identifiers a {@link Stream} of task identifiers
     * @return a newly created {@link Multimap} from the directory-component to the original
     *     identifier (both keys and values for a given key are in alphabetical order)
     */
    public static Multimap<String, String> indexBySubdirectory(Stream<String> identifiers) {
        Multimap<String, String> map = MultimapBuilder.treeKeys().treeSetValues().build();
        identifiers.forEach(
                identifier -> {
                    Path path = Paths.get(identifier);
                    map.put(directoryComponent(path), fileNameComponent(path));
                });
        return map;
    }

    /**
     * Extracts the directory component from a path.
     *
     * @param path the {@link Path} to extract from
     * @return the directory part of the path (using forward slashes) or an empty string if it
     *     doesn't exist
     */
    private static String directoryComponent(Path path) {
        Path parent = path.getParent();
        if (parent != null) {
            return FilePathToUnixStyleConverter.toStringUnixStyle(parent);
        } else {
            return "";
        }
    }

    /**
     * Extracts the filename component from a path.
     *
     * @param path the {@link Path} to extract from
     * @return the filename part of the path
     */
    private static String fileNameComponent(Path path) {
        return path.getFileName().toString();
    }
}
