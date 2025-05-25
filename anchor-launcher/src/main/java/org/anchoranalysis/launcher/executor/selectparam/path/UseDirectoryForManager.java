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

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.PrettyPathConverter;

/**
 * Uses the path directory as a manager.
 *
 * @author Owen Feehan
 */
class UseDirectoryForManager implements SelectParam<Optional<Path>> {

    /** Whether this is an input manager (true) or output manager (false). */
    private final boolean input;

    /** The directory to be used as a manager. */
    private final Path directory;

    /**
     * Constructor for UseDirectoryForManager.
     *
     * @param directory the {@link Path} to the directory to be used as a manager
     * @param input if true, then we are replacing the input-manager, otherwise the output-manager
     * @param checkDirectoryExists if true, performs a check that the directory already exists
     *     before using it as a manager
     * @throws CommandLineException if checkDirectoryExists is true and the directory does not exist
     */
    public UseDirectoryForManager(Path directory, boolean input, boolean checkDirectoryExists) {
        this.input = input;
        this.directory = directory;
        if (checkDirectoryExists && !directory.toFile().isDirectory()) {
            throw new CommandLineException(
                    String.format(
                            "The path %s to UseDirectoryForManager must be a directory",
                            directory));
        }
    }

    // Overridden methods do not need doc-strings as per instructions

    @Override
    public Optional<Path> select(ExecutionArguments executionArguments) {
        if (input) {
            executionArguments
                    .input()
                    .getContextParameters()
                    .assignInputDirectory(Optional.of(directory));
        } else {
            executionArguments.output().getPrefixer().assignOutputDirectory(directory);
        }
        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return PrettyPathConverter.prettyPath(directory);
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
