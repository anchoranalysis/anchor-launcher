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
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.anchoranalysis.core.collection.StringSetTrie;
import org.anchoranalysis.core.format.FormatExtensions;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.arguments.ExecutionArguments;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Uses the path directory as a manager.
 *
 * @author Owen Feehan
 */
class UseAsExtension implements SelectParam<Optional<Path>> {

    private StringSetTrie extensions;

    /**
     * Create for particular extensions.
     *
     * @param extensionsUnsplit the various strings passed to -i that are extensions. Each string
     *     must have a leading period, and may or may not be comma-separated.
     */
    public UseAsExtension(String[] extensionsUnsplit) {
        this.extensions = splitAndNormalize(extensionsUnsplit);
    }

    @Override
    public Optional<Path> select(ExecutionArguments executionArguments) {
        executionArguments.inputContextParameters().assignInputFilterExtensions(extensions);
        return Optional.empty();
    }

    @Override
    public String describe() throws ExperimentExecutionException {
        return String.join(", ", extensions.values());
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    /**
     * Creates a set of extensions without the leading period.
     *
     * <p>Each string is guaranteed to begin with a leading period, but may contain more extensions,
     * seperated by a comma character. Each further extension may or may not have a leading period.
     *
     * @param extensionsUnsplit an array with extensions that may require splitting / normalization.
     * @return a set with each element, and with the leading period removed.
     */
    private static StringSetTrie splitAndNormalize(String[] extensionsUnsplit) {
        StringSetTrie trie = new StringSetTrie();
        Arrays.stream(extensionsUnsplit)
                .flatMap(str -> splitAsStream(str, ","))
                .map(String::trim)
                .map(FormatExtensions::removeAnyLeadingPeriod)
                .map(FormatExtensions::normalizeToLowerCase)
                .forEach(trie::add);
        return trie;
    }

    private static Stream<String> splitAsStream(String strToSplit, String splitRegEx) {
        return Arrays.stream(strToSplit.split(splitRegEx));
    }
}
