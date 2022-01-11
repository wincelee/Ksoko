package manu.apps.ksoko;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import manu.apps.ksoko.classes.Config;
import manu.apps.ksoko.fragments.ProductsFragment;
import manu.apps.ksoko.interfaces.ToolbarInterface;

public class MainActivity extends AppCompatActivity implements ToolbarInterface {

    NavController navController;
    AppBarConfiguration appBarConfiguration;

    private static final int BACK_PRESS_TIME_INTERVAL = 2000;
    private long backPressTime;

    MaterialToolbar tbMain;

    AppBarLayout appBarLayout;

    //https://www.maasaimarkets.com/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.main_activity);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        if (BuildConfig.DEBUG) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

            if (Build.MODEL.equalsIgnoreCase("ANE-LX1")) {

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            }

        }

        tbMain = findViewById(R.id.tb_main);
        appBarLayout = findViewById(R.id.app_bar_layout);

        setSupportActionBar(tbMain);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_products_fragment)
                .build();

        NavigationUI.setupWithNavController(tbMain, navController, appBarConfiguration);


        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            if (destination.getId() == R.id.nav_products_fragment) {

                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                );

                appBarLayout.setVisibility(View.GONE);

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.whiteColor));

            } else if (destination.getId() == R.id.nav_product_details_fragment) {

                tbMain.setNavigationIcon(Config.returnScaledDrawable(MainActivity.this,
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_back),
                        75, 75));

                appBarLayout.setVisibility(View.VISIBLE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            }

        });

    }

    @Override
    public void setToolbarTitle(String toolbarTitle) {
        tbMain.setTitle(toolbarTitle);
    }

    @Override
    public void onBackPressed() {

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);


        if (navHostFragment != null && navHostFragment.getChildFragmentManager().getBackStackEntryCount() == 0) {

            if (backPressTime + BACK_PRESS_TIME_INTERVAL > System.currentTimeMillis()) {

                super.onBackPressed();

                return;

            } else {

                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();

            }

            backPressTime = System.currentTimeMillis();

        } else {

            super.onBackPressed();

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (getCurrentFocus() != null) {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        }

        return super.dispatchTouchEvent(ev);
    }
}