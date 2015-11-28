package com.michaldrabik.tapbarmenu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

public class MainActivity extends AppCompatActivity {

  @Bind(R.id.tapBarMenu) TapBarMenu tapBarMenu;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.tapBarMenu) public void onMenuButtonClick() {
    tapBarMenu.toggle();
  }

  @OnClick({ R.id.item1, R.id.item2, R.id.item3, R.id.item4 }) public void onMenuItemClick(View view) {
    tapBarMenu.close();
    switch (view.getId()) {
      case R.id.item1:
        Log.i("TAG", "Item 1 selected");
        break;
      case R.id.item2:
        Log.i("TAG", "Item 2 selected");
        break;
      case R.id.item3:
        Log.i("TAG", "Item 3 selected");
        break;
      case R.id.item4:
        Log.i("TAG", "Item 4 selected");
        break;
    }
  }
}
