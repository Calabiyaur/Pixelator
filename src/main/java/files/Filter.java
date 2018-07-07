package main.java.files;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Filter {

    private final String name;
    private final List<String> extensions;

    public Filter(String name, Extension... extensions) {
        this.name = name;
        this.extensions = Arrays.stream(extensions).map(e -> e.getSuffix()).collect(Collectors.toList());
    }

    public Filter(Extension... extensions) {
        this(extensions[0].getName(), extensions);
    }

    public String getName() {
        return name;
    }

    public List<String> getExtensions() {
        return extensions;
    }

}
