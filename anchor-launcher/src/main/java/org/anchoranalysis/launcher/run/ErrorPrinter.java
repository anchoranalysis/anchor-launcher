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
