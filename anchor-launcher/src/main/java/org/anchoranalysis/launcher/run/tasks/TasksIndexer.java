/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
