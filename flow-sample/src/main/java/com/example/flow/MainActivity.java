/*
 * Copyright 2013 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.flow;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.example.flow.Paths.ConversationList;
import com.example.flow.pathview.HandlesBack;
import flow.Flow;
import flow.History;
import flow.path.PathContainerView;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static flow.Flow.Direction.FORWARD;

public class MainActivity extends Activity {
  private PathContainerView container;
  private HandlesBack containerAsBackTarget;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final ActionBar actionBar = getActionBar();
    actionBar.setDisplayShowHomeEnabled(false);
    setContentView(R.layout.root_layout);
    container = (PathContainerView) findViewById(R.id.container);
    containerAsBackTarget = (HandlesBack) container;
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Flow.onNewIntent(intent, this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    menu.add("Friends")
        .setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
          @Override public boolean onMenuItemClick(MenuItem menuItem) {
            Flow.get(MainActivity.this).setHistory(History.emptyBuilder() //
                .push(new ConversationList()) //
                .push(new Paths.FriendList()) //
                .build(), FORWARD);
            return true;
          }
        });
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(Flow.installer().install(newBase, this));
  }

  @Override public void onBackPressed() {
    if (containerAsBackTarget.onBackPressed()) return;
    if (Flow.onBackPressed(this)) return;
    super.onBackPressed();
  }
}
