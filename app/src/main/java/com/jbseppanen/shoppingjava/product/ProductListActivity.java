package com.jbseppanen.shoppingjava.product;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.jbseppanen.shoppingjava.CartViewActivity;
import com.jbseppanen.shoppingjava.DataDao;
import com.jbseppanen.shoppingjava.PublicFunctions;
import com.jbseppanen.shoppingjava.R;
import com.jbseppanen.shoppingjava.supplier.SupplierDetailActivity;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.commons.text.WordUtils.capitalizeFully;

/**
 * An activity representing a list of Contacts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProductDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ProductListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    public static ArrayList<Product> productList;
    static Context context;
    SimpleItemRecyclerViewAdapter listAdapter;
    private boolean supplierSelection;

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


        setContentView(R.layout.activity_product_list);

        supplierSelection = getIntent().getStringExtra(SupplierDetailActivity.SUPPLIER_PRODUCT_SELECTION_KEY) != null;

        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab_product_add_new_item);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        findViewById(R.id.fab_view_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        Intent intent = new Intent(context, CartViewActivity.class);
        startActivity(intent);
            }
        });

        if (findViewById(R.id.product_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        final View recyclerView = findViewById(R.id.product_list);
        assert recyclerView != null;

        final DataDao.ObjectCallback<ArrayList<Product>> callback = new DataDao.ObjectCallback<ArrayList<Product>>() {
            @Override
            public void returnObjects(ArrayList<Product> products) {
                productList = products;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRecyclerView((RecyclerView) recyclerView);
                        supportStartPostponedEnterTransition();
                    }
                });
            }
        };
            DataDao.getAllProducts(callback);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        listAdapter = new SimpleItemRecyclerViewAdapter(this, productList, mTwoPane);
        recyclerView.setAdapter(listAdapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ProductListActivity mParentActivity;
        private final ArrayList<Product> mValues;
        private final boolean mTwoPane;


        SimpleItemRecyclerViewAdapter(ProductListActivity parent,
                                      ArrayList<Product> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_content, parent, false);
            boolean test = false;
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.status.set(false);

            int imageId = context.getResources().getIdentifier(mValues.get(position).getProductname(), "drawable", context.getPackageName());
            if (imageId != 0) {
                holder.mImageView.setImageResource(imageId);
            }

            String vanityName = capitalizeFully(mValues.get(position).productname.replace("_", " "));
            holder.mNameView.setText(vanityName);
            holder.mIdView.setText(String.valueOf(mValues.get(position).productid));
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Product product = (Product) v.getTag();
                    if (supplierSelection) {
                        Intent intent = new Intent();
                        intent.putExtra(SupplierDetailActivity.SUPPLIER_PRODUCT_SELECTION_KEY, product.getProductid());
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putInt(ProductDetailFragment.ARG_ITEM_ID, product.productid - 1);
                            ProductDetailFragment fragment = new ProductDetailFragment();
                            fragment.setArguments(arguments);
                            mParentActivity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.product_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, ProductDetailActivity.class);
                            intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, product.productid - 1);
                            final ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, holder.mImageView, ViewCompat.getTransitionName(holder.mImageView));
                            context.startActivity(intent, activityOptions.toBundle());
                        }
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
            final TextView mIdView;
            final TextView mNameView;
            final ImageView mImageView;
            final AtomicBoolean status;

            ViewHolder(View view) {
                super(view);
                mIdView = view.findViewById(R.id.id_text_product_qty);
                mNameView = view.findViewById(R.id.text_product_list_name);
                mImageView = view.findViewById(R.id.image_product_list);
                status = new AtomicBoolean(false);
            }
        }
    }
}