package org.osiam.tests.stress;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize()
public class Metric {

    @JsonProperty("jvm.gc.PS-MarkSweep.count")
    private String count;

    public String getCount() {
        return count;
    }
}
