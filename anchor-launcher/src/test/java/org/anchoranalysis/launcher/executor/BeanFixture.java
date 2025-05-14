/*-
 * #%L
 * anchor-plugin-quick
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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.experiment.bean.task.Task;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.mpp.io.input.MultiInput;

/**
 * A fixture bean that is defined in XML for the tests, but otherwise doesn't do anything.
 *
 * @author Owen Feehan
 * @param <S> shared-state
 */
public class BeanFixture<S> extends AnchorBean<BeanFixture<S>> {

    // START BEAN PROPERTIES
    /**
     * A string indicating the input file(s)
     *
     * <p>Either: 1. a file-path to a single image 2. a file glob matching several images (e.g.
     * /somedir/somefile*.png) 3. a file-path ending in .xml or .XML. This is then interpreted
     * treated a a paths to BeanXML describing a NamedMultiCollectionInputManager
     */
    @BeanField @Getter @Setter private String fileInput;

    @BeanField @Getter @Setter private String directoryOutput;

    @BeanField @Getter @Setter private Task<MultiInput, S> task;

    @BeanField @Getter @Setter private String inputName = "stackInput";

    @BeanField @Getter @Setter
    private OutputWriteSettings outputWriteSettings = new OutputWriteSettings();
    // END BEAN PROPERTIES
}
