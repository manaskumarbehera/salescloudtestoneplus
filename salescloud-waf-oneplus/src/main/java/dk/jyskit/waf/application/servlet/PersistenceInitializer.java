package dk.jyskit.waf.application.servlet;

import javax.inject.Inject;

import com.google.inject.persist.PersistService;

/**
 * @author: Carlos A Becker
 */
public class PersistenceInitializer {
    @Inject
    public PersistenceInitializer(PersistService service) {
        service.start();
    }
}