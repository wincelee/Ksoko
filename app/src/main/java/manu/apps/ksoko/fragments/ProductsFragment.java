package manu.apps.ksoko.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import manu.apps.ksoko.R;
import manu.apps.ksoko.adapters.ProductAdapter;
import manu.apps.ksoko.classes.Config;
import manu.apps.ksoko.viewmodels.FetchingViewModel;

public class ProductsFragment extends Fragment {

    RecyclerView rvProducts;
    NestedScrollView nsvProducts;
    SwipeRefreshLayout srlProducts;
    private FetchingViewModel fetchingViewModel;

    private NavController navController;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        fetchingViewModel = new ViewModelProvider(this).get(FetchingViewModel.class);

        rvProducts = view.findViewById(R.id.rv_products);
        nsvProducts = view.findViewById(R.id.nsv_products);
        srlProducts = view.findViewById(R.id.srl_products);

        Config.swipeRefreshLayoutColorScheme(requireActivity(), srlProducts);

        srlProducts.setOnRefreshListener(this::fetchProducts);

        fetchProducts();

    }

    private void fetchProducts() {

        fetchingViewModel.observeProducts(true).observe(getViewLifecycleOwner(), observedProducts -> {

            setRefreshingFalse();

            ProductAdapter productAdapter = new ProductAdapter(requireActivity(), observedProducts,
                    (product, pos) -> {

                        ProductsFragmentDirections.ActionProductToProductDetailsFragment actionProductToProductDetailsFragment =
                                ProductsFragmentDirections.actionProductToProductDetailsFragment();

                        actionProductToProductDetailsFragment.setId(product.getId());
                        actionProductToProductDetailsFragment.setTitle(product.getName());
                        actionProductToProductDetailsFragment.setPrice(String.valueOf(product.getPrice()));
                        actionProductToProductDetailsFragment.setDescription(product.getDescription());
                        actionProductToProductDetailsFragment.setPhoto(String.valueOf(product.getPhoto()));

                        navController.navigate(actionProductToProductDetailsFragment);

                    });


            rvProducts.setLayoutManager(new LinearLayoutManager(requireActivity()));

            rvProducts.setAdapter(productAdapter);


        });

    }

    private void setRefreshingFalse() {

        srlProducts.setRefreshing(false);

    }
}