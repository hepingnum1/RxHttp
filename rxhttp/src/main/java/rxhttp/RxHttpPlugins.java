package rxhttp;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;
import io.reactivex.internal.util.ExceptionHelper;
import rxhttp.wrapper.converter.GsonConverter;
import rxhttp.wrapper.callback.IConverter;
import rxhttp.wrapper.param.Param;

/**
 * RxHttp 插件类
 * User: ljx
 * Date: 2019-07-14
 * Time: 11:24
 */
public class RxHttpPlugins {

    private static Function<? super Param, ? extends Param> mOnParamAssembly;
    private static Function<? super String, String> decoder;
    private static IConverter converter = GsonConverter.create();

    //设置公共参数装饰
    public static void setOnParamAssembly(@Nullable Function<? super Param, ? extends Param> onParamAssembly) {
        mOnParamAssembly = onParamAssembly;
    }

    //设置转换器,可用于对Http返回的String 字符串解密

    /**
     * @deprecated please user {@link #setResultDecoder(Function)} instead
     */
    @Deprecated
    public static void setOnConverter(@Nullable Function<? super String, String> decoder) {
        setResultDecoder(decoder);
    }

    //设置解码/解密器,可用于对Http返回的String 字符串解码/解密
    public static void setResultDecoder(@Nullable Function<? super String, String> decoder) {
        RxHttpPlugins.decoder = decoder;
    }

    public static void setConverter(@NonNull IConverter converter) {
        if (converter == null)
            throw new IllegalArgumentException("converter can not be null");
        RxHttpPlugins.converter = converter;
    }

    public static IConverter getConverter() {
        return converter;
    }

    /**
     * <P>对Param参数添加一层装饰,可以在该层做一些与业务相关工作，
     * <P>例如：添加公共参数/请求头信息
     *
     * @param source Param
     * @return 装饰后的参数
     */
    public static Param onParamAssembly(Param source) {
        if (source == null || !source.isAssemblyEnabled()) return source;
        Function<? super Param, ? extends Param> f = mOnParamAssembly;
        if (f != null) {
            return apply(f, source);
        }
        return source;
    }

    /**
     * 对字符串进行解码/解密
     *
     * @param source String字符串
     * @return 解码/解密后字符串
     */
    public static String onResultDecoder(String source) {
        Function<? super String, String> f = decoder;
        if (f != null) {
            return apply(f, source);
        }
        return source;
    }

    @NonNull
    private static <T, R> R apply(@NonNull Function<T, R> f, @NonNull T t) {
        try {
            return f.apply(t);
        } catch (Throwable ex) {
            throw ExceptionHelper.wrapOrThrow(ex);
        }
    }
}
