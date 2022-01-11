package manu.apps.ksoko.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import manu.apps.ksoko.R;
import manu.apps.ksoko.classes.Config;
import manu.apps.ksoko.interfaces.ToolbarInterface;

public class ProductDetailsFragment extends Fragment implements View.OnClickListener {

    ImageView imvBookImage;
    NavController navController;


    TextView tvTitle, tvDescription;

    private RelativeLayout btnBuyWithGPay;

    private ProgressDialog pdPayment;

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "KE");

    PaymentsClient paymentsClient;

    private Dialog errorExceptionDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        imvBookImage = view.findViewById(R.id.imv_book_image);

        tvTitle = view.findViewById(R.id.tv_title);
        tvDescription = view.findViewById(R.id.tv_description);
        btnBuyWithGPay = view.findViewById(R.id.btn_buy_with_g_pay);

        ProductDetailsFragmentArgs productDetailsFragmentArgs =
                ProductDetailsFragmentArgs.fromBundle(getArguments());


        Glide.with(this).asBitmap().load(Integer.parseInt(productDetailsFragmentArgs
                .getPhoto())).into(imvBookImage);

        tvTitle.setText(productDetailsFragmentArgs.getTitle());
        tvDescription.setText(productDetailsFragmentArgs.getDescription());

        ((ToolbarInterface) requireActivity()).setToolbarTitle(String.format("%s%s%s",
                requireActivity().getString(R.string.usd), requireActivity().getString(R.string.space),
                Config.numberFormatter(Double.parseDouble(productDetailsFragmentArgs.getPrice()))));

        btnBuyWithGPay.setOnClickListener(this);

        pdPayment = new ProgressDialog(requireActivity());
        pdPayment.setMessage("Please wait ....... ");
        pdPayment.setCancelable(false);

        // Creating a PaymentsClient instance
        paymentsClient = createPaymentsClient(requireActivity());
        possiblyShowGooglePayButton();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_buy_with_g_pay){

            requestPayment("0.01");

        }
    }

    private String returnProductPrice(){

        return ProductDetailsFragmentArgs.fromBundle(getArguments()).getPrice();

    }

    // Defining Google API version
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    // Request a payment token for your payment provider
    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {{
                put("gateway", Config.GATEWAY);
                put("gatewayMerchantId", Config.GATEWAY_MERCHANT_ID);
            }});
        }};
    }

    // Define supported payment card networks allow more below
    // Other card payment ["AMEX", "DISCOVER", "INTERAC", "JCB"]
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                .put("MASTERCARD")
                .put("VISA");
    }

    // Allowed Card Authentication Methods
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    // Describe your allowed payment methods
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());

        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        // Enable or disable billing address here
        parameters.put("billingAddressRequired", true);

        JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);

        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }

    // Describe information expected to be returned to your application,
    // which must include tokenized payment data.
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());

        return cardPaymentMethod;
    }

    // Create a PaymentsClient instance method
    private static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(Config.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    private Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);

        } catch (JSONException e) {

            e.printStackTrace();

            showErrorExceptionDialog("isReadyToPayRequestFailure", e.getMessage(),
                    "We encountered an error while preparing to pay");

            return Optional.empty();

        }
    }

    private void possiblyShowGooglePayButton() {

        final Optional<JSONObject> isReadyToPayJson = getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(requireActivity(),
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {

                            Log.wtf("isReadyToPayFailed", task.getException());

                            showErrorExceptionDialog("isReadyToPayFailed", "Ready to make payment failed" + "\n" +
                                            task.getException(),
                                    "We encountered an error while preparing to pay");
                        }
                    }
                });
    }

    private void setGooglePayAvailable(boolean available) {

        if (available) {

            btnBuyWithGPay.setVisibility(View.VISIBLE);

        } else {

            showActionDialog("Google Pay Status Un Available", R.drawable.ic_error);
        }

    }

    private static JSONObject getTransactionInfo(String amount) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", amount);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", Config.COUNTRY_CODE);
        transactionInfo.put("currencyCode", Config.CURRENCY_CODE);
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

        return transactionInfo;
    }

    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", Config.MERCHANT_NAME);
    }

    private Optional<JSONObject> getPaymentDataRequest(String amount) {


        try {
            JSONObject paymentDataRequest = getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", getTransactionInfo(amount));
            paymentDataRequest.put("merchantInfo", getMerchantInfo());

      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
            paymentDataRequest.put("shippingAddressRequired", true);

            JSONObject shippingAddressParameters = new JSONObject();
            shippingAddressParameters.put("phoneNumberRequired", false);

            JSONArray allowedCountryCodes = new JSONArray(SHIPPING_SUPPORTED_COUNTRIES);

            shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);

            //showActionDialog(paymentDataRequest.toString(),R.drawable.ic_info);

            return Optional.of(paymentDataRequest);

        } catch (JSONException e) {

            e.printStackTrace();

            showErrorExceptionDialog("getPaymentDataRequestFailed", e.getMessage(),
                    "We encountered an error while getting payment data request");

            return Optional.empty();
        }
    }


    private void requestPayment(String amount) {

        // Disables the button to prevent multiple clicks.
        btnBuyWithGPay.setClickable(false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            btnBuyWithGPay.setClickable(true);

        }, 1000);

        //pdPayment.show();

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.

        Optional<JSONObject> paymentDataRequestJson = getPaymentDataRequest(amount);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }

        PaymentDataRequest request = PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {

            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request),
                    requireActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // value passed in AutoResolveHelper
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {

            switch (resultCode) {

                case Activity.RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);
                    handlePaymentSuccess(paymentData);
                    Log.wtf("PaymentResultOk", "true");
                    break;

                case Activity.RESULT_CANCELED:
                    // The user cancelled the payment attempt
                    Log.wtf("PaymentResultCanceled", "true");
                    break;

                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    handleError(status.getStatusCode());

                    Log.wtf("PaymentResultError", "true");
                    break;
            }

            // Re-enables the Google Pay payment button.
            btnBuyWithGPay.setClickable(true);

            pdPayment.dismiss();

        }
    }
    private void handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {

            Log.wtf("PaymentInfoIsNull", "true");

            return;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String tokenizationType = tokenizationData.getString("type");
            final String token = tokenizationData.getString("token");

            if ("PAYMENT_GATEWAY".equals(tokenizationType) && "examplePaymentMethodToken".equals(token)) {

                showActionDialog("Gateway name set to example - please modify Constants.java and replace it with your own gateway.",
                        R.drawable.ic_info);
            }

            final JSONObject info = paymentMethodData.getJSONObject("info");
            final String billingName = info.getJSONObject("billingAddress").getString("name");

            showActionDialog("Successfully received payment data for %s" + billingName,
                    R.drawable.ic_info);

            // Logging token string.
            Log.wtf("GooglePayToken", token);

        } catch (JSONException e) {

            e.printStackTrace();

            showErrorExceptionDialog("paymentError", e.getMessage(),
                    "We encountered an error while making payment");

            throw new RuntimeException("Payment Error");
        }
    }

    private void handleError(int statusCode) {
        Log.wtf("loadPaymentData failed", "Error code: " + statusCode);

        showErrorExceptionDialog("loadPaymentDataFailure", "Error code: %d" + statusCode,
                "We encountered an error while making payment");
    }


    public void showActionDialog(String tvMessage, int drawableId) {

        final Dialog actionDialog = new Dialog(requireActivity());
        actionDialog.setContentView(R.layout.layout_action_dialog);
        actionDialog.show();
        actionDialog.setCancelable(false);

        // Setting dialog background to transparent
        // Don't set to any other color it will show the rectangular shape of the dialog
        actionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // Setting size of the dialog
        actionDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imvActionDialog = actionDialog.findViewById(R.id.imv_action_dialog);
        TextView tvActionDialog = actionDialog.findViewById(R.id.tv_action_dialog_message);
        MaterialButton btnCancel = actionDialog.findViewById(R.id.btn_cancel);
        MaterialButton btnRetry = actionDialog.findViewById(R.id.btn_retry);


        imvActionDialog.setImageDrawable(ContextCompat.getDrawable(requireActivity(), drawableId));
        tvActionDialog.setText(tvMessage);

        btnCancel.setVisibility(View.GONE);

        btnRetry.setText(R.string.close);

        btnRetry.setOnClickListener(v -> {

            actionDialog.dismiss();

            // Retry action

        });

    }

    private void showErrorExceptionDialog(String statType, String stat, String tvMessage) {

        if (errorExceptionDialog == null) {

            errorExceptionDialog = new Dialog(requireActivity());
            errorExceptionDialog.setContentView(R.layout.layout_error_exception_dialog);

            errorExceptionDialog.setCancelable(false);

            errorExceptionDialog.show();

        } else if (!errorExceptionDialog.isShowing()) {

            errorExceptionDialog.show();

        }

        // Setting dialog background to transparent
        (errorExceptionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Setting size of the dialog
        errorExceptionDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        ImageView imvErrorExceptionDialog = errorExceptionDialog.findViewById(R.id.imv_error_exception_dialog);
        TextView tvErrorExceptionDialogMessage = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_message);
        TextView tvErrorExceptionDialogStatsForNerdsType = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_stats_for_nerds_type);
        TextView tvErrorExceptionDialogStatsForNerds = errorExceptionDialog.findViewById(R.id.tv_error_exception_dialog_stats_for_nerds);
        MaterialButton btnErrorExceptionCancel = errorExceptionDialog.findViewById(R.id.btn_error_exception_cancel);
        MaterialButton btnErrorExceptionRetry = errorExceptionDialog.findViewById(R.id.btn_error_exception_retry);

        imvErrorExceptionDialog.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_error));
        tvErrorExceptionDialogMessage.setText(tvMessage);
        tvErrorExceptionDialogStatsForNerdsType.setText(statType);
        tvErrorExceptionDialogStatsForNerds.setText(stat);

        imvErrorExceptionDialog.setOnClickListener(v -> {

            tvErrorExceptionDialogStatsForNerdsType.setVisibility(View.VISIBLE);
            tvErrorExceptionDialogStatsForNerds.setVisibility(View.VISIBLE);

        });

        if (statType.equalsIgnoreCase("isReadyToPayRequestFailure") |
                statType.equalsIgnoreCase("isReadyToPayFailed") |
                statType.equalsIgnoreCase("getPaymentDataRequestFailed") |
                statType.equalsIgnoreCase("paymentError") |
                statType.equalsIgnoreCase("loadPaymentDataFailure")) {

            btnErrorExceptionCancel.setVisibility(View.GONE);


            btnErrorExceptionRetry.setText(R.string.close);

            btnErrorExceptionRetry.setOnClickListener(v -> errorExceptionDialog.dismiss());


        }




    }
}