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

package org.anchoranalysis.launcher.executor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.primitive.StringSet;
import org.anchoranalysis.bean.xml.BeanXMLLoader;
import org.anchoranalysis.bean.xml.exception.BeanXMLException;
import org.anchoranalysis.core.collection.StringSetTrie;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.plugin.io.input.path.RootPathMap;

/** Loads additional configuration (for executing an experiment) from the filesystem. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperLoadAdditionalConfig {

    /** Name of anchor subdirectory in user directory. */
    private static final String ANCHOR_USER_SUBDIR = ".anchor/";

    /** Filename (relative to anchor root) for default instances config file. */
    private static final String DEFAULT_INSTANCES_FILENAME = "defaultBeans.xml";

    /** Filename (relative to anchor root) for default extensions. */
    private static final String DEFAULT_EXTENSIONS_FILENAME = "defaultInputExtensions.xml";

    /** Filename (relative to anchor root) for root path map. */
    private static final String ROOT_PATH_MAP_FILENAME = "rootPaths.xml";

    /**
     * Loads default instances from configuration files.
     *
     * @param pathConfigurationDirectory the {@link Path} to the configuration directory
     * @return a {@link BeanInstanceMap} containing the loaded default instances
     * @throws ExperimentExecutionException if loading fails
     */
    public static BeanInstanceMap loadDefaultInstances(Path pathConfigurationDirectory)
            throws ExperimentExecutionException {

        Path pathHome = pathConfigurationDirectory.resolve(DEFAULT_INSTANCES_FILENAME).normalize();
        Path pathUser = getAnchorUserDir().resolve(DEFAULT_INSTANCES_FILENAME).normalize();

        if (!pathHome.toFile().exists() && !pathUser.toFile().exists()) {
            throw new ExperimentExecutionException(
                    String.format(
                            "Cannot find a config file for defaultBean instances, looking at:%n%s%n%s",
                            pathHome, pathUser));
        }

        BeanInstanceMap map = new BeanInstanceMap();
        addDefaultInstancesFromDirectory(pathHome, map);
        addDefaultInstancesFromDirectory(pathUser, map);
        return map;
    }

    /**
     * Loads a set of default file extensions from the {@code config/} directory.
     *
     * @param pathConfigDirectory the {@link Path} to the directory in which the configuration files
     *     reside
     * @return an {@link Optional} containing a {@link StringSetTrie} of extensions (without any
     *     period, and in lower-case) or empty if the file doesn't exist
     * @throws ExperimentExecutionException if loading fails
     */
    public static Optional<StringSetTrie> loadDefaultExtensions(Path pathConfigDirectory)
            throws ExperimentExecutionException {

        Path path = pathConfigDirectory.resolve(DEFAULT_EXTENSIONS_FILENAME).normalize();

        if (path.toFile().exists()) {
            try {
                StringSet setBean = BeanXMLLoader.loadBean(path, "bean");
                return Optional.of(new StringSetTrie(setBean.set()));

            } catch (BeanXMLException e) {
                throw new ExperimentExecutionException(
                        String.format("An error occurred loading bean XML from %s", path), e);
            }
        }

        return Optional.empty();
    }

    /**
     * Adds default instances from a directory to a {@link BeanInstanceMap}.
     *
     * @param path the {@link Path} to the directory containing default instances
     * @param addToMap the {@link BeanInstanceMap} to add the instances to
     * @throws ExperimentExecutionException if adding fails
     */
    private static void addDefaultInstancesFromDirectory(Path path, BeanInstanceMap addToMap)
            throws ExperimentExecutionException {
        if (path.toFile().exists()) {
            try {
                List<NamedBean<?>> listDefaults = BeanXMLLoader.loadBean(path, "bean");
                addToMap.addFrom(listDefaults);

            } catch (BeanXMLException | BeanMisconfiguredException e) {
                throw new ExperimentExecutionException(
                        String.format("An error occurred loading bean XML from %s", path), e);
            }
        }
    }

    /**
     * Loads root paths from configuration files.
     *
     * @param pathConfigurationDirectory the {@link Path} to the configuration directory
     * @return a {@link RootPathMap} containing the loaded root paths
     * @throws ExperimentExecutionException if loading fails
     */
    public static RootPathMap loadRootPaths(Path pathConfigurationDirectory)
            throws ExperimentExecutionException {

        // First we look in the Anchor Home directory
        // Then we look in the Anchor User directory

        Path pathHome = pathConfigurationDirectory.resolve(ROOT_PATH_MAP_FILENAME).normalize();
        Path pathUser = getAnchorUserDir().resolve(ROOT_PATH_MAP_FILENAME).normalize();

        addRootPathsFromDir(pathHome, RootPathMap.instance());
        addRootPathsFromDir(pathUser, RootPathMap.instance());

        return RootPathMap.instance();
    }

    /**
     * Adds root paths from a directory to a {@link RootPathMap}.
     *
     * @param path the {@link Path} to the directory containing root paths
     * @param addToMap the {@link RootPathMap} to add the root paths to
     * @throws ExperimentExecutionException if adding fails
     */
    private static void addRootPathsFromDir(Path path, RootPathMap addToMap)
            throws ExperimentExecutionException {

        if (path.toFile().exists()) {
            try {
                addToMap.addFromXmlFile(path);
            } catch (OperationFailedException e) {
                throw new ExperimentExecutionException(
                        String.format(
                                "An error occurred adding a root-path from the XML file %s", path),
                        e);
            }
        }
    }

    /**
     * Gets the Anchor user directory.
     *
     * @return the {@link Path} to the Anchor user directory
     */
    private static Path getAnchorUserDir() {
        Path currentUsersHomeDir = Paths.get(System.getProperty("user.home"));
        return currentUsersHomeDir.resolve(ANCHOR_USER_SUBDIR);
    }
}
