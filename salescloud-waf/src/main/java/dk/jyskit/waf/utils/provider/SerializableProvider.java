package dk.jyskit.waf.utils.provider;

import java.io.Serializable;

import com.google.inject.Provider;

public interface SerializableProvider<T> extends Provider<T>, Serializable {
}
