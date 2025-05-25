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

package org.anchoranalysis.launcher;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.experiment.log.ConsoleMessageLogger;
import org.anchoranalysis.launcher.config.LauncherConfig;
import org.anchoranalysis.launcher.run.ParseArgumentsAndRunExperiment;

/**
 * A command-line interface used for launching experiments.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Launch {

    /**
     * Entry point for command-line application.
     *
     * <p>The output-folder may open in the desktop, depending on the arguments passed, and whether
     * it is supported by the local operating-system.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        runCommandLineApplication(args, Optional.empty());
    }

    /**
     * Like {@link #main(String[])} but additionally specifies a path to the default-experiment.
     *
     * <p>This function may be useful for automated tests that aren't CLIs but call this library
     * simulating a CLI.
     *
     * <p>The output-folder does not open in the desktop, as it is presumed this method is being
     * called for testing purposes.
     *
     * @param args command line arguments
     * @param defaultExperiment the path to the default-experiment, if it is known, or empty if
     *     unknown
     */
    public static void mainDefaultExperiment(String[] args, Path defaultExperiment) {
        runCommandLineApplication(args, Optional.of(defaultExperiment));
    }

    /**
     * Runs a command-line application, by parsing arguments, and then executing an experiment.
     *
     * @param args arguments from command-line application
     * @param defaultExperiment the path to the default-experiment, if it is known, or empty if
     *     unknown
     */
    private static void runCommandLineApplication(String[] args, Optional<Path> defaultExperiment) {
        Logger logger = new Logger(new ConsoleMessageLogger());
        LauncherConfig config = new LauncherConfigCommandLine();
        DirtyInitializer.dirtyInitialization();
        new ParseArgumentsAndRunExperiment(logger, defaultExperiment).parseAndRun(args, config);
    }
}
