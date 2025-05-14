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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.ArgumentConverter;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;

/**
 * Determines {@link Path}s related to tasks.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskFactory {

    /**
     * Creates a {@link SelectParam} for a path or task name.
     *
     * <p>If the argument is a name (no extension, no root, no special-chars apart from
     * forward-slashes), then construct an automatic path to the tasks in the configuration
     * directory. Otherwise treat as path to BeanXML.
     *
     * @param args the command-line arguments
     * @param tasksDirectory the directory containing task configurations
     * @return a {@link SelectParam} for an optional {@link Path}
     * @throws CommandLineException if invalid arguments were passed
     */
    public static SelectParam<Optional<Path>> pathOrTaskName(String[] args, Path tasksDirectory) {

        if (args == null) {
            throw new CommandLineException("An argument (a task-name) must be specified after -t");
        }

        if (args.length != 1) {
            throw new CommandLineException("Only one instance of the -t option is permitted.");
        }

        String taskArg = args[0];

        if (isTaskName(taskArg)) {
            Path path = pathForTaskCheckExists(taskArg, tasksDirectory);
            return new UpdateTaskName<>(new UseAsCustomManager(path), taskArg);
        } else {
            try {
                return new UseAsCustomManager(ArgumentConverter.pathFromArgument(taskArg));
            } catch (InvalidPathArgumentException e) {
                throw new CommandLineException(e.toString());
            }
        }
    }

    /**
     * Constructs a path for a task and checks if it exists.
     *
     * @param taskName the name of the task
     * @param tasksDirectory the directory containing task configurations
     * @return the {@link Path} to the task configuration
     * @throws CommandLineException if the task doesn't exist
     */
    private static Path pathForTaskCheckExists(String taskName, Path tasksDirectory) {
        Path path = pathForTaskName(taskName, tasksDirectory);

        if (path.toFile().exists()) {
            return path;
        } else {
            throw new CommandLineException(String.format("The task '%s' is not known.", taskName));
        }
    }

    /**
     * Constructs a path for a task name.
     *
     * @param filenameWithoutExtension the task name without file extension
     * @param tasksDirectory the directory containing task configurations
     * @return the {@link Path} to the task configuration
     */
    private static Path pathForTaskName(String filenameWithoutExtension, Path tasksDirectory) {
        return NonImageFileFormat.XML.buildPath(tasksDirectory, filenameWithoutExtension);
    }

    /**
     * Checks if the argument is a valid task name.
     *
     * <p>A valid task name contains only alphanumeric characters, hyphens, underscores, and
     * forward-slashes.
     *
     * @param arg the argument to check
     * @return true if the argument is a valid task name, false otherwise
     */
    private static boolean isTaskName(String arg) {
        return arg.matches("^[a-zA-Z0-9_\\-\\/]+$");
    }
}
