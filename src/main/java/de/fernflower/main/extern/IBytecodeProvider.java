package de.fernflower.main.extern;

import java.io.InputStream;

public interface IBytecodeProvider {

    public InputStream getBytecodeStream(String externPath, String internPath);

}