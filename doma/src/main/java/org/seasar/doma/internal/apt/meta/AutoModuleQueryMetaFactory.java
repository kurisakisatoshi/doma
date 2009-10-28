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
package org.seasar.doma.internal.apt.meta;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.seasar.doma.In;
import org.seasar.doma.InOut;
import org.seasar.doma.Out;
import org.seasar.doma.ResultSet;
import org.seasar.doma.internal.apt.AptException;
import org.seasar.doma.internal.apt.AptIllegalStateException;
import org.seasar.doma.internal.apt.type.BasicType;
import org.seasar.doma.internal.apt.type.DataType;
import org.seasar.doma.internal.apt.type.DomainType;
import org.seasar.doma.internal.apt.type.EntityType;
import org.seasar.doma.internal.apt.type.EnumType;
import org.seasar.doma.internal.apt.type.ListType;
import org.seasar.doma.internal.apt.type.ReferenceType;
import org.seasar.doma.internal.apt.type.SimpleDataTypeVisitor;
import org.seasar.doma.internal.message.DomaMessageCode;

/**
 * @author taedium
 * 
 */
public abstract class AutoModuleQueryMetaFactory<M extends AutoModuleQueryMeta>
        extends AbstractQueryMetaFactory<M> {

    public AutoModuleQueryMetaFactory(ProcessingEnvironment env) {
        super(env);
    }

    @Override
    protected void doParameters(M queryMeta, ExecutableElement method,
            DaoMeta daoMeta) {
        for (VariableElement parameter : method.getParameters()) {
            QueryParameterMeta parameterMeta = createParameterMeta(parameter);
            queryMeta.addParameterMeta(parameterMeta);

            CallableSqlParameterMeta callableSqlParameterMeta = createParameterMeta(parameterMeta);
            queryMeta.addCallableSqlParameterMeta(callableSqlParameterMeta);

            if (parameterMeta.isBindable()) {
                queryMeta.addBindableParameterType(parameterMeta.getName(),
                        parameterMeta.getType());
            }
        }
    }

    protected CallableSqlParameterMeta createParameterMeta(
            final QueryParameterMeta parameterMeta) {
        if (parameterMeta.isAnnotated(ResultSet.class)) {
            return createResultSetParameterMeta(parameterMeta);
        }
        if (parameterMeta.isAnnotated(In.class)) {
            return createInParameterMeta(parameterMeta);
        }
        if (parameterMeta.isAnnotated(Out.class)) {
            return createOutParameterMeta(parameterMeta);
        }
        if (parameterMeta.isAnnotated(InOut.class)) {
            return createInOutParameterMeta(parameterMeta);
        }
        throw new AptException(DomaMessageCode.DOMA4066, env, parameterMeta
                .getElement());
    }

    protected CallableSqlParameterMeta createResultSetParameterMeta(
            final QueryParameterMeta parameterMeta) {
        ListType listType = parameterMeta.getDataType().accept(
                new SimpleDataTypeVisitor<ListType, Void, RuntimeException>() {

                    @Override
                    protected ListType defaultAction(DataType type, Void p)
                            throws RuntimeException {
                        throw new AptException(DomaMessageCode.DOMA4062, env,
                                parameterMeta.getElement());
                    }

                    @Override
                    public ListType visitListType(ListType dataType, Void p)
                            throws RuntimeException {
                        return dataType;
                    }

                }, null);
        return listType
                .getElementType()
                .accept(
                        new SimpleDataTypeVisitor<CallableSqlParameterMeta, Void, RuntimeException>() {

                            @Override
                            protected CallableSqlParameterMeta defaultAction(
                                    DataType type, Void p)
                                    throws RuntimeException {
                                throw new AptIllegalStateException(
                                        parameterMeta.getElement().toString());
                            }

                            @Override
                            public CallableSqlParameterMeta visitEntityType(
                                    EntityType dataType, Void p)
                                    throws RuntimeException {
                                return new EntityListParameterMeta(
                                        parameterMeta.getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitBasicType(
                                    BasicType dataType, Void p)
                                    throws RuntimeException {
                                return new BasicListParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitEnumType(
                                    EnumType dataType, Void p)
                                    throws RuntimeException {
                                return new EnumListParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitDomainType(
                                    DomainType dataType, Void p)
                                    throws RuntimeException {
                                return new DomainListParameterMeta(
                                        parameterMeta.getName(), dataType);
                            }

                        }, null);
    }

    protected CallableSqlParameterMeta createInParameterMeta(
            final QueryParameterMeta parameterMeta) {
        return parameterMeta
                .getDataType()
                .accept(
                        new SimpleDataTypeVisitor<CallableSqlParameterMeta, Void, RuntimeException>() {

                            @Override
                            protected CallableSqlParameterMeta defaultAction(
                                    DataType type, Void p)
                                    throws RuntimeException {
                                throw new AptException(
                                        DomaMessageCode.DOMA4101, env,
                                        parameterMeta.getElement(),
                                        parameterMeta.getType());
                            }

                            @Override
                            public CallableSqlParameterMeta visitBasicType(
                                    BasicType dataType, Void p)
                                    throws RuntimeException {
                                return new BasicInParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitEnumType(
                                    EnumType dataType, Void p)
                                    throws RuntimeException {
                                return new EnumInParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitDomainType(
                                    DomainType dataType, Void p)
                                    throws RuntimeException {
                                return new DomainInParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                        }, null);
    }

    protected CallableSqlParameterMeta createOutParameterMeta(
            final QueryParameterMeta parameterMeta) {
        final ReferenceType referenceType = parameterMeta
                .getDataType()
                .accept(
                        new SimpleDataTypeVisitor<ReferenceType, Void, RuntimeException>() {

                            @Override
                            protected ReferenceType defaultAction(
                                    DataType type, Void p)
                                    throws RuntimeException {
                                throw new AptException(
                                        DomaMessageCode.DOMA4098, env,
                                        parameterMeta.getElement());
                            }

                            @Override
                            public ReferenceType visitReferenceType(
                                    ReferenceType dataType, Void p)
                                    throws RuntimeException {
                                return dataType;
                            }

                        }, null);
        return referenceType
                .getReferentType()
                .accept(
                        new SimpleDataTypeVisitor<CallableSqlParameterMeta, Void, RuntimeException>() {

                            @Override
                            protected CallableSqlParameterMeta defaultAction(
                                    DataType type, Void p)
                                    throws RuntimeException {
                                throw new AptException(
                                        DomaMessageCode.DOMA4100, env,
                                        parameterMeta.getElement(),
                                        referenceType.getReferentTypeMirror());
                            }

                            @Override
                            public CallableSqlParameterMeta visitBasicType(
                                    BasicType dataType, Void p)
                                    throws RuntimeException {
                                return new BasicOutParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitEnumType(
                                    EnumType dataType, Void p)
                                    throws RuntimeException {
                                return new EnumOutParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitDomainType(
                                    DomainType dataType, Void p)
                                    throws RuntimeException {
                                return new DomainOutParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                        }, null);
    }

    protected CallableSqlParameterMeta createInOutParameterMeta(
            final QueryParameterMeta parameterMeta) {
        final ReferenceType referenceType = parameterMeta
                .getDataType()
                .accept(
                        new SimpleDataTypeVisitor<ReferenceType, Void, RuntimeException>() {

                            @Override
                            protected ReferenceType defaultAction(
                                    DataType type, Void p)
                                    throws RuntimeException {
                                throw new AptException(
                                        DomaMessageCode.DOMA4111, env,
                                        parameterMeta.getElement());
                            }

                            @Override
                            public ReferenceType visitReferenceType(
                                    ReferenceType dataType, Void p)
                                    throws RuntimeException {
                                return dataType;
                            }

                        }, null);
        return referenceType
                .getReferentType()
                .accept(
                        new SimpleDataTypeVisitor<CallableSqlParameterMeta, Void, RuntimeException>() {

                            @Override
                            protected CallableSqlParameterMeta defaultAction(
                                    DataType type, Void p)
                                    throws RuntimeException {
                                throw new AptException(
                                        DomaMessageCode.DOMA4100, env,
                                        parameterMeta.getElement(),
                                        referenceType.getReferentTypeMirror());
                            }

                            @Override
                            public CallableSqlParameterMeta visitBasicType(
                                    BasicType dataType, Void p)
                                    throws RuntimeException {
                                return new BasicInOutParameterMeta(
                                        parameterMeta.getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitEnumType(
                                    EnumType dataType, Void p)
                                    throws RuntimeException {
                                return new EnumInOutParameterMeta(parameterMeta
                                        .getName(), dataType);
                            }

                            @Override
                            public CallableSqlParameterMeta visitDomainType(
                                    DomainType dataType, Void p)
                                    throws RuntimeException {
                                return new DomainInOutParameterMeta(
                                        parameterMeta.getName(), dataType);
                            }

                        }, null);
    }

}
