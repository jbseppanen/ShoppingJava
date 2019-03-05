package com.jbseppanen.shoppingjava;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

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


    @Override
    public void onStart() {
        super.onStart();
        PublicFunctions.deleteCache(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        final Fade transition = new  Fade();
        transition.setStartDelay(250);
        transition.setDuration(500);
        getWindow().setEnterTransition(transition);
        getWindow().setExitTransition(transition);
        supportPostponeEnterTransition();


        setContentView(R.layout.activity_contact_list);

        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.contact_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        final View recyclerView = findViewById(R.id.contact_list);
        assert recyclerView != null;

        AtomicBoolean canceled = new AtomicBoolean(false);
        ProductsDao.getContacts(canceled, new ProductsDao.ObjectCallback<ArrayList<Product>>() {
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
        });

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
                    .inflate(R.layout.contact_list_content, parent, false);
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


            holder.mNameView.setText(mValues.get(position).productname);
            holder.mIdView.setText(String.valueOf(mValues.get(position).productid));
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Product product = (Product) v.getTag();
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(ProductDetailFragment.ARG_ITEM_ID, product.productid);
                        ProductDetailFragment fragment = new ProductDetailFragment();
                        fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.contact_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, product.productid);
                        final ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation((Activity) context, holder.mImageView, ViewCompat.getTransitionName(holder.mImageView));
                        context.startActivity(intent, activityOptions.toBundle());
                    }

                }
            });

            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
            animation.setDuration(1000);
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
                mIdView = view.findViewById(R.id.id_text);
                mNameView = view.findViewById(R.id.name);
                mImageView = view.findViewById(R.id.image);
                status = new AtomicBoolean(false);
            }
        }
    }
}