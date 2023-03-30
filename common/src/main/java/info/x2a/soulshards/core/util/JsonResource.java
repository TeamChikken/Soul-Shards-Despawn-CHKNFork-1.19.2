package info.x2a.soulshards.core.util;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class JsonResource<T> {
    private final File src;
    private final T value;
    private final TypeToken<T> tkn;

    public JsonResource(File src, T fallback, TypeToken<T> tkn) {
        this.src = src;
        this.value = JsonUtil.fromJson(tkn, src, fallback);
        this.tkn = tkn;
    }

    public T get() {
        return value;
    }

    public void save() {
        JsonUtil.toJson(value, tkn, src);
    }
}
