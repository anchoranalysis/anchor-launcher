/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
