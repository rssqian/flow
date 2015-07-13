package flow;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import static flow.Preconditions.checkArgument;

/**
 * Pay no attention to this class. It's only public because it has to be.
 */
public final class InternalFragment extends Fragment {
  static final String FRAGMENT_TAG = "flow-fragment-tag";

  static InternalFragment find(Activity activity) {
    return (InternalFragment) activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
  }

  static void install(Application app, final Activity activity, final StateParceler parceler,
      final History defaultHistory, final Flow.Dispatcher dispatcher) {
    app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
          @Override public void onActivityCreated(Activity a, Bundle savedInstanceState) {
            if (a == activity) {
              InternalFragment fragment = find(activity);
              boolean newFragment = fragment == null;
              if (newFragment) {
                fragment = new InternalFragment();
              }
              fragment.defaultHistory = defaultHistory;
              fragment.dispatcher = dispatcher;
              fragment.parceler = parceler;
              if (newFragment) {
                activity.getFragmentManager()
                    .beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commit();
              }
            }
          }

          @Override public void onActivityStarted(Activity activity) {
          }

          @Override public void onActivityResumed(Activity activity) {
          }

          @Override public void onActivityPaused(Activity activity) {
          }

          @Override public void onActivityStopped(Activity activity) {
          }

          @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
          }

          @Override public void onActivityDestroyed(Activity activity) {
            activity.getApplication().unregisterActivityLifecycleCallbacks(this);
          }
        });
  }

  Flow flow;
  StateParceler parceler;
  History defaultHistory;
  Flow.Dispatcher dispatcher;
  Intent intent;

  public InternalFragment() {
    super();
    setRetainInstance(true);
  }

  void onNewIntent(Intent intent) {
    if (intent.hasExtra(Flow.HISTORY_KEY)) {
      History history = History.from(intent.getParcelableExtra(Flow.HISTORY_KEY), parceler);
      flow.setHistory(history, Flow.Direction.REPLACE);
    }
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (flow == null) {
      History savedHistory = null;
      if (savedInstanceState != null && savedInstanceState.containsKey(Flow.HISTORY_KEY)) {
        savedHistory = History.from(savedInstanceState.getParcelable(Flow.HISTORY_KEY), parceler);
      }
      flow = new Flow(selectHistory(intent, savedHistory, defaultHistory, parceler));
    }
  }

  @Override public void onResume() {
    super.onResume();
    flow.setDispatcher(dispatcher);
  }

  @Override public void onPause() {
    flow.removeDispatcher(dispatcher);
    super.onPause();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    checkArgument(outState != null, "outState may not be null");
    if (parceler == null) {
      return;
    }

    Parcelable parcelable = flow.getHistory().getParcelable(parceler, new History.Filter() {
      @Override public boolean apply(Object state) {
        return !state.getClass().isAnnotationPresent(NotPersistent.class);
      }
    });
    if (parcelable != null) {
      //noinspection ConstantConditions
      outState.putParcelable(Flow.HISTORY_KEY, parcelable);
    }
  }

  private static History selectHistory(Intent intent, History saved,
      History defaultHistory, StateParceler parceler) {
    if (intent != null && intent.hasExtra(Flow.HISTORY_KEY)) {
      return History.from(intent.getParcelableExtra(Flow.HISTORY_KEY), parceler);
    }
    if (saved != null) {
      return saved;
    }
    return defaultHistory;
  }
}
