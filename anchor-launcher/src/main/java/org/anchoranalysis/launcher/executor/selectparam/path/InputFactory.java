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
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;
import org.anchoranalysis.launcher.CommandLineException;
import org.anchoranalysis.launcher.executor.selectparam.SelectParam;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.ArgumentConverter;
import org.anchoranalysis.launcher.executor.selectparam.path.convert.InvalidPathArgumentException;

/**
 * {@code SelectParam<Path>} factory for inputs.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputFactory {

    /**
     * Creates a {@link SelectParam} for path, directory, glob, or file extension inputs.
     *
     * @param arguments the command-line arguments to parse
     * @return a {@link SelectParam} for an optional {@link Path}
     * @throws InvalidPathArgumentException if the arguments cannot be parsed into a valid path
     */
    public static SelectParam<Optional<Path>> pathOrDirectoryOrGlobOrExtension(String[] arguments)
            throws InvalidPathArgumentException {
        Optional<SelectParam<Optional<Path>>> optional =
                OptionalUtilities.orFlatSupplier(
                        () -> checkWildcard(arguments),
                        () -> checkXmlExtension(arguments),
                        () -> checkFileExtension(arguments),
                        () -> checkDirectory(pathFromArguments(arguments)));
        return OptionalUtilities.orElseGet(
                optional, () -> new UseListFilesForManager(pathFromArguments(arguments)));
    }

    /**
     * Checks if the arguments contain a wildcard and creates a {@link UseAsGlob} if true.
     *
     * @param arguments the command-line arguments to check
     * @return an {@link Optional} containing a {@link UseAsGlob} if a wildcard is found, otherwise
     *     empty
     * @throws InvalidPathArgumentException if multiple wildcard arguments are found
     */
    private static Optional<SelectParam<Optional<Path>>> checkWildcard(String[] arguments)
            throws InvalidPathArgumentException {
        return check(
                Arrays.stream(arguments).anyMatch(s -> s.contains("*")),
                arguments.length == 1,
                () -> new UseAsGlob(arguments[0]),
                "Only a single wildcard argument is permitted to -i");
    }

    /**
     * Checks if the arguments are file extensions and creates a {@link UseAsExtension} if true.
     *
     * @param args the command-line arguments to check
     * @return an {@link Optional} containing a {@link UseAsExtension} if file extensions are found,
     *     otherwise empty
     * @throws InvalidPathArgumentException if not all arguments are file extensions
     */
    private static Optional<SelectParam<Optional<Path>>> checkFileExtension(String[] args)
            throws InvalidPathArgumentException {
        return check(
                Arrays.stream(args).anyMatch(ExtensionHelper::isFileExtension),
                Arrays.stream(args).allMatch(ExtensionHelper::isFileExtension),
                () -> new UseAsExtension(args),
                "If a file-extension (e.g. .png) is specified, all other arguments to -i must also be file-extensions");
    }

    /**
     * Checks if an argument ends with .xml and creates a {@link UseAsCustomManager} if true.
     *
     * @param args the command-line arguments to check
     * @return an {@link Optional} containing a {@link UseAsCustomManager} if an XML file is found,
     *     otherwise empty
     * @throws InvalidPathArgumentException if multiple XML arguments are found
     */
    private static Optional<SelectParam<Optional<Path>>> checkXmlExtension(String[] args)
            throws InvalidPathArgumentException {
        return check(
                Arrays.stream(args).anyMatch(ExtensionHelper::hasXmlExtension),
                args.length == 1,
                () -> new UseAsCustomManager(ArgumentConverter.pathFromArgument(args[0])),
                "Only a single BeanXML argument is permitted after -i (i.e. with a path with a .xml extension)");
    }

    /**
     * Checks if the paths contain a directory and creates a {@link UseDirectoryForManager} if true.
     *
     * @param paths the list of {@link Path}s to check
     * @return an {@link Optional} containing a {@link UseDirectoryForManager} if a directory is
     *     found, otherwise empty
     * @throws InvalidPathArgumentException if multiple directories are found
     */
    private static Optional<SelectParam<Optional<Path>>> checkDirectory(List<Path> paths)
            throws InvalidPathArgumentException {
        return check(
                paths.stream().anyMatch(path -> path.toFile().isDirectory()),
                paths.size() == 1,
                () -> new UseDirectoryForManager(paths.get(0), true, true),
                String.join(
                        System.lineSeparator(),
                        "with -i, please specify either:",
                        "\ta single directory",
                        "\tOR one or more files",
                        "\tOR a file extension (with a leading period)",
                        "\tBUT NOT multiple directories.",
                        "Perhaps your wildcard match is ill-specified?"));
    }

    /**
     * Checks conditions and creates a result or throws an exception.
     *
     * @param <T> the type of the result
     * @param condition1 the first condition to check
     * @param condition2 the second condition to check
     * @param supplier the supplier to create the result
     * @param errorMessage the error message to use if conditions are not met
     * @return an {@link Optional} containing the result if conditions are met, otherwise empty
     * @throws InvalidPathArgumentException if conditions are not met
     */
    private static <T> Optional<T> check(
            boolean condition1,
            boolean condition2,
            CheckedSupplier<T, InvalidPathArgumentException> supplier,
            String errorMessage)
            throws InvalidPathArgumentException {
        if (condition1) {
            if (condition2) {
                return Optional.of(supplier.get());
            } else {
                throw new CommandLineException(errorMessage);
            }
        }
        return Optional.empty();
    }

    /**
     * Converts string arguments to a list of {@link Path}s.
     *
     * @param args the string arguments to convert
     * @return a {@link List} of {@link Path}s
     * @throws InvalidPathArgumentException if any argument cannot be converted to a valid path
     */
    private static List<Path> pathFromArguments(String[] args) throws InvalidPathArgumentException {
        return FunctionalList.mapToList(
                args, InvalidPathArgumentException.class, ArgumentConverter::pathFromArgument);
    }
}
