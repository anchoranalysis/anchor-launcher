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
