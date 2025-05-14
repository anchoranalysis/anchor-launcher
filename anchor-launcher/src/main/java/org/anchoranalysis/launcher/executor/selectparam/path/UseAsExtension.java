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
