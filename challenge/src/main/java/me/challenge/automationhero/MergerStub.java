package me.challenge.automationhero;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

class MergerStub implements SortedMerger {
    @Override
    public void mergeStreams(List<InputStream> streamsToMerge, OutputStream outputTarget) {
    }
}
