package com.jbseppanen.shoppingjava.product;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbseppanen.shoppingjava.R;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.text.WordUtils.capitalizeFully;

/**
 * A fragment representing a single Product detail screen.
 * This fragment is either contained in a {@link ProductListActivity}
 * in two-pane mode (on tablets) or a {@link ProductDetailActivity}
 * on handsets.
 */
public class ProductDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Product mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ProductDetailFragment() {
    }

    Context context;
//    AtomicBoolean cancelStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
//        cancelStatus = new AtomicBoolean(false);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = ProductListActivity.productList.get(getArguments().getInt(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                String vanityName = capitalizeFully((mItem.productname.replace("_"," ")));
                appBarLayout.setTitle(vanityName);
            }
        }
        getActivity().supportPostponeEnterTransition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.product_detail, container, false);
//        cancelStatus = new AtomicBoolean(false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.product_description)).setText(mItem.getDescription());
            String price = "$" + mItem.price;
            ((TextView) rootView.findViewById(R.id.product_price)).setText(price);
            ((TextView) rootView.findViewById(R.id.product_qty)).setText(String.valueOf(mItem.getQtyinstock()));

            int imageId = context.getResources().getIdentifier(mItem.getProductname(), "drawable", context.getPackageName());
            if (imageId != 0) {
                ((ImageView) rootView.findViewById(R.id.image_large)).setImageResource(imageId);
            }
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().supportStartPostponedEnterTransition();

    }

    @Override
    public void onPause() {
        super.onPause();
//        cancelStatus.set(true);
    }
}
