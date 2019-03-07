package com.jbseppanen.shoppingjava;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jbseppanen.shoppingjava.product.Product;

import java.util.ArrayList;

import static org.apache.commons.text.WordUtils.capitalizeFully;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemQty;
        ImageView imageView;
        ImageButton deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.text_cart_list_name);
            itemQty = itemView.findViewById(R.id.id_text_cart_qty);
            imageView = itemView.findViewById(R.id.image_cart_list);
            deleteButton = itemView.findViewById(R.id.button_cart_delete_item);
        }
    }

    private ArrayList<Product> dataList;
    private Context context;

    public CartListAdapter(ArrayList<Product> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        context = viewGroup.getContext();
        View view = LayoutInflater.from(
                viewGroup.getContext())
                .inflate(
                        R.layout.cart_list_element,
                        viewGroup,
                        false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListAdapter.ViewHolder viewHolder, int i) {
        Product product = dataList.get(i);

        String vanityName = capitalizeFully(product.getProductname().replace("_", " "));
        viewHolder.itemName.setText(vanityName);
        viewHolder.itemQty.setText(String.valueOf(product.getQtyinstock()));
        int imageId = context.getResources().getIdentifier(product.getProductname(), "drawable", context.getPackageName());
        if (imageId != 0) {
            viewHolder.imageView.setImageResource(imageId);
        }

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Function to delete not yet implemented", Toast.LENGTH_SHORT);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
