package me.challenge.automationhero.reduce;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface StreamReducer {

    void mergeStreams(List<InputStream> streamsToMerge, OutputStream outputTarget);
}
