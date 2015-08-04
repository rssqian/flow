package flow;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import static flow.Preconditions.checkState;

public final class Installer {
  private StateParceler parceler;
  private Object defaultState;
  private Flow.Dispatcher dispatcher;

  Installer() {
  }

  public Installer surviveProcessDeath(StateParceler parceler) {
    this.parceler = parceler;
    return this;
  }

  public Installer useDispatcher(Flow.Dispatcher dispatcher) {
    this.dispatcher = dispatcher;
    return this;
  }

  public Installer defaultState(Object state) {
    this.defaultState = state;
    return this;
  }

  public Context install(Context baseContext, Activity activity) {
    checkState(dispatcher == null || defaultState != null,
        "If using a custom dispatcher, you need to also set a default state.");
    final Flow.Dispatcher dis = dispatcher != null ? dispatcher : new DefaultDispatcher(activity);
    final Object state = defaultState != null ? defaultState : DefaultDispatcher.DEFAULT_STATE;
    final History defaultHistory = History.single(state);
    checkState(InternalFragment.find(activity) == null,
        "Flow is already installed in this Activity.");
    final Application app = (Application) baseContext.getApplicationContext();
    InternalFragment.install(app, activity, parceler, defaultHistory, dis);
    return new InternalContextWrapper(baseContext, activity);
  }
}
