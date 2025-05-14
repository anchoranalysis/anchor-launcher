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

package org.anchoranalysis.launcher.executor.selectparam.path.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class PrettyPathConverterTest {

    private static final Path PATH_ABSOLUTE_BIG = path("/a/b/c/d/e/f");
    private static final Path PATH_ABSOLUTE_MEDIUM = path("/a/b/c/d");
    private static final Path PATH_ABSOLUTE_DIFF_BIG_TO_MEDIUM = path("e/f");
    private static final Path PATH_ABSOLUTE_DIFF_MEDIUM_TO_BIG = path("../..");

    private static final Path PATH_RELATIVE_BIG = path("../../../");
    private static final Path PATH_RELATIVE_MEDIUM = path("../../");
    private static final Path PATH_RELATIVE_DIFF_BIG_TO_MEDIUM = path("../");

    private static final String IDENTICAL = ".";

    @Test
    void testBiggerAbsolute() {
        test(PATH_ABSOLUTE_BIG, PATH_ABSOLUTE_MEDIUM, PATH_ABSOLUTE_DIFF_BIG_TO_MEDIUM.toString());
    }

    @Test
    void testSmallerAbsolute() {
        test(PATH_ABSOLUTE_MEDIUM, PATH_ABSOLUTE_BIG, PATH_ABSOLUTE_DIFF_MEDIUM_TO_BIG.toString());
    }

    @Test
    void testIdenticalAbsolute() {
        test(PATH_ABSOLUTE_MEDIUM, PATH_ABSOLUTE_MEDIUM, IDENTICAL);
    }

    @Test
    void testBiggerRelative() {
        test(PATH_RELATIVE_BIG, PATH_RELATIVE_MEDIUM, PATH_RELATIVE_DIFF_BIG_TO_MEDIUM.toString());
    }

    @Test
    void testIdenticalRelative() {
        test(PATH_RELATIVE_MEDIUM, PATH_RELATIVE_MEDIUM, IDENTICAL);
    }

    private static void test(Path test, Path workingDir, String expected) {
        assertEquals(expected, PrettyPathConverter.prettyPath(test, workingDir));
    }

    private static Path path(String str) {
        return Paths.get(str);
    }
}
