/*-
 * #%L
 * anchor-plugin-quick
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
