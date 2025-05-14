/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.launcher.run.tasks;

import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.experiment.bean.task.TaskWithoutSharedState;
import org.anchoranalysis.experiment.task.InputBound;
import org.anchoranalysis.experiment.task.InputTypesExpected;
import org.anchoranalysis.experiment.task.NoSharedState;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.launcher.AnchorWebsiteLinks;

/**
 * A dummy task that simply writes a message to all log files, specifically: 1. log file for
 * experiment 2. log file for each input-object
 *
 * <p>The message is:
 *
 * <ol>
 *   <li>a line saying Hello World.
 *   <li>a recommendation to replace the task with a specific task.
 *   <li>a link to a page on the Anchor website showing predefined tasks.
 * </ol>
 *
 * @param <S> the type of {@link InputFromManager} expected
 * @author Owen Feehan
 */
public class HelloWorld<S extends InputFromManager> extends TaskWithoutSharedState<S> {

    @Override
    public InputTypesExpected inputTypesExpected() {
        return new InputTypesExpected(InputFromManager.class);
    }

    @Override
    public void doJobOnInput(InputBound<S, NoSharedState> input) throws JobExecutionException {
        printMessage(input.getLogger().messageLogger());
    }

    @Override
    public boolean hasVeryQuickPerInputExecution() {
        return true;
    }

    /**
     * Prints the Hello World message to the logger.
     *
     * @param logger the {@link MessageLogger} to print the message to
     */
    private void printMessage(MessageLogger logger) {
        logger.log("Hello World");
        logger.log("");
        logger.log("Consider replacing this task, with one appropriate to your intentions.");
        logger.log("");
        logger.log("Some predefined tasks can be viewed at:");
        logger.log(AnchorWebsiteLinks.URL_PREDEFIEND_TASKS);
    }
}
