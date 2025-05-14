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

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.launcher.AnchorWebsiteLinks;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.ArgumentConverter;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;
import org.anchoranalysis.launcher.options.CommandLineOptions;

/**
 * {@code SelectParam<Path>} factory for outputs.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OutputFactory {

    /**
     * Creates a {@link SelectParam} for output paths or directories.
     *
     * <p>If the argument is a path to a directory, then this directory is set as the default.
     * Otherwise, the argument is treated like a path to BeanXML.
     *
     * @param arguments the command-line arguments to parse
     * @param writeIntoRoot whether to write directly into an output directory's root (in which case
     *     a directory must be selected)
     * @return a {@link SelectParam} for optional {@link Path}.
     * @throws CommandLineException if invalid arguments were passed
     */
    public static SelectParam<Optional<Path>> pathOrDirectory(
            String[] arguments, boolean writeIntoRoot) {

        if (arguments.length > 1) {
            throw new CommandLineException(
                    String.format(
                            "More than one argument was passed to -%s. Only one is allowed!",
                            CommandLineOptions.SHORT_OPTION_OUTPUT));
        }

        String pathArgument = arguments[0];

        try {
            Path path = ArgumentConverter.pathFromArgument(pathArgument);
            File file = path.toFile();
            if (file.isDirectory()) {

                if (writeIntoRoot) {
                    throw new CommandLineException(
                            String.format(
                                    "The output-directory already exists. This is not permitted when option -%s is employed.",
                                    CommandLineOptions
                                            .SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER));
                }

                // If the path exists AND is a directory...
                return usePathAsDirectoryForManager(path, false, true);
            } else if (file.exists()) {

                if (writeIntoRoot) {
                    throw new CommandLineException(
                            String.format(
                                    "No output-directory was selected, as is required with option -%s",
                                    CommandLineOptions
                                            .SHORT_OPTION_OUTPUT_OMIT_EXPERIMENT_IDENTIFIER));
                }

                // If the path exists BUT isn't a directory
                return usePathAsBeanXML(path);
            } else {
                return pathNotExisting(pathArgument, path, file, writeIntoRoot);
            }
        } catch (InvalidPathArgumentException e) {
            throw e.toCommandLineException();
        }
    }

    /** If the path for outputting doesn't exist... */
    private static SelectParam<Optional<Path>> pathNotExisting(
            String pathArgument, Path path, File file, boolean writeIntoRoot) {
        if (looksLikeDirectoryPath(pathArgument) || writeIntoRoot) {
            // If it looks like a directory (i.e. has a trailing slash, or if we are writing into
            // the root, we assume it's a directory.

            if (!writeIntoRoot) {
                // If it looks like a directory, create this directory, and then output into it.
                file.mkdirs();
            }
            return usePathAsDirectoryForManager(path, false, !writeIntoRoot);
        } else {
            throw new CommandLineException(
                    String.format(
                            "The argument '%s' for -%s or -%s:%n  - is neither a path to an existing file (BeanXML for an output-manager).%n  - nor looks like a directory (into which outputting occurs).%n%nSee %s%n",
                            pathArgument,
                            CommandLineOptions.SHORT_OPTION_OUTPUT,
                            CommandLineOptions.LONG_OPTION_OUTPUT,
                            AnchorWebsiteLinks.URL_OUTPUT_OPTIONS));
        }
    }

    /** The path is used as a directory for the output-manager to output into. */
    private static SelectParam<Optional<Path>> usePathAsDirectoryForManager(
            Path path, boolean input, boolean checkDirectoryExists) {
        return new UseDirectoryForManager(path, input, checkDirectoryExists);
    }

    /** The path is interpreted as pointing to BeanXML defining an output-manager. */
    private static SelectParam<Optional<Path>> usePathAsBeanXML(Path path) {
        return new UseAsCustomManager(path);
    }

    private static boolean looksLikeDirectoryPath(String argument) {
        return argument.endsWith("/") || argument.endsWith("\\");
    }
}
