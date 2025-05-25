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
