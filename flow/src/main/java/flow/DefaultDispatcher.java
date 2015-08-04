package flow;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import static flow.Preconditions.checkArgument;

final class DefaultDispatcher implements Flow.Dispatcher {
  static final Object DEFAULT_STATE = new Object();

  private final Activity activity;

  DefaultDispatcher(Activity activity) {
    this.activity = activity;
  }

  @Override public void dispatch(Flow.Traversal traversal, Flow.TraversalCallback callback) {
    // Probably not. It'd be nice to useful efault behavior here.
    // Maybe we could bring back that @Layout annotation, or something.
    checkArgument(traversal.destination.top() == DEFAULT_STATE, "To use your own states, you must use a custom Dispatcher");

    final TextView view = new TextView(activity);
    view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
    view.setGravity(Gravity.CENTER);
    view.setText("ohai");
    activity.setContentView(view);
    callback.onTraversalCompleted();
  }
}
