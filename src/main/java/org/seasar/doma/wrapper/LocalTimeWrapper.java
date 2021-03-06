/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.wrapper;

import java.time.LocalTime;

import org.seasar.doma.DomaNullPointerException;

/**
 * @author nakamura-to
 * @since 2.0.0
 */
public class LocalTimeWrapper extends AbstractWrapper<LocalTime> {

    /**
     * インスタンスを構築します。
     */
    public LocalTimeWrapper() {
        super(LocalTime.class);
    }

    /**
     * 値を指定してインスタンスを構築します。
     * 
     * @param value
     *            値
     */
    public LocalTimeWrapper(LocalTime value) {
        super(LocalTime.class, value);
    }

    @Override
    public <R, P, Q, TH extends Throwable> R accept(
            WrapperVisitor<R, P, Q, TH> visitor, P p, Q q) throws TH {
        if (visitor == null) {
            throw new DomaNullPointerException("visitor");
        }
        return visitor.visitLocalTimeWrapper(this, p, q);
    }

}
