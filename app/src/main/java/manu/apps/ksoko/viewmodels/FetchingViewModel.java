package manu.apps.ksoko.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import manu.apps.ksoko.R;
import manu.apps.ksoko.classes.Product;

public class FetchingViewModel extends AndroidViewModel {

    private MutableLiveData<List<Product>> products;

    public FetchingViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Product>> observeProducts(boolean isRefresh) {

        if (isRefresh) {

            initializeFetchingProducts(true);

        } else {

            if (products == null) {

                initializeFetchingProducts(false);

            }

        }

        return products;
    }

    private void initializeFetchingProducts(boolean isRefresh) {

        products = new MutableLiveData<>();

        fetchProducts(isRefresh);

    }

    private void fetchProducts(boolean isRefresh) {

        List<Product> productsList = new ArrayList<>();

        productsList.add(new Product("1", "Palm Tree Bowl and Spoon Set",
                "This is our unique and beautiful palm tree bowl and spoon set. " +
                        "Palm tree is typical found at the coastal side of Kenya and is " +
                        "characterized by being resilient and malleable\n" +
                        "\n" +
                        "As these are all individually handmade, they will not all be identical, " +
                        "their flaws or differences are their charm. They are created by " +
                        "talented artists and a lot of time and love go into the making " +
                        "of these kind of products.\n" +
                        "\n" +
                        "Length Bowl: ~ 39,0 cm / 15,4 in\n" +
                        "\n" +
                        "Length Spoons: ~ 27,0 cm / 10,6 in\n" +
                        "\n" +
                        "Weight: ~ 1,10 kg / 2,42 lb", 50,
                R.drawable.palm_tree_bowl_and_spoon_set));

        productsList.add(new Product("2", "Maasai Heads Set Small",
                "East African Heads are a very common gift for people " +
                        "visiting Africa to take back home. These are beautifully " +
                        "detailed depictions of a traditional Maasai male and female. " +
                        "These sculptures are commonly made out of a mix of bronze " +
                        "filing and polyester put together in a mold. This is then rubbed" +
                        " down with sandpaper and steel wire to create the shiny appearance.\n" +
                        "\n" +
                        "As with all handmade items, slight variations can occur\n" +
                        "\n" +
                        "Height: ~15,0 cm / 5,9 in\n" +
                        "\n" +
                        "Weight: ~1,1 kg / 2,42 lb", 31,
                R.drawable.maasai_heads_small_set));

        productsList.add(new Product("3", "Colourful Maasai Shuka",
                "This is known as ‘the African blanket’ or traditionally as " +
                        "Maasai ‘Shuka’. Culturally the Maasai tribe is identified by " +
                        "their colourful beads, height and of course the red variations " +
                        "of these shuka’s, they are thick and come in variations of " +
                        "red with black, blue and some more vibrant colours.\n" +
                        "\n" +
                        "Length: ~200,0 cm / 78,7 in\n" +
                        "\n" +
                        "Width: ~150,0 cm / 59,0 in\n" +
                        "\n" +
                        "Weight: ~ 0,6 kg / 1,3 lb", 50,
                R.drawable.colourful_maasai_shuka));

        productsList.add(new Product("4", "Banana Weaved Table Runner and Mats " +
                "Natural", "This is a tightly weaved set of a long table" +
                "runner with eight table mats made out of dried banana fibre and " +
                "rafia grass. There is no colouring to this specific set as it is completely natural.\n" +
                "\n" +
                " Length Table Runner: ~ 178,0 cm / 70,1\n" +
                "\n" +
                "Height Table Runner: ~36,0 cm / 14,2 in\n" +
                "\n" +
                "Length Placemats: ~42,0 cm / 16,5 in\n" +
                "\n" +
                "Height Placemats: ~35,0 cm / 13,8 in\n" +
                "\n" +
                "Weight: ~ 1,0 kg / 2,2 lb", 40,
                R.drawable.banana_weaved__mat));

        productsList.add(new Product("5", "White Kamba Sisal Basket",
                "This is a gorgeous natural and white coloured sisal " +
                        "basket. Depending on the sizes a Kenyan weaver works up " +
                        "to five days on one of those pieces. Every basked is trimmed " +
                        "with authentic dark leather.\n" +
                        "\n" +
                        "Diameter Small - Large: ~ 15,0 cm / 5,9 in,  ~ 23,0 cm / 9,1 in,  ~ 40,0 cm / 15,7 in\n" +
                        "\n" +
                        "Height Small - Large: ~ 16,0 cm / 6,3 in,  ~ 28,0 cm / 11,0 in,  ~ 37,0 cm / 14,6 in\n" +
                        "\n" +
                        "Weight Small - Large: ~ 0,35 kg / 0,77 lb,  ~ 0,80 kg / 1,76 lb,  ~ 1,30 kg / 2,87 lb",
                29, R.drawable.white_kamba_basket_medium));

        productsList.add(new Product("6", "Grey, White and purple Pearl Necklace",
                "This is a 3 line fresh water pearl set with an array " +
                        "of grey and white pearls.\n" +
                        "\n" +
                        "18\" Inches ", 130, R.drawable.peal_necklace));

        productsList.add(new Product("7", "Tsavo Key Holder",
                "These are vibrant and interesting key holders made " +
                        "out of different African beads and material.\n" +
                        "\n" +
                        "Length: ~ 14,0 cm / 5,5 in\n" +
                        "\n" +
                        "Weight: ~ 0,05 kg / 0,11 lb", 10, R.drawable.tsavo_key_holder));


        products.setValue(productsList);

    }

}