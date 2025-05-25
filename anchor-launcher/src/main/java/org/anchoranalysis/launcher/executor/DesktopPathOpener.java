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
package org.anchoranalysis.launcher.executor;

import com.google.common.base.Preconditions;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.error.ErrorReporter;

/**
 * Opens a directory path in the desktop.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DesktopPathOpener {

    /**
     * Opens a directory path in the desktop.
     *
     * @param path the directory {@link Path} to open.
     * @param errorReporter the {@link ErrorReporter} to record any error that may occur.
     */
    public static void openPathInDesktop(Path path, ErrorReporter errorReporter) {
        Preconditions.checkArgument(path.toFile().isDirectory());
        try {
            // Some experiments have an output-directory but never create anything in it.
            // We do not want to open these directories, so we check first if the directory exists.
            if (path.toFile().exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(path.toFile());
            }
        } catch (UnsupportedOperationException e) {
            // Ignore as irrelevant
        } catch (IOException e) {
            errorReporter.recordError(
                    DesktopPathOpener.class,
                    "Failed to open output-directory in desktop, as requested:",
                    e);
        }
    }
}
