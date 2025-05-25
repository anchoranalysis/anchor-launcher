package org.anchoranalysis.launcher.options;

/*-
 * #%L
 * anchor-launcher
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
