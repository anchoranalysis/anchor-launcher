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
