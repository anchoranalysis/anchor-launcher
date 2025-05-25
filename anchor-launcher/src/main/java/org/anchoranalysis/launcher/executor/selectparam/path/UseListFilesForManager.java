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
