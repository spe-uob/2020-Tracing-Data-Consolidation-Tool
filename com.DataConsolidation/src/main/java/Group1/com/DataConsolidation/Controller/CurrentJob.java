package Group1.com.DataConsolidation.Controller;

public class CurrentJob {
    private int jobId;

    private String error;

    public CurrentJob(int jobId, String error) {
        this.jobId = jobId;
        this.error = error;
    }

    public int getJobId() {
        return jobId;
    }
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
