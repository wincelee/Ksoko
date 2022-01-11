package manu.apps.ksoko.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.wallet.WalletConstants;

import java.text.DecimalFormat;

import manu.apps.ksoko.R;

public class Config {

    // payment environments
    // for testing use WalletConstants.ENVIRONMENT_TEST
    // for production use WalletConstants.ENVIRONMENT_PRODUCTION
    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST;

    // for testing use exampleGatewayMerchantId
    // for production use you own merchant id
    public static final String GATEWAY_MERCHANT_ID = "exampleGatewayMerchantId";

    // Merchant Name
    public static final String MERCHANT_NAME = "Manu";

    public static final String GATEWAY = "example";

    // COUNTRY CODE
    // for USA use "US"
    // for Kenya use "KE"
    public static final String COUNTRY_CODE = "KE";

    // CURRENCY CODE
    // for USA use "USD"
    // for Kenya use "KES"
    public static final String CURRENCY_CODE = "KES";


    public static String numberFormatter(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        return decimalFormat.format(d);
    }

    public static void swipeRefreshLayoutColorScheme(Context context, SwipeRefreshLayout swipeRefreshLayout) {

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.primaryLightColor),
                ContextCompat.getColor(context, R.color.secondaryLightColor),
                ContextCompat.getColor(context, R.color.primaryDarkColor));
    }

    public static Drawable returnScaledDrawable(Context context, Bitmap bitmap,
                                                int newHeight, int newWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return new BitmapDrawable(context.getResources(), Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, false));
    }
}
