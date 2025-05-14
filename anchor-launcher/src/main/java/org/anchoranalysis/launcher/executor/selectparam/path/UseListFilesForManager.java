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

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.PrettyPathConverter;

/**
 * Uses a list of paths to specific files as a manager.
 *
 * @author Owen Feehan
 */
class UseListFilesForManager implements SelectParam<Optional<Path>> {

    /** The list of paths to be used as input files. */
    private final List<Path> paths;

    /**
     * Constructor for UseListFilesForManager.
     *
     * @param paths the {@link List} of {@link Path}s to be used as input files
     * @throws InvalidPathArgumentException if any of the paths doesn't exist, or is a directory
     */
    public UseListFilesForManager(List<Path> paths) throws InvalidPathArgumentException {
        this.paths = paths;
        checkNoDirectories(paths);
    }

    // Overridden methods do not need doc-strings as per instructions

    @Override
    public Optional<Path> select(ExecutionArguments executionArguments) {
        executionArguments.input().getContextParameters().assignPaths(paths);
        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.join(", ", FunctionalList.mapToList(paths, PrettyPathConverter::prettyPath));
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    /**
     * Checks that all paths in the list exist and are not directories.
     *
     * @param paths the {@link List} of {@link Path}s to check
     * @throws InvalidPathArgumentException if any path doesn't exist or is a directory
     */
    private void checkNoDirectories(List<Path> paths) throws InvalidPathArgumentException {
        for (Path path : paths) {
            File file = path.toFile();
            if (!file.exists()) {
                throw new InvalidPathArgumentException(
                        String.format("No input file exists at path: %s", path));
            }

            if (file.isDirectory()) {
                throw new InvalidPathArgumentException(
                        String.format("Path is a directory not a file for input!%n%s", path));
            }
        }
    }
}
