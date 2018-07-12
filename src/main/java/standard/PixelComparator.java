package main.java.standard;

public class PixelComparator {

    //private final PixelArray then;
    //private final PixelArray now;

    //public PixelComparator(PixelArray then, PixelArray now) {
    //    this.then = then;
    //    this.now = now;
    //}

    //public Result getResult() {
    //    PixelArray created = new PixelArray();
    //    PixelArray retained = new PixelArray();
    //    PixelArray deleted = new PixelArray();

    //    for (int i = 0; i < then.size(); i++) {
    //        if (!now.contains(then.x[i], then.y[i])) {
    //            deleted.add(then.x[i], then.y[i], then.previousColor[i], then.color[i]);
    //        }
    //    }
    //    for (int i = 0; i < now.size(); i++) {
    //        if (!then.contains(now.x[i], now.y[i])) {
    //            created.add(now.x[i], now.y[i], now.previousColor[i], now.color[i]);
    //        } else {
    //            retained.add(then.x[i], then.y[i], then.previousColor[i], then.color[i]);
    //        }
    //    }

    //    return new Result(created, retained, deleted);
    //}

    //public class Result {
    //    private final PixelArray created;
    //    private final PixelArray retained;
    //    private final PixelArray deleted;

    //    private Result(PixelArray created, PixelArray retained, PixelArray deleted) {
    //        this.created = created;
    //        this.retained = retained;
    //        this.deleted = deleted;
    //    }

    //    public PixelArray getCreated() {
    //        return created;
    //    }

    //    public PixelArray getRetained() {
    //        return retained;
    //    }

    //    public PixelArray getDeleted() {
    //        return deleted;
    //    }
    //}
}
