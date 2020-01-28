package dk.jyskit.waf.wicket.components.tables.bootstraptable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 */
public enum BootstrapTableStyle {
    BORDERED("table-bordered"),
    STRIPED("table-striped"),
    CONDENSED("table-condensed"),
    FILTERTOOLBAR(""),
    FILTER_STATE(""),
    FILTER_SEARCH(""),
    HEADERTOOLBAR(""),
    PAGINGBOTTOMTOOLBAR(""),
    FLOATINGHEADER("");

    private String cssClass;

    BootstrapTableStyle(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static Set<BootstrapTableStyle> toSet(BootstrapTableStyle[] styles) {
    	EnumSet<BootstrapTableStyle> set = EnumSet.noneOf(BootstrapTableStyle.class);
    	set.addAll(Arrays.asList(styles));
    	return set;
    }

    public static Set<BootstrapTableStyle> add(Set<BootstrapTableStyle> styles, BootstrapTableStyle... additionalStyles) {
    	EnumSet<BootstrapTableStyle> set = EnumSet.copyOf(styles);
    	set.addAll(Arrays.asList(additionalStyles));
    	return set;
    }

    public static Set<BootstrapTableStyle> remove(Set<BootstrapTableStyle> styles, BootstrapTableStyle... additionalStyles) {
    	EnumSet<BootstrapTableStyle> set = EnumSet.copyOf(styles);
    	set.removeAll(Arrays.asList(additionalStyles));
    	return set;
    }

	public static final BootstrapTableStyle[] DEFAULT_STYLES = {
		HEADERTOOLBAR
		,PAGINGBOTTOMTOOLBAR
		,FILTERTOOLBAR
		,FILTER_SEARCH
		,BORDERED
		,CONDENSED
		,STRIPED
		,FLOATINGHEADER 
	};

	public static final BootstrapTableStyle[] IE8_SAFE_STYLES = {
		HEADERTOOLBAR
		,PAGINGBOTTOMTOOLBAR
		,FILTERTOOLBAR
		,FILTER_SEARCH
		,BORDERED
		,CONDENSED
		,STRIPED
	};

	public static final BootstrapTableStyle[] SIMPLE_STYLES = {
		HEADERTOOLBAR,
		BORDERED,
		CONDENSED,
		STRIPED
	};

	public static final BootstrapTableStyle[] SIMPLE_PAGING_STYLES = {
		HEADERTOOLBAR,
		PAGINGBOTTOMTOOLBAR,
		BORDERED,
		CONDENSED,
		STRIPED
	};

}
