package de.domjos.myarchiveservices.customTasks;


public class CustomCurrentTask<Params, Progress, Result> {
    private CustomAbstractTask<Params, Progress, Result> abstractTask;

    public CustomCurrentTask() {
        this.abstractTask = null;
    }

    public CustomAbstractTask<Params, Progress, Result> getAbstractTask() {
        return this.abstractTask;
    }

    public void setAbstractTask(CustomAbstractTask<Params, Progress, Result> abstractTask) {
        this.abstractTask = abstractTask;
    }
}
