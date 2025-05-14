package org.anchoranalysis.launcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.bioformats.ConfigureBioformatsLogging;

/*
 * #%L
 * anchor-browser
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

/**
 * Inelegant initialization methods that should be done in the plugins themselves, when a proper
 * framework exists for initializing plugins.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DirtyInitializer {

    /**
     * Dirty initialization work done that must be done every time we initialise an application that
     * will use the Anchor platform
     */
    public static void dirtyInitialization() {
        ConfigureBioformatsLogging.instance().makeSureConfigured();
        disableAccessWarnings();
    }

    /** Disables the ugly warning that is appearing on newer JVMs from OpenCV */
    private static void disableAccessWarnings() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true); // NOSONAR
            Object unsafe = field.get(null);

            Method putObjectVolatile =
                    unsafeClass.getDeclaredMethod(
                            "putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset =
                    unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class<?> loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
            // Deliberately empty as we wish to take no action when these exceptions are caught
            // (so as to ignore them)
        }
    }
}
