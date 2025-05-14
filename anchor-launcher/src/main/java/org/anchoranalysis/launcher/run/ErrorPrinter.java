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

package org.anchoranalysis.launcher.run;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.experiment.ExperimentExecutionException;

/**
 * Displays some common error messages to the user.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ErrorPrinter {

    /**
     * How long the indentation+message can be before wrapping in the error log.
     *
     * <p>-1 disables wrapping.
     */
    private static final int ERROR_LOG_WRAP_MESSAGE = 200;

    /** Prints an error message to the console when too many arguments are provided. */
    public static void printTooManyArguments() {
        System.err.println( // NOSONAR
                "Please only pass a single experiment-file as an argument. Multiple files are not allowed");
    }

    /**
     * Prints an exception to the filesystem as an error log.
     *
     * @param cause the {@link ExperimentExecutionException} that occurred to cause the error
     * @param errorLogPath the {@link Path} where the error log should be written
     */
    public static void printErrorLog(ExperimentExecutionException cause, Path errorLogPath) {
        try {
            // Let's also store the error in a file, along with a stack trace
            FileWriter writer = new FileWriter(errorLogPath.toFile());
            cause.friendlyMessageHierarchy(writer, ERROR_LOG_WRAP_MESSAGE, true);
            writer.write(System.lineSeparator());
            writer.write(System.lineSeparator());
            writer.write("STACK TRACE:");
            writer.write(System.lineSeparator());
            cause.printStackTrace(new PrintWriter(writer));
            writer.close();
        } catch (IOException exc) {
            System.err.printf("Cannot write the error log due to an I/O error%n"); // NOSONAR
            System.err.println(exc.toString()); // NOSONAR
        }
    }
}
