package main.java.standard;

public class PointComparator {

    //private final PointArray then;
    //private final PointArray now;

    //public PointComparator(PointArray then, PointArray now) {
    //    this.then = then;
    //    this.now = now;
    //}

    //public Result getResult() {
    //    PointArray created = new PointArray();
    //    PointArray retained = new PointArray();
    //    PointArray deleted = new PointArray();

    //    for (int i = 0; i < then.size(); i++) {
    //        if (now.contains(then.x[i], then.y[i])) {
    //            retained.add(then.x[i], then.y[i]);
    //        } else {
    //            deleted.add(then.x[i], then.y[i]);
    //        }
    //    }
    //    for (int i = 0; i < now.size(); i++) {
    //        if (!then.contains(now.x[i], now.y[i])) {
    //            created.add(now.x[i], now.y[i]);
    //        }
    //    }

    //    return new Result(created, retained, deleted);
    //}

    //public class Result {
    //    private final PointArray created;
    //    private final PointArray retained;
    //    private final PointArray deleted;

    //    private Result(PointArray created, PointArray retained, PointArray deleted) {
    //        this.created = created;
    //        this.retained = retained;
    //        this.deleted = deleted;
    //    }

    //    public PointArray getCreated() {
    //        return created;
    //    }

    //    public PointArray getRetained() {
    //        return retained;
    //    }

    //    public PointArray getDeleted() {
    //        return deleted;
    //    }
    //}
}
