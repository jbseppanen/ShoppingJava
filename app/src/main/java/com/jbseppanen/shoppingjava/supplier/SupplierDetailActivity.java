package com.jbseppanen.shoppingjava.supplier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jbseppanen.shoppingjava.DataDao;
import com.jbseppanen.shoppingjava.R;
import com.jbseppanen.shoppingjava.product.ProductListActivity;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An activity representing a single Supplier detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SupplierListActivity}.
 */
public class SupplierDetailActivity extends AppCompatActivity {

    public static final String SUPPLIER_PRODUCT_SELECTION_KEY = "SupplierProductSelectionKey";
    public static final int REQUEST_CODE = 79;
    Context context;
    int supplierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        context = this;
        supplierId = getIntent().getIntExtra(SupplierDetailFragment.ARG_ITEM_ID, 0);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_supplier_add_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add product to this supplier", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(context, ProductListActivity.class);
                intent.putExtra(SUPPLIER_PRODUCT_SELECTION_KEY, "SupplierProductSelection");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putInt(SupplierDetailFragment.ARG_ITEM_ID, getIntent().getIntExtra(SupplierDetailFragment.ARG_ITEM_ID, 0));
            SupplierDetailFragment fragment = new SupplierDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.supplier_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, SupplierListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data != null) {
                    int productId = data.getIntExtra(SUPPLIER_PRODUCT_SELECTION_KEY, -1);
                    DataDao.addProductToSupplier(new DataDao.ObjectCallback<Supplier>() {
                        @Override
                        public void returnObjects(Supplier object) {
                            String message = (object != null) ? "Product added to this supplier" : "Product failed to add to this supplier";
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    }, supplierId, productId);
                }
            }
        }
    }
}
