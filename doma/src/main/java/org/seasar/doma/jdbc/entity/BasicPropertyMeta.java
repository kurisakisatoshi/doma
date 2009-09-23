/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.doma.jdbc.entity;

import org.seasar.doma.DomaNullPointerException;
import org.seasar.doma.wrapper.Wrapper;

/**
 * 基本のプロパティです。
 * 
 * @author taedium
 * 
 */
public class BasicPropertyMeta<W extends Wrapper<?>> implements
        EntityPropertyMeta<W> {

    /** 名前 */
    protected final String name;

    /** カラム名 */
    protected final String columnName;

    /** ドメイン */
    protected final W wrapper;

    /** INSERT文に含める対象かどうか */
    protected final boolean insertable;

    /** UPDATE文のSET句に含める対象かどうか */
    protected final boolean updatable;

    /**
     * インスタンスを構築します。
     * 
     * @param name
     *            名前
     * @param columnName
     *            カラム名
     * @param wrapper
     *            ドメイン
     * @param insertable
     *            INSERT文に含める対象かどうか
     * @param updatable
     *            UPDATE文のSET句に含める対象かどうか
     * @throws DomaNullPointerException
     *             {@code name} もしくは {@code domain} が {@code null} の場合
     */
    public BasicPropertyMeta(String name, String columnName, W wrapper,
            boolean insertable, boolean updatable) {
        if (name == null) {
            throw new DomaNullPointerException("name");
        }
        if (wrapper == null) {
            throw new DomaNullPointerException("wrapper");
        }
        this.name = name;
        this.columnName = columnName;
        this.wrapper = wrapper;
        this.insertable = insertable;
        this.updatable = updatable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public W getWrapper() {
        return wrapper;
    }

    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public boolean isVersion() {
        return false;
    }

    @Override
    public boolean isInsertable() {
        return insertable;
    }

    @Override
    public boolean isUpdatable() {
        return updatable;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public String toString() {
        return wrapper != null ? wrapper.toString() : null;
    }

}