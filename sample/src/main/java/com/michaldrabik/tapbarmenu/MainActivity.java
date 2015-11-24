package com.michaldrabik.tapbarmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

public class MainActivity extends AppCompatActivity {

  @Bind(R.id.tapBarMenu) TapBarMenu tapBarMenu;
  @Bind(R.id.label) TextView label;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.tapBarMenu) public void onMenuButtonClick() {
    tapBarMenu.toggle();
    label.setText(tapBarMenu.isOpened() ? "Menu opened" : "Menu closed");
  }

  @OnClick({ R.id.item1, R.id.item2, R.id.item3, R.id.item4 }) public void onMenuItemClick(View view) {
    switch (view.getId()) {
      case R.id.item1:
        label.setText("Item 1 selected");
        break;
      case R.id.item2:
        label.setText("Item 2 selected");
        break;
      case R.id.item3:
        label.setText("Item 3 selected");
        break;
      case R.id.item4:
        label.setText("Item 4 selected");
        break;
    }
    tapBarMenu.close();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
