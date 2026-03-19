package com.um.springbootprojstructure.dto;

import java.time.Instant;
import java.util.Map;

public class AdminUserMergeResultResponse {

    private String sourcePublicRef;
    private String targetPublicRef;

    private boolean sourceDisabled;
    private String mergedInto; // targetPublicRef

    /**
     * Indicates which fields were copied from source to target.
     * Example: {"displayName":"source->target", "bio":"source->target"}
     */
    private Map<String, String> appliedChanges;

    private Instant mergedAt;

    public String getSourcePublicRef() { return sourcePublicRef; }
    public String getTargetPublicRef() { return targetPublicRef; }
    public boolean isSourceDisabled() { return sourceDisabled; }
    public String getMergedInto() { return mergedInto; }
    public Map<String, String> getAppliedChanges() { return appliedChanges; }
    public Instant getMergedAt() { return mergedAt; }

    public void setSourcePublicRef(String sourcePublicRef) { this.sourcePublicRef = sourcePublicRef; }
    public void setTargetPublicRef(String targetPublicRef) { this.targetPublicRef = targetPublicRef; }
    public void setSourceDisabled(boolean sourceDisabled) { this.sourceDisabled = sourceDisabled; }
    public void setMergedInto(String mergedInto) { this.mergedInto = mergedInto; }
    public void setAppliedChanges(Map<String, String> appliedChanges) { this.appliedChanges = appliedChanges; }
    public void setMergedAt(Instant mergedAt) { this.mergedAt = mergedAt; }
}
