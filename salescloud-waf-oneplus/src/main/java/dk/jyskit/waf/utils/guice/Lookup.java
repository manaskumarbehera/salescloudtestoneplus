package dk.jyskit.waf.utils.guice;

import org.apache.wicket.Application;
import org.apache.wicket.guice.GuiceInjectorHolder;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class Lookup {
	public static <T> T lookup(Class<T> clazz) {
		final GuiceInjectorHolder holder = Application.get().getMetaData(GuiceInjectorHolder.INJECTOR_KEY);
		Injector injector = holder.getInjector();
		Key<T> key = Key.get(TypeLiteral.get(clazz));
		try {
			if (injector.getBinding(key) == null) {
				return null;
			}
		} catch (RuntimeException e) {
			return null;
		}

		return injector.getInstance(key);
	}
}
