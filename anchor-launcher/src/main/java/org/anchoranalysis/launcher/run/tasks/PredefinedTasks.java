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
package org.anchoranalysis.launcher.run.tasks;

import com.google.common.collect.Multimap;
import java.io.PrintStream;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.input.InputReadFailedException;
import org.anchoranalysis.launcher.options.CommandLineOptions;
import org.anchoranalysis.launcher.resources.Resources;

/**
 * Loading and printing predefined-tasks.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredefinedTasks {

    /**
     * Prints names of the predefined tasks that are available to the application.
     *
     * @param tasksDirectory the directory in which the task XML files reside.
     * @param resources resource-loader.
     * @param printTo the stream to print to.
     */
    public static void printTasksToConsole(
            Path tasksDirectory, Resources resources, PrintStream printTo) {

        try {
            Multimap<String, String> tasksIndexed =
                    TasksIndexer.indexBySubdirectory(FindTasks.taskNames(tasksDirectory));
            if (!tasksIndexed.isEmpty()) {
                DescribeTaskNames.printTaskNames(printTo, tasksIndexed);
                printTo.println();
                printHelpfulTextAdvice(printTo, resources);
            } else {
                printTo.printf("No predefined tasks exist (in %s%n).", tasksDirectory);
            }
        } catch (InputReadFailedException e) {
            printTo.printf(
                    "An error occurred searching the file-system for predefined tasks: %s",
                    e.toString());
        }
    }

    private static void printHelpfulTextAdvice(PrintStream printTo, Resources resources) {
        printTo.printf(
                "Run a predefined task with the -%s <taskName> command line option.%n",
                CommandLineOptions.SHORT_OPTION_TASK);
        printTo.println("e.g. anchor -t resize");
        printTo.println("e.g. anchor -t montage/reorder");
        printTo.println("e.g. anchor -t segment/text");
        printTo.println();
        printTo.println(
                "When no task is explicitly specified, the default task is used: summmarize/paths");
        printTo.println(resources.tasksFooter());
    }
}
