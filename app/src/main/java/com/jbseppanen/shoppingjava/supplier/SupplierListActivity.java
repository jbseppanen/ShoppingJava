package com.jbseppanen.shoppingjava.supplier;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jbseppanen.shoppingjava.DataDao;
import com.jbseppanen.shoppingjava.PublicFunctions;
import com.jbseppanen.shoppingjava.R;
import com.jbseppanen.shoppingjava.product.Product;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.text.WordUtils.capitalizeFully;

/**
 * An activity representing a list of Contacts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SupplierDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SupplierListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static ArrayList<Supplier> supplierList;
    static Context context;
    SimpleItemRecyclerViewAdapter listAdapter;
    DataDao.ObjectCallback callback;


    @Override
    public void onStart() {
        super.onStart();
        PublicFunctions.deleteCache(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        final Fade transition = new Fade();
        transition.setStartDelay(250);
        transition.setDuration(500);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
        supportPostponeEnterTransition();


        setContentView(R.layout.activity_supplier_list);

        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab_supplier_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add new supplier", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                addNewSupplierDialog();
            }
        });

        if (findViewById(R.id.supplier_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        final View recyclerView = findViewById(R.id.supplier_list);
        assert recyclerView != null;

        callback = new DataDao.ObjectCallback<ArrayList<Supplier>>() {
            @Override
            public void returnObjects(ArrayList<Supplier> suppliers) {
                supplierList = suppliers;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRecyclerView((RecyclerView) recyclerView);
                        supportStartPostponedEnterTransition();
                    }
                });
            }
        };

        DataDao.getAllSuppliers(callback);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        listAdapter = new SimpleItemRecyclerViewAdapter(this, supplierList, mTwoPane);
        recyclerView.setAdapter(listAdapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final SupplierListActivity mParentActivity;
        private final ArrayList<Supplier> mValues;
        private final boolean mTwoPane;


        SimpleItemRecyclerViewAdapter(SupplierListActivity parent,
                                      ArrayList<Supplier> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.supplier_list_content, parent, false);
            boolean test = false;
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.status.set(false);

            String vanityName = capitalizeFully(mValues.get(position).suppliername.replace("_", " "));
            holder.mNameView.setText(vanityName);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Supplier supplier = (Supplier) v.getTag();
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(SupplierDetailFragment.ARG_ITEM_ID, supplier.supplierid - 1);
                        SupplierDetailFragment fragment = new SupplierDetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.supplier_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, SupplierDetailActivity.class);
                        intent.putExtra(SupplierDetailFragment.ARG_ITEM_ID, supplier.supplierid - 1);
                        context.startActivity(intent);
                    }

                }
            });

            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
            animation.setDuration(200);
            holder.itemView.startAnimation(animation);

        }

        @Override
        public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            holder.status.set(true);
        }

        @Override
        public int getItemCount() {
            if (mValues == null) {
                return 0;
            } else {
                return mValues.size();
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mNameView;
            final AtomicBoolean status;

            ViewHolder(View view) {
                super(view);
                mNameView = view.findViewById(R.id.text_product_list_name);
                status = new AtomicBoolean(false);
            }
        }

    }

    private void addNewSupplierDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_supplier_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        final EditText supplierNameView = dialog.findViewById(R.id.edit_add_supplier_name);

        dialog.findViewById(R.id.button_save_supplier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Supplier supplier = new Supplier();
                if (supplierNameView.getText().toString().equals("")) {
                    Toast.makeText(context, "Name is required.", Toast.LENGTH_SHORT).show();
                    return;
                }
                supplier.setSuppliername(supplierNameView.getText().toString());
                supplier.setAddress(((EditText) dialog.findViewById(R.id.edit_add_supplier_address)).getText().toString());
                supplier.setCity(((EditText) dialog.findViewById(R.id.edit_add_supplier_city)).getText().toString());
                supplier.setState(((EditText) dialog.findViewById(R.id.edit_add_supplier_state)).getText().toString());
                supplier.setZipcode(((EditText) dialog.findViewById(R.id.edit_add_supplier_zipcode)).getText().toString());
                supplier.setPhonenumber(((EditText) dialog.findViewById(R.id.edit_add_supplier_phone)).getText().toString());
                DataDao.addNewSupplier(supplier);
                DataDao.getAllSuppliers(callback);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}