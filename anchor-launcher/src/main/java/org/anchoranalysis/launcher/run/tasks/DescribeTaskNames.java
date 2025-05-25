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
package org.anchoranalysis.launcher.run.tasks;

import com.google.common.collect.Multimap;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Describe all available tasks by name.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DescribeTaskNames {

    /**
     * Print one or more lines describe all the task-names that exist in {@code tasks}.
     *
     * @param printTo how to print.
     * @param tasks the tasks, indexed by their subdirectory name (or "" if the root directory).
     */
    public static void printTaskNames(PrintStream printTo, Multimap<String, String> tasks) {
        printTo.printf("There are %d predefined tasks:%n", tasks.size());
        printTo.println();

        // Remove from the root, any tasks that have a subdirectory with the same name, as we will
        // show them in describeSubdirectory instead
        Set<String> rootTasks = new HashSet<>(tasks.get(""));

        SortedSet<String> rootTasksExcluding = removeTasksWithSubdirectory(rootTasks, tasks);

        // The first line of tasks in the tasks/ directory root
        printTo.println(describeDirectoryRoot(rootTasksExcluding));

        // Put a blank line if subdirectories of tasks also exist
        if (tasks.keySet().size() > 1) {
            printTo.println();
        }

        for (String key : tasks.keySet()) {
            if (!key.isEmpty()) {
                // Each subdirectory of tasks
                printTo.println(describeSubdirectory(key, tasks.get(key), rootTasks.contains(key)));
            }
        }
    }

    /**
     * Derive a new {@link SortedSet} containing only tasks that do not have a subdirectory with the
     * same name.
     */
    private static SortedSet<String> removeTasksWithSubdirectory(
            Set<String> rootTasks, Multimap<String, String> tasksIndexed) {
        return rootTasks.stream()
                .filter(task -> !tasksIndexed.containsKey(task))
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * A string describing all the tasks in the root subdirectory.
     *
     * @param filenames the filenames that exist for predefefined-tasks in {@code directory}.
     * @return a string with newlines, describing all the predefined-tasks in this directory, such
     *     that a user can potentially call them from the command-line.
     */
    private static String describeDirectoryRoot(Collection<String> filenames) {
        return String.join(", ", filenames);
    }

    /**
     * A string describing all the tasks in a particular subdirectory.
     *
     * @param directory the directory part of a predefined-task, without a trailing forward slash,
     *     and using only forward slashes. Empty for the root directory.
     * @param filenames the filenames that exist for predefefined-tasks in {@code directory}.
     * @param taskInRoot true when a task exists in the root, with the same name as {@code
     *     directory}.
     * @return a string with or without out newlines, describing all the predefined-tasks in this
     *     directory, such that a user can potentially call them from the command-line.
     */
    private static String describeSubdirectory(
            String directory, Collection<String> filenames, boolean taskInRoot) {
        StringBuilder builder = new StringBuilder();
        if (taskInRoot) {
            builder.append(directory);
            builder.append(" -or- ");
        }
        builder.append(String.format("%s/{%s}", directory, String.join(" | ", filenames)));
        return builder.toString();
    }
}
