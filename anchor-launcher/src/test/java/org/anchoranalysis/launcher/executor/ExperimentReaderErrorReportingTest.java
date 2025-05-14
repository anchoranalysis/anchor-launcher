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

package org.anchoranalysis.launcher.executor;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.test.TestLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Tests the different kind of error messages produced by configuration-files that have deliberate
 * errors
 *
 * <p>When debugging, a line System.out.println(output) can be inserted into a test, so that the
 * error message is outputted to the console.
 *
 * @author Owen Feehan
 */
class ExperimentReaderErrorReportingTest {

    private static TestLoader loader = TestLoader.createFromMavenWorkingDirectory();

    @BeforeAll
    static void setUp() throws Exception {
        ExperimentExecutorAfter.initializeIfNecessary(loader.getRoot(), false, false);
    }

    @Test
    void testNonExistentFilePath() {
        assertException(() -> readExperiment("a non-existent config-name", 1));
    }

    @Test
    void testIncorrectEndTag() {
        assertException(() -> readExperiment("incorrectEndTag", 4));
    }

    @Test
    void testMissingRootTag() {
        assertException(() -> readExperiment("missingRootTag", 3, "experiment"));
    }

    @Test
    void testIncorrectClass() {
        assertException(
                () ->
                        readExperiment(
                                "incorrectClass",
                                9,
                                new String[] {
                                    "Please check spelling of config-class attributes",
                                    "org.anchoranalysis.ANonExistentClass"
                                }));
    }

    @Test
    void testIncorrectFactory() {
        assertException(
                () ->
                        readExperiment(
                                "incorrectFactory",
                                8,
                                new String[] {"Unknown bean factory", "nonExistentFactory"}));
    }

    @Test
    void testIncorrectClassNested() {
        assertException(
                () ->
                        readExperiment(
                                "incorrectClassNested",
                                13,
                                new String[] {
                                    "Please check spelling of config-class attributes",
                                    "org.anchoranalysis.SomeNoneExistentClass"
                                }));
    }

    @Test
    void testIncorrectIncludeFile() {
        assertException(
                () -> readExperiment("incorrectIncludeFile", 10, "Cannot find included file"));
    }

    @Test
    void testIncorrectIncludeFileNested() {
        assertException(
                () -> readExperiment("incorrectIncludeFileNested", 6, "Cannot find included file"));
    }

    @Test
    void testMalformedXMLTag() {
        assertException(() -> readExperiment("malformedXMLTag", 7, "/>"));
    }

    @Test
    void testNonExistingBeanField() {
        assertException(() -> readExperiment("nonExistingBeanField", 13));
    }

    @Test
    void testMissingRequiredBeanField() {
        assertException(
                () ->
                        readExperiment(
                                "missingRequiredBeanField",
                                12,
                                new String[] {
                                    "stackProviderID",
                                    "org.anchoranalysis.plugin.image.bean.channel.provider.FromStack",
                                    "must be non-null"
                                }));
    }

    @Test
    void testIncludeFileOverflow() {
        assertException(
                () ->
                        readExperiment(
                                "includeFileOverflow", 10, "Including file would cause overflow"));
    }

    /** With no contains... */
    private void readExperiment(String configName, int expectedNumberOfLines)
            throws ExperimentExecutionException {
        readExperiment(configName, expectedNumberOfLines, new String[] {});
    }

    /** With a single string as contains... */
    private void readExperiment(String configName, int expectedNumberOfLines, String contains)
            throws ExperimentExecutionException {
        readExperiment(configName, expectedNumberOfLines, new String[] {contains});
    }

    /** With multiple strings as contains... */
    private void readExperiment(String configName, int expectedNumberOfLines, String[] contains)
            throws ExperimentExecutionException {

        String testPath = testPathToConfig(configName);
        Path experimentConfigFile = loader.resolveTestPath(testPath);

        // Catch and display the output
        try {
            BeanReader.readExperimentFromXML(experimentConfigFile);
        } catch (ExperimentExecutionException e) {

            String output = e.friendlyMessageHierarchy();

            assertOutput(output, testPath, expectedNumberOfLines);
            assertOutputContains(output, contains);
            throw e;
        }
    }

    private static String testPathToConfig(String configName) {
        String directory = String.format("erroredConfig/%s", configName);
        return NonImageFileFormat.XML.buildPath(directory, "config");
    }

    /**
     * Asserts that the output contains each item in an array
     *
     * @param output the output of running the test
     * @param contains the array of items, each one should be contained in output, or else an
     *     assertion is thrown
     */
    private void assertOutputContains(String output, String[] contains) {
        for (String str : contains) {
            assertTrue(output.contains(str));
        }
    }

    /**
     * Asserts the output has certain attributes
     *
     * @param output the output of running the test
     * @param testPath the path used to form the input
     * @param expectedNumberOfLines expected number of lines in the error message
     */
    private void assertOutput(String output, String testPath, int expectedNumberOfLines) {
        assertFalse(output.isEmpty());
        assertEquals(expectedNumberOfLines, numberOfLines(output));
        assertTrue(containsPathWithForwardSlashes(output, testPath));
    }

    /**
     * The number of lines in a string (splitting by the current environments line seperator)
     *
     * @param str a string with 0 or more lines
     * @return the number of lines
     */
    private static int numberOfLines(String str) {
        return str.split(System.getProperty("line.separator")).length;
    }

    /**
     * Converts all backslashes to forward slashes in the source string, and searches for a path
     *
     * @param search the string to search
     * @param path path to search for (String.contains method)
     * @return true if path is contained within search, after it is converted to forward slashes
     */
    private boolean containsPathWithForwardSlashes(String search, String path) {
        String searchConvert = search.replace("\\", "/");
        return searchConvert.contains(path);
    }

    private static void assertException(Executable executable) {
        assertThrows(ExperimentExecutionException.class, executable);
    }
}
