package dk.jyskit.waf.application.utils.exceptions;

import java.io.Serializable;

public class EntityNotFoundException extends RuntimeException
{
    public EntityNotFoundException(Class<?> clazz, Serializable id)
    {
        super(clazz.getName() + " with id " + id.toString() + " not found.");
    }

}
