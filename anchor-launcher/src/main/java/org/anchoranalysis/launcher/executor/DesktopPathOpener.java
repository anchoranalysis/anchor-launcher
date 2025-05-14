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
