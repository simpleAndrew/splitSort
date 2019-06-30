package me.challenge.automationhero;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

interface SortedMerger {

    void mergeStreams(List<InputStream> streamsToMerge, OutputStream outputTarget);
}
