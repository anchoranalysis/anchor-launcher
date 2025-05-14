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
package org.anchoranalysis.launcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A centralized location for hyperlinks to the Anchor website.
 *
 * <p>Note that this does not include any links that are mentioned in {@code resources/} that are
 * ultimately exposed to the user.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnchorWebsiteLinks {

    /** A URL for showing command-line options relating to outputting. */
    public static final String URL_OUTPUT_OPTIONS =
            "https://www.anchoranalysis.org/user_guide_command_line.html#output-options";

    /** A URL for showing predefined tasks that can be used. */
    public static final String URL_PREDEFIEND_TASKS =
            "https://www.anchoranalysis.org/user_guide_examples.html#predefined-tasks";
}
