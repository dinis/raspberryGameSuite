package pt.dinis.common.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;

import java.io.IOException;

/**
 * Created by tiago on 28-02-2017.
 */
public class MessagesIdResolver implements TypeIdResolver {

    private static final String MESSAGE_PACKAGE = GenericMessage.class.getPackage().getName();
    private JavaType mBaseType;

    @Override
    public void init(JavaType baseType)
    {
        mBaseType = baseType;
    }

    @Override
    public JsonTypeInfo.Id getMechanism()
    {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public String idFromValue(Object obj)
    {
        return idFromValueAndType(obj, obj.getClass());
    }

    @Override
    public String idFromBaseType()
    {
        return idFromValueAndType(null, mBaseType.getRawClass());
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Class<?> clazz;
        String clazzName = MESSAGE_PACKAGE + "." + id;
        try {
            clazz = TypeFactory.defaultInstance().findClass(clazzName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("cannot find class '" + clazzName + "'");
        }
        return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, clazz);
    }

    @Override
    public String getDescForKnownTypeIds() {
        return null;
    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> clazz)
    {
        String name = clazz.getName();
        if ( name.startsWith(MESSAGE_PACKAGE) ) {
            return name.substring(MESSAGE_PACKAGE.length() + 1);
        }
        throw new IllegalStateException("class " + clazz + " is not in the package " + MESSAGE_PACKAGE);
    }

}