package org.anchoranalysis.launcher.options;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.Option;

/**
 * Different types of options used by Anchor that required an argument(s).
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CustomArgumentOptions {

    public static Option optionalStringArgument(
            String optionName, String longOptionName, String description) {
        Option option = new Option(optionName, longOptionName, true, description);
        option.setOptionalArg(true);
        option.setArgs(1);
        return option;
    }

    public static Option requiredStringArgument(
            String shortOptionName, String longOptionName, String description) {
        return new Option(shortOptionName, longOptionName, true, description);
    }

    public static Option requiredNumberArgument(
            String shortOptionName, String longOptionName, String description) {
        Option option = new Option(shortOptionName, longOptionName, true, description);
        option.setType(Number.class);
        return option;
    }
}
