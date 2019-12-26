package rigor.io.junkshop.utils;

import javafx.concurrent.Task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class TaskTool<T> {

  public Task<T> createTask(Supplier<T> task) {
    return new Task<T>() {
      @Override
      protected T call() throws Exception {
        return task.get();
      }
    };
  }

  public void execute(Task<T> task){
    Executor exec = Executors.newCachedThreadPool(runnable -> {
      Thread t = new Thread(runnable);
      t.setDaemon(true);
      return t;
    });
    exec.execute(task);
  }



}
