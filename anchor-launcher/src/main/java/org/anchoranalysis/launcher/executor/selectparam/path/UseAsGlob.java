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
import java.nio.file.Paths;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.collection.StringSetTrie;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.path.GlobExtractor;
import org.anchoranalysis.io.input.path.GlobExtractor.GlobWithDirectory;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Uses the path directory as a manager
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class UseAsGlob implements SelectParam<Optional<Path>> {

    /** String containing a wildcard */
    private String stringWithWildcard;

    @Override
    public Optional<Path> select(ExecutionArguments executionArguments) {

        // Isolate a directory component, from the rest of the glob
        // to allow matches like sdsds/sdsds/*.jpg
        GlobWithDirectory glob = GlobExtractor.extract(stringWithWildcard);

        InputContextParameters parameters = executionArguments.inputContextParameters();
        parameters.assignInputDirectory(glob.getDirectory().map(Paths::get));
        parameters.assignFilterGlob(glob.getGlob());

        // An empty set, means no filter check is applied
        parameters.assignInputFilterExtensions(new StringSetTrie());

        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return stringWithWildcard;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}
