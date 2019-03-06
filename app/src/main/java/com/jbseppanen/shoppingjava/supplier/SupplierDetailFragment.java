package com.jbseppanen.shoppingjava.supplier;

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
import android.widget.TextView;

import com.jbseppanen.shoppingjava.R;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.text.WordUtils.capitalizeFully;

/**
 * A fragment representing a single Supplier detail screen.
 * This fragment is either contained in a {@link SupplierListActivity}
 * in two-pane mode (on tablets) or a {@link SupplierDetailActivity}
 * on handsets.
 */
public class SupplierDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Supplier mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SupplierDetailFragment() {
    }

    Context context;
    AtomicBoolean cancelStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        cancelStatus = new AtomicBoolean(false);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = SupplierListActivity.supplierList.get(getArguments().getInt(ARG_ITEM_ID));
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                String vanityName = capitalizeFully((mItem.suppliername.replace("_"," ")));
                appBarLayout.setTitle(vanityName);
            }
        }
        getActivity().supportPostponeEnterTransition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.supplier_detail, container, false);
        cancelStatus = new AtomicBoolean(false);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.supplier_phone)).setText(mItem.getPhonenumber());
            ((TextView) rootView.findViewById(R.id.supplier_address)).setText(mItem.getAddress());
            String cityStateZip = mItem.getCity() + ", " + mItem.getState() + " " + mItem.getZipcode();
            ((TextView) rootView.findViewById(R.id.supplier_citystatezip)).setText(cityStateZip);
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
        cancelStatus.set(true);
    }
}
