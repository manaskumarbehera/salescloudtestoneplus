package dk.jyskit.waf.wicket.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.JQueryResourceReference;

public abstract class JavaScriptJQueryDependantResourceReference extends JavaScriptResourceReference { 

    public JavaScriptJQueryDependantResourceReference(Class<?> scope, String name, Locale locale, String style, String variation) { 
        super(scope, name, locale, style, variation); 
    } 

    public JavaScriptJQueryDependantResourceReference(Class<?> scope, String name) { 
        super(scope, name); 
    } 

    @Override 
    public Iterable<? extends HeaderItem> getDependencies() { 
        
        List<HeaderItem> dependencies = new ArrayList<HeaderItem>(); 
        Iterable<? extends HeaderItem> iterable =  super.getDependencies(); 
        if (iterable != null) 
            for(HeaderItem headerItem : iterable) 
                dependencies.add(headerItem);        
        dependencies.add(JavaScriptReferenceHeaderItem.forReference(JQueryResourceReference.get())); 
        return dependencies; 

    } 
} 