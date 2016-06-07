/*******************************************************************************
 * Copyright 2006-2016 TyTech B.V., Saturnusstraat 60, 2516 AH, The Hague, The Netherlands All rights reserved. This software is proprietary
 * information of TyTech B.V..
 ******************************************************************************/
package nl.tytech.core.util;

import java.util.Collection;
import nl.tytech.core.structure.ItemNamespace;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 *
 * Type resolver that behaves as a normal class resolver except for known Tygron Item. They use the same naming as ItemNameSpace.
 *
 * @author Maxim Knepfle
 *
 */
public class ItemTypeResolverBuilder extends DefaultTypeResolverBuilder {

    public class ItemTypeIdResolver extends ClassNameIdResolver {

        private JavaType baseType;

        public ItemTypeIdResolver(JavaType baseType, TypeFactory factory) {
            super(baseType, factory);
            this.baseType = baseType;
        }

        @Override
        public String idFromValue(Object value) {

            if (ItemNamespace.containsClass(value.getClass())) {
                return ItemNamespace.getSimpleName(value.getClass());
            }
            return super.idFromValue(value);
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> type) {

            if (ItemNamespace.containsClass(type)) {
                return ItemNamespace.getSimpleName(type);
            }
            return super.idFromValueAndType(value, type);
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String simpleName) {

            if (ItemNamespace.containsSimpleName(simpleName)) {
                Class<?> classz = ItemNamespace.getClass(simpleName);
                return this._typeFactory.constructSpecializedType(baseType, classz);
            }
            return super.typeFromId(context, simpleName);
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -11025605395210958L;

    /**
     * Constructor with custom configuration included
     */
    public ItemTypeResolverBuilder() {

        super(DefaultTyping.NON_CONCRETE_AND_ARRAYS);

        init(JsonTypeInfo.Id.CLASS, null);

        inclusion(As.WRAPPER_OBJECT);
    }

    @Override
    protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer,
            boolean forDeser) {
        return new ItemTypeIdResolver(baseType, config.getTypeFactory());
    }
}
