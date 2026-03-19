package com.um.springbootprojstructure.dto;

import java.util.List;

public class XmlImportSummaryResponse {

    private int totalRecords;
    private int imported;
    private int skipped;
    private int rejected;

    /**
     * Limited set of rejection reasons (no sensitive data).
     * Each entry: "lineOrIndex:reason"
     */
    private List<String> rejectionDetails;

    public int getTotalRecords() { return totalRecords; }
    public int getImported() { return imported; }
    public int getSkipped() { return skipped; }
    public int getRejected() { return rejected; }
    public List<String> getRejectionDetails() { return rejectionDetails; }

    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }
    public void setImported(int imported) { this.imported = imported; }
    public void setSkipped(int skipped) { this.skipped = skipped; }
    public void setRejected(int rejected) { this.rejected = rejected; }
    public void setRejectionDetails(List<String> rejectionDetails) { this.rejectionDetails = rejectionDetails; }
}
