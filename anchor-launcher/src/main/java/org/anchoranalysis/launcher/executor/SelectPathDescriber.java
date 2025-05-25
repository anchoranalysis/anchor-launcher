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
package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;

/**
 * Describes the {@link Path}s optionally specified for the input, output and task.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class SelectPathDescriber {

    /**
     * Creates a single-line {@link String} describing in a user-friendly way the selected {@link
     * SelectParam}s.
     *
     * @param input the {@link SelectParam} specified for <b>inputs</b>.
     * @param output the {@link SelectParam} specified for <b>outputs</b>.
     * @param task the {@link SelectParam} specified for the <b>task</b>.
     * @return the single-lined description, as above.
     * @throws ExperimentExecutionException if an error occurs with {@link SelectParam#describe()}.
     */
    public static String describe(
            SelectParam<Optional<Path>> input,
            SelectParam<Optional<Path>> output,
            SelectParam<Optional<Path>> task)
            throws ExperimentExecutionException {

        // Components
        List<String> list = new ArrayList<>(3);
        maybeAddDescriptionFor(input, "input", list);
        maybeAddDescriptionFor(output, "output", list);
        maybeAddDescriptionFor(task, "task", list);

        return reduceIntoOneLine(list);
    }

    /** Adds a description for {@code selectParam} to {@code list} if it's non-default. */
    private static void maybeAddDescriptionFor(
            SelectParam<Optional<Path>> selectParam, String identifier, List<String> list)
            throws ExperimentExecutionException {
        if (!selectParam.isDefault()) {
            list.add(String.format("%s %s", identifier, selectParam.describe()));
        }
    }

    /** Combine each element in {@code list} into a single-lined {@link String}. */
    private static String reduceIntoOneLine(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(i == 0 ? " with " : " and ");
            builder.append(list.get(i));
        }
        return builder.toString();
    }
}
